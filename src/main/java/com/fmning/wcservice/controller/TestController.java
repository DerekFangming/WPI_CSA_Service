package com.fmning.wcservice.controller;

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
		return new ResponseEntity<Map<String, Object>>(respond, HttpStatus.OK);
	}
	
	@RequestMapping(value="/build", method = RequestMethod.GET)
	public String goToBuilder(HttpServletRequest request, ModelMap model){
		//Pair<Cookie, Cookie> cookies = cookieManager.extractPartnerCookies(request);
		//model.addAttribute("username",cookies.getFirst().getValue());
		String a = (String)request.getParameter("haha");
		model.addAttribute("message", "Hello Spring MVC Framework!");
		model.addAttribute("color", a);
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
