package com.fmning.wcservice.controller.mvc;

import java.time.Instant;
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
import com.fmning.service.manager.FeedManager;
import com.fmning.service.manager.HelperManager;
import com.fmning.service.manager.ImageManager;
import com.fmning.service.manager.UserManager;
import com.fmning.util.Util;
import com.fmning.wcservice.model.LoginForm;
import com.fmning.wcservice.model.RegisterForm;
import com.fmning.wcservice.utils.Utils;

@Controller
public class IndexController {
	
	@Autowired private UserManager userManager;
	@Autowired private FeedManager feedManager;
	@Autowired private ImageManager imageManager;
	@Autowired private HelperManager helperManager;
	
	@RequestMapping(value = "/", method = RequestMethod.GET)
    public String indexController(HttpServletRequest request, HttpServletResponse response, ModelMap model) {
		
		Cookie[] cookies = request.getCookies();
		if(cookies != null) {
			for(Cookie c : cookies){
				if (c.getName().equals("access_token")) {
					try {
						User user = userManager.validateAccessToken(c.getValue());
						String name = userManager.getUserDetail(user.getId()).getName();
						if (name == null)
							name = "Unknown";
						
						user.setName(name);
						model.addAttribute("user", user);
						
						if (!c.getValue().equals(user.getAuthToken())) {
							Cookie cookie = new Cookie("access_token", user.getAuthToken());
							cookie.setMaxAge(63113904);
							response.addCookie(cookie);
						}
					} catch (NotFoundException e) {
						Cookie cookie = new Cookie("access_token", "invalid");
						cookie.setMaxAge(0);
						response.addCookie(cookie);
					}
					
				}
			}
		}
		
		List<Feed> feedList = feedManager.getRecentFeedByDate(Instant.now(), 10);
		
		for(Feed m : feedList){
			m.setBody(m.getBody().replaceAll("\\<[^>]*>",""));
			try {
				int imgId = imageManager.getImageByTypeAndMapping("FeedCover", m.getId()).getId();
				m.setCoverImageId(imgId);
			}catch(Exception e) {}
		}
		
		model.addAttribute("feedList", feedList);
		model.addAttribute("redirectPage", "index");
		
		return "index";
	}
	
	@RequestMapping("/web_login")
    public String loginController(@ModelAttribute LoginForm form, HttpServletResponse response, ModelMap model) {
		
		String accessToken;
		User user;
		try {
			user = userManager.webLogin(form.getUsername(), form.getPassword());
			accessToken = user.getAuthToken();
			String name = userManager.getUserDetail(user.getId()).getName();
			if (name == null)
				name = "Unknown";
			user.setName(name);
			
			
			
		} catch (NotFoundException e) {
			model.addAttribute("errorMessage", e.getMessage());
			return "errorview/errorMessage";
		}
		
		
		Cookie cookie = new Cookie("access_token", accessToken);
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
			accessToken = user.getAuthToken();
			userManager.saveUserDetail(user.getId(), user.getName(), null, Util.nullInt, null, null, null, 
					null, null, null);
			
			String username = form.getNewUsername();
			
			String veriCode = helperManager.getEmailConfirmCode(username);
			userManager.updateVeriCode(username, veriCode);
			String message = Utils.createVerificationEmail(veriCode);
			helperManager.sendEmail("no-reply@fmning.com", username, "Email Confirmation", message);
			
		} catch (IllegalStateException e) {
			model.addAttribute("errorMessage", e.getMessage());
			return "errorview/errorMessage";
		}
		
		
		Cookie cookie = new Cookie("access_token", accessToken);
		cookie.setMaxAge(63113904);
		
		response.addCookie(cookie);
		
		model.addAttribute("user", user);
	
		return "subview/navUserLoggedIn";
	}
	
	@RequestMapping("/logout")
    public String logoutController(@ModelAttribute LoginForm form, HttpServletResponse response, ModelMap model) {
		
		Cookie cookie = new Cookie("access_token", "invalid");
		cookie.setMaxAge(0);
		
		response.addCookie(cookie);
		
		model.addAttribute("redirectPage", "index");
		
		List<Feed> feedList = feedManager.getRecentFeedByDate(Instant.now(), 10);
		
		for(Feed m : feedList){
			m.setBody(m.getBody().replaceAll("\\<[^>]*>",""));
			try {
				int imgId = imageManager.getImageByTypeAndMapping("FeedCover", m.getId()).getId();
				m.setCoverImageId(imgId);
			}catch(Exception e) {}
		}
		
		model.addAttribute("feedList", feedList);
	
		return "index";
	}

}
