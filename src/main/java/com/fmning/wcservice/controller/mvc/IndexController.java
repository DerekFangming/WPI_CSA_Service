package com.fmning.wcservice.controller.mvc;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.fmning.service.domain.Feed;
import com.fmning.service.domain.User;
import com.fmning.service.exceptions.NotFoundException;
import com.fmning.service.manager.ErrorManager;
import com.fmning.service.manager.FeedManager;
import com.fmning.service.manager.HelperManager;
import com.fmning.service.manager.ImageManager;
import com.fmning.service.manager.UserManager;
import com.fmning.util.ImageType;
import com.fmning.util.Util;
import com.fmning.wcservice.controller.rest.FeedController;
import com.fmning.wcservice.model.FeedModel;
import com.fmning.wcservice.model.LoginForm;
import com.fmning.wcservice.model.RegisterForm;
import com.fmning.wcservice.utils.Utils;

@Controller
public class IndexController {
	
	@Autowired private UserManager userManager;
	@Autowired private FeedManager feedManager;
	@Autowired private ImageManager imageManager;
	@Autowired private ErrorManager errorManager;
	@Autowired private HelperManager helperManager;
	
	@RequestMapping(value = "/", method = RequestMethod.GET)
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
		
		model.addAttribute("feedList", getFeedList(0));
		model.addAttribute("pageCount", (int) Math.ceil((double) FeedController.totalFeeds / 10));
		model.addAttribute("redirectPage", "index");
		
		return "index";
	}
	
	@RequestMapping("/web_login")
    public String loginController(@ModelAttribute LoginForm form, HttpServletResponse response, ModelMap model) {
		
		String accessToken;
		User user;
		try {
			user = userManager.webLogin(form.getUsername(), form.getPassword());
			accessToken = user.getAccessToken();
			String name = userManager.getUserDetail(user.getId()).getName();
			if (name == null)
				name = "Unknown";
			user.setName(name);
			
			
			
		} catch (NotFoundException e) {
			model.addAttribute("errorMessage", e.getMessage());
			return "errorview/errorMessage";
		}
		
		
		Cookie cookie = new Cookie("accessToken", accessToken);
		cookie.setMaxAge(form.getRemember() == null ? 86400 : 63113904);
		
		response.addCookie(cookie);
		
		model.addAttribute("user", user);
	
		return "subview/navUserLoggedIn";
	}
	
	@RequestMapping("/web_register")
    public String registerController(@ModelAttribute RegisterForm form, HttpServletResponse response, ModelMap model) {
		
		String accessToken;
		User user;
		try {
			user = userManager.webRegister(form.getNewUsername(), form.getNewPassword());
			user.setName(form.getNewName());
			accessToken = user.getAccessToken();
			userManager.saveUserDetail(user.getId(), user.getName(), null, Util.nullInt, null, null, null, 
					null, null, null);
			
			String username = form.getNewUsername();
			
			String veriCode = helperManager.getEmailConfirmCode(username);
			userManager.updateVeriCode(username, veriCode);
			String message = Utils.createVerificationEmail(user.getName(), veriCode);
			if (Utils.prodMode){
				try {
					helperManager.sendEmail("no-reply@fmning.com", username, "Email Confirmation", message);
				} catch (Exception e) {
					errorManager.logError(e);
				}
			} else {
				System.out.println(message);
			}
			
		} catch (IllegalStateException e) {
			model.addAttribute("errorMessage", e.getMessage());
			return "errorview/errorMessage";
		}
		
		
		Cookie cookie = new Cookie("accessToken", accessToken);
		cookie.setMaxAge(63113904);
		
		response.addCookie(cookie);
		
		model.addAttribute("user", user);
	
		return "subview/navUserLoggedIn";
	}
	
	@RequestMapping("/logout")
    public String logoutController(@ModelAttribute LoginForm form, HttpServletResponse response, ModelMap model) {
		
		Cookie cookie = new Cookie("accessToken", "invalid");
		cookie.setMaxAge(0);
		
		response.addCookie(cookie);
		
		model.addAttribute("redirectPage", "index");
		
		
		model.addAttribute("feedList", getFeedList(0));
		model.addAttribute("pageCount", (int) Math.ceil((double) FeedController.totalFeeds / 10));
	
		return "index";
	}
	
	private List<FeedModel> getFeedList(int pageIndex) {
		List<Feed> feedList = feedManager.getRecentFeedByPageIndex(pageIndex, 10);
		List<FeedModel> feedModelList = new ArrayList<>();
		
		for(Feed m : feedList){
			FeedModel fm = new FeedModel();
			m.setBody(m.getBody().replaceAll("\\<[^>]*>",""));
			fm.setFeed(m);
			fm.setOwnerName(userManager.getUserDisplayedName(m.getOwnerId()));
			
			try {
				int imgId = imageManager.getImageByTypeAndMapping(ImageType.FEED_COVER.getName(), m.getId()).getId();
				fm.setCoverImageId(imgId);
			}catch(Exception e) {}
			
			feedModelList.add(fm);
		}
		
		if (FeedController.totalFeeds == Util.nullInt) {
			FeedController.totalFeeds = feedManager.getFeedCount();
		}
		
		return feedModelList;
	}

}
