package com.fmning.wcservice.controller.mvc;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.fmning.service.domain.Event;
import com.fmning.service.domain.Feed;
import com.fmning.service.domain.User;
import com.fmning.service.exceptions.NotFoundException;
import com.fmning.service.manager.ErrorManager;
import com.fmning.service.manager.EventManager;
import com.fmning.service.manager.FeedManager;
import com.fmning.service.manager.ImageManager;
import com.fmning.service.manager.UserManager;
import com.fmning.util.ErrorMessage;
import com.fmning.util.EventType;
import com.fmning.util.FeedType;
import com.fmning.util.ImageType;
import com.fmning.wcservice.model.FeedModel;
import com.fmning.wcservice.utils.UserRole;

@Controller
public class FeedDetailController {
	
	@Autowired private UserManager userManager;
	@Autowired private FeedManager feedManager;
	@Autowired private ImageManager imageManager;
	@Autowired private EventManager eventManager;
	@Autowired private ErrorManager errorManager;
	
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
		try {
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
			model.addAttribute("notFound", false);
		} catch (Exception e) {
			errorManager.logError(e, request);
			model.addAttribute("notFound", true);
		}
		
		model.addAttribute("redirectPage", "feed");
		
		return "feed";
	}
	
	@RequestMapping(value = "/new_article", method = RequestMethod.GET)
    public String addFeedController(HttpServletRequest request, HttpServletResponse response, ModelMap model) {
		Cookie cookie = null;
		try {
			User user = userManager.validateAccessToken(request);
			if (!user.getEmailConfirmed()) {
				model.addAttribute("errorMessage", ErrorMessage.EMAIL_NOT_CONFIRMED.getMsg());
				return "errorview/403";
			}
			String name = userManager.getUserDetail(user.getId()).getName();
			if (name == null){name = "Unknown";}
			user.setName(name);
			model.addAttribute("user", user);
			if (user.isTokenUpdated()) {
				cookie = new Cookie("accessToken", user.getAccessToken());
				cookie.setMaxAge(63113904);
			}
			
			try{
				imageManager.getTypeUniqueImage(ImageType.AVATAR.getName(), user.getId()).getId();
				model.addAttribute("hasAvatar", true);
			}catch(Exception e){}
		} catch (NotFoundException e) {
			model.addAttribute("errorMessage", ErrorMessage.NO_USER_LOGGED_IN.getMsg());
			return "errorview/403";
		}
		
		if (cookie != null) {
			response.addCookie(cookie);
		}
		
		return "feedEditor";
	}
	
	@RequestMapping(value = "/edit_article", method = RequestMethod.GET)
    public String editFeedController(HttpServletRequest request, HttpServletResponse response, ModelMap model) {
		Cookie cookie = null;
		User user;
		try {
			user = userManager.validateAccessToken(request);
			String name = userManager.getUserDetail(user.getId()).getName();
			if (name == null){name = "Unknown";}
			user.setName(name);
			model.addAttribute("user", user);
			if (user.isTokenUpdated()) {
				cookie = new Cookie("accessToken", user.getAccessToken());
				cookie.setMaxAge(63113904);
			}
			
		} catch (NotFoundException e) {
			model.addAttribute("errorMessage", ErrorMessage.NO_USER_LOGGED_IN.getMsg());
			return "errorview/403";
		}
		
		if (cookie != null) {
			response.addCookie(cookie);
		}
		
		String feedId = request.getParameter("id");
		try {
			Feed feed = feedManager.getFeedById(Integer.parseInt(feedId));
			if (feed.getOwnerId() != user.getId()) {
				if (!UserRole.isAdmin(user.getRoleId())) {
					model.addAttribute("errorMessage", ErrorMessage.NO_PERMISSION.getMsg());
					return "errorview/403";
				}
			}
			
			FeedModel fm = new FeedModel();
			fm.setFeed(feed);
			try {
				int imgId = imageManager.getImageByTypeAndMapping(ImageType.FEED_COVER.getName(), feed.getId()).getId();
				fm.setCoverImageId(imgId);
			}catch(Exception e) {}
			
			model.addAttribute("fm", fm);
			model.addAttribute("notFound", false);
		} catch (Exception e) {
			errorManager.logError(e, request);
			model.addAttribute("notFound", true);
		}
		model.addAttribute("editorHTMLOption", true);
		model.addAttribute("editMode", true);
		return "feedEditor";
	}

}
