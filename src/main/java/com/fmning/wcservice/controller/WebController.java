package com.fmning.wcservice.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

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
import com.fmning.service.manager.PaymentManager;
import com.fmning.service.manager.UserManager;
import com.fmning.util.PaymentType;
import com.fmning.wcservice.utils.Utils;

@Controller
public class WebController {
	
	@Autowired private PaymentManager paymentManager;
	@Autowired private UserManager userManager;
	
	@RequestMapping(value = "/", method = RequestMethod.GET)
    public String helloWorld(HttpServletRequest request, HttpServletResponse response, ModelMap model) {
		
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
	
	@RequestMapping(value = "/list", method = RequestMethod.GET)
    public String getList(HttpServletRequest request, HttpServletResponse response, ModelMap model) {
		
		List<User> list = new ArrayList<User>();
		
		try{
			int hardCodedId = 2;//TODO: Remove this in future ...
			if(Utils.schedulerEnabled) {
				hardCodedId = 1;
			}
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

}
