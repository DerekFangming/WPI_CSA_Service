package com.fmning.wcservice.controller.rest;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.fmning.service.domain.Event;
import com.fmning.service.domain.Payment;
import com.fmning.service.domain.User;
import com.fmning.service.manager.ErrorManager;
import com.fmning.service.manager.EventManager;
import com.fmning.service.manager.PaymentManager;
import com.fmning.service.manager.UserManager;
import com.fmning.util.ErrorMessage;
import com.fmning.util.EventType;
import com.fmning.util.PaymentType;
import com.fmning.util.Util;
import com.fmning.wcservice.model.PartiListModel;
import com.fmning.wcservice.utils.UserRole;
import com.fmning.wcservice.utils.Utils;

@Controller
public class EventController {
	
	@Autowired private UserManager userManager;
	@Autowired private EventManager eventManager;
	@Autowired private PaymentManager paymentManager;
	@Autowired private ErrorManager errorManager;

	@RequestMapping(value = "/get_event", method = RequestMethod.GET) //Either by actual id or by mapping id
    public ResponseEntity<Map<String, Object>> getEvent(HttpServletRequest request) {
		Map<String, Object> respond = new HashMap<String, Object>();
		try{
			Event event;
			try {
				int mappingId = Integer.parseInt(request.getParameter("mappingId"));
				event = eventManager.getEventByType(EventType.FEED.toString(), mappingId);
			} catch (NumberFormatException e) {
				int id = Integer.parseInt(request.getParameter("id"));
				event = eventManager.getEventById(id);
			}
			respond.put("id", event.getId());
			respond.put("title", event.getTitle());
			respond.put("description", event.getDescription());
			respond.put("startTime",event.getStartTime().toString());
			respond.put("endTime", event.getEndTime().toString());
			respond.put("location", event.getLocation());
			respond.put("ownerId",event.getOwnerId());
			respond.put("createdAt",event.getCreatedAt().toString());
			
			if(event.getFee() != Util.nullInt) {
				respond.put("fee", event.getFee());
			}
			respond.put("error", "");
		}catch(Exception e){
			respond = errorManager.createErrorRespondFromException(e, request);
		}
		return new ResponseEntity<Map<String, Object>>(respond, HttpStatus.OK);
	}
	
	
	@RequestMapping("/update_event_status")
	public ResponseEntity<Map<String, Object>> updateEventStatus(@RequestBody Map<String, Object> request) {
		Map<String, Object> respond = new HashMap<String, Object>();
		try{
			User user = userManager.validateAccessToken(request);
			
			int id = (int)request.get("id");
			boolean newStatus = (boolean)request.get("status");
			Event event = eventManager.getEventById(id);
			
			if (event.getOwnerId() != user.getId() &&  !UserRole.isAdmin(user.getRoleId())) {
				throw new IllegalStateException(ErrorMessage.CHANGE_STATUS_NOT_ALLOWED.getMsg());
			}
			
			//message will get ignored if new status is enable
			eventManager.setStatus(id, newStatus, ErrorMessage.TICKET_SOLD_OUT.getMsg(), user.getId());
			
			if (user.isTokenUpdated()) {
				respond.put("accessToken", user.getAccessToken());
			}
			respond.put("error", "");
			
		}catch(Exception e){
			respond = errorManager.createErrorRespondFromException(e, Utils.rootDir + "/update_event_status", request);
		}
		return new ResponseEntity<Map<String, Object>>(respond, HttpStatus.OK);
	}
	
	@RequestMapping("/update_event_balance")
	public ResponseEntity<Map<String, Object>> updateEventBalance(@RequestBody Map<String, Object> request) {
		Map<String, Object> respond = new HashMap<String, Object>();
		try{
			User user = userManager.validateAccessToken(request);
			
			int id = (int)request.get("id");
			int balance = (int)request.get("balance");
			
			if (balance < 0 || balance > 500){ 
				throw new IllegalStateException(ErrorMessage.TICKET_BALANCE_RANGE_ERROR.getMsg());
			}
			
			Event event = eventManager.getEventById(id);
			
			if (event.getOwnerId() != user.getId() &&  !UserRole.isAdmin(user.getRoleId())) {
				throw new IllegalStateException(ErrorMessage.CHANGE_BALANCE_NOT_ALLOWED.getMsg());
			}
			
			eventManager.setBalance(id, balance, user.getId());
			
			if (user.isTokenUpdated()) {
				respond.put("accessToken", user.getAccessToken());
			}
			respond.put("error", "");
			
		}catch(Exception e){
			respond = errorManager.createErrorRespondFromException(e, Utils.rootDir + "/update_event_balance", request);
		}
		return new ResponseEntity<Map<String, Object>>(respond, HttpStatus.OK);
	}
	
	@RequestMapping("/update_event_details")
	public ResponseEntity<Map<String, Object>> updateEventDetails(@RequestBody Map<String, Object> request) {
		Map<String, Object> respond = new HashMap<String, Object>();
		try{
			User user = userManager.validateAccessToken(request);
			
			int id = (int)request.get("id");
			
			Event event = eventManager.getEventById(id);
			
			if (event.getOwnerId() != user.getId() &&  !UserRole.isAdmin(user.getRoleId())) {
				throw new IllegalStateException(ErrorMessage.CHANGE_EVENT_NOT_ALLOWED.getMsg());
			}
			
			String title = (String)request.get("title");
			String startTime = (String)request.get("startTime");
			String endTime = (String)request.get("endTime");
			String location = (String)request.get("location");
			
			eventManager.updateEventDetails(id, title, null, startTime == null ? null : Instant.parse(startTime),
					endTime == null ? null :Instant.parse(endTime), location, Util.nullInt, user.getId());
			
			
			if (user.isTokenUpdated()) {
				respond.put("accessToken", user.getAccessToken());
			}
			respond.put("error", "");
			
		}catch(Exception e){
			respond = errorManager.createErrorRespondFromException(e, Utils.rootDir + "/update_event_details", request);
		}
		return new ResponseEntity<Map<String, Object>>(respond, HttpStatus.OK);
	}
	
	@RequestMapping("/get_parti_list")
	public ResponseEntity<Map<String, Object>> getPartiList(@RequestBody Map<String, Object> request) {
		Map<String, Object> respond = new HashMap<String, Object>();
		try{
			User user = userManager.validateAccessToken(request);
			
			int id = (int)request.get("id");
			
			Event event = eventManager.getEventById(id);
			
			if (event.getOwnerId() != user.getId() &&  !UserRole.isAdmin(user.getRoleId())) {
				throw new IllegalStateException(ErrorMessage.VIEW_PARTICIPANTS_NOT_ALLOWED.getMsg());
			}
			
			
			List<Payment> paymentList = paymentManager.getSuccessfulPaymentsByType(PaymentType.EVENT.getName(), id);
			List<PartiListModel> partiList = new ArrayList<>();
			
			for (Payment p : paymentList) {
				PartiListModel pm = new PartiListModel();
				User payer = userManager.getUserById(p.getPayerId());
				pm.setEmail(payer.getUsername());
				pm.setName(StringUtils.capitalize(userManager.getUserDisplayedName(payer.getId())));
				pm.setRegiTime(p.getCreatedAt().toString());
				partiList.add(pm);
			}
			
			
			Collections.sort(partiList, new Comparator<PartiListModel>() {
		        @Override
		        public int compare(PartiListModel o1, PartiListModel o2) {
		            return o1.getName().compareTo(o2.getName());
		        }
		    });
			
			respond.put("partiList", partiList);
			
			if (user.isTokenUpdated()) {
				respond.put("accessToken", user.getAccessToken());
			}
			respond.put("error", "");
			
		}catch(Exception e){
			respond = errorManager.createErrorRespondFromException(e, Utils.rootDir + "/get_parti_list", request);
		}
		return new ResponseEntity<Map<String, Object>>(respond, HttpStatus.OK);
	}
	
}
