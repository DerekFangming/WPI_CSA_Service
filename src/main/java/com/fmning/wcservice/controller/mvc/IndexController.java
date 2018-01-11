package com.fmning.wcservice.controller.mvc;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.fmning.util.Util;
import com.fmning.wcservice.model.LoginForm;

@Controller
public class IndexController {
	
	@RequestMapping(value = "/", method = RequestMethod.GET)
    public String indexController(HttpServletRequest request, HttpServletResponse response, ModelMap model) {
		boolean loggedIn = false;
		
		Cookie[] cookies = request.getCookies();
		if(cookies != null) {
			for(Cookie c : cookies){
				if (c.getName().equals("access_token")) {
					System.out.println("Found access token values: " + c.getValue());
					loggedIn = true;
					model.addAttribute("nameOfUser", "Fangming");
				}
			}
		}
		
		model.addAttribute("loggedIn", loggedIn);
		model.addAttribute("redirectPage", "index");
		
		return "index";//new ModelAndView("index");
	}
	
	@RequestMapping("/web_login")
    public String loginController(@ModelAttribute LoginForm form, HttpServletResponse response, ModelMap model) {
		
		System.out.println(form.getUsername() + " " + form.getPassword() + " " + form.getRemember());
		
		Cookie cookie = new Cookie("access_token", "the_token_value");
		cookie.setMaxAge(604800);// seven days
		
		response.addCookie(cookie);
		
		model.addAttribute("loggedIn", true);
		model.addAttribute("nameOfUser", "Fangming");
	
		return form.getRedirect();
	}
	
	@RequestMapping("/logout")
    public String logoutController(@ModelAttribute LoginForm form, HttpServletResponse response, ModelMap model) {
		
		Cookie cookie = new Cookie("access_token", "the_token_value");
		cookie.setMaxAge(0);
		
		response.addCookie(cookie);
		
		model.addAttribute("loggedIn", false);
	
		return "index";
	}

}
