package com.fmning.wcservice.controller.mvc;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
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
import com.fmning.service.domain.EditingQueue;
import com.fmning.service.exceptions.NotFoundException;
import com.fmning.service.manager.EditingQueueManager;
import com.fmning.service.manager.ErrorManager;
import com.fmning.service.manager.SGManager;
import com.fmning.service.manager.UserManager;
import com.fmning.util.EditingQueueType;
import com.fmning.util.ErrorMessage;
import com.fmning.util.Util;
import com.fmning.wcservice.utils.Utils;

@Controller
public class SgDetailController {
	
	@Autowired private UserManager userManager;
	@Autowired private SGManager sgManager;
	@Autowired private EditingQueueManager eqManager;
	@Autowired private ErrorManager errorManager;
	
	private String generatedMenu;
	
	@RequestMapping(value = "/refresh_sg_menu", method = RequestMethod.GET)
	public void sgRefreshRedirectController(HttpServletRequest request, HttpServletResponse response) {
		try {
			generatedMenu = generateMenu(Util.nullInt, "");
			String queryString = request.getQueryString();
			response.sendRedirect("/sg" + (queryString == null ? "" : "?" + queryString));
		} catch (IOException e) {
			errorManager.logError(e, request);
		}
	}
	
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
		
		String id = request.getParameter("id");
		if (id != null) {
			model.addAttribute("initialId", id);
		} else {
			model.addAttribute("initialId", "1");
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
							+ collapseId + "\">"+ sg.getTitle() + "</a><i class=\"fa fa-chevron-down float-right\"></i></div>";
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
	
	@RequestMapping(value = "/edit_sg", method = RequestMethod.GET)
    public String editFeedController(HttpServletRequest request, HttpServletResponse response, ModelMap model) {
		Cookie cookie = null;
		User user;
		try {
			user = userManager.validateAccessToken(request);
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
		
		String sgId = request.getParameter("id");
		try {
			SurvivalGuide sg = sgManager.getArticleById(Integer.parseInt(sgId));
			model.addAttribute("sg", sg);
			model.addAttribute("notFound", false);
		} catch (Exception e) {
			errorManager.logError(e, request);
			model.addAttribute("notFound", true);
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
		
		EditingQueue eq = eqManager.updateQueue(EditingQueueType.SG, Integer.parseInt(sgId), user.getId());
		if (eq != null && eq.getOwnerId() != user.getId()) {
			Instant now = Instant.now();
			if (eq.getCreatedAt().isAfter(now.minus(Duration.ofMinutes(1)))) {
				User confUser = userManager.getUserById(eq.getOwnerId());
				confUser.setName(userManager.getUserDisplayedName(eq.getOwnerId()));
				model.addAttribute("confUser", confUser);
			}
		}
		model.addAttribute("editMode", true);
		return "sgEditor";
	}
	
}
