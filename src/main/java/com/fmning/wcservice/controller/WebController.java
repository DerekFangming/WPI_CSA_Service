package com.fmning.wcservice.controller;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class WebController {
	
	@RequestMapping(value = "/", method = RequestMethod.GET)
    public String emailVerifivation(HttpServletRequest request, HttpServletResponse response, ModelMap model) {
		
		//model.addAttribute("msg", "Your email address has been confirmed");
		Cookie[] cookies = request.getCookies();
		
		if(cookies != null) {
			for(Cookie c : cookies){
				System.out.println("key: " + c.getName() + " values: " + c.getValue());
			}
		}else{
			System.out.println("no cookies");
		}
		
		Cookie cookie = new Cookie("haha", "accessvalue");
		cookie.setMaxAge(10);
		
		response.addCookie(cookie);
		
		return "index";
	}

}
