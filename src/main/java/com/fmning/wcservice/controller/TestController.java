package com.fmning.wcservice.controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.ProcessBuilder.Redirect;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.fmning.util.Util;
import com.fmning.wcservice.utils.Utils;

@Controller
public class TestController {
	
	@RequestMapping("/auth/*")
    public ResponseEntity<String> home(HttpServletRequest request) {
		return new ResponseEntity<String>("test string", HttpStatus.OK);
    }
	
	@RequestMapping(value = "/get", method = RequestMethod.GET)
	public ResponseEntity<Map<String, Object>> getFeedPreviewImage(HttpServletRequest request) {
		Map<String, Object> respond = new HashMap<String, Object>();
		try{
			String a = (String)request.getParameter("haha");
			respond.put("haha", a);
		}catch(Exception e){
			respond = Util.createErrorRespondFromException(e);
		}

		respond.put("exp", "");
		respond.put("e", "");

	    Process process = null;
		try {

			ClassLoader classLoader = getClass().getClassLoader();
			File file = new File(classLoader.getResource("dbBackup.sh").getFile());
			System.out.println(file.getAbsolutePath());
			respond.put("path", file.getAbsolutePath());
			
			process = Runtime.getRuntime().exec(file.getAbsolutePath() + " " + Utils.dbBackupFolder);
			
			BufferedReader in = new BufferedReader(new InputStreamReader(
			        process.getErrorStream()));             
			String line;
			while ((line = in.readLine()) != null) {
				respond.put("exp", respond.get("exp") + line + "\n");
			    System.out.println("-----" + line);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			respond.put("e", respond.get("e") + e.getStackTrace().toString() + "\n");
		}

	    int exitValue = 0;
		try {
			exitValue = process.waitFor();
			BufferedReader in = new BufferedReader(new InputStreamReader(
			        process.getErrorStream()));             
			String line;
			while ((line = in.readLine()) != null) {
			    System.out.println("+++++" + line);
			}
			
			
		} catch (InterruptedException | NullPointerException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    if (exitValue != 0) {
	        // check for errors
	        //new BufferedInputStream(process.getErrorStream());
	        throw new RuntimeException("execution of script failed!");
	    }
		
		return new ResponseEntity<Map<String, Object>>(respond, HttpStatus.OK);
	}
	
	@RequestMapping(value="/build", method = RequestMethod.GET)
	public String goToBuilder(HttpServletRequest request, ModelMap model){
		model.addAttribute("message", "Hello Spring MVC Framework!");
		return "emailConfirm";
	}
	
	@RequestMapping("/test")
	public ResponseEntity<String> test(@RequestBody Map<String, Object> request) {

		
		Instant a = Instant.now();
		System.out.println(a.toString());
		System.out.println(Date.from(a));
		System.out.println(Timestamp.from(a));
		
		
		
		
		return new ResponseEntity<String>("", HttpStatus.OK);
	}

}
