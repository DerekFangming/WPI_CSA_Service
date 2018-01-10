package com.fmning.wcservice.controller.rest;

import java.time.Instant;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.fmning.service.domain.User;
import com.fmning.service.exceptions.NotFoundException;
import com.fmning.service.manager.HelperManager;
import com.fmning.service.manager.UserManager;
import com.fmning.util.Util;
import com.fmning.wcservice.utils.Utils;

@Controller
public class EmailController {
	
	@Autowired private HelperManager helperManager;
	@Autowired private UserManager userManager;
	
	@RequestMapping("/send_verification_email")
    public ResponseEntity<Map<String, Object>> sendEmailConfirmation(@RequestBody Map<String, Object> request) {
		Map<String, Object> respond = new HashMap<String, Object>();
		try{
			User user = userManager.validateAccessToken(request);
			if(user.getEmailConfirmed()) {
				throw new IllegalStateException("Your email is already confirmed. Please restart the app or log out and log back in again.");
			}
			
			String username = user.getUsername();
			
			String veriCode = helperManager.getEmailConfirmCode(username);
			userManager.updateVeriCode(username, veriCode);
			String message = Utils.createVerificationEmail(veriCode);
			helperManager.sendEmail("no-reply@fmning.com", username, "Email Confirmation", message);
			
			respond.put("error", "");
		}catch(Exception e){
			respond = Util.createErrorRespondFromException(e);
		}
	
		return new ResponseEntity<Map<String, Object>>(respond, HttpStatus.OK);
		
	}
	
	
	@RequestMapping(value = "/email_verification/*", method = RequestMethod.GET)
    public String emailVerifivation(HttpServletRequest request, ModelMap model) {
		String veriCode = request.getRequestURI().split("/email_verification/")[1];
		veriCode = veriCode.replace("=", ".");
		String respond = "";
		
		try{
			Map<String, Object> result = helperManager.decodeJWT(veriCode);
			String username = (String)result.get("username");
			userManager.checkVeriCode(username, veriCode);
			Instant expTime = Instant.parse((String) result.get("expire"));
			if(expTime.compareTo(Instant.now()) > 0){
				userManager.confirmEmail(username);
				respond = "success";
			}else{
				veriCode = helperManager.getEmailConfirmCode(username);
				userManager.updateVeriCode(username, veriCode);
				String message = Utils.createVerificationEmail(veriCode);
				helperManager.sendEmail("no-reply@fmning.com", username, "Email Confirmation", message);
				respond = "resend";
			}
		}catch(IllegalStateException e){
			respond = e.getMessage();
		}catch(DateTimeParseException e){
			respond = "Expiration date format incorrect";
		}catch(NotFoundException e){
			respond = e.getMessage();
		}
		
		if (respond.equals("success")) {
			model.addAttribute("msg", "Your email address has been confirmed");
		} else if (respond.equals("resend")) {
			model.addAttribute("msg", "Your confirmation code has expired and a new confirmation email has been sent to your inbox.");
		} else {
			model.addAttribute("msg", respond + "<br>Please email <a style=\"color:white\" href=\"mailto:admin@fmning.com?Subject=" + 
						respond.replace(" ", "%20") + "\" target=\"_top\">admin@fmning.com</a> for support");
		}
		
		return "emailConfirm";
	}
	
	
	
	

}