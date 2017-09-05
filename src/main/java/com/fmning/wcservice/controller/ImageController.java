package com.fmning.wcservice.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.fmning.service.domain.Image;
import com.fmning.service.exceptions.NotFoundException;
import com.fmning.service.manager.ImageManager;
import com.fmning.service.manager.RelationshipManager;
import com.fmning.service.manager.UserManager;
import com.fmning.util.ErrorMessage;
import com.fmning.util.ImageType;
import com.fmning.util.RelationshipType;
import com.fmning.util.Util;

@Controller
public class ImageController {
	
	@Autowired private ImageManager imageManager;
	@Autowired private UserManager userManager;
	@Autowired private RelationshipManager relationshipManager;
	
	@RequestMapping("/create_image")
    public ResponseEntity<Map<String, Object>> uploadImage(@RequestBody Map<String, Object> request) {
		Map<String, Object> respond = new HashMap<String, Object>();
		try{
			int id = userManager.validateAccessToken(request);
			
			String title = "";
			try{
				title = (String)request.get("title");
			}catch(NullPointerException e){
				//
			}
			
			String verifiedType = "";
			int typeMappingId = 0;
			try{
				verifiedType = Util.verifyImageType((String)request.get("type"));
				typeMappingId = (int)request.get("typeMappingId");
			}catch(NullPointerException e){
				//
			}
			
			imageManager.saveImage((String)request.get("image"), verifiedType, typeMappingId, id, title);
			
			respond.put("error", "");
		}catch(Exception e){
			respond = Util.createErrorRespondFromException(e);
		}
		return new ResponseEntity<Map<String, Object>>(respond, HttpStatus.OK);
		
	}
	
	@RequestMapping("/download_image_by_id")
    public ResponseEntity<Map<String, Object>> downloadImageById(@RequestBody Map<String, Object> request) {
		Map<String, Object> respond = new HashMap<String, Object>();
		try{
			userManager.validateAccessToken(request);
			int imageId = (int)request.get("imageId");
			
			Image image = imageManager.retrieveImageById(imageId);
			
			respond.put("error", "");
			respond.put("createdAt", image.getCreatedAt().toString());
			respond.put("image", image.getImageData());
			respond.put("title", image.getTitle());
		}catch(Exception e){
			respond = Util.createErrorRespondFromException(e);
		}
		return new ResponseEntity<Map<String, Object>>(respond, HttpStatus.OK);
	}
	
	@RequestMapping("/delete_image")
    public ResponseEntity<Map<String, Object>> deleteImage(@RequestBody Map<String, Object> request) {
		Map<String, Object> respond = new HashMap<String, Object>();
		try{
			int id = userManager.validateAccessToken(request);
			
			int imageId = (int)request.get("imageId");
			imageManager.softDeleteImage(imageId, id);
			
			respond.put("error", "");
		}catch(Exception e){
			respond = Util.createErrorRespondFromException(e);
		}
		return new ResponseEntity<Map<String, Object>>(respond, HttpStatus.OK);
		
	}
	
	@RequestMapping("/get_image_ids_by_type")
	public ResponseEntity<Map<String, Object>> getImageIds(@RequestBody Map<String, Object> request) {
		Map<String, Object> respond = new HashMap<String, Object>();
		try{
			userManager.validateAccessToken(request);
			
			int userId = (int)request.get("userId");
			
			respond.put("idList", imageManager.getImageIdListByType((String)request.get("type"), userId));
			
			respond.put("error", "");
		}catch(Exception e){
			respond = Util.createErrorRespondFromException(e);
		}
		return new ResponseEntity<Map<String, Object>>(respond, HttpStatus.OK);
	}
	
	@RequestMapping("/get_singleton_img_by_type")
	public ResponseEntity<Map<String, Object>> getAvatar(@RequestBody Map<String, Object> request) {
		Map<String, Object> respond = new HashMap<String, Object>();
		try{
			userManager.validateAccessToken(request);
			
			int userId = (Integer)request.get("userId");
			String imgType = (String)request.get("imgType");
			int avatarId;
			try{
				avatarId = imageManager.getSingltonImageIdByType(imgType, userId);
			}catch(NotFoundException nfe){
				throw new NotFoundException(ErrorMessage.SINGLETON_IMG_NOT_FOUND.getMsg() + imgType.toLowerCase());
			}
			
			Image avatar = imageManager.retrieveImageById(avatarId);
			respond.put("image", avatar.getImageData());
			
			respond.put("error", "");
		}catch(Exception e){
			respond = Util.createErrorRespondFromException(e);
		}
		return new ResponseEntity<Map<String, Object>>(respond, HttpStatus.OK);
	}
	
	
	@RequestMapping(value = "/file", method = RequestMethod.GET)
	public void getFile(HttpServletResponse response) {
	    try {

	    	File file = new File("/Volumes/Data/images/1.jpg");
	        InputStream is = new FileInputStream(file);
	        org.apache.commons.io.IOUtils.copy(is, response.getOutputStream());
	      response.flushBuffer();
	    } catch (IOException ex) {
	      ex.printStackTrace();
	    }

	}
	
	@RequestMapping(value = "/get_image", method = RequestMethod.GET)
	public void getImage(HttpServletRequest request, HttpServletResponse response) {
		try{
			int imageId = Integer.parseInt(request.getParameter("id"));
			Image image = imageManager.retrieveImageById(imageId);
			File file = new File(image.getLocation());
			InputStream is = new FileInputStream(file);
	        org.apache.commons.io.IOUtils.copy(is, response.getOutputStream());
	        response.flushBuffer();
		} catch (Exception e) {
			//e.printStackTrace();
			try {
				response.setStatus(200);
				response.getWriter().write(e.getMessage());
				response.getWriter().flush();
				response.getWriter().close();
				
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}

	}



}
