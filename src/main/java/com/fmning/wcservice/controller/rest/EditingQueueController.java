package com.fmning.wcservice.controller.rest;

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

import com.fmning.service.domain.EditingQueue;
import com.fmning.service.domain.User;
import com.fmning.service.manager.EditingQueueManager;
import com.fmning.service.manager.ErrorManager;
import com.fmning.service.manager.UserManager;
import com.fmning.util.EditingQueueType;
import com.fmning.wcservice.utils.Utils;

@Controller
public class EditingQueueController {
	
	@Autowired private UserManager userManager;
	@Autowired private EditingQueueManager eqManager;
	@Autowired private ErrorManager errorManager;
	
	@RequestMapping("/check_editing_queue")
	public ResponseEntity<Map<String, Object>> checkEditingQueue(@RequestBody Map<String, Object> request) {
		Map<String, Object> respond = new HashMap<String, Object>();
		try{
			User user = userManager.validateAccessToken(request);
			String type = (String)request.get("type");
			int mappingId = (int)request.get("mappingId");
			EditingQueueType eqt = type.equals(EditingQueueType.SG.getName()) ? EditingQueueType.SG : EditingQueueType.FEED;
			EditingQueue eq = eqManager.updateQueue(eqt, mappingId, user.getId());
			
			if (eq != null && eq.getOwnerId() != user.getId() && eq.getCreatedAt().isAfter(Instant.now().minus(Duration.ofMinutes(1)))) {
				User confUser = userManager.getUserById(eq.getOwnerId());
				respond.put("conflict", true);
				respond.put("userId", confUser.getId());
				respond.put("username", confUser.getUsername());
				respond.put("name", userManager.getUserDisplayedName(eq.getOwnerId()));
			} else {
				respond.put("conflict", false);
			}
			
			respond.put("error", "");
			if (user.isTokenUpdated()) {
				respond.put("accessToken", user.getAccessToken());
			}
		}catch(Exception e){
			respond = errorManager.createErrorRespondFromException(e, Utils.rootDir + "/check_editing_queue", request);
		}
		return new ResponseEntity<Map<String, Object>>(respond, HttpStatus.OK);
	}

}
