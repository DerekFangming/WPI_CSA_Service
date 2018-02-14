package com.fmning.wcservice.controller.mvc;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.fmning.service.domain.Event;
import com.fmning.service.domain.Image;
import com.fmning.service.domain.User;
import com.fmning.service.domain.UserDetail;
import com.fmning.service.exceptions.NotFoundException;
import com.fmning.service.manager.ErrorManager;
import com.fmning.service.manager.FeedManager;
import com.fmning.service.manager.ImageManager;
import com.fmning.service.manager.UserManager;
import com.fmning.util.ErrorMessage;
import com.fmning.util.ImageType;
import com.fmning.util.PaymentType;
import com.fmning.util.Util;
import com.fmning.wcservice.model.EventModel;
import com.fmning.wcservice.model.UserModel;
import com.fmning.wcservice.utils.UserRole;

@Controller
public class ProfileController {
	
	@Autowired private UserManager userManager;
	@Autowired private ImageManager imageManager;
	@Autowired private FeedManager feedManager;
	@Autowired private ErrorManager errorManager;
	
	@RequestMapping(value = "/profile", method = RequestMethod.GET)
    public String adminEventController(HttpServletRequest request, HttpServletResponse response, ModelMap model) {
		
		Cookie cookie = null;
		UserModel um = new UserModel();
		try {
			User user = userManager.validateAccessToken(request);
			um.setUser(user);
			if (user.isTokenUpdated()) {
				cookie = new Cookie("accessToken", user.getAccessToken());
				cookie.setMaxAge(63113904);
			}
		} catch (NotFoundException e) {
			model.addAttribute("errorMessage", ErrorMessage.NO_USER_LOGGED_IN.getMsg());
			return "errorview/403";
		}
		
		if (cookie != null) {
			response.addCookie(cookie);
		}
		
		try{
			UserDetail detail = userManager.getUserDetail(um.getUser().getId());
			um.setUserName(Util.nullToEmptyString(detail.getName()));
			um.setUserBirthday(Util.nullToEmptyString(detail.getBirthday()));
			um.setUserClassof(Util.nullToEmptyString(detail.getYear()));
			um.setUserMajor(Util.nullToEmptyString(detail.getMajor()));
			um.getUser().setName(um.getUserName());
			
		}catch(NotFoundException e){
			um.setUserName("Unknown");
		}
		
		try{
			int avatarId = imageManager.getTypeUniqueImage(ImageType.AVATAR.getName(), um.getUser().getId()).getId();
			um.setUserAvatarId(avatarId);
		}catch(Exception e){
			um.setUserAvatarId(0);
		}
		
		model.addAttribute("user", um.getUser());//For nav bar
		model.addAttribute("um", um);
		
		return "profile";
	}
	
}
