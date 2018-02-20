package com.fmning.wcservice.controller.rest;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.fmning.service.domain.SurvivalGuide;
import com.fmning.service.domain.User;
import com.fmning.service.manager.ErrorManager;
import com.fmning.service.manager.ImageManager;
import com.fmning.service.manager.SGManager;
import com.fmning.service.manager.UserManager;
import com.fmning.util.ErrorMessage;
import com.fmning.util.ImageType;
import com.fmning.util.Util;
import com.fmning.wcservice.utils.Utils;

@Controller
public class SgController {
	
	@Autowired private UserManager userManager;
	@Autowired private SGManager sgManager;
	@Autowired private ImageManager imageManager;
	@Autowired private ErrorManager errorManager;
	
	@RequestMapping(value = "/get_sg", method = RequestMethod.GET)
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
	
	@RequestMapping(value = "/search_sg", method = RequestMethod.GET)
    public ResponseEntity<Map<String, Object>> searchSg(HttpServletRequest request) {
		
		Map<String, Object> respond = new HashMap<String, Object>();
		try{
			List<SurvivalGuide> sgList = sgManager.searchArticle(request.getParameter("keyword"));
			List<Map<String, Object>> processedSgList = new ArrayList<Map<String, Object>>();
			for (SurvivalGuide s : sgList) {
				Map<String, Object> processedSg = new HashMap<String, Object>();
				processedSg.put("id", s.getId());
				processedSg.put("title", s.getTitle());
				processedSgList.add(processedSg);
			}
			respond.put("sgList", processedSgList);
			respond.put("error", "");
		}catch(Exception e){
			respond = errorManager.createErrorRespondFromException(e, request);
		}
		return new ResponseEntity<Map<String, Object>>(respond, HttpStatus.OK);
	}
	
	@RequestMapping("/create_sg")
    public ResponseEntity<Map<String, Object>> creatSG(@RequestBody Map<String, Object> request) {
		Map<String, Object> respond = new HashMap<String, Object>();
		try{
			User user = userManager.validateAccessToken(request);
			if (!user.getUsername().endsWith("@wpi.edu")) {
				throw new IllegalStateException(ErrorMessage.SG_INVALID_EMAIL.getMsg());
			}
			if (!user.getEmailConfirmed()) {
				throw new IllegalStateException(ErrorMessage.EMAIL_NOT_CONFIRMED.getMsg());
			}
			
			int relId = (int)request.get("relId");
			boolean placeAfter = (boolean)request.get("placeAfter");
			String menuTitle = (String)request.get("menuTitle");
			String title = (String)request.get("title");
			String content = (String)request.get("content");
			
			if(title == null || content == null) {
				throw new IllegalStateException(ErrorMessage.INVALID_SG_INPUT.getMsg());
			}
			
			content = saveImagesForSGBody(content, user.getId());
			
			String bgDivWithColor = null;
			SurvivalGuide relSg = sgManager.getArticleById(relId);
			List<SurvivalGuide> sgList = sgManager.getChildArticles(relSg.getParentId(), true);
			int newPosition = relSg.getPosition();
			if (placeAfter) {
				newPosition += 1;
			}
			//Moving positions to make space for the new article
			for (SurvivalGuide s : sgList) {
				if (s.getPosition() >= newPosition) {
					sgManager.softUpdateSG(s.getId(), null, null, Util.nullInt, s.getPosition() + 1, user.getId());
				}
				if(bgDivWithColor == null && s.getContent() != null) {
					bgDivWithColor = s.getContent().split(">", 2)[0] + '>';
				}
			}
			//Get background color
			if(bgDivWithColor == null) {
				bgDivWithColor = "<div color=\"" + Utils.SG_BG_COLOR +"\">";
			}
			
			if(menuTitle == null) {//Creating new article only
				sgManager.createSG(title, content.replaceFirst("<div>", bgDivWithColor), relSg.getParentId(), newPosition, user.getId());
			} else {//Create a menu and then place article inside
				int parentId = sgManager.createSG(menuTitle, null, relSg.getParentId(), newPosition, user.getId());
				sgManager.createSG(title, content.replaceFirst("<div>", bgDivWithColor), parentId, 0, user.getId());
			}
			
			if (user.isTokenUpdated()) {
				respond.put("accessToken", user.getAccessToken());
			}
			respond.put("error", "");
		}catch(Exception e){
			respond = errorManager.createErrorRespondFromException(e, Utils.rootDir + "/create_feed", request);
		}
		
		return new ResponseEntity<Map<String, Object>>(respond, HttpStatus.OK);
	}
	
	private String saveImagesForSGBody(String body, int userId) {
		Matcher imgMatcher = Pattern.compile("(<imgtxt.*?>.*?<\\/imgtxt>)|(<img.*?>)").matcher(body);
		while (imgMatcher.find()) {
			String imgTag = imgMatcher.group();
			
			Matcher srcMatcher = Pattern.compile("src=\".*?\"").matcher(imgTag);
			if (srcMatcher.find()) {
				try {
					String fullSrc = srcMatcher.group();
					String src = fullSrc.replace("src=", "").replace("\"", "");
					int imgId = 0;
					if (src.toLowerCase().contains("i.froala.com")) {
						URL url = new URL(src);
						imgId = imageManager.createImage(url, ImageType.FEED.getName(), Util.nullInt, userId, null);
					} else if(src.toLowerCase().contains("/images/")) {
						continue;
					} else {
						imgId = imageManager.createImage(src, ImageType.FEED.getName(), Util.nullInt, userId, null);
					}
					String dimension = Util.shrinkImageAndGetDimension(imgId);
					if(imgTag.startsWith("<imgtxt")) {
						String newImgtxtTag = imgTag.replace(fullSrc, "src=\"WCImage_" + Integer.toString(imgId) + "\" " + dimension);
						body = body.replace(imgTag, newImgtxtTag);
					} else {
						body = body.replace(imgTag, "<img src=\"WCImage_" + Integer.toString(imgId) + "\" " + dimension + " />");
					}
				} catch (Exception e) {
					body = body.replace(imgTag, "");
				}
				
			} else {
				body = body.replace(imgTag, "");
			}
		}
		
		return body;
	}

}
