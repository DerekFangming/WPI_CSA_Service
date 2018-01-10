package com.fmning.wcservice.controller.rest;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.fmning.service.dao.SgDao;
import com.fmning.service.dao.WcAppVersionDao;
import com.fmning.service.dao.WcArticleDao;
import com.fmning.service.dao.WcReportDao;
import com.fmning.service.dao.impl.CoreTableType;
import com.fmning.service.dao.impl.QueryBuilder;
import com.fmning.service.dao.impl.QueryTerm;
import com.fmning.service.dao.impl.QueryType;
import com.fmning.service.dao.impl.RelationalOpType;
import com.fmning.service.dao.impl.ResultsOrderType;
import com.fmning.service.domain.Sg;
import com.fmning.service.domain.WcAppVersion;
import com.fmning.service.domain.WcArticle;
import com.fmning.service.domain.WcReport;
import com.fmning.service.exceptions.NotFoundException;
import com.fmning.service.manager.HelperManager;
import com.fmning.service.manager.UserManager;
import com.fmning.util.Util;

@CrossOrigin(origins = "*", maxAge = 3600)
@Controller
public class SgMobileController {
	@Autowired private SgDao sgDao;
	@Autowired private WcReportDao wcReportDao;
	@Autowired private WcArticleDao wcArticleDao;
	@Autowired private WcAppVersionDao wcAppVersionDao;
	@Autowired private UserManager userManager;
	@Autowired private HelperManager helperManager;
	
	
	@RequestMapping(value = "/get_sg", method = RequestMethod.GET)
	public ResponseEntity<Map<String, Object>> getSg(HttpServletRequest request) {
		Map<String, Object> respond = new HashMap<String, Object>();
		try{
			int menuId = Integer.parseInt(request.getParameter("menuId"));
			
			QueryBuilder qb = QueryType.getQueryBuilder(CoreTableType.SG, QueryType.FIND);
		    qb.addFirstQueryExpression(new QueryTerm(SgDao.Field.MENU_ID.name, menuId));
		    qb.setOrdering(SgDao.Field.CREATED_AT.name, ResultsOrderType.DESCENDING);
		    qb.setLimit(1);
		    Sg sg = sgDao.findAllObjects(qb.createQuery()).get(0);
		    respond.put("title", sg.getTitle());
		    respond.put("content", sg.getContent());
		    respond.put("error", "");
		}catch(NumberFormatException e){
			respond.put("error", "Incorrect request format. Please use menuId as key and put number only as value");
		}catch(NotFoundException e){
			respond.put("error", "Article does not exist");
		}catch(Exception e){
			respond.put("error", e.getStackTrace());
		}
		return new ResponseEntity<Map<String, Object>>(respond, HttpStatus.OK);
	}
	
	
	@RequestMapping(value = "/add_sg", method = RequestMethod.POST)
    public ResponseEntity<Map<String, Object>> addSg(@RequestBody Map<String, Object> request) {
		Map<String, Object> respond = new HashMap<String, Object>();
		try{
			int menuId = (Integer)request.get("menuId");
			String title = (String)request.get("title");
			String content = (String)request.get("content");
			
			if (title == null || content == null) throw new IllegalStateException("Need a valid title and content");
			
			Sg sg = new Sg();
			sg.setMenuId(menuId);
			sg.setTitle(title);
			sg.setContent(content);
			sg.setCreatedAt(Instant.now());
			sgDao.persist(sg);
			respond.put("error", "");
		}catch(IllegalStateException e){
			respond.put("error", e.getMessage());
		}catch(Exception e){
			respond.put("error", e.getMessage());
		}
	
		return new ResponseEntity<Map<String, Object>>(respond, HttpStatus.OK);
		
	}
	
	@RequestMapping(value = "/get_version_info", method = RequestMethod.GET)
	public ResponseEntity<Map<String, Object>> getVersionInfo(HttpServletRequest request) {
		Map<String, Object> respond = new HashMap<String, Object>();
		try{
			String versionNo = request.getParameter("version");
			
			List<QueryTerm> values = new ArrayList<QueryTerm>();
			values.add(WcAppVersionDao.Field.APP_VERSION.getQueryTerm(versionNo));
			WcAppVersion version = wcAppVersionDao.findObject(values);
			if(version.getStatus().equals("OK")){
				respond.put("status", "OK");
		    	respond.put("error", "");
			}else if (version.getStatus().equals("AU")){
				respond.put("status", "AU");
				respond.put("title", version.getTitle());
				respond.put("message", version.getMessage());
				respond.put("error", "");
			}else{
				QueryBuilder qb = QueryType.getQueryBuilder(CoreTableType.WC_APP_VERSIONS, QueryType.FIND);
			    
			    qb.addFirstQueryExpression(new QueryTerm(WcAppVersionDao.Field.APP_VERSION.name, 
			    		RelationalOpType.GE, versionNo));
			    qb.setOrdering(WcAppVersionDao.Field.APP_VERSION.name, ResultsOrderType.ASCENDING);
			    List<WcAppVersion> versionList = wcAppVersionDao.findAllObjects(qb.createQuery());
			    
			    int versionSize = versionList.size();
				if(versionSize < 2){
					respond.put("error", "Internal server error. Please contact admin@fmning.com for help");
				}else{
					String updates = "";
				    for(WcAppVersion av : versionList){
				    	if(av.getUpdates() != null){
				    		updates += av.getUpdates();
				    	}
				    }
					WcAppVersion verCheck = versionList.get(versionSize - 2);
					if(verCheck.getStatus().equals("BM")){
						respond.put("status", "BM");
						respond.put("title", verCheck.getTitle());
						respond.put("message", verCheck.getMessage());
					}else{
						respond.put("status", "CU");
					}
					respond.put("newVersion", versionList.get(versionSize - 1).getAppVersion());
					respond.put("updates", updates);
					respond.put("error", "");
				}
			}
		}catch(NotFoundException e){
			respond.put("error", "Version does not exist");
		}catch(Exception e){
			respond.put("error", "Unknown error");
		}
		return new ResponseEntity<Map<String, Object>>(respond, HttpStatus.OK);
	}
	
	@RequestMapping(value = "/create_sg_report", method = RequestMethod.POST)
    public ResponseEntity<Map<String, Object>> addSgReport(@RequestBody Map<String, Object> request) {
		Map<String, Object> respond = new HashMap<String, Object>();
		try{
			int menuId = (int)request.get("menuId");
			int userId = request.get("userId") == null ? Util.nullInt : (int)request.get("userId");
			String email = (String)request.get("email");
			String report = (String)request.get("report");
			
			if (email == null || report == null) throw new IllegalStateException("Need a valid email or report");
			if (email.length()>50 || report.length()>500) throw new IllegalStateException("Input too long");
			
			WcReport sgReport = new WcReport();
			sgReport.setUserId(userId);
			sgReport.setMenuId(menuId);
			sgReport.setEmail(email);
			sgReport.setReport(report);
			sgReport.setCreatedAt(Instant.now());
			wcReportDao.persist(sgReport);
			String emailList = "fning@wpi.edu,sxie@wpi.edu,ysong5@wpi.edu";
			String emailContent = "Sender: ";
			emailContent += email == null ? "Anonymity" : email;
			emailContent += "\nReport: " + report;
			emailContent += "\n\n\n\n\nPlease reply this email to unsubscribe";
			helperManager.sendEmail("admin@fmning.com", emailList, "WPI CSA app user report", emailContent);
			respond.put("error", "");
		}catch(IllegalStateException e){
			respond.put("error", e.getMessage());
		}catch(Exception e){
			respond.put("error", e.getMessage());
		}
	
		return new ResponseEntity<Map<String, Object>>(respond, HttpStatus.OK);
	}
	
	@RequestMapping(value = "/create_sg_article", method = RequestMethod.POST)
    public ResponseEntity<Map<String, Object>> addSgArticle(@RequestBody Map<String, Object> request) {
		Map<String, Object> respond = new HashMap<String, Object>();
		try{
			int userId = userManager.validateAccessToken(request).getId();
			int menuId = (int)request.get("menuId");
			String title = (String)request.get("title");
			String article = (String)request.get("article");
			
			if (title == null || article == null) throw new IllegalStateException("Need a valid title and article");
			
			WcArticle sgArticle = new WcArticle();
			sgArticle.setUserId(userId);
			sgArticle.setMenuId(menuId);
			sgArticle.setTitle(title);
			sgArticle.setArticle(article);
			sgArticle.setCreatedAt(Instant.now());
			wcArticleDao.persist(sgArticle);
			respond.put("error", "");
		}catch(IllegalStateException e){
			respond.put("error", e.getMessage());
		}catch(Exception e){
			respond.put("error", e.getMessage());
		}
	
		return new ResponseEntity<Map<String, Object>>(respond, HttpStatus.OK);
	}

}

