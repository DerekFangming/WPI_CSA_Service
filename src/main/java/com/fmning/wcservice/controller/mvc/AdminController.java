package com.fmning.wcservice.controller.mvc;

import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.fmning.service.domain.Event;
import com.fmning.service.domain.Image;
import com.fmning.service.domain.User;
import com.fmning.service.exceptions.NotFoundException;
import com.fmning.service.manager.EventManager;
import com.fmning.service.manager.ImageManager;
import com.fmning.service.manager.PaymentManager;
import com.fmning.service.manager.UserManager;
import com.fmning.util.ErrorMessage;
import com.fmning.util.ImageType;
import com.fmning.util.PaymentType;
import com.fmning.util.Util;
import com.fmning.wcservice.model.EventModel;
import com.fmning.wcservice.utils.UserRole;

@Controller
public class AdminController {
	
	@Autowired private UserManager userManager;
	@Autowired private EventManager eventManager;
	@Autowired private ImageManager imageManager;
	@Autowired private PaymentManager paymentManager;
	
	@RequestMapping(value = "/admin", method = RequestMethod.GET)
	public void adminRedirectController(HttpServletRequest request, HttpServletResponse response) {
		try {
			response.sendRedirect("/admin/event");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@RequestMapping(value = "/admin/event", method = RequestMethod.GET)
    public String adminEventController(HttpServletRequest request, HttpServletResponse response, ModelMap model) {
		
		Cookie cookie = null;
		try {
			User user = userManager.validateAccessToken(request);
			if (!user.getEmailConfirmed()) {
				model.addAttribute("errorMessage", ErrorMessage.EMAIL_NOT_CONFIRMED.getMsg());
				return "errorview/403";
			}
			if (!UserRole.isAdmin(user.getRoleId())) {
				return "errorview/403";
			}
			
			String name = userManager.getUserDetail(user.getId()).getName();
			if (name == null){name = "Unknown";}
			user.setName(name);
			model.addAttribute("user", user);
			if (user.isTokenUpdated()) {
				cookie = new Cookie("accessToken", user.getAccessToken());
				cookie.setMaxAge(63113904);
				cookie.setPath("/");
			}
		} catch (NotFoundException e) {
			model.addAttribute("errorMessage", ErrorMessage.NO_USER_LOGGED_IN.getMsg());
			return "errorview/403";
		}
		
		if (cookie != null) {
			response.addCookie(cookie);
		}
		
		List<Event> eventList = eventManager.getRecentEventByDate(Instant.now(), 10);
		List<EventModel> eventModelList = new ArrayList<>();
		
		for (Event e : eventList) {
			EventModel em = new EventModel();
			em.setEvent(e);
			em.setRegistedUserCount(paymentManager.getPaidUsersCountByType(PaymentType.EVENT.getName(), e.getId()));
			try {
				Image img = imageManager.getImageByTypeAndMapping(ImageType.FEED_COVER.getName(), e.getMappingId());
				em.setCoverImageId(img.getId());
			} catch (NotFoundException ex) {
				em.setCoverImageId(Util.nullInt);
			}
			eventModelList.add(em);
		}
		
		model.addAttribute("eventList", eventModelList);
		model.addAttribute("extraPath", "./..");
		
		return "adminEvent";
	}
	
	
	@RequestMapping(value = "/admin/user", method = RequestMethod.GET)
    public String adminUserController(HttpServletRequest request, HttpServletResponse response, ModelMap model) {
		
		Cookie cookie = null;
		try {
			User user = userManager.validateAccessToken(request);
			if (!user.getEmailConfirmed()) {
				model.addAttribute("errorMessage", ErrorMessage.EMAIL_NOT_CONFIRMED.getMsg());
				return "errorview/403";
			}
			if (!UserRole.isAdmin(user.getRoleId())) {
				return "errorview/403";
			}
			
			String name = userManager.getUserDetail(user.getId()).getName();
			if (name == null){name = "Unknown";}
			user.setName(name);
			model.addAttribute("currentUser", user);
			if (user.isTokenUpdated()) {
				cookie = new Cookie("accessToken", user.getAccessToken());
				cookie.setMaxAge(63113904);
				cookie.setPath("/");
			}
		} catch (NotFoundException e) {
			model.addAttribute("errorMessage", ErrorMessage.NO_USER_LOGGED_IN.getMsg());
			return "errorview/403";
		}
		
		if (cookie != null) {
			response.addCookie(cookie);
		}
		
		List<User> userList = userManager.getAllUsers();
		
		for (User u : userList) {
			u.setName(userManager.getUserDisplayedName(u.getId()));
		}
		
		model.addAttribute("userList", userList);
		model.addAttribute("extraPath", "./..");
		return "adminUser";
	}
	
	@RequestMapping(value = "/admin/help", method = RequestMethod.GET)
    public String adminHelpController(HttpServletRequest request, HttpServletResponse response, ModelMap model) {
		
		Cookie cookie = null;
		try {
			User user = userManager.validateAccessToken(request);
			if (!user.getEmailConfirmed()) {
				model.addAttribute("errorMessage", ErrorMessage.EMAIL_NOT_CONFIRMED.getMsg());
				return "errorview/403";
			}
			if (!UserRole.isAdmin(user.getRoleId())) {
				return "errorview/403";
			}
			
			String name = userManager.getUserDetail(user.getId()).getName();
			if (name == null){name = "Unknown";}
			user.setName(name);
			model.addAttribute("user", user);
			if (user.isTokenUpdated()) {
				cookie = new Cookie("accessToken", user.getAccessToken());
				cookie.setMaxAge(63113904);
				cookie.setPath("/");
			}
		} catch (NotFoundException e) {}
		
		if (cookie != null) {
			response.addCookie(cookie);
		}
		
		
		model.addAttribute("extraPath", "./..");
		return "adminHelp";
	}
	
}
