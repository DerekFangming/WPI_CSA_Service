package com.fmning.wcservice.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class WebController {
	
	@RequestMapping(value = "/", method = RequestMethod.GET)
    public String emailVerifivation(HttpServletRequest request, ModelMap model) {
		
		//model.addAttribute("msg", "Your email address has been confirmed");


		System.out.println("WORKING");
		return "index";
	}

}
