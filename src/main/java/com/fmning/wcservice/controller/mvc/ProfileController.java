package com.fmning.wcservice.controller.mvc;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

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
import com.fmning.service.domain.UserDetail;
import com.fmning.service.exceptions.NotFoundException;
import com.fmning.service.manager.FeedManager;
import com.fmning.service.manager.ImageManager;
import com.fmning.service.manager.UserManager;
import com.fmning.util.ErrorMessage;
import com.fmning.util.ImageType;
import com.fmning.util.Util;
import com.fmning.wcservice.model.UserModel;
import com.fmning.wcservice.utils.UserRole;
import com.fmning.wcservice.utils.Utils;

@Controller
public class ProfileController {
	
	@Autowired private UserManager userManager;
	@Autowired private ImageManager imageManager;
	@Autowired private FeedManager feedManager;
	
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
			um.setUserClassof(Util.nullToEmptyString(detail.getYear()));
			um.setUserMajor(Util.nullToEmptyString(detail.getMajor()));
			um.getUser().setName(um.getUserName());
			
			String bDay = Util.nullToEmptyString(detail.getBirthday());
			if (bDay.equals("")) {
				um.setUserBirthday("");
			} else {
				try {
					DateFormat df1 = new SimpleDateFormat("MM/DD/yy", Locale.ENGLISH);
					Date date = df1.parse(bDay);
					DateFormat df2 = new SimpleDateFormat("yyyy-MM-DD", Locale.ENGLISH);
					String reportDate = df2.format(date);
					um.setUserBirthday(reportDate);
				} catch (ParseException e) {
					um.setUserBirthday("");
				}
			}
			
			
		}catch(NotFoundException e){
			um.setUserName("Unknown");
		}
		
		try{
			int avatarId = imageManager.getTypeUniqueImage(ImageType.AVATAR.getName(), um.getUser().getId()).getId();
			um.setUserAvatarId(avatarId);
		}catch(Exception e){
			um.setUserAvatarId(0);
		}
		
		List<Feed> feedList = new ArrayList<>();
		try{
			feedList.addAll(feedManager.getRecentFeedByCreator(um.getUser().getId(), 0));
		} catch (NotFoundException e) {}
		
		if (UserRole.isAdmin(um.getUser().getRoleId())) {
			try{
				feedList.addAll(feedManager.getRecentFeedByCreator(Utils.CSA_ID, 0));
			} catch (NotFoundException e) {}
		}
		
		model.addAttribute("feedList", feedList);
		model.addAttribute("user", um.getUser());//For nav bar
		model.addAttribute("um", um);
		
		return "profile";
	}
	
}
