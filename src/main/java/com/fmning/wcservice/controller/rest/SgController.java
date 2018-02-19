package com.fmning.wcservice.controller.rest;

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

import com.fmning.service.domain.SurvivalGuide;
import com.fmning.service.manager.ErrorManager;
import com.fmning.service.manager.SGManager;
import com.fmning.service.manager.UserManager;

@Controller
public class SgController {
	
	@Autowired private UserManager userManager;
	@Autowired private SGManager sgManager;
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

}
