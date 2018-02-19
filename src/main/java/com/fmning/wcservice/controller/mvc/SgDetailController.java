package com.fmning.wcservice.controller.mvc;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.fmning.service.domain.SurvivalGuide;
import com.fmning.service.domain.User;
import com.fmning.service.exceptions.NotFoundException;
import com.fmning.service.manager.SGManager;
import com.fmning.service.manager.UserManager;
import com.fmning.util.ErrorMessage;
import com.fmning.util.Util;
import com.fmning.wcservice.utils.Utils;

@Controller
public class SgDetailController {
	
	@Autowired private UserManager userManager;
	@Autowired private SGManager sgManager;
	
	private String generatedMenu;
	
	@RequestMapping(value = "/sg", method = RequestMethod.GET)
    public String sgController(HttpServletRequest request, HttpServletResponse response, ModelMap model) {
		
		Cookie cookie = null;
		try {
			User user = userManager.validateAccessToken(request);
			String name = userManager.getUserDetail(user.getId()).getName();
			if (name == null){name = "Unknown";}
			user.setName(name);
			model.addAttribute("user", user);
			if (user.isTokenUpdated()) {
				cookie = new Cookie("accessToken", user.getAccessToken());
				cookie.setMaxAge(63113904);
			}
		} catch (NotFoundException e) {
			cookie = new Cookie("accessToken", "invalid");
			cookie.setMaxAge(0);
		}
		
		if (cookie != null) {
			response.addCookie(cookie);
		}
		
		model.addAttribute("redirectPage", "sg");
		
		if (generatedMenu == null) {
			generatedMenu = generateMenu(Util.nullInt, "");
			model.addAttribute("menuList", generatedMenu);
		} else {
			model.addAttribute("menuList", generatedMenu);
		}
		
		return "sg";
	}
	
	@RequestMapping(value = "/get_full_sg_menu", method = RequestMethod.GET)
    public ResponseEntity<Map<String, Object>> getFullSgMenu(HttpServletRequest request) {
		Map<String, Object> respond = new HashMap<String, Object>();
		
		if (generatedMenu == null) {
			generatedMenu = generateMenu(Util.nullInt, "");
			respond.put("menuList", generatedMenu);
		} else {
			respond.put("menuList", generatedMenu);
		}
		respond.put("error", "");
		return new ResponseEntity<Map<String, Object>>(respond, HttpStatus.OK);
	}
	
	private String generateMenu(int parentId, String prefix) {
		String content = "";
		List<SurvivalGuide> currentList = sgManager.getChildArticles(parentId);
		if (currentList.size() > 0) {
			if (parentId == Util.nullInt) {
				content = "<div class=\"card\">";
				String collapseId = Integer.toString(parentId);
				content += "<div class=\"card-header\">Survival Guide Menu</div>";
				content += "<div id=\"collapse" + collapseId + "\" class=\"card-collapse collapse"
						+ (parentId == Util.nullInt ? " show" : "") + "\">";
			}
			
			
			for (SurvivalGuide sg : currentList) {
				String childContent = generateMenu(sg.getId(), prefix + "<div class=\"menu-block\"></div>");
				if (childContent.equals("")) {
					String jsFunc = "openSG(" + Integer.toString(sg.getId()) + ");";
					content += "<div class=\"card-body sg-menu-item border-bottom\">" + prefix + "<a onclick=\"" + jsFunc
							+ "\" href=\"#\">" + sg.getTitle() + "</a></div>";
				} else {
					String collapseId = Integer.toString(sg.getId());
					content += "<div class=\"card-header\">" + prefix + "<a data-toggle=\"collapse\" href=\"#collapse"
							+ collapseId + "\">"+ sg.getTitle() + "</a></div>";
					content += "<div id=\"collapse" + collapseId + "\" class=\"card-collapse collapse\">";
					content += childContent;
					content += "</div>";
				}
			}
			
			if (parentId == Util.nullInt) {
				content += "</div></div>";
			}
		}
		
		return content;
	}
	
	@RequestMapping(value = "/new_sg", method = RequestMethod.GET)
    public String addFeedController(HttpServletRequest request, HttpServletResponse response, ModelMap model) {
		Cookie cookie = null;
		try {
			User user = userManager.validateAccessToken(request);
			if (!user.getUsername().endsWith("@wpi.edu")) {
				model.addAttribute("errorMessage", ErrorMessage.SG_INVALID_EMAIL.getMsg());
				return "errorview/403";
			}
			if (!user.getEmailConfirmed()) {
				model.addAttribute("errorMessage", ErrorMessage.EMAIL_NOT_CONFIRMED.getMsg());
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
		
		if (Utils.prodMode) {
			model.addAttribute("editorHTMLOption", false);
		} else {
			model.addAttribute("editorHTMLOption", true);
		}
		
		if (generatedMenu == null) {
			generatedMenu = generateMenu(Util.nullInt, "");
			model.addAttribute("menuList", generatedMenu);
		} else {
			model.addAttribute("menuList", generatedMenu);
		}
		return "sgEditor";
	}
	
}
