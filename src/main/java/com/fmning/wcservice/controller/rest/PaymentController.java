package com.fmning.wcservice.controller.rest;

import java.io.ByteArrayInputStream;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.braintreegateway.Result;
import com.braintreegateway.Transaction;
import com.braintreegateway.TransactionRequest;
import com.fmning.service.domain.Event;
import com.fmning.service.domain.Payment;
import com.fmning.service.domain.Ticket;
import com.fmning.service.domain.TicketTemplate;
import com.fmning.service.domain.User;
import com.fmning.service.exceptions.NotFoundException;
import com.fmning.service.manager.ErrorManager;
import com.fmning.service.manager.EventManager;
import com.fmning.service.manager.HelperManager;
import com.fmning.service.manager.PaymentManager;
import com.fmning.service.manager.TicketManager;
import com.fmning.service.manager.UserManager;
import com.fmning.util.ErrorMessage;
import com.fmning.util.PaymentStatusType;
import com.fmning.util.PaymentType;
import com.fmning.util.TicketType;
import com.fmning.util.Util;
import com.fmning.wcservice.utils.Utils;

@Controller
public class PaymentController {
	
	@Autowired private UserManager userManager;
	@Autowired private PaymentManager paymentManager;
	@Autowired private EventManager eventManager;
	@Autowired private TicketManager ticketManager;
	@Autowired private ErrorManager errorManager;
	@Autowired private HelperManager helperManager;
	
	
	private static DateTimeFormatter formatter =
			  DateTimeFormatter.ofPattern("yyMMddHHmmss").withZone(ZoneId.systemDefault());
	
	
	
	@RequestMapping("/pay")
	public ResponseEntity<String> test(@RequestBody Map<String, Object> request) {
		
		TransactionRequest paymentRequest = new TransactionRequest()
			    .amount(new BigDecimal("1.34"))
			    .paymentMethodNonce((String)request.get("nonce"))
			    .options()
			      .submitForSettlement(true)
			      .done();

		Result<Transaction> result = Utils.gateway.transaction().sale(paymentRequest);
		
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
	
	@RequestMapping("/check_payment_status")
	public ResponseEntity<Map<String, Object>> checkPaymentStatus(@RequestBody Map<String, Object> request) {
		Map<String, Object> respond = new HashMap<String, Object>();
		
		try{
			User user = userManager.validateAccessToken(request);
			String type = (String)request.get("type");
			int id = (int)request.get("id");
			
			if (!type.equals(PaymentType.EVENT.getName()))
				throw new IllegalStateException(ErrorMessage.PAYMENT_NOT_SUPPORTED.getMsg());
			
			Event event = eventManager.getEventById(id);
			
			try {
				Payment payment = paymentManager.getPaymentByTypeAndPayer(PaymentType.EVENT.getName(),
						event.getId(), user.getId(), event.getOwnerId());
				if (payment.getStatus().equals(PaymentStatusType.DONE.getName())) {
					try{
						Ticket ticket = ticketManager.getTicketByType(TicketType.PAYMENT.getName(), payment.getId());
						respond.put("error", "");
						respond.put("status", PaymentStatusType.ALREADY_PAID.getName());
						respond.put("ticketId", ticket.getId());
					} catch (NotFoundException e){
						throw new IllegalStateException(ErrorMessage.TICKET_INTERNAL_ERROR.getMsg());
					}
				} else {
					respond.put("error", "");
					respond.put("status", payment.getStatus());// Should only be Rejected
				}
				
			} catch (NotFoundException e){
				respond.put("error", "");
				respond.put("status", PaymentStatusType.NOT_EXIST.getName());
			}
			
			if (user.isTokenUpdated()) {
				respond.put("accessToken", user.getAccessToken());
			}
			
		}catch(Exception e){
			respond = errorManager.createErrorRespondFromException(e, Utils.rootDir + "/check_payment_status", request);
		}
		
		return new ResponseEntity<Map<String, Object>>(respond, HttpStatus.OK);
		
	}
	
	@RequestMapping("/make_payment")
	public ResponseEntity<Map<String, Object>> makePayment(@RequestBody Map<String, Object> request) {
		Map<String, Object> respond = new HashMap<String, Object>();
		try{
			User user = userManager.validateAccessToken(request);
			
			int payerId = user.getId();
			String type = (String)request.get("type");
			int id = (int)request.get("id"); 
			double amount = 0;
			try{
				amount = (double)request.get("amount");
			}catch(ClassCastException e){
				amount =  (int)request.get("amount");
			}
			String nonce = (String)request.get("nonce");
			String method = (String)request.get("method");
			
			if (!type.equals(PaymentType.EVENT.getName()))
				throw new IllegalStateException(ErrorMessage.PAYMENT_NOT_SUPPORTED.getMsg());
			if (!user.getEmailConfirmed())
				throw new IllegalStateException(ErrorMessage.EMAIL_NOT_CONFIRMED.getMsg());
			if (amount > 0 && nonce == null)
				throw new IllegalStateException(ErrorMessage.INVALID_PAYMENT_TOKEN.getMsg());
			
			Event event = eventManager.getEventById(id);
			
			if(event.getFee() != amount)
				throw new IllegalStateException(ErrorMessage.PAYMENT_AMOUNT_INVALID.getMsg());
			if(event.getFee() == 0 && !user.getUsername().endsWith("@wpi.edu"))
				throw new IllegalStateException(ErrorMessage.FREE_TICKET_INVALID_EMAIL.getMsg());
			
			//If the payment is already done, re-provide ticket and bypass validation
			try{
				Payment payment = paymentManager.getPaymentByTypeAndPayer(PaymentType.EVENT.getName(),
						event.getId(), payerId, event.getOwnerId());
				if (!payment.getStatus().equals(PaymentStatusType.DONE.getName())) {
					//Ticket status rejected. throw exception to try making payment again
					throw new NotFoundException();
				} else {
					try{
						Ticket ticket = ticketManager.getTicketByType(TicketType.PAYMENT.getName(), payment.getId());
						respond.put("error", "");
						respond.put("status", PaymentStatusType.ALREADY_PAID.getName());
						respond.put("ticketId", ticket.getId());
					} catch (NotFoundException e){
						throw new IllegalStateException(ErrorMessage.TICKET_INTERNAL_ERROR.getMsg());
					}
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
					eventManager.setBalance(event.getId(), event.getTicketBalance() - 1, Util.nullInt);
				}
				
				
				//Validation done. Start to process payment and generate new ticket
				//Payment for non-free tickets
				int paymentId;
				boolean paymentRejected = false;
				if (amount > 0) {
					paymentId = 0;
					TransactionRequest paymentRequest = new TransactionRequest()
						    .amount(new BigDecimal(String.format( "%.2f", amount )))
						    .paymentMethodNonce(nonce)
						    .options()
						    .submitForSettlement(true)
						    .done();

					Result<Transaction> result = Utils.gateway.transaction().sale(paymentRequest);
					String status;
					String message = null;
					if (result.isSuccess()) {
						status = PaymentStatusType.DONE.getName();
						respond.put("error", "");
						respond.put("status", PaymentStatusType.DONE.getName());
						try {
							String emailMsg = createReceiptEmail(userManager.getUserDisplayedName(user.getId()), event.getTitle(),
									method, String.format( "%.2f", amount));
							if (Utils.prodMode){
								try {
									helperManager.sendEmail("no-reply@fmning.com", user.getUsername(), "Receipt", emailMsg);
								} catch (Exception e1) {
									errorManager.logError(e1);
								}
							} else {
								System.out.println(emailMsg);
							}
						} catch (Exception e1){
							errorManager.logError(e1, Utils.rootDir + "/make_payment", request);
						}
					} else {
						paymentRejected = true;
						status = PaymentStatusType.REJECTED.getName();
						message = result.getMessage();
						if (message == null) {
							message = ErrorMessage.INVALID_PAYMENT_TOKEN.getMsg();
						} else if (message.trim().contentEquals("")) {
							message = ErrorMessage.INVALID_PAYMENT_TOKEN.getMsg();
						}
						respond.put("error", ErrorMessage.PAYMENT_REJECTED.getMsg() + " " + message);
						message = StringUtils.abbreviate(message, 200);
					}
					paymentId = paymentManager.savePayment(PaymentType.EVENT.getName(), event.getId(), amount,
							status, message, payerId, event.getOwnerId(), StringUtils.abbreviate(method, 100), nonce);
					
				} else {// payment for free tickets
					paymentId = paymentManager.savePayment(PaymentType.EVENT.getName(), event.getId(), amount,
						PaymentStatusType.DONE.getName(), null, payerId, event.getOwnerId(), null, null);
					try {
						String emailMsg = createFreeTicketEmail(userManager.getUserDisplayedName(user.getId()), event.getTitle());
						if (Utils.prodMode){
							try {
								helperManager.sendEmail("no-reply@fmning.com", user.getUsername(), "Receipt", emailMsg);
							} catch (Exception e1) {
								errorManager.logError(e1);
							}
						} else {
							System.out.println(emailMsg);
						}
					} catch (Exception e1){
						errorManager.logError(e1, Utils.rootDir + "/make_payment", request);
					}
					respond.put("error", "");
					respond.put("status", PaymentStatusType.DONE.getName());
				}
				
				//Payment is done without exceptions. Start creating ticket
				if(event.getTicketTemplateId() == Util.nullInt) {
					throw new IllegalStateException(ErrorMessage.EVENT_WITHOUT_TICKET_TEMPLATE.getMsg());
				} else if (!paymentRejected){
					TicketTemplate template = ticketManager.getTicketTemplateById(event.getTicketTemplateId());
					try{
						byte[] ticket = TicketController.createTicket(event, template, userManager.getUserDisplayedName(payerId));
						String ticketFile = Utils.ticketPath + "T_" + Integer.toString(payerId);
						ticketFile += "_" + formatter.format(Instant.now()) + ".pkpass";
						int ticketId = ticketManager.createTicket(template.getId(), TicketType.PAYMENT.getName(), paymentId,
								ticketFile, payerId);
						
						if (request.get("web") != null) {// For web requests, return the ticket id instead of ticket itself
							respond.put("ticketId", ticketId);
						} else {
							respond.put("ticket", ticket);
						}
						ByteArrayInputStream inputStream = new ByteArrayInputStream(ticket);
						IOUtils.copy(inputStream, new FileOutputStream(ticketFile));
						
					} catch (Exception e1) {
						throw new IllegalStateException(ErrorMessage.TICKET_CREATION_FAILED.getMsg());
					}
				}
			}
			
			if (user.isTokenUpdated()) {
				respond.put("accessToken", user.getAccessToken());
			}
			
		}catch(Exception e){
			respond = errorManager.createErrorRespondFromException(e, Utils.rootDir + "/make_payment", request);
		}
		return new ResponseEntity<Map<String, Object>>(respond, HttpStatus.OK);
		
	}
	
	public static String createReceiptEmail(String name, String eventName, String paymentMethod, String amount) {
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		df.setTimeZone(TimeZone.getTimeZone("EST"));
		
		String message = "Hi " + name + ",";
		message += "\n";
		message += "Thank you for purchasing ticket with WPI CSA. Your payment is processed successfully.";
		message += "\n\nEvent: " + eventName;
		message += "\nPayment method: " + paymentMethod;
		message += "\nAmount: $" + amount;
		message += "\nDate: " + df.format(new Date());
		message += "\n\nThank you.";
		message += "\n";
		return message;
	}
	
	public static String createFreeTicketEmail(String name, String eventName) {
		String message = "Hi " + name + ",";
		message += "\n";
		message += "Thank you for registering event " + eventName + ".";
		message += "\n\nSince this is a free event, please remember to let us know if you cannot attend due to any reason.";
		message += "\nTo do that, you can email csa@wpi.edu, admin@fmning.com or send a WeChat message to any of the CSA members.";
		message += "\n\nThank you.";
		message += "\n";
		return message;
	}
	
	
}
