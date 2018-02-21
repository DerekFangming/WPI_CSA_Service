package com.fmning.wcservice.controller.rest;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.fmning.service.exceptions.NotFoundException;
import com.fmning.service.manager.CommentManager;
import com.fmning.service.manager.HelperManager;
import com.fmning.service.manager.ImageManager;
import com.fmning.util.ImageType;
import com.fmning.util.Util;
import com.fmning.wcservice.utils.Utils;

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

@SuppressWarnings("unused")
@Controller
public class TestController {

	@Autowired private ImageManager imageManager;
	@Autowired private HelperManager helperManager;
	@Autowired private CommentManager cManager;
	
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
				
			}
		}

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
	
	@RequestMapping(value = "/get", method = RequestMethod.GET)
	public ResponseEntity<Map<String, Object>> getFeedPreviewImage(HttpServletRequest request) {
		Map<String, Object> respond = new HashMap<String, Object>();
		try{
			helperManager.sendEmail("no-reply@fmning.com", "fning@wpi.edu", "Some title", "message",
					"/Volumes/Data/passTemplates/preview.pkpass", "filename");
			
		}catch(Exception e){
			e.printStackTrace();
		}
		return new ResponseEntity<Map<String, Object>>(respond, HttpStatus.OK);
	}
	
	@RequestMapping(value="/build", method = RequestMethod.GET)
	public String goToBuilder(HttpServletRequest request, ModelMap model){
		model.addAttribute("message", "Hello Spring MVC Framework!");
		return "emailConfirm";
	}
	
	@RequestMapping("/test")
	public ResponseEntity<String> test(@RequestBody Map<String, Object> request) throws IOException {
	
		//int a = (int) request.get("a");//nullpointer if a is not there  class case if type not right
		//double b = (double) request.get("b");//nullponter if b is not there
		String background = (String)request.get("b");
		if(background.contains(",")){background = background.split(",")[1];}
		byte[] bgData = Base64.decodeBase64(background);
		BufferedImage bg = ImageIO.read(new ByteArrayInputStream(bgData));
		
		BufferedImage newImage = new BufferedImage(bg.getWidth(), bg.getHeight(), BufferedImage.TYPE_INT_ARGB);
		Graphics g = newImage.createGraphics();
		g.drawImage(bg, 0, 0, bg.getWidth(), bg.getHeight(), null);
		g.dispose();
		ImageIO.write(newImage, "png",
				new java.io.File("/Volumes/Data/passTemplates/gg.png"));

		
		return new ResponseEntity<String>("", HttpStatus.OK);
	}
	

	
	@RequestMapping(value = "/files", method = RequestMethod.GET)
	public void getFile(){
		try (OutputStream stream = new FileOutputStream(Util.imagePath + Integer.toString(99) + ".jpg")) {
		    
			System.out.println(Instant.now().toString());
			for (int i = 0; i < 1000; i ++) {
				cManager.saveComment("something", "something", i, 0, 1);
			}
			System.out.println(Instant.now().toString());
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@RequestMapping(value = "/haha", method = RequestMethod.GET)
	public void gethaha(){
		System.out.println(Util.imagePath);
	}

	
}
