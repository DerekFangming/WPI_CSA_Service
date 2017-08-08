package com.fmning.wcservice.controller;

import java.sql.Timestamp;
import java.time.Duration;
import java.time.Instant;
import java.time.format.DateTimeParseException;
import java.util.Date;
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

import com.fmning.service.dao.UserDao;
import com.fmning.service.domain.User;
import com.fmning.service.domain.UserDetail;
import com.fmning.service.exceptions.NotFoundException;
import com.fmning.service.manager.HelperManager;
import com.fmning.service.manager.UserManager;
import com.fmning.util.Util;

@Controller
public class AuthController {
	
	@Autowired private HelperManager helperManager;
	@Autowired private UserManager userManager;
	
	@Autowired UserDao u;
	
	@RequestMapping("/register_for_salt")
    public ResponseEntity<Map<String, Object>> registerForSalt(@RequestBody Map<String, Object> request) {
		String salt = "";
		Map<String, Object> respond = new HashMap<String, Object>();
		try{
			salt = userManager.registerForSalt((String)request.get("username"), 
					(int)request.get("offset"));
			respond.put("salt", salt);
			respond.put("error", "");
		}catch(Exception e){
			respond = Util.createErrorRespondFromException(e);
		}
	
		return new ResponseEntity<Map<String, Object>>(respond, HttpStatus.OK);
		
	}
	
	@RequestMapping("/register")
    public ResponseEntity<Map<String, Object>> register(@RequestBody Map<String, Object> request) {
		Map<String, Object> respond = new HashMap<String, Object>();
		try{
			String username = (String)request.get("username");
			String password = (String)request.get("password");
			userManager.register(username, password);
			
			String code = helperManager.getEmailConfirmCode(username);
			userManager.updateVeriCode(username, code);
			helperManager.emailConfirm(username, code.replace(".", "="));
			
			Instant exp = Instant.now().plus(Duration.ofDays(1));
			//Convert to ISO8601 formatted string such as 2013-06-25T16:22:52.966Z
			String accessToken = helperManager.createAccessToken(username, exp);
			userManager.updateAccessToken(username, accessToken);
			
			respond.put("userId", userManager.getUserId(username));//TODO really need this id?
			respond.put("username", username);
			respond.put("accessToken", accessToken);
			respond.put("expire", exp.toString());
			respond.put("emailConfirmed",false);
			respond.put("error", "");
		}catch(Exception e){
			respond = Util.createErrorRespondFromException(e);
		}
	
		return new ResponseEntity<Map<String, Object>>(respond, HttpStatus.OK);
		
	}
	
	@RequestMapping("/send_email_confirmation")
    public ResponseEntity<Map<String, Object>> sendEmailConfirmation(@RequestBody Map<String, Object> request) {
		Map<String, Object> respond = new HashMap<String, Object>();
		try{
			int userId = userManager.validateAccessToken(request);
			String username = userManager.getUsername(userId);
			
			String code = helperManager.getEmailConfirmCode(username);
			userManager.updateVeriCode(username, code);
			helperManager.emailConfirm(username, code.replace(".", "="));
			
			respond.put("error", "");
		}catch(Exception e){
			respond = Util.createErrorRespondFromException(e);
		}
	
		return new ResponseEntity<Map<String, Object>>(respond, HttpStatus.OK);
		
	}
	
	
	@RequestMapping(value = "/email/*", method = RequestMethod.GET)
    public String emailVerifivation(HttpServletRequest request, ModelMap model) {
		String code = request.getRequestURI().split("/email/")[1];
		code = code.replace("=", ".");
		String respond = "";
		
		try{
			Map<String, Object> result = helperManager.decodeJWT(code);
			String username = (String)result.get("username");
			userManager.checkVeriCode(username, code);
			Instant expTime = Instant.parse((String) result.get("expire"));
			if(expTime.compareTo(Instant.now()) > 0){
				userManager.confirmEmail(username);
				respond = "success";
			}else{
				code = helperManager.getEmailConfirmCode(username);
				userManager.updateVeriCode(username, code);
				helperManager.emailConfirm(username, code.replace(".", "="));
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
			model.addAttribute("msgFont", "text-danger");
			model.addAttribute("msg", "Your confirmation code has expired");
			model.addAttribute("extraMsg", "<div class=\"intro-lead-in\">A new confirmation email has been sent to your inbox</div>");
		} else {
			model.addAttribute("msgFont", "text-danger");
			model.addAttribute("msg", respond);
			model.addAttribute("extraMsg", "<div class=\"intro-lead-in\">Please email <a href=\"mailto:admin@fmning.com?Subject="
					+ respond.replace(" ", "%20") + "\" target=\"_top\">admin@fmning.com</a> for support</div>");
		}
		
		return "emailConfirm";
	}
	
	@RequestMapping("/login_for_salt")
    public ResponseEntity<Map<String, Object>> loginForSalt(@RequestBody Map<String, Object> request) {
		Map<String, Object> respond = new HashMap<String, Object>();
		try{
			String username = (String)request.get("username");
			respond.put("salt", userManager.loginForSalt(username));
			respond.put("error", "");
		}catch(Exception e){
			respond = Util.createErrorRespondFromException(e);
		}
		return new ResponseEntity<Map<String, Object>>(respond, HttpStatus.OK);
		
	}
	
	@RequestMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody Map<String, Object> request) {
		Map<String, Object> respond = new HashMap<String, Object>();
		try{
			String username = (String)request.get("username");
			String password = (String)request.get("password");
			
			Instant exp = Instant.now().plus(Duration.ofDays(1));
			String accessToken = helperManager.createAccessToken(username, exp);
			
			User user = userManager.login(username, password, accessToken);
			
			respond.put("userId", user.getId());//TODO really need this id?
			respond.put("username", username);
			respond.put("accessToken", accessToken);
			respond.put("expire", exp.toString());
			respond.put("emailConfirmed",user.getEmailConfirmed());
			try{
				UserDetail detail = userManager.getUserDetail(user.getId());
				
				respond.put("name", Util.nullToEmptyString(detail.getName()));
				respond.put("nickname", Util.nullToEmptyString(detail.getNickname()));
				respond.put("age", detail.getAge());
				respond.put("gender", Util.nullToEmptyString(detail.getGender()));
				respond.put("location", Util.nullToEmptyString(detail.getLocation()));
				respond.put("whatsUp", Util.nullToEmptyString(detail.getWhatsUp()));
			}catch(NotFoundException e){}
			respond.put("error", "");
		}catch(Exception e){
			respond = Util.createErrorRespondFromException(e);
		}
		return new ResponseEntity<Map<String, Object>>(respond, HttpStatus.OK);
		
	}
	
	@RequestMapping("/change_password")
    public ResponseEntity<Map<String, Object>> changePassword(@RequestBody Map<String, Object> request) {
		Map<String, Object> respond = new HashMap<String, Object>();
		try{
			int userId = userManager.validateAccessToken(request);
			String oldPwd = (String)request.get("oldPwd");
			String newPwd = (String)request.get("newPwd");
			String username = userManager.getUsername(userId);
			Instant exp = Instant.now().plus(Duration.ofDays(1));
			String accessToken = helperManager.createAccessToken(username, exp);
			
			userManager.changePassword(username, oldPwd, newPwd, accessToken);
			respond.put("accessToken", accessToken);
			respond.put("error", "");
		}catch(Exception e){
			respond = Util.createErrorRespondFromException(e);
		}
		return new ResponseEntity<Map<String, Object>>(respond, HttpStatus.OK);
		
	}
	
	
	@RequestMapping("/auth/*")
    public ResponseEntity<String> home(HttpServletRequest request) {
		return new ResponseEntity<String>(helperManager.getEmailConfirmedPage("Invalid code"), HttpStatus.OK);
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