package com.fmning.wcservice.controller.rest;

import java.time.Instant;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.fmning.service.domain.User;
import com.fmning.service.exceptions.NotFoundException;
import com.fmning.service.manager.ErrorManager;
import com.fmning.service.manager.HelperManager;
import com.fmning.service.manager.UserManager;
import com.fmning.util.ErrorMessage;
import com.fmning.wcservice.utils.UserRole;
import com.fmning.wcservice.utils.Utils;

@Controller
public class EmailController {
	
	@Autowired private HelperManager helperManager;
	@Autowired private UserManager userManager;
	@Autowired private ErrorManager errorManager;
	
	@CrossOrigin(origins = "*", maxAge = 3600)
	@RequestMapping(value = "/send_email", method = RequestMethod.POST)
    public ResponseEntity<Map<String, Object>> sendEmail(@RequestBody Map<String, Object> request) {
		Map<String, Object> respond = new HashMap<String, Object>();
		try{
			String title = (String)request.get("title");
			String content = (String)request.get("content");
			String recipient = (String)request.get("recipient");
			
			if (title == null && content == null && recipient == null) 
				throw new IllegalStateException("Nothing to send");
			
			if (recipient == null)
				recipient = "fning@wpi.edu";
			if (title == null)
				title = "New message from fmning.com";
			
			if (Utils.prodMode) {
				helperManager.sendEmail("no-reply@fmning.com", recipient, title, content);
			} else {
				System.out.println(recipient + "\n\n" + title + "\n\n" + content);
			}
			
			respond.put("error", "");
		}catch(Exception e){
			respond = errorManager.createErrorRespondFromException(e, Utils.rootDir + "/send_email", request);
		}
		return new ResponseEntity<Map<String, Object>>(respond, HttpStatus.OK);
	}
	
	@RequestMapping("/send_verification_email")
    public ResponseEntity<Map<String, Object>> sendEmailConfirmation(@RequestBody Map<String, Object> request) {
		Map<String, Object> respond = new HashMap<String, Object>();
		try{
			User user = userManager.validateAccessToken(request);
			String username = user.getUsername();
			
			try{
				int requestedUserId = (int)request.get("requestedUserId");
				if (!UserRole.isAdmin(user.getRoleId())) {
					throw new IllegalStateException(ErrorMessage.NO_PERMISSION.getMsg());
				} else if (!user.getEmailConfirmed()) {
					throw new IllegalStateException(ErrorMessage.EMAIL_NOT_CONFIRMED.getMsg());
				} else {
					try {
						User requestedUser = userManager.getUserById(requestedUserId);
						if(requestedUser.getEmailConfirmed()) {
							throw new IllegalStateException(ErrorMessage.REQUESTED_USER_EMAIL_VERIFIED.getMsg());
						} else {
							username = requestedUser.getUsername();
						}
					} catch (NotFoundException e) {
						throw new IllegalStateException(ErrorMessage.USER_NOT_FOUND.getMsg());
					}
				}
			}catch(NullPointerException e){
				if(user.getEmailConfirmed()) {
					throw new IllegalStateException(ErrorMessage.EMAIL_ALREADY_VERIFIED.getMsg());
				}
			}
			
			
			String veriCode = helperManager.getEmailConfirmCode(username);
			userManager.updateVeriCode(username, veriCode);
			user = userManager.getUserByUsername(username);
			String name = userManager.getUserDisplayedName(user.getId());
			String message = Utils.createVerificationEmail(name, veriCode);
			if (Utils.prodMode) {
				try {
					helperManager.sendEmail("no-reply@fmning.com", username, "Email Confirmation", message);
				} catch (Exception e) {
					errorManager.logError(e);
				}
			} else {
				System.out.println(message);
			}
			
			respond.put("error", "");
			if (user.isTokenUpdated()) {
				respond.put("accessToken", user.getAccessToken());
			}
		}catch(Exception e){
			respond = errorManager.createErrorRespondFromException(e, Utils.rootDir + "/send_verification_email", request);
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
			userManager.checkVeriCode(username, veriCode, (String)result.get("action"));
			Instant expTime = Instant.parse((String) result.get("expire"));
			if(expTime.compareTo(Instant.now()) > 0){
				userManager.confirmEmail(username);
				respond = "success";
			}else{
				veriCode = helperManager.getEmailConfirmCode(username);
				userManager.updateVeriCode(username, veriCode);
				User user = userManager.getUserByUsername(username);
				String name = userManager.getUserDisplayedName(user.getId());
				String message = Utils.createVerificationEmail(name, veriCode);
				if (Utils.prodMode) {
					try {
						helperManager.sendEmail("no-reply@fmning.com", username, "Email Confirmation", message);
					} catch (Exception e) {
						errorManager.logError(e);
					}
				} else {
					System.out.println(message);
				}
				respond = "resend";
			}
		}catch(IllegalStateException e){
			errorManager.logError(e, request);
			respond = e.getMessage();
		}catch(DateTimeParseException e){
			errorManager.logError(e, request);
			respond = "Expiration date format incorrect";
		}catch(NotFoundException e){
			errorManager.logError(e, request);
			respond = e.getMessage();
		}catch(Exception e) {
			errorManager.logError(e, request);
		}
		
		if (respond.equals("success")) {
			model.addAttribute("msg", "Your email address has been confirmed");
		} else if (respond.equals("resend")) {
			model.addAttribute("msg", "Your confirmation code has expired and a new confirmation email has been sent to your inbox.");
		} else {
			model.addAttribute("msg", respond + "<br>Please email <a href=\"mailto:admin@fmning.com?Subject=" + 
						respond.replace(" ", "%20") + "\" target=\"_top\">admin@fmning.com</a> for support");
		}
		
		return "emailConfirm";
	}
	
	@RequestMapping("/send_change_pwd_email")
    public ResponseEntity<Map<String, Object>> sendEmailChangePwd(@RequestBody Map<String, Object> request) {
		Map<String, Object> respond = new HashMap<String, Object>();
		try{
			String username = (String)request.get("email");
			if (username == null)
				throw new NotFoundException(ErrorMessage.USER_NOT_FOUND.getMsg());
			
			User user = userManager.getUserByUsername(username);
			
			if (!user.getEmailConfirmed()) {
				String veriCode = helperManager.getEmailConfirmCode(username);
				userManager.updateVeriCode(username, veriCode);
				String name = userManager.getUserDisplayedName(user.getId());
				String message = Utils.createVerificationEmail(name, veriCode);
				if (Utils.prodMode) {
					try {
						helperManager.sendEmail("no-reply@fmning.com", username, "Password reset", message);
					} catch (Exception e) {
						errorManager.logError(e);
					}
				} else {
					System.out.println(message);
				}
				throw new NotFoundException(ErrorMessage.CHANGE_PWD_BUT_NOT_VERIFIED.getMsg());
			}
			
			String veriCode = helperManager.getChangePasswordCode(username);
			userManager.updateVeriCode(username, veriCode);
			String name = userManager.getUserDisplayedName(user.getId());
			String message = Utils.createChangePwdEmail(name, veriCode);
			if (Utils.prodMode) {
				try {
					helperManager.sendEmail("no-reply@fmning.com", username, "Password reset", message);
				} catch (Exception e) {
					errorManager.logError(e);
				}
			} else {
				System.out.println(message);
			}
			
			respond.put("error", "");
		}catch(Exception e){
			respond = errorManager.createErrorRespondFromException(e, Utils.rootDir + "/send_change_pwd_email", request);
		}
	
		return new ResponseEntity<Map<String, Object>>(respond, HttpStatus.OK);
		
	}
	
	@RequestMapping(value = "/reset_password/*", method = RequestMethod.GET)
    public String resetPassword(HttpServletRequest request, HttpServletResponse response, ModelMap model) {
		String veriCode = request.getRequestURI().split("/reset_password/")[1];
		veriCode = veriCode.replace("=", ".");
		String respond = "";
		String username= "";
		
		try{
			Map<String, Object> result = helperManager.decodeJWT(veriCode);
			username = (String)result.get("username");
			userManager.checkVeriCode(username, veriCode, (String)result.get("action"));
			Instant expTime = Instant.parse((String) result.get("expire"));
			if(expTime.compareTo(Instant.now()) > 0){
				//userManager.updateVeriCode(username, ""); do another round of validation when user update password
				respond = "success";
			}else{
				veriCode = helperManager.getChangePasswordCode(username);
				userManager.updateVeriCode(username, veriCode);
				User user = userManager.getUserByUsername(username);
				String name = userManager.getUserDisplayedName(user.getId());
				String message = Utils.createChangePwdEmail(name, veriCode);
				if (Utils.prodMode) {
					try {
						helperManager.sendEmail("no-reply@fmning.com", username, "Password reset", message);
					} catch (Exception e) {
						errorManager.logError(e);
					}
				} else {
					System.out.println(message);
				}
				respond = "resend";
			}
		}catch(IllegalStateException e){
			errorManager.logError(e, request);
			respond = "The token is not in correct format. Please copy and paste the url in browser and try again.";
		}catch(DateTimeParseException e){
			errorManager.logError(e, request);
			respond = "Expiration date format incorrect";
		}catch(NotFoundException e){
			errorManager.logError(e, request);
			respond = e.getMessage();
		}catch(Exception e) {
			errorManager.logError(e, request);
		}
		
		if (respond.equals("success")) {
			User user = userManager.getUserByUsername(username);
			model.addAttribute("veriToken", veriCode);
			model.addAttribute("changePwd", true);
			
			Cookie cookie = new Cookie("accessToken", user.getAccessToken());
			cookie.setMaxAge(63113904);
			cookie.setPath("/");
			response.addCookie(cookie);
		} else if (respond.equals("resend")) {
			model.addAttribute("changePwd", false);
			model.addAttribute("msg", "Your reset password code has expired and a new password reset email has been sent to your inbox.");
		} else {
			model.addAttribute("changePwd", false);
			model.addAttribute("msg", respond + "<br>Please email <a href=\"mailto:admin@fmning.com?Subject=" + 
						respond.replace(" ", "%20") + "\" target=\"_top\">admin@fmning.com</a> for support");
		}
		
		return "resetPassword";
	}
	

}