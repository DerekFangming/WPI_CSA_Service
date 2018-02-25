package com.fmning.wcservice.controller.rest;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.fmning.service.domain.SurvivalGuide;
import com.fmning.service.domain.SurvivalGuideHist;
import com.fmning.service.domain.User;
import com.fmning.service.exceptions.NotFoundException;
import com.fmning.service.manager.ErrorManager;
import com.fmning.service.manager.ImageManager;
import com.fmning.service.manager.SGManager;
import com.fmning.service.manager.UserManager;
import com.fmning.util.ErrorMessage;
import com.fmning.util.HistType;
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
			int sgId = Integer.parseInt(request.getParameter("id"));
			SurvivalGuide sg = sgManager.getArticleById(sgId);
			respond.put("title", sg.getTitle());
			respond.put("content", sg.getContent());
			respond.put("createdAt", sg.getCreatedAt().toString());
			respond.put("ownerId", sg.getOwnerId());
			respond.put("ownerName", userManager.getUserDisplayedName(sg.getOwnerId()));
			
			try {
				List<SurvivalGuideHist> sghList = sgManager.getEditingHistory(sgId, false, HistType.UPDATE, Util.nullInt);
				List<Map<String, Object>> processedHistList = new ArrayList<>();
				for (SurvivalGuideHist s : sghList) {
					Map<String, Object> ns = new HashMap<>();
					ns.put("id", s.getId());
					ns.put("createdAt", s.getCreatedAt().toString());
					ns.put("ownerId", s.getOwnerId());
					ns.put("ownerName", userManager.getUserDisplayedName(s.getOwnerId()));
					processedHistList.add(ns);
				}
				respond.put("history", processedHistList);
			} catch (NotFoundException e) {}
			
			respond.put("error", "");
		}catch(Exception e){
			respond = errorManager.createErrorRespondFromException(e, request);
		}
		return new ResponseEntity<Map<String, Object>>(respond, HttpStatus.OK);
	}
	
	@RequestMapping(value = "/get_sg_history", method = RequestMethod.GET)
    public ResponseEntity<Map<String, Object>> getSgHist(HttpServletRequest request) {
		
		Map<String, Object> respond = new HashMap<String, Object>();
		try{
			int histId = Integer.parseInt(request.getParameter("historyId"));
			SurvivalGuideHist sgh = sgManager.getEdintingHistorybyId(histId);
			respond.put("title", sgh.getTitle());
			respond.put("content", sgh.getContent());
			respond.put("createdAt", sgh.getCreatedAt().toString());
			respond.put("ownerId", sgh.getOwnerId());
			respond.put("ownerName", userManager.getUserDisplayedName(sgh.getOwnerId()));
			
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
					updateSGLocation(s.getId(), s.getPosition() + 1);
				}
				if(bgDivWithColor == null && s.getContent() != null) {
					bgDivWithColor = s.getContent().split(">", 2)[0] + '>';
				}
			}
			if(bgDivWithColor == null) {
				bgDivWithColor = "<div color=\"" + Utils.SG_BG_COLOR +"\">";
			}
			
			if(menuTitle == null) {//Creating new article only
				int sgId = sgManager.createSG(title, content.replaceFirst("<div>", bgDivWithColor), relSg.getParentId(), newPosition, user.getId());
				respond.put("id", sgId);
			} else {//Create a menu and then place article inside
				int parentId = sgManager.createSG(menuTitle, null, relSg.getParentId(), newPosition, user.getId());
				int sgId =sgManager.createSG(title, content.replaceFirst("<div>", bgDivWithColor), parentId, 0, user.getId());
				respond.put("id", sgId);
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
					} else if(src.toLowerCase().contains("wcimage_")) {
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
	
	@RequestMapping(value = "/update_sg")
    public ResponseEntity<Map<String, Object>> updateSg(@RequestBody Map<String, Object> request) {
		Map<String, Object> respond = new HashMap<String, Object>();
		try{
			User user = userManager.validateAccessToken(request);
			int userId = user.getId();
			if (!user.getUsername().endsWith("@wpi.edu")) {
				throw new IllegalStateException(ErrorMessage.SG_INVALID_EMAIL.getMsg());
			}
			if (!user.getEmailConfirmed()) {
				throw new IllegalStateException(ErrorMessage.EMAIL_NOT_CONFIRMED.getMsg());
			}
			
			int sgId = (int)request.get("id");
			String title = (String)request.get("title");
			String content = (String)request.get("content");
			int relId;
			boolean placeAfter = false;
			try {
				relId = (int)request.get("relId");
				placeAfter = (boolean)request.get("placeAfter");
			} catch (Exception e) {
				relId = Util.nullInt;
			}
			
			if (content != null) {
				content = saveImagesForSGBody(content, userId);
			}

			SurvivalGuide currentSg = sgManager.getArticleById(sgId);
			if (relId == Util.nullInt) {//If no location change, update title and content only
				/*System.out.println("No move, content update only");*/
				sgManager.softUpdateSG(currentSg.getId(), title, content, Util.nullInt, Util.nullInt, userId);
			} else {//Otherwise, do location and/or theme color changes
				List<SurvivalGuide> siblingList = sgManager.getChildArticles(currentSg.getParentId(), false);
				if (siblingList.size() == 0) {
					throw new IllegalStateException(ErrorMessage.INTERNAL_LOGIC_ERROR.getMsg());
				} else {
					boolean needFullUpdate = false;
					String bgDivWithColor = null;
					if (siblingList.size() == 1) {
						//The current sg is the only one in parent. The parent needs to be deleted.
						//Pretending that we need to move the parent to that location. Replacing parameters.
						SurvivalGuide parentSg = sgManager.getArticleById(currentSg.getParentId());
						siblingList = sgManager.getChildArticles(parentSg.getParentId(), true);
						currentSg.setParentId(parentSg.getParentId());
						currentSg.setPosition(parentSg.getPosition());
						sgManager.deleteSg(parentSg.getId());
						needFullUpdate = true;
					}
					
					
					final int finalRelId = relId;
					List<SurvivalGuide> sameDirSg = siblingList.stream()
						    .filter(s -> s.getId() == finalRelId).collect(Collectors.toList());
					if (sameDirSg.size() == 1) {
						/*System.out.println("Move to SAME DIRECTORY without removing parent");*/
						int newPosition = sameDirSg.get(0).getPosition();
						if (newPosition < currentSg.getPosition()) {//Moving up the sg in the directory
							if (placeAfter) {newPosition += 1;}
							for (SurvivalGuide s : siblingList) {
								if (s.getPosition() >= newPosition && s.getPosition() < currentSg.getPosition()) {
									updateSGLocation(s.getId(), s.getPosition() + 1);
								}
								if(bgDivWithColor == null && s.getContent() != null) {
									bgDivWithColor = s.getContent().split(">", 2)[0] + '>';
								}
							}
						} else if (newPosition > currentSg.getPosition()) {//Moving down the sg in the directory
							if (!placeAfter) {newPosition -= 1;}
							for (SurvivalGuide s : siblingList) {
								if (s.getPosition() > currentSg.getPosition() && s.getPosition() <= newPosition) {
									updateSGLocation(s.getId(), s.getPosition() - 1);
								}
								if(bgDivWithColor == null && s.getContent() != null) {
									bgDivWithColor = s.getContent().split(">", 2)[0] + '>';
								}
							}
						}
						if(bgDivWithColor == null) {
							bgDivWithColor = "<div color=\"" + Utils.SG_BG_COLOR +"\">";
						}
						//Doing a soft update because parent will not be updated anyway
						if (needFullUpdate) {
							String newContent = content == null ? currentSg.getContent() : content;
							newContent = bgDivWithColor + newContent.split(">", 2)[1];
							String newTitle = title == null ? currentSg.getTitle() : title;
							sgManager.updateSG(currentSg.getId(), newTitle, newContent, currentSg.getParentId(), newPosition, userId);
						} else {
							sgManager.softUpdateSG(currentSg.getId(), title, content, Util.nullInt, newPosition, userId);
						}
					} else {
						/*System.out.println("Move to DIFF DIRECTORY without removing parent");*/
						for (SurvivalGuide s : siblingList) {
							if (s.getPosition() > currentSg.getPosition()) {
								updateSGLocation(s.getId(), s.getPosition() - 1);
							}
						}
						SurvivalGuide relSg = sgManager.getArticleById(relId);
						List<SurvivalGuide> relSiblingList = sgManager.getChildArticles(relSg.getParentId(), true);
						boolean movingDown = false;
						int newPosition = 0;
						for (SurvivalGuide s : relSiblingList) {
							if (movingDown) {
								updateSGLocation(s.getId(), s.getPosition() + 1);
							} else if (s.getId() == relId) {
								movingDown = true;
								if (placeAfter) {
									newPosition = s.getPosition() + 1;
								} else {
									newPosition = s.getPosition();
									updateSGLocation(s.getId(), s.getPosition() + 1);
								}
							}
							if(bgDivWithColor == null && s.getContent() != null) {
								bgDivWithColor = s.getContent().split(">", 2)[0] + '>';
							}
						}
						if(bgDivWithColor == null) {
							bgDivWithColor = "<div color=\"" + Utils.SG_BG_COLOR +"\">";
						}
						String newContent = content == null ? currentSg.getContent() : content;
						newContent = bgDivWithColor + newContent.split(">", 2)[1];
						String newTitle = title == null ? currentSg.getTitle() : title;
						//A full update is performed here since parent id may be updated to null, and soft update will skip it.
						sgManager.updateSG(currentSg.getId(), newTitle, newContent, relSg.getParentId(), newPosition, userId);
						
					}
					
				}
			}
			
			if (user.isTokenUpdated()) {
				respond.put("accessToken", user.getAccessToken());
			}
			respond.put("error", "");
		} catch (Exception e) {
			respond = errorManager.createErrorRespondFromException(e, Utils.rootDir + "/update_sg", request);
		}
		return new ResponseEntity<Map<String, Object>>(respond, HttpStatus.OK);
	}
	
	private void updateSGLocation (int id, int newLoc) {
		//This makes sure that location only changes are not logged as an actual change, with no updates on owner id or created date
		sgManager.softUpdateSG(id, null, null, Util.nullInt, newLoc, Util.nullInt);
	}

}
