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

import com.fmning.service.domain.Image;
import com.fmning.service.exceptions.NotFoundException;
import com.fmning.service.manager.ImageManager;
import com.fmning.service.manager.UserManager;
import com.fmning.util.ErrorMessage;
import com.fmning.util.Util;

@Controller
public class ImageController {
	
	@Autowired private ImageManager imageManager;
	@Autowired private UserManager userManager;
	
	
	
	@RequestMapping("/download_image_by_id")
    public ResponseEntity<Map<String, Object>> downloadImageById(@RequestBody Map<String, Object> request) {
		Map<String, Object> respond = new HashMap<String, Object>();
		try{
			userManager.validateAccessToken(request);
			int imageId = (int)request.get("imageId");
			
			Image image = imageManager.getImageById(imageId);
			
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
			int id = userManager.validateAccessToken(request).getId();
			
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
				avatarId = imageManager.getTypeUniqueImage(imgType, userId).getId();
			}catch(NotFoundException nfe){
				throw new NotFoundException(ErrorMessage.SINGLETON_IMG_NOT_FOUND.getMsg() + imgType.toLowerCase());
			}
			
			Image avatar = imageManager.getImageById(avatarId);
			respond.put("image", avatar.getImageData());
			
			respond.put("error", "");
		}catch(Exception e){
			respond = Util.createErrorRespondFromException(e);
		}
		return new ResponseEntity<Map<String, Object>>(respond, HttpStatus.OK);
	}
	
	//Following methods are used for CSA project
	@RequestMapping("/save_type_unique_image")
    public ResponseEntity<Map<String, Object>> uploadImage(@RequestBody Map<String, Object> request) {
		Map<String, Object> respond = new HashMap<String, Object>();
		try{
			int id = userManager.validateAccessToken(request).getId();
			
			String title = (String)request.get("title");
			String type = (String)request.get("type");
			int typeMappingId = Util.nullInt;
			/*if(type != null) { //Commented out for type unique image
				try{
					typeMappingId = (int)request.get("typeMappingId");
				}catch(NullPointerException e) {}
			}*/
			String base64 = (String)request.get("image");
			if(base64 == null)
				throw new IllegalStateException(ErrorMessage.INCORRECT_PARAM.getMsg());
			
			int imgId = imageManager.saveTypeUniqueImage(base64, type, typeMappingId, id, title);
			
			respond.put("imageId", imgId);
			respond.put("error", "");
		}catch(Exception e){
			respond = Util.createErrorRespondFromException(e);
		}
		return new ResponseEntity<Map<String, Object>>(respond, HttpStatus.OK);
		
	}
	
	@RequestMapping(value = "/get_image", method = RequestMethod.GET)
	public void getImage(HttpServletRequest request, HttpServletResponse response) {
		try{
			int imageId = Integer.parseInt(request.getParameter("id"));
			Image image = imageManager.getImageById(imageId);
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
