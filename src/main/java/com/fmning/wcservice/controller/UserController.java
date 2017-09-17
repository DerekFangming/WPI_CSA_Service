package com.fmning.wcservice.controller;

import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.fmning.service.domain.User;
import com.fmning.service.domain.UserDetail;
import com.fmning.service.exceptions.NotFoundException;
import com.fmning.service.manager.HelperManager;
import com.fmning.service.manager.ImageManager;
import com.fmning.service.manager.UserManager;
import com.fmning.util.Util;
import com.fmning.wcservice.utils.Utils;

@Controller
public class UserController {
	
	@Autowired private UserManager userManager;
	@Autowired private ImageManager imageManager;
	@Autowired private HelperManager helperManager;
	
	/*
	 * Register and login
	 */
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
			
			String veriCode = helperManager.getEmailConfirmCode(username);
			userManager.updateVeriCode(username, veriCode);
			String message = Utils.createVerificationEmail(veriCode);
			helperManager.sendEmail("no-reply@fmning.com", username, "Email Confirmation", message);
			
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
				respond.put("birthday", Util.nullToEmptyString(detail.getBirthday()));
				respond.put("year", Util.nullToEmptyString(detail.getYear()));
				respond.put("major", Util.nullToEmptyString(detail.getMajor()));
			}catch(NotFoundException e){}
			
			try{
				int avatarId = imageManager.getTypeUniqueImage("Avatar", user.getId()).getId();
				respond.put("avatarId", avatarId);
			}catch(Exception e){}
			
			respond.put("error", "");
		}catch(Exception e){
			respond = Util.createErrorRespondFromException(e);
		}
		
		return new ResponseEntity<Map<String, Object>>(respond, HttpStatus.OK);
	}
	
	
	/*
	 * User details
	 */
	@RequestMapping("/get_user_detail")
    public ResponseEntity<Map<String, Object>> getUserDetail(@RequestBody Map<String, Object> request) {
		Map<String, Object> respond = new HashMap<String, Object>();
		try{
			userManager.validateAccessToken(request);
			
			UserDetail detail = userManager.getUserDetail((int)request.get("userId"));
			
			respond.put("name", Util.nullToEmptyString(detail.getName()));
			respond.put("birthday", Util.nullToEmptyString(detail.getBirthday()));
			respond.put("year", Util.nullToEmptyString(detail.getYear()));
			respond.put("major", Util.nullToEmptyString(detail.getMajor()));
			respond.put("error", "");
			
		}catch(Exception e){
			respond = Util.createErrorRespondFromException(e);
		}
		
		return new ResponseEntity<Map<String, Object>>(respond, HttpStatus.OK);
	}
	
	@RequestMapping("/save_user_detail")
    public ResponseEntity<Map<String, Object>> saveCurrentUserDetail(@RequestBody Map<String, Object> request) {
		Map<String, Object> respond = new HashMap<String, Object>();
		try{
			int userId = userManager.validateAccessToken(request);

			userManager.saveUserDetail(userId, (String)request.get("name"), null, Util.nullInt, null, null, null, 
					(String)request.get("birthday"), (String)request.get("year"), (String)request.get("major"));
			respond.put("error", "");
			
		}catch(Exception e){
			respond = Util.createErrorRespondFromException(e);
		}
		
		return new ResponseEntity<Map<String, Object>>(respond, HttpStatus.OK);
	}
	
	
	/*
	 * Changing user info
	 */
	@RequestMapping("/update_password")
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
	

}
