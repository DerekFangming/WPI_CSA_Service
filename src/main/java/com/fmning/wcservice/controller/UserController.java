package com.fmning.wcservice.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.fmning.service.domain.UserDetail;
import com.fmning.service.manager.UserManager;
import com.fmning.util.Util;

@Controller
public class UserController {
	
	@Autowired private UserManager userManager;
	
	@RequestMapping("/get_user_detail")
    public ResponseEntity<Map<String, Object>> getUserDetail(@RequestBody Map<String, Object> request) {
		Map<String, Object> respond = new HashMap<String, Object>();
		try{
			userManager.validateAccessToken(request);
			
			UserDetail detail = userManager.getUserDetail((int)request.get("userId"));
			
			respond.put("name", Util.nullToEmptyString(detail.getName()));
			respond.put("nickname", Util.nullToEmptyString(detail.getNickname()));
			respond.put("age", detail.getAge());
			respond.put("gender", Util.nullToEmptyString(detail.getGender()));
			respond.put("location", Util.nullToEmptyString(detail.getLocation()));
			respond.put("whatsUp", Util.nullToEmptyString(detail.getWhatsUp()));
			respond.put("error", "");
			
		}catch(Exception e){
			respond = Util.createErrorRespondFromException(e);
		}
		return new ResponseEntity<Map<String, Object>>(respond, HttpStatus.OK);
		
	}
	
	@RequestMapping("/save_current_user_detail")
    public ResponseEntity<Map<String, Object>> saveCurrentUserDetail(@RequestBody Map<String, Object> request) {
		Map<String, Object> respond = new HashMap<String, Object>();
		try{
			int userId = userManager.validateAccessToken(request);

			int age = request.get("age") == null ? Util.nullInt : (int)request.get("age");
			userManager.saveUserDetail(userId, (String)request.get("name"), (String)request.get("nickname"), age,
					(String)request.get("gender"), (String)request.get("location"), (String)request.get("whatsUp"));
			respond.put("error", "");
			
		}catch(Exception e){
			respond = Util.createErrorRespondFromException(e);
		}
		return new ResponseEntity<Map<String, Object>>(respond, HttpStatus.OK);
		
	}

}
