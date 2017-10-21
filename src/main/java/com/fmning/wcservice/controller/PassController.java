package com.fmning.wcservice.controller;

import java.io.ByteArrayInputStream;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

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
public class PassController {

	@RequestMapping(value = "/create_pass", method = RequestMethod.GET)
    public ResponseEntity<Map<String, Object>> getRecentFeedsForUser(HttpServletRequest request) {
		Map<String, Object> respond = new HashMap<String, Object>();
		
		ClassLoader classLoader = getClass().getClassLoader();
		String privateKeyPath = classLoader.getResource("Certificates.p12").getFile();
		String appleWWDRCA = classLoader.getResource("AppleWWDRCA.cer").getFile();
		
		String privateKeyPassword = "flashvb6"; // the password you used to export
        try {
          
            PKSigningInformation pkSigningInformation = new PKSigningInformationUtil().loadSigningInformationFromPKCS12AndIntermediateCertificate(
                    privateKeyPath, privateKeyPassword, appleWWDRCA);
           
            PKPass pass = new PKPass();
            pass.setPassTypeIdentifier("pass.com.fmning.WPI-CSA");
            //pass.setAuthenticationToken("vxwxd7J8AlNNFPS8k0a0FfUFtq0ewzFdcdc");
            pass.setSerialNumber("123456780003");
            pass.setTeamIdentifier("NK4455562X"); // replace this with your team ID
            pass.setOrganizationName("fmning.com");
            pass.setDescription("WPI CSA Event");
            pass.setLogoText("WPI CSA");
            pass.setForegroundColor("#FFFFFF");
            pass.setBackgroundColor("#007AFF");

            PKBarcode barcode = new PKBarcode();
            barcode.setFormat(PKBarcodeFormat.PKBarcodeFormatQR);
            barcode.setMessageEncoding(Charset.forName("iso-8859-1"));
            barcode.setMessage("Some very long message oh my fuck god hahahahahahah");
            pass.setBarcodes(Collections.singletonList(barcode));

            //PKGenericPass generic = new PKGenericPass();
            PKEventTicket event = new PKEventTicket();
            PKField member = new PKField();
            member.setKey("name");
            member.setValue("Hot Pot Event");
            event.setPrimaryFields(Collections.singletonList(member));
            
            List<PKField> secondField = new ArrayList<PKField>();
            PKField loc = new PKField();
            loc.setKey("location"); // some unique key for primary field
            loc.setLabel("Location");
            loc.setValue("CC 1st floor");
            secondField.add(loc);
            event.setSecondaryFields(secondField);
            
            List<PKField> auxilField = new ArrayList<PKField>();
            PKField date = new PKField();
            date.setKey("date");
            date.setLabel("Start time");
            date.setDateStyle(PKDateStyle.PKDateStyleMedium);
            date.setTimeStyle(PKDateStyle.PKDateStyleMedium);
            date.setValue("2018-04-10T22:00:00Z");
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
                String pathToTemplateDirectory = "/Volumes/Data/StoreCard.raw"; // replace with your folder with the icons
                byte[] passZipAsByteArray = new PKFileBasedSigningUtil().createSignedAndZippedPkPassArchive(pass, pathToTemplateDirectory, pkSigningInformation);
               
                String outputFile = "/Volumes/Data/mypass.pkpass"; // change the name of the pass
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
			InputStream is = new FileInputStream("/Volumes/Data/mypass.pkpass");
	        org.apache.commons.io.IOUtils.copy(is, response.getOutputStream());
	        response.flushBuffer();
		} catch (Exception e) {
			//e.printStackTrace();
			try {
				response.setStatus(200);
				response.getWriter().write(e.getMessage());
				response.getWriter().flush();
				response.getWriter().close();
				
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}

	}
	
}
