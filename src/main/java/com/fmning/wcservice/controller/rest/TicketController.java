package com.fmning.wcservice.controller.rest;

import java.awt.Color;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.fmning.service.domain.Ticket;
import com.fmning.service.domain.User;
import com.fmning.service.manager.TicketManager;
import com.fmning.service.manager.UserManager;
import com.fmning.util.ErrorMessage;
import com.fmning.util.Util;

import de.brendamour.jpasskit.PKBarcode;
import de.brendamour.jpasskit.PKField;
import de.brendamour.jpasskit.PKLocation;
import de.brendamour.jpasskit.PKPass;
import de.brendamour.jpasskit.enums.PKBarcodeFormat;
import de.brendamour.jpasskit.enums.PKDateStyle;
import de.brendamour.jpasskit.passes.PKEventTicket;
import de.brendamour.jpasskit.signing.PKFileBasedSigningUtil;
import de.brendamour.jpasskit.signing.PKSigningInformation;
import de.brendamour.jpasskit.signing.PKSigningInformationUtil;

@Controller
public class TicketController {
	
	@Autowired private UserManager userManager;
	@Autowired private TicketManager ticketManager;

	@RequestMapping(value = "/test_pass", method = RequestMethod.GET)
    public ResponseEntity<Map<String, Object>> getRecentFeedsForUser(HttpServletRequest request) {
		Map<String, Object> respond = new HashMap<String, Object>();
		
		ClassLoader classLoader = getClass().getClassLoader();
		String privateKeyPath = classLoader.getResource("passCertificate.p12").getFile();
		String appleWWDRCA = classLoader.getResource("AppleWWDRCA.cer").getFile();
		
		String privateKeyPassword = "fmning123!"; // the password you used to export
        try {
          
            PKSigningInformation pkSigningInformation = new PKSigningInformationUtil().loadSigningInformationFromPKCS12AndIntermediateCertificate(
                    privateKeyPath, privateKeyPassword, appleWWDRCA);
           
            PKPass pass = new PKPass();
            pass.setPassTypeIdentifier("pass.com.fmning.WPI-CSA");
            //pass.setAuthenticationToken("vxwxd7J8AlNNFPS8k0a0FfUFtq0ewzFdcdc");
            pass.setSerialNumber("123456780003");
            pass.setTeamIdentifier("NK4455562X"); // replace this with your team ID
            pass.setOrganizationName("fmning.com");
            pass.setDescription("WPI CSA Event6");
            pass.setLogoText("WPI CSA");
            pass.setForegroundColor("#630C0C");
            pass.setBackgroundColor("#212121");
            pass.setLabelColor("#2D3681");
            

            PKBarcode barcode = new PKBarcode();
            barcode.setFormat(PKBarcodeFormat.PKBarcodeFormatQR);
            barcode.setMessageEncoding(Charset.forName("iso-8859-1"));
            barcode.setMessage("Some very long message oh my fuck god hahahahahahah");
            pass.setBarcodes(Collections.singletonList(barcode));

            //PKGenericPass generic = new PKGenericPass();
            PKEventTicket event = new PKEventTicket();
            PKField member = new PKField();
            member.setKey("name");
            member.setValue("11/11 Singles' Day Formal");
            event.setPrimaryFields(Collections.singletonList(member));
            
            List<PKField> secondField = new ArrayList<PKField>();
            PKField loc = new PKField();
            loc.setKey("location"); // some unique key for primary field
            loc.setLabel("Location");
            loc.setValue("Campus Center Odeum");
            secondField.add(loc);
            event.setSecondaryFields(secondField);
            
            List<PKField> auxilField = new ArrayList<PKField>();
            PKField date = new PKField();
            date.setKey("date");
            date.setLabel("Start time");
            date.setDateStyle(PKDateStyle.PKDateStyleMedium);
            date.setTimeStyle(PKDateStyle.PKDateStyleMedium);
            date.setValue("2017-11-11T23:00:00Z");
            auxilField.add(date);
            
            PKField att = new PKField();
            att.setKey("participant");
            att.setLabel("Participant");
            att.setValue("Fangming Ning");
            auxilField.add(att);
            
            event.setAuxiliaryFields(auxilField);
            
            pass.setEventTicket(event);
            
            PKLocation location = new PKLocation();
            location.setLatitude(37.33182); // replace with some lat
            location.setLongitude(-122.03118); // replace with some long
            List<PKLocation> locations = new ArrayList<PKLocation>();
            locations.add(location);
            pass.setLocations(locations);
           
            if (pass.isValid()) {
                String pathToTemplateDirectory = "/Volumes/Data/passTemplates/test"; // replace with your folder with the icons
                byte[] passZipAsByteArray = new PKFileBasedSigningUtil().createSignedAndZippedPkPassArchive(pass, pathToTemplateDirectory, pkSigningInformation);
                //respond.put("data", passZipAsByteArray);
                
                String outputFile = "/Volumes/Data/testTickets/test.pkpass"; // change the name of the pass
                ByteArrayInputStream inputStream = new ByteArrayInputStream(passZipAsByteArray);
                IOUtils.copy(inputStream, new FileOutputStream(outputFile));
                System.out.println("Done!");
            } else {
                System.out.println("the pass is NOT Valid man!!!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("failed!");
        }

		return new ResponseEntity<Map<String, Object>>(respond, HttpStatus.OK);
	}
	
	@RequestMapping(value = "/get_pass", method = RequestMethod.GET)
	public void getPass(HttpServletRequest request, HttpServletResponse response) {
		try{
			
			InputStream is = new FileInputStream("/Volumes/Data/testTickets/test.pkpass");
	        IOUtils.copy(is, response.getOutputStream());
	        response.setContentType("application/pkpass");
	        response.setHeader("Content-Disposition", "attachment; filename=\"test.pkpass\"");
	        response.flushBuffer();
		} catch (Exception e) {
			try {
				response.setStatus(200);
				response.getWriter().write(e.getMessage());
				response.getWriter().flush();
				response.getWriter().close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}

	}
	
	@RequestMapping(value = "/download_ticket", method = RequestMethod.GET)
	public ResponseEntity<InputStreamResource> downloadTicket(HttpServletRequest request) throws IOException {
		int userId = userManager.validateAccessToken(request.getParameter("accessToken")).getId();
		Ticket ticket = ticketManager.getTicketById(Integer.parseInt(request.getParameter("id")));
		if (ticket.getOwnerId() != userId)
			throw new IOException();
		
		File file = new File(ticket.getLocation());
		
		HttpHeaders respHeaders = new HttpHeaders();
		respHeaders.add("Content-Type", "application/pkpass");
		respHeaders.setContentDispositionFormData("attachment", "ticket.pkpass");
		
		InputStreamResource isr = new InputStreamResource(new FileInputStream(file));
		return new ResponseEntity<InputStreamResource>(isr, respHeaders, HttpStatus.OK);
	}
	
	@RequestMapping(value = "/dtt", method = RequestMethod.GET)
	public ResponseEntity<InputStreamResource> downloadTestTicket(HttpServletRequest request) throws IOException {
		File file = new File("/Volumes/Data/testTickets/test.pkpass");
		
		HttpHeaders respHeaders = new HttpHeaders();
		respHeaders.add("Content-Type", "application/pkpass");
		respHeaders.setContentDispositionFormData("attachment", "ticket.pkpass");
		
		InputStreamResource isr = new InputStreamResource(new FileInputStream(file));
		return new ResponseEntity<InputStreamResource>(isr, respHeaders, HttpStatus.OK);
	}
	
	
	@RequestMapping("/get_ticket")
    public ResponseEntity<Map<String, Object>> getTicket(@RequestBody Map<String, Object> request) {
		Map<String, Object> respond = new HashMap<String, Object>();
		try{
			User user = userManager.validateAccessToken(request);
			int userId = user.getId();
			Ticket ticket = ticketManager.getTicketById((int)request.get("id"));
			if (ticket.getOwnerId() != userId)
				throw new IllegalStateException(ErrorMessage.TICKET_NOT_OWNED.getMsg());
			
			InputStream is = new FileInputStream(ticket.getLocation());
			
			respond.put("ticket", IOUtils.toByteArray(is));
			respond.put("error", "");
			if (user.isTokenUpdated()) {
				respond.put("accessToken", user.getAccessToken());
			}
		}catch(Exception e){
			respond = Util.createErrorRespondFromException(e);
		}
	
		return new ResponseEntity<Map<String, Object>>(respond, HttpStatus.OK);
	}
	
}
