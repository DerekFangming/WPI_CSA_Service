package com.fmning.wcservice.controller.mvc;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.fmning.service.domain.Ticket;
import com.fmning.service.domain.User;
import com.fmning.service.exceptions.NotFoundException;
import com.fmning.service.manager.PaymentManager;
import com.fmning.service.manager.UserManager;
import com.fmning.util.ErrorMessage;
import com.fmning.util.PaymentType;
import com.fmning.util.Util;
import com.fmning.wcservice.model.LoginForm;
import com.fmning.wcservice.utils.Utils;

@Controller
public class WebController {
	
	@Autowired private PaymentManager paymentManager;
	@Autowired private UserManager userManager;
	
	
	
	@RequestMapping(value = "/list", method = RequestMethod.GET)
    public String getList(HttpServletRequest request, HttpServletResponse response, ModelMap model) {
		
		List<User> list = new ArrayList<User>();
		
		try{
			int hardCodedId = 4;//TODO: Remove this in future ...
			
			List<User> userList = paymentManager.getPaidUserByType(PaymentType.EVENT.getName(), hardCodedId);//
			for(User u : userList){
				u.setName(userManager.getUserDisplayedName(u.getId()));
				list.add(u);
			}
			
			Collections.sort(list, new Comparator<User>() {
		        @Override
		        public int compare(User o1, User o2) {
		            return o1.getName().compareTo(o2.getName());
		        }
		    });
			
		}catch(NotFoundException e){}
		
		model.addAttribute("nameList", list);
		model.addAttribute("count", list.size());
		
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
