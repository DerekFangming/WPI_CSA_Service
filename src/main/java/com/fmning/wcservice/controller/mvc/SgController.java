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
import com.fmning.service.manager.ErrorManager;
import com.fmning.service.manager.SGManager;
import com.fmning.service.manager.UserManager;
import com.fmning.util.Util;

@Controller
public class SgController {
	
	@Autowired private UserManager userManager;
	@Autowired private SGManager sgManager;
	@Autowired private ErrorManager errorManager;
	
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
	
	@RequestMapping(value = "/get_sg_article", method = RequestMethod.GET)
    public ResponseEntity<Map<String, Object>> getSg(HttpServletRequest request) {
		
		Map<String, Object> respond = new HashMap<String, Object>();
		try{
			SurvivalGuide sg = sgManager.getArticleById(Integer.parseInt(request.getParameter("id")));
			respond.put("title", sg.getTitle());
			respond.put("content", sg.getContent());
			respond.put("createdAt", sg.getCreatedAt().toString());
			respond.put("ownerId", sg.getOwnerId());
			respond.put("ownerName", userManager.getUserDisplayedName(sg.getOwnerId()));
			respond.put("error", "");
		}catch(Exception e){
			respond = errorManager.createErrorRespondFromException(e, request);
		}
		return new ResponseEntity<Map<String, Object>>(respond, HttpStatus.OK);
	}
	
	private String generateMenu(int parentId, String prefix) {
		String content = "";
		List<SurvivalGuide> currentList = sgManager.getChildArticles(parentId);
		if (currentList.size() > 0) {
			if (parentId == Util.nullInt) {
				content = "<div class=\"card\">\n";
				String collapseId = Integer.toString(parentId);
				content += "<div class=\"card-header\">Survival Guide Menu</div>\n";
				content += "<div id=\"collapse" + collapseId + "\" class=\"card-collapse collapse"
						+ (parentId == Util.nullInt ? " show" : "") + "\">\n";
			}
			
			
			for (SurvivalGuide sg : currentList) {
				String childContent = generateMenu(sg.getId(), prefix + "&emsp;");
				if (childContent.equals("")) {
					String jsFunc = "openSG(" + Integer.toString(sg.getId()) + ");";
					content += "<div class=\"card-body sg-menu-item\"><a onclick=\"" + jsFunc + "\" href=\"#\">"
							+ prefix + sg.getTitle() + "</a></div>\n";
				} else {
					String collapseId = Integer.toString(sg.getId());
					content += "<div class=\"card-header\"><a data-toggle=\"collapse\" href=\"#collapse"
							+ collapseId + "\">" + prefix + sg.getTitle() + "</a></div>\n";
					content += "<div id=\"collapse" + collapseId + "\" class=\"card-collapse collapse\">\n";
					content += childContent;
					content += "</div>\n";
				}
			}
			
			if (parentId == Util.nullInt) {
				content += "</div>\n</div>";
			}
		}
		
		return content;
	}
	
}
