package com.fmning.wcservice.controller.rest;

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
import com.fmning.util.ErrorMessage;
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
	
	
	//This will be deprecated after migration
	@RequestMapping("/login_migration")
    public ResponseEntity<Map<String, Object>> login1(@RequestBody Map<String, Object> request) {
		Map<String, Object> respond = new HashMap<String, Object>();
		try{
			String username = (String)request.get("username");
			String password = (String)request.get("password");

			
			User user = userManager.loginMigrate(username, password);
			
			respond.put("accessToken", user.getAccessToken());
			
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
			User user = userManager.webRegister(username, password);
			
			String message = Utils.createVerificationEmail(user.getVeriToken());
			System.out.println(message);
			helperManager.sendEmail("no-reply@fmning.com", username, "Email Confirmation", message);
			
			userManager.saveUserDetail(user.getId(), (String)request.get("name"), null, Util.nullInt, null, null, null, 
					(String)request.get("birthday"), (String)request.get("year"), (String)request.get("major"));
			
			String base64 = (String)request.get("avatar");
			if(base64 != null){
				int imgId = imageManager.saveTypeUniqueImage(base64, "Avatar", Util.nullInt, user.getId(), null);
				respond.put("imageId", imgId);
			}
			
			
			respond.put("accessToken", user.getAccessToken());
			respond.put("emailConfirmed",false);
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
			User user;
			if (request.containsKey("accessToken")) {
				user = userManager.validateAccessToken((String)request.get("accessToken"));
			} else if (request.containsKey("username") && request.containsKey("password")) {
				user = userManager.webLogin((String)request.get("username"), (String)request.get("password"));
			} else {
				throw new NullPointerException();
			}

			respond.put("username", user.getUsername());
			respond.put("accessToken", user.getAccessToken());
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
			User user = userManager.validateAccessToken(request);
			
			UserDetail detail = userManager.getUserDetail((int)request.get("userId"));
			
			respond.put("name", Util.nullToEmptyString(detail.getName()));
			respond.put("birthday", Util.nullToEmptyString(detail.getBirthday()));
			respond.put("year", Util.nullToEmptyString(detail.getYear()));
			respond.put("major", Util.nullToEmptyString(detail.getMajor()));
			respond.put("error", "");
			if (user.isTokenUpdated()) {
				respond.put("accessToken", user.getAccessToken());
			}
		}catch(Exception e){
			respond = Util.createErrorRespondFromException(e);
		}
		
		return new ResponseEntity<Map<String, Object>>(respond, HttpStatus.OK);
	}
	
	@RequestMapping("/save_user_detail")
    public ResponseEntity<Map<String, Object>> saveCurrentUserDetail(@RequestBody Map<String, Object> request) {
		Map<String, Object> respond = new HashMap<String, Object>();
		try{
			User user = userManager.validateAccessToken(request);
			int userId = user.getId();

			userManager.saveUserDetail(userId, (String)request.get("name"), null, Util.nullInt, null, null, null, 
					(String)request.get("birthday"), (String)request.get("year"), (String)request.get("major"));
			
			String base64 = (String)request.get("avatar");
			if(base64 != null){
				int imgId = imageManager.saveTypeUniqueImage(base64, "Avatar", Util.nullInt, userId, null);
				respond.put("imageId", imgId);
			}
			if (user.isTokenUpdated()) {
				respond.put("accessToken", user.getAccessToken());
			}
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
			User user = userManager.validateAccessToken(request);
			String oldPwd = (String)request.get("oldPwd");
			String newPwd = (String)request.get("newPwd");
			
			if (!Util.MD5(oldPwd + user.getSalt()).equals(user.getPassword()))
				throw new IllegalStateException(ErrorMessage.INCORRECT_PASSWORD.getMsg());
			
			userManager.changePassword(user.getId(), newPwd);
			if (user.isTokenUpdated()) {
				respond.put("accessToken", user.getAccessToken());
			}
			respond.put("error", "");
		}catch(Exception e){
			respond = Util.createErrorRespondFromException(e);
		}
		
		return new ResponseEntity<Map<String, Object>>(respond, HttpStatus.OK);
	}
	

}
