package com.fmning.wcservice.controller.rest;

import java.io.StringReader;
import java.time.Instant;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import com.fmning.service.domain.Event;
import com.fmning.service.domain.Feed;
import com.fmning.service.domain.User;
import com.fmning.service.domain.UserDetail;
import com.fmning.service.exceptions.NotFoundException;
import com.fmning.service.manager.EventManager;
import com.fmning.service.manager.FeedManager;
import com.fmning.service.manager.ImageManager;
import com.fmning.service.manager.UserManager;
import com.fmning.util.ErrorMessage;
import com.fmning.util.EventType;
import com.fmning.util.ImageType;
import com.fmning.util.Util;

@Controller
public class FeedController {
	
	@Autowired private UserManager userManager;
	@Autowired private FeedManager feedManager;
	@Autowired private ImageManager imageManager;
	@Autowired private EventManager eventManager;
	
	@RequestMapping(value = "/get_recent_feeds", method = RequestMethod.GET)
    public ResponseEntity<Map<String, Object>> getRecentFeeds(HttpServletRequest request) {
		Map<String, Object> respond = new HashMap<String, Object>();
		try{
			//Thread.sleep(500);
			int limit = 10;
			try{
				limit = Integer.parseInt(request.getParameter("limit"));
				limit = limit > 100 ? 100 : limit;
			}catch(NumberFormatException e){}
			
			Instant checkPoint = Instant.now();
			try{
				String timeStr = request.getParameter("checkPoint");
				if(timeStr != null) {
					checkPoint = Instant.parse(timeStr);
				}
			}catch(DateTimeParseException e){}
			
			List<Feed> feedList = feedManager.getRecentFeedByDate(checkPoint, limit);
			List<Map<String, Object>> processedFeedList = new ArrayList<Map<String, Object>>();
			
			for(Feed m : feedList){
				Map<String, Object> processedFeed = new HashMap<String, Object>();
				processedFeed.put("id", m.getId());
				processedFeed.put("title", m.getTitle());
				processedFeed.put("type", m.getType());
				//processedFeed.put("body", m.getBody());
				processedFeed.put("ownerId", m.getOwnerId());
				processedFeed.put("ownerName", userManager.getUserDisplayedName(m.getOwnerId()));
				processedFeed.put("createdAt", m.getCreatedAt().toString());
				try {
					int imgId = imageManager.getImageByTypeAndMapping("FeedCover", m.getId()).getId();
					processedFeed.put("coverImgId", imgId);
				}catch(Exception e) {}
				
				try {
					int avatarId = imageManager.getTypeUniqueImage("Avatar", m.getOwnerId()).getId();
					processedFeed.put("avatarId", avatarId);
				}catch(Exception e) {}
				
				processedFeedList.add(processedFeed);
			}
			respond.put("feedList", processedFeedList);
			respond.put("checkPoint", feedList.get(feedList.size() - 1).getCreatedAt().toString());
			respond.put("error", "");
		}catch(Exception e){
			respond = Util.createErrorRespondFromException(e);
		}
		return new ResponseEntity<Map<String, Object>>(respond, HttpStatus.OK);
	}
	
	@RequestMapping(value = "/get_feed", method = RequestMethod.GET)
    public ResponseEntity<Map<String, Object>> getRecentFeedsForUser(HttpServletRequest request) {
		Map<String, Object> respond = new HashMap<String, Object>();
		try{
			
			int feedId = Integer.parseInt(request.getParameter("id"));
			
			Feed feed = feedManager.getFeedById(feedId);
			
			respond.put("id", feed.getId());
			respond.put("title", feed.getTitle());
			respond.put("type", feed.getType());
			respond.put("body", feed.getBody());
			respond.put("ownerId", feed.getOwnerId());
			respond.put("createdAt", feed.getCreatedAt().toString());
			
			try {
				Event event = eventManager.getEventByType(EventType.FEED.getName(), feed.getId());
				Map<String, Object> values = new HashMap<String, Object>();
				values.put("id", event.getId());
				values.put("title", event.getTitle());
				values.put("description", event.getDescription());
				values.put("startTime",event.getStartTime().toString());
				values.put("endTime", event.getEndTime().toString());
				values.put("location", event.getLocation());
				values.put("ownerId",event.getOwnerId());
				values.put("createdAt",event.getCreatedAt().toString());
				if(event.getFee() != Util.nullInt) {
					values.put("fee", event.getFee());
				}
				
				respond.put("event", values);
			} catch (NotFoundException e) {}
			
			respond.put("error", "");
		}catch(Exception e){
			respond = Util.createErrorRespondFromException(e);
		}
		return new ResponseEntity<Map<String, Object>>(respond, HttpStatus.OK);
	}
	

	@RequestMapping("/create_feed")
    public ResponseEntity<Map<String, Object>> creatFeed(@RequestBody Map<String, Object> request) {
		Map<String, Object> respond = new HashMap<String, Object>();
		try{
			User user = userManager.validateAccessToken(request);
			int userId = user.getId();
			
			String title = (String)request.get("title");
			String type = (String)request.get("type");
			String body = (String)request.get("body");
			String coverImageString = (String)request.get("coverImage");

			if (title == null || type == null || body == null) {
				throw new IllegalStateException(ErrorMessage.INVALID_FEED_INPUT.getMsg());
			}
			
			//body = "<img src=\"file:///Attachment.png\" alt=\"Attachment.png\">wtf<img src=\"file:///Attachment123.png\">";
			Matcher imgMatcher = Pattern.compile("<img.*?>").matcher(body);
			while (imgMatcher.find()) {
				String imgTag = imgMatcher.group();
				
				Matcher srcMatcher = Pattern.compile("src=\".*?\"").matcher(imgTag);
				
				if (srcMatcher.find()) {
					String base64 = srcMatcher.group().replace("src=", "").replace("\"", "");
					try {
						int imgId = imageManager.createImage(base64, ImageType.FEED.getName(), Util.nullInt, userId, null);
						//int imgId = 33;
						body = body.replace(base64, "WCImage_" + Integer.toString(imgId));
					} catch (Exception e) {
						body = body.replace(imgTag, "");
					}
				} else {
					body = body.replace(imgTag, "");
				}
			}
			
			int feedId = feedManager.createFeed(title, type, body, userId);
			if (coverImageString != null) {
				imageManager.createImage(coverImageString, ImageType.FEED_COVER.getName(), feedId, userId, null);
			}
			
			respond.put("error", "");
			if (user.isTokenUpdated()) {
				respond.put("accessToken", user.getAccessToken());
			}
		}catch(Exception e){
			respond = Util.createErrorRespondFromException(e);
		}
		
		return new ResponseEntity<Map<String, Object>>(respond, HttpStatus.OK);
	}

}
