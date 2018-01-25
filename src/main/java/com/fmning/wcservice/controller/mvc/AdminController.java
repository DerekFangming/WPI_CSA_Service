package com.fmning.wcservice.controller.mvc;

import java.time.Instant;
import java.util.List;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.fmning.service.domain.Feed;
import com.fmning.service.domain.User;
import com.fmning.service.exceptions.NotFoundException;
import com.fmning.service.manager.UserManager;
import com.fmning.util.ErrorMessage;
import com.fmning.wcservice.utils.Utils;

@Controller
public class AdminController {
	
	@Autowired private UserManager userManager;
	
	@RequestMapping(value = "/admin/event", method = RequestMethod.GET)
    public String adminEventController(HttpServletRequest request, HttpServletResponse response, ModelMap model) {
		
		Cookie cookie = null;
		try {
			User user = userManager.validateAccessToken(request);
			if (!user.getEmailConfirmed()) {
				model.addAttribute("errorMessage", ErrorMessage.EMAIL_NOT_CONFIRMED.getMsg());
			}
			if (user.getRoleId() == 10) {
				return "errorview/403";
			}
			
			String name = userManager.getUserDetail(user.getId()).getName();
			if (name == null){name = "Unknown";}
			user.setName(name);
			model.addAttribute("user", user);
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
		
		
		/*List<Feed> feedList = feedManager.getRecentFeedByDate(Instant.now(), 10);
		
		for(Feed m : feedList){
			m.setBody(m.getBody().replaceAll("\\<[^>]*>",""));
			try {
				int imgId = imageManager.getImageByTypeAndMapping("FeedCover", m.getId()).getId();
				m.setCoverImageId(imgId);
			}catch(Exception e) {}
		}
		
		model.addAttribute("feedList", feedList);
		model.addAttribute("redirectPage", "index");*/
		model.addAttribute("prodMode", Utils.prodMode);
		System.out.println("ok");
		return "adminEvent";
	}
}
