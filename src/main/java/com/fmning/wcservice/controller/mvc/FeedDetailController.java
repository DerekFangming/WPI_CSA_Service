package com.fmning.wcservice.controller.mvc;

import java.io.StringReader;
import java.time.Instant;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;

import com.fmning.service.domain.Event;
import com.fmning.service.domain.Feed;
import com.fmning.service.domain.User;
import com.fmning.service.exceptions.NotFoundException;
import com.fmning.service.manager.EventManager;
import com.fmning.service.manager.FeedManager;
import com.fmning.service.manager.UserManager;
import com.fmning.util.EventType;
import com.fmning.util.FeedType;
import com.fmning.util.Util;
import com.fmning.wcservice.model.FeedModel;
import com.fmning.wcservice.utils.Utils;

@Controller
public class FeedDetailController {
	
	@Autowired private UserManager userManager;
	@Autowired private FeedManager feedManager;
	@Autowired private EventManager eventManager;
	
	@RequestMapping(value = "/feed", method = RequestMethod.GET)
    public String indexController(HttpServletRequest request, HttpServletResponse response, ModelMap model) {
		
		Cookie cookie = null;
		try {
			User user = userManager.validateAccessToken(request);
			String name = userManager.getUserDetail(user.getId()).getName();
			if (name == null){name = "Unknown";}
			user.setName(name);
			model.addAttribute("user", user);
			if (user.isTokenUpdated()) {
				cookie = new Cookie("accessToken", user.getAccessToken());
				cookie.setMaxAge(63113904);
			}
		} catch (NotFoundException e) {
			cookie = new Cookie("accessToken", "invalid");
			cookie.setMaxAge(0);
		}
		
		if (cookie != null) {
			response.addCookie(cookie);
		}
		
		String feedId = request.getParameter("id");
		Feed feed = feedManager.getFeedById(Integer.parseInt(feedId));
		
		FeedModel fm = new FeedModel();
		fm.setFeed(feed);
		fm.setOwnerName(userManager.getUserDisplayedName(feed.getOwnerId()));
		
		if (feed.getType().equals(FeedType.EVENT.getName())) {
			try {
				Event event = eventManager.getEventByType(EventType.FEED.getName(), feed.getId());
				fm.setEvent(event);
				//model.addAttribute("event", event);
			} catch (NotFoundException e){}
		}
		
		
		model.addAttribute("fm", fm);
		
		
		
		
		model.addAttribute("redirectPage", "feed");
		
		return "feed";
	}
	
	

}
