package com.fmning.wcservice.controller;

import java.time.Instant;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.fmning.service.domain.Feed;
import com.fmning.service.manager.FeedManager;
import com.fmning.service.manager.ImageManager;
import com.fmning.service.manager.UserManager;
import com.fmning.util.Util;

@Controller
public class FeedController {
	
	@Autowired private UserManager userManager;
	@Autowired private FeedManager feedManager;
	@Autowired private ImageManager imageManager;
	
	@RequestMapping(value = "/get_recent_feeds", method = RequestMethod.GET)
    public ResponseEntity<Map<String, Object>> getRecentFeedsForUser(HttpServletRequest request) {
		Map<String, Object> respond = new HashMap<String, Object>();
		try{
			Thread.sleep(500);
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
				processedFeed.put("feedId", m.getId());
				processedFeed.put("feedTitle", m.getTitle());
				processedFeed.put("feedType", m.getType());
				processedFeed.put("feedBody", m.getBody());
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

}
