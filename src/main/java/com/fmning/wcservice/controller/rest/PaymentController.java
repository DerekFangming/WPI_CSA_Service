package com.fmning.wcservice.controller.rest;

import java.io.ByteArrayInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.braintreegateway.BraintreeGateway;
import com.braintreegateway.Environment;
import com.braintreegateway.Result;
import com.braintreegateway.Transaction;
import com.braintreegateway.TransactionRequest;
import com.fmning.service.domain.Event;
import com.fmning.service.domain.Payment;
import com.fmning.service.domain.Ticket;
import com.fmning.service.domain.TicketTemplate;
import com.fmning.service.exceptions.NotFoundException;
import com.fmning.service.manager.EventManager;
import com.fmning.service.manager.PaymentManager;
import com.fmning.service.manager.TicketManager;
import com.fmning.service.manager.UserManager;
import com.fmning.util.ErrorMessage;
import com.fmning.util.PaymentStatusType;
import com.fmning.util.PaymentType;
import com.fmning.util.TicketType;
import com.fmning.util.Util;
import com.fmning.wcservice.utils.Utils;

import de.brendamour.jpasskit.PKBarcode;
import de.brendamour.jpasskit.PKField;
import de.brendamour.jpasskit.PKPass;
import de.brendamour.jpasskit.enums.PKBarcodeFormat;
import de.brendamour.jpasskit.enums.PKDateStyle;
import de.brendamour.jpasskit.passes.PKEventTicket;
import de.brendamour.jpasskit.signing.PKFileBasedSigningUtil;
import de.brendamour.jpasskit.signing.PKSigningException;
import de.brendamour.jpasskit.signing.PKSigningInformation;
import de.brendamour.jpasskit.signing.PKSigningInformationUtil;

@Controller
public class PaymentController {
	
	@Autowired private UserManager userManager;
	@Autowired private PaymentManager paymentManager;
	@Autowired private EventManager eventManager;
	@Autowired private TicketManager ticketManager;
	
	private static DateTimeFormatter formatter =
			  DateTimeFormatter.ofPattern("yyMMddHHmmss").withZone(ZoneId.systemDefault());
	
	private static BraintreeGateway gateway = new BraintreeGateway(
			  Environment.SANDBOX,
			  "wnbj3bx4nwmtyz77",
			  "2x688s4dpnpxf2dd",
			  "0806df90cf0bd867727c42077e6b41bd"
			);
	
	@RequestMapping("/pay")
	public ResponseEntity<String> test(@RequestBody Map<String, Object> request) {
		
		TransactionRequest paymentRequest = new TransactionRequest()
			    .amount(new BigDecimal("10.00"))
			    .paymentMethodNonce((String)request.get("nonce"))
			    .options()
			      .submitForSettlement(true)
			      .done();

		Result<Transaction> result = gateway.transaction().sale(paymentRequest);
		
		if (result.isSuccess()) {
			System.out.println("scuessfull");
			System.out.println(result.getMessage());
		} else {
			System.out.println("fail");
			System.out.println(result.getMessage());
		}
		
		System.out.println(request.get("nonce"));
		return new ResponseEntity<String>("", HttpStatus.OK);
	}
	
	@RequestMapping("/make_payment")
	public ResponseEntity<Map<String, Object>> getTicket(@RequestBody Map<String, Object> request) {
		Map<String, Object> respond = new HashMap<String, Object>();
		try{
			int payerId = userManager.validateAccessToken(request).getId();
			String type = (String)request.get("type");
			int id = (int)request.get("id"); 
			double amount = 0;
			try{
				amount = (double)request.get("amount");
			}catch(ClassCastException e){
				amount =  (int)request.get("amount");
			}
			
			if (!type.equals(PaymentType.EVENT.getName()) || amount > 0)
				throw new IllegalStateException(ErrorMessage.PAYMENT_NOT_SUPPORTED.getMsg());
			
			Event event = eventManager.getEventById(id);
			
			if(event.getFee() != amount)
				throw new IllegalStateException("Payment amount is not correct.");
			
			
			try{//If the payment is already done, re-provide ticket and bypass validation
				Payment payment = paymentManager.getPaymentByTypeAndPayer(PaymentType.EVENT.getName(),
						event.getId(), payerId, event.getOwnerId());
				try{
					Ticket ticket = ticketManager.getTicketByType(TicketType.PAYMENT.getName(), payment.getId());
					respond.put("error", "");
					respond.put("status", "AlreadyPaid");
					respond.put("ticketStatus", "ok");
					respond.put("ticketId", ticket.getId());
				} catch (NotFoundException e){
					throw new IllegalStateException(ErrorMessage.TICKET_INTERNAL_ERROR.getMsg());
				}
				
			}catch (NotFoundException e){
				//Validation for buying new ticket
				if(!event.getActive()){
					if(event.getMessage() == null)
						throw new IllegalStateException(ErrorMessage.EVENT_NOT_ACTIVE.getMsg());
					else
						throw new IllegalStateException(event.getMessage());
				}
				if(event.getTicketBalance() < 1) {
					throw new IllegalStateException(ErrorMessage.TICKET_SOLD_OUT.getMsg());
				} else {
					eventManager.setBalance(event.getId(), event.getTicketBalance() - 1);
				}
				
				
				//Validation done
				int paymentId = paymentManager.createPayment(PaymentType.EVENT.getName(), event.getId(), amount,
						PaymentStatusType.DONE.getName(), null, payerId, event.getOwnerId(), null, null);
				respond.put("error", "");
				respond.put("status", "ok");
				
				if(event.getTicketTemplateId() == Util.nullInt) {
					respond.put("ticketStatus", "There is no ticket for this event.");
				} else {
					TicketTemplate template = ticketManager.getTicketTemplateById(event.getTicketTemplateId());
					try{
						byte[] ticket = createTicket(event, template, payerId);
						String ticketFile = Utils.ticketPath + "T_" + Integer.toString(payerId);
						ticketFile += "_" + formatter.format(Instant.now()) + ".pkpass";
						ticketManager.createTicket(template.getId(), TicketType.PAYMENT.getName(), paymentId,
								ticketFile, payerId);
						respond.put("ticketStatus", "ok");
						respond.put("ticket", ticket);
						try{
							ByteArrayInputStream inputStream = new ByteArrayInputStream(ticket);
							IOUtils.copy(inputStream, new FileOutputStream(ticketFile));
						}catch(Exception e1){}//TODO: Do something here?
					} catch (Exception e1) {
						respond.put("ticketStatus", "Ticket generation failed.");
					}
				}
			}
			
			
			
			
		}catch(Exception e){
			respond = Util.createErrorRespondFromException(e);
		}
		return new ResponseEntity<Map<String, Object>>(respond, HttpStatus.OK);
		
	}
	
	private byte[] createTicket(Event event, TicketTemplate template, int userId) throws UnrecoverableKeyException,
			NoSuchAlgorithmException, CertificateException, KeyStoreException, IOException, PKSigningException{
		ClassLoader classLoader = getClass().getClassLoader();
		String privateKeyPath = classLoader.getResource("passCertificate.p12").getFile();
		String appleWWDRCA = classLoader.getResource("AppleWWDRCA.cer").getFile();
		String privateKeyPassword = "fmning123!";
		
		PKSigningInformation pkSigningInformation = new PKSigningInformationUtil().loadSigningInformationFromPKCS12AndIntermediateCertificate(
                privateKeyPath, privateKeyPassword, appleWWDRCA);
       
        PKPass pass = new PKPass();
        pass.setPassTypeIdentifier("pass.com.fmning.WPI-CSA");
        pass.setSerialNumber(Integer.toString(template.getSerialNumber()));
        pass.setTeamIdentifier("NK4455562X");
        pass.setOrganizationName("fmning.com");
        pass.setDescription(template.getDescription());
        pass.setLogoText(template.getLogoText());
        if(template.getBgColor() != null)
        	pass.setBackgroundColor(template.getBgColor());

        PKBarcode barcode = new PKBarcode();
        barcode.setFormat(PKBarcodeFormat.PKBarcodeFormatQR);
        barcode.setMessageEncoding(Charset.forName("iso-8859-1"));
        barcode.setMessage("If you have questions, please contact admin@fmning.com");
        pass.setBarcodes(Collections.singletonList(barcode));

        PKEventTicket eventTicket = new PKEventTicket();
        PKField title = new PKField();
        title.setKey("name");
        title.setValue(event.getTitle());
        eventTicket.setPrimaryFields(Collections.singletonList(title));
        
        PKField location = new PKField();
        location.setKey("location");
        location.setLabel("Location");
        location.setValue(event.getLocation());
        eventTicket.setSecondaryFields(Collections.singletonList(location));
        
        List<PKField> auxilField = new ArrayList<PKField>();
        PKField startDate = new PKField();
        startDate.setKey("date");
        startDate.setLabel("Start time");
        startDate.setDateStyle(PKDateStyle.PKDateStyleMedium);
        startDate.setTimeStyle(PKDateStyle.PKDateStyleMedium);
        startDate.setValue(event.getStartTime().toString());
        auxilField.add(startDate);
        
        PKField participant = new PKField();
        participant.setKey("participant");
        participant.setLabel("Participant");
        participant.setValue(userManager.getUserDisplayedName(userId));
        auxilField.add(participant);
        
        eventTicket.setAuxiliaryFields(auxilField);
        
        pass.setEventTicket(eventTicket);
        
        if (pass.isValid()) {
            String pathToTemplateDirectory = template.getLocation();
            byte[] passZipAsByteArray = new PKFileBasedSigningUtil().createSignedAndZippedPkPassArchive(pass, pathToTemplateDirectory, pkSigningInformation);
            
            return passZipAsByteArray;
        } else {
            throw new IllegalStateException("Payment processed but ticket failed to generate. Please contact admin@fmning.com");
        }
	}
	
}
