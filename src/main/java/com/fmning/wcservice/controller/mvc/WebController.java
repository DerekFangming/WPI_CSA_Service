package com.fmning.wcservice.controller.mvc;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class WebController {
	
	
	@Deprecated
	@RequestMapping(value = "/list", method = RequestMethod.GET)
    public String getList(HttpServletRequest request, HttpServletResponse response, ModelMap model) {
		
		return "list";
	}
	
	@RequestMapping(value = "/terms", method = RequestMethod.GET)
    public String terms() {
		
		return "terms";
	}
	
	@RequestMapping(value = "/privacy", method = RequestMethod.GET)
    public String privacy() {
		
		return "privacy";
	}
	
	@RequestMapping(value = "/support", method = RequestMethod.GET)
    public String support() {
		
		return "support";
	}

}
