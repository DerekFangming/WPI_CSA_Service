package com.fmning.wcservice.controller.mvc;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.fmning.service.domain.User;
import com.fmning.service.exceptions.NotFoundException;
import com.fmning.service.manager.UserManager;
import com.fmning.util.Util;
import com.fmning.wcservice.model.LoginForm;

@Controller
public class IndexController {
	
	@Autowired private UserManager userManager;
	
	@RequestMapping(value = "/", method = RequestMethod.GET)
    public String indexController(HttpServletRequest request, HttpServletResponse response, ModelMap model) {
		boolean loggedIn = false;
		
		Cookie[] cookies = request.getCookies();
		if(cookies != null) {
			for(Cookie c : cookies){
				if (c.getName().equals("access_token")) {
					try {
						User user = userManager.validateAccessToken(c.getValue());
						String name = userManager.getUserDetail(user.getId()).getName();
						if (name == null)
							name = "Unknown";
						
						loggedIn = true;
						model.addAttribute("nameOfUser", name);
						
						if (!c.getValue().equals(user.getAuthToken())) {
							Cookie cookie = new Cookie("access_token", user.getAuthToken());
							cookie.setMaxAge(63113904);
							response.addCookie(cookie);
						}
					} catch (NotFoundException e) {
						loggedIn = false;
						Cookie cookie = new Cookie("access_token", "invalid");
						cookie.setMaxAge(0);
						response.addCookie(cookie);
					}
					
				}
			}
		}
		
		
		
		model.addAttribute("loggedIn", loggedIn);
		model.addAttribute("redirectPage", "index");
		
		return "index";
	}
	
	@RequestMapping("/web_login")
    public String loginController(@ModelAttribute LoginForm form, HttpServletResponse response, ModelMap model) {
		
		String accessToken;
		String name;
		try {
			User user = userManager.webLogin(form.getUsername(), form.getPassword());
			accessToken = user.getAuthToken();
			name = userManager.getUserDetail(user.getId()).getName();
			if (name == null)
				name = "Unknown";
		} catch (NotFoundException e) {
			model.addAttribute("errorMessage", e.getMessage());
			return "errorview/errorMessage";
		}
		
		
		Cookie cookie = new Cookie("access_token", accessToken);
		cookie.setMaxAge(form.getRemember() == null ? 86400 : 63113904);
		
		response.addCookie(cookie);
		
		model.addAttribute("loggedIn", true);
		model.addAttribute("nameOfUser", name);
	
		return "subview/navUserLoggedIn";
	}
	
	@RequestMapping("/logout")
    public String logoutController(@ModelAttribute LoginForm form, HttpServletResponse response, ModelMap model) {
		
		Cookie cookie = new Cookie("access_token", "invalid");
		cookie.setMaxAge(0);
		
		response.addCookie(cookie);
		
		model.addAttribute("loggedIn", false);
		model.addAttribute("redirectPage", "index");
	
		return "index";
	}

}
