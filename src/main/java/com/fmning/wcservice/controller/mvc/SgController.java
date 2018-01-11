package com.fmning.wcservice.controller.mvc;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class SgController {
	
	@RequestMapping(value = "/sg", method = RequestMethod.GET)
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
		model.addAttribute("redirectPage", "sg");
		
		return "sg";
	}
	
}
