package com.fmning.wcservice.controller.rest;

import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Date;
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

import com.fmning.service.domain.Event;
import com.fmning.service.domain.Feed;
import com.fmning.service.domain.User;
import com.fmning.service.exceptions.NotFoundException;
import com.fmning.service.manager.ErrorManager;
import com.fmning.service.manager.EventManager;
import com.fmning.service.manager.FeedManager;
import com.fmning.service.manager.ImageManager;
import com.fmning.service.manager.TicketManager;
import com.fmning.service.manager.UserManager;
import com.fmning.util.ErrorMessage;
import com.fmning.util.EventType;
import com.fmning.util.FeedType;
import com.fmning.util.ImageType;
import com.fmning.util.Util;
import com.fmning.wcservice.utils.UserRole;
import com.fmning.wcservice.utils.Utils;

@Controller
public class FeedController {
	
	@Autowired private UserManager userManager;
	@Autowired private FeedManager feedManager;
	@Autowired private ImageManager imageManager;
	@Autowired private EventManager eventManager;
	@Autowired private ErrorManager errorManager;
	@Autowired private TicketManager ticketManager;
	
	public static int totalFeeds = Util.nullInt;
	
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
			
			String pageIndex = request.getParameter("page");
			if (pageIndex == null) {
				Instant checkPoint = Instant.now();
				try{
					String timeStr = request.getParameter("checkPoint");
					if(timeStr != null) {
						checkPoint = Instant.parse(timeStr);
					}
				}catch(DateTimeParseException e){}
				
				List<Feed> feedList = feedManager.getRecentFeedByDate(checkPoint, limit);
				
				respond.put("feedList", processFeedList(feedList, false));
				respond.put("checkPoint", feedList.get(feedList.size() - 1).getCreatedAt().toString());
			} else {
				List<Feed> feedList = feedManager.getRecentFeedByPageIndex(Integer.parseInt(pageIndex), limit);
				if (totalFeeds == Util.nullInt) {
					totalFeeds = feedManager.getFeedCount();
				}
				respond.put("feedList", processFeedList(feedList, true));
				respond.put("pageCount", (int) Math.ceil((double) FeedController.totalFeeds / 10));
			}
			respond.put("error", "");
		}catch(Exception e){
			respond = errorManager.createErrorRespondFromException(e, request);
		}
		return new ResponseEntity<Map<String, Object>>(respond, HttpStatus.OK);
	}
	
	@RequestMapping(value = "/search_feed", method = RequestMethod.GET)
    public ResponseEntity<Map<String, Object>> searchFeeds(HttpServletRequest request) {
		Map<String, Object> respond = new HashMap<String, Object>();
		try{
			String type = request.getParameter("type");
			String keyword = request.getParameter("keyword");
			boolean sendBody = request.getParameter("web") != null;
			
			List<Feed> feedList;
			if (type == null && keyword == null) {
				feedList = feedManager.getRecentFeedByDate(Instant.now(), 10);
			} else {
				feedList = feedManager.searchFeed(type, keyword);
			}
			respond.put("feedList", processFeedList(feedList, sendBody));
			respond.put("error", "");
		}catch(Exception e){
			respond = errorManager.createErrorRespondFromException(e, request);
		}
		return new ResponseEntity<Map<String, Object>>(respond, HttpStatus.OK);
	}
	
	private List<Map<String, Object>> processFeedList (List<Feed> feedList, boolean sendBody) {
		List<Map<String, Object>> processedFeedList = new ArrayList<Map<String, Object>>();
		for(Feed m : feedList){
			Map<String, Object> processedFeed = new HashMap<String, Object>();
			processedFeed.put("id", m.getId());
			processedFeed.put("title", m.getTitle());
			processedFeed.put("type", m.getType());
			if (sendBody) processedFeed.put("body", m.getBody());
			processedFeed.put("ownerId", m.getOwnerId());
			processedFeed.put("ownerName", userManager.getUserDisplayedName(m.getOwnerId()));
			processedFeed.put("createdAt", m.getCreatedAt().toString());
			try {
				int imgId = imageManager.getImageByTypeAndMapping(ImageType.FEED_COVER.getName(), m.getId()).getId();
				processedFeed.put("coverImgId", imgId);
			}catch(Exception e) {}
			
			try {
				int avatarId = imageManager.getTypeUniqueImage(ImageType.AVATAR.getName(), m.getOwnerId()).getId();
				processedFeed.put("avatarId", avatarId);
			}catch(Exception e) {}
			
			processedFeedList.add(processedFeed);
		}
		return processedFeedList;
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
				values.put("active", event.getActive());
				if(event.getFee() != Util.nullInt) {
					values.put("fee", event.getFee());
				}
				
				respond.put("event", values);
			} catch (NotFoundException e) {}
			
			respond.put("error", "");
		}catch(Exception e){
			respond = errorManager.createErrorRespondFromException(e, request);
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
			
			if (type.equals(FeedType.EVENT.getName())) {
				if (UserRole.isAdmin(user.getRoleId())) {
					userId = Utils.CSA_ID;
				} else {
					throw new IllegalStateException(ErrorMessage.FEED_NO_PERMISSION.getMsg());
				}
			}
			
			body = saveImagesForFeedBody(body, userId);
			
			int feedId = 0;
			if (type.equals(FeedType.EVENT.getName())) {
				feedId = feedManager.createFeed(title, type, body, userId, user.getId());
			} else {
				feedId = feedManager.createFeed(title, type, body, userId);
			}
			if (totalFeeds == Util.nullInt) {
				totalFeeds = feedManager.getFeedCount();
			} else {
				totalFeeds += 1;
			}
			if (coverImageString != null) {
				imageManager.createImage(coverImageString, ImageType.FEED_COVER.getName(), feedId, userId, null);
			}
			
			//Check if there is an event. If so, save it after saving feeds
			String eventTitle = (String)request.get("eventTitle");
			if (eventTitle != null) {
				if(!UserRole.isAdmin(user.getRoleId())) {
					throw new IllegalStateException(ErrorMessage.EVENT_NO_PERMISSION.getMsg());
				}
				String eventDesc = (String)request.get("eventDesc");
				String startTime = (String)request.get("eventStartTime");
				String endTime = (String)request.get("eventEndTime");
				String eventLocation = (String)request.get("eventLocation");
				
				if (eventDesc == null || startTime == null || endTime == null || eventLocation == null) {
					throw new IllegalStateException(ErrorMessage.EVENT_NOT_CREATED.getMsg());
				}
				
				//Check if ticket is available for this event or not
				String ticketBgImage = (String)request.get("ticketBgImage");
				if (ticketBgImage != null) {
					// Adding full event with ticket design
					try {
						String ticketThumbImage = (String)request.get("ticketThumbImage");
						boolean ticketActive = (boolean)request.get("ticketActive");
						int ticketBalance = (int)request.get("ticketBalance");
						
						double ticketFee = 0;
						try{
							ticketFee = (double)request.get("ticketFee");
						}catch(ClassCastException e){
							ticketFee =  (int)request.get("ticketFee");
						}
						
						//Creating ticket template
						String location = "/Volumes/Data/passTemplates/";
						String folderName = eventTitle.replaceAll("\\s+","");
						folderName = folderName.length() < 20 ? folderName : folderName.substring(0, 20);
						folderName += new SimpleDateFormat("yyyyMMddss").format(new Date());
						location += folderName;
						
						int templateId = ticketManager.createTicketTemplate(location, "WPI CSA Event", "WPI CSA", null, userId);
						try {
							TicketController.createTicketTemplate(ticketBgImage, ticketThumbImage, folderName);
						} catch (IOException e) {
							errorManager.logError(e, Utils.rootDir + "/create_feed", request);
						}
						
						eventManager.createEvent(EventType.FEED.getName(), feedId, eventTitle, eventDesc, Instant.parse(startTime),
								Instant.parse(endTime), eventLocation, ticketFee, userId, templateId, ticketActive,
								"", ticketBalance);
						
						
					} catch (NullPointerException | ClassCastException | NumberFormatException e) {
						e.printStackTrace();
						throw new IllegalStateException(ErrorMessage.EVENT_NOT_CREATED.getMsg());
					}
					
				} else {
					// Adding calendar only event with default fee and no ticket design
					eventManager.createEvent(EventType.FEED.getName(), feedId, eventTitle, eventDesc, Instant.parse(startTime),
							Instant.parse(endTime), eventLocation, 0, userId, Util.nullInt, false,
							"", 0);
				}
				
			}
			
			respond.put("error", "");
			if (user.isTokenUpdated()) {
				respond.put("accessToken", user.getAccessToken());
			}
		}catch(Exception e){
			respond = errorManager.createErrorRespondFromException(e, Utils.rootDir + "/create_feed", request);
		}
		
		return new ResponseEntity<Map<String, Object>>(respond, HttpStatus.OK);
	}
	
	private String saveImagesForFeedBody(String body, int userId) {
		Matcher imgMatcher = Pattern.compile("<img.*?>").matcher(body);
		while (imgMatcher.find()) {
			String imgTag = imgMatcher.group();
			
			Matcher srcMatcher = Pattern.compile("src=\".*?\"").matcher(imgTag);
			
			if (srcMatcher.find()) {
				try {
					String src = srcMatcher.group().replace("src=", "").replace("\"", "");
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
					body = body.replace(imgTag, "<img src=\"WCImage_" + Integer.toString(imgId) + "\" " + dimension + " />");
				} catch (Exception e) {
					body = body.replace(imgTag, "");
				}
				
			} else {
				body = body.replace(imgTag, "");
			}
		}
		
		return body;
	}
	
	
	
	@RequestMapping(value = "/update_feed")
    public ResponseEntity<Map<String, Object>> updateFeed(@RequestBody Map<String, Object> request) {
		Map<String, Object> respond = new HashMap<String, Object>();
		try{
			User user = userManager.validateAccessToken(request);
			int userId = user.getId();
			
			int feedId = (int)request.get("feedId");
			String title = (String)request.get("title");
			String body = (String)request.get("body");
			String coverImageString = (String)request.get("coverImage");
			
			Feed feed = feedManager.getFeedById(feedId);
			
			if (body != null) {
				body = saveImagesForFeedBody(body, userId);
			}
			
			if (feed.getOwnerId() != userId) {
				if (UserRole.isAdmin(user.getRoleId())) {
					feedManager.softUpdateFeed(feedId, title, body, null, userId);
				} else {
					throw new IllegalStateException(ErrorMessage.NO_PERMISSION.getMsg());
				}
			} else {
				feedManager.softUpdateFeed(feedId, title, body, null, userId);
			}
			
			if (coverImageString != null) {
				imageManager.saveTypeUniqueImage(coverImageString, ImageType.FEED_COVER.getName(), feedId, userId, null);
			}
			
			if (user.isTokenUpdated()) {
				respond.put("accessToken", user.getAccessToken());
			}
			respond.put("error", "");
		} catch (Exception e) {
			respond = errorManager.createErrorRespondFromException(e, Utils.rootDir + "/delete_feed", request);
		}
		return new ResponseEntity<Map<String, Object>>(respond, HttpStatus.OK);
	}
	
	@RequestMapping(value = "/delete_feed")
    public ResponseEntity<Map<String, Object>> deleteFeed(@RequestBody Map<String, Object> request) {
		Map<String, Object> respond = new HashMap<String, Object>();
		try{
			User user = userManager.validateAccessToken(request);
			
			Feed feed = feedManager.getFeedById((int)request.get("feedId"));
			
			if (feed.getOwnerId() == user.getId()) {
				feedManager.softDeleteFeed(feed.getId(), user.getId());
			} else if (UserRole.isAdmin(user.getRoleId())) {
				feedManager.softDeleteFeed(feed.getId(), feed.getOwnerId(), user.getId());
			} else {
				throw new IllegalStateException(ErrorMessage.UNAUTHORIZED_FEED_DELETE.getMsg());
			}
			
			if (user.isTokenUpdated()) {
				respond.put("accessToken", user.getAccessToken());
			}
			respond.put("error", "");
		} catch (Exception e) {
			respond = errorManager.createErrorRespondFromException(e, Utils.rootDir + "/delete_feed", request);
		}
		return new ResponseEntity<Map<String, Object>>(respond, HttpStatus.OK);
	}

}
