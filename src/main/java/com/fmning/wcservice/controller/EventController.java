package com.fmning.wcservice.controller;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.fmning.service.domain.Event;
import com.fmning.service.manager.EventManager;
import com.fmning.util.Util;

@Controller
public class EventController {
	
	@Autowired private EventManager eventManager;

	@RequestMapping(value = "/get_event", method = RequestMethod.GET) //Either by actual id or by mapping id
    public ResponseEntity<Map<String, Object>> getEvent(HttpServletRequest request) {
		Map<String, Object> respond = new HashMap<String, Object>();
		try{
			Event event;
			try {
				int mappingId = Integer.parseInt(request.getParameter("mappingId"));
				event = eventManager.getEventByMappingId(mappingId);
			} catch (NumberFormatException e) {
				int id = Integer.parseInt(request.getParameter("id"));
				event = eventManager.getEventById(id);
			}
			respond.put("id", event.getId());
			respond.put("title", event.getTitle());
			respond.put("startTime",event.getStartTime().toString());
			respond.put("endTime", event.getEndTime().toString());
			respond.put("location", event.getLocation());
			respond.put("ownerId",event.getOwnerId());
			respond.put("createdAt",event.getCreatedAt().toString());
			respond.put("error", "");
		}catch(Exception e){
			respond = Util.createErrorRespondFromException(e);
		}
		return new ResponseEntity<Map<String, Object>>(respond, HttpStatus.OK);
		
	}
}
