package com.fmning.wcservice.controller.mvc;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.fmning.service.domain.User;
import com.fmning.service.exceptions.NotFoundException;
import com.fmning.service.manager.UserManager;

@Controller
public class SgController {
	
	@Autowired private UserManager userManager;
	
	@RequestMapping(value = "/sg", method = RequestMethod.GET)
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
		
		model.addAttribute("redirectPage", "sg");
		
		return "sg";
	}
	
}
