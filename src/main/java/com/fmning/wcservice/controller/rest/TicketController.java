package com.fmning.wcservice.controller.rest;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.fmning.service.domain.Event;
import com.fmning.service.domain.Ticket;
import com.fmning.service.domain.TicketTemplate;
import com.fmning.service.domain.User;
import com.fmning.service.manager.ErrorManager;
import com.fmning.service.manager.HelperManager;
import com.fmning.service.manager.TicketManager;
import com.fmning.service.manager.UserManager;
import com.fmning.util.ErrorMessage;
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
public class TicketController {
	
	@Autowired private UserManager userManager;
	@Autowired private TicketManager ticketManager;
	@Autowired private HelperManager helperManager;
	@Autowired private ErrorManager errorManager;
	
	@RequestMapping("/preview_ticket")
    public ResponseEntity<Map<String, Object>> previewTicket(@RequestBody Map<String, Object> request) {
		Map<String, Object> respond = new HashMap<String, Object>();
		try{
			User user = userManager.validateAccessToken(request);
			
			String ticketBgImage = (String)request.get("ticketBgImage");
			String ticketThumbImage = (String)request.get("ticketThumbImage");
			
			try {
				TicketController.createTicketTemplate(ticketBgImage, ticketThumbImage, "preview");
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			TicketTemplate template = new TicketTemplate();
			template.setDescription("WPI CSA Event Preview");
			template.setLogoText("WPI CSA");
			template.setSerialNumber(1234567890);
			template.setLocation("/Volumes/Data/passTemplates/preview");


			Event event = new Event();
			event.setTitle("Event preview");
			event.setLocation("Location preview");
			event.setStartTime(Instant.parse("2018-01-01T16:00:00Z"));
			
			byte[] ticket = createTicket(event, template, "Participant preview");
			
			ByteArrayInputStream inputStream = new ByteArrayInputStream(ticket);
			IOUtils.copy(inputStream, new FileOutputStream("/Volumes/Data/passTemplates/pewview.pkpass"));
			
			try {
				helperManager.sendEmail("no-reply@fmning.com", user.getUsername(), "Ticket preview", "Here is the ticket preview you just created",
						"/Volumes/Data/passTemplates/pewview.pkpass", "pewview.pkpass");
			} catch (Exception e) {
				errorManager.logError(e);
			}
			
			respond.put("error", "");
			if (user.isTokenUpdated()) {
				respond.put("accessToken", user.getAccessToken());
			}
		}catch(Exception e){
			respond = errorManager.createErrorRespondFromException(e, Utils.rootDir + "/get_ticket", request);
		}
	
		return new ResponseEntity<Map<String, Object>>(respond, HttpStatus.OK);
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
			respond = errorManager.createErrorRespondFromException(e, Utils.rootDir + "/get_ticket", request);
		}
	
		return new ResponseEntity<Map<String, Object>>(respond, HttpStatus.OK);
	}
	
	public static void createTicketTemplate(String background, String thumbnail, String folderName) throws IOException {
		File srcDir = new File("/Volumes/Data/passTemplates/base");
		File destDir = new File("/Volumes/Data/passTemplates/" + folderName);

		//Creating base folder
		FileUtils.copyDirectory(srcDir, destDir);
		
		//Saving background and thumbnails
		if(background.contains(",")){background = background.split(",")[1];}
		byte[] bgData = Base64.decodeBase64(background);
		BufferedImage bg = ImageIO.read(new ByteArrayInputStream(bgData));
		int bgWidth = bg.getWidth();
		int bgHeight = bg.getHeight();
		for (int i = 1; i < 4; i ++) {
			int newWidth = bgWidth > bgHeight ? bgWidth * 220 / bgHeight * i : 180 * i;
			int newHeight = bgWidth > bgHeight ? 220 * i : bgHeight * 180 / bgWidth * i;
			BufferedImage newImage = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_ARGB);
			Graphics g = newImage.createGraphics();
			g.drawImage(bg, 0, 0, newWidth, newHeight, null);
			g.dispose();
			ImageIO.write(newImage, "png",
					new File("/Volumes/Data/passTemplates/" + folderName + "/background@" + Integer.toString(i) + "x.png"));
		}
		
		if(thumbnail.contains(",")){thumbnail = thumbnail.split(",")[1];}
		byte[] thData = Base64.decodeBase64(thumbnail);
		BufferedImage th = ImageIO.read(new ByteArrayInputStream(thData));
		int thWidth = th.getWidth();
		int thHeight = th.getHeight();
		for (int i = 1; i < 4; i ++) {
			int newWidth = thWidth > thHeight ? thWidth * 90 / thHeight * i : 90 * i;
			int newHeight = thWidth > thHeight ? 90 * i : thHeight * 90 / thWidth * i;
			BufferedImage newImage = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_ARGB);
			Graphics g = newImage.createGraphics();
			g.drawImage(th, 0, 0, newWidth, newHeight, null);
			g.dispose();
			ImageIO.write(newImage, "png",
					new File("/Volumes/Data/passTemplates/" + folderName + "/thumbnail@" + Integer.toString(i) + "x.png"));
		}
	}
	
	public static byte[] createTicket(Event event, TicketTemplate template, String participaintName) throws UnrecoverableKeyException,
	NoSuchAlgorithmException, CertificateException, KeyStoreException, IOException, PKSigningException{
		
		PKSigningInformation pkSigningInformation = new PKSigningInformationUtil().loadSigningInformationFromPKCS12AndIntermediateCertificate(
		       Utils.privateKeyPath, Utils.privateKeyPassword, Utils.appleWWDRCA);
		
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
		participant.setValue(participaintName);
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
