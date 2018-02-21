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

import com.fmning.service.dao.WcAppVersionDao;
import com.fmning.service.dao.WcArticleDao;
import com.fmning.service.dao.WcReportDao;
import com.fmning.service.dao.impl.CoreTableType;
import com.fmning.service.dao.impl.QueryBuilder;
import com.fmning.service.dao.impl.QueryTerm;
import com.fmning.service.dao.impl.QueryType;
import com.fmning.service.dao.impl.RelationalOpType;
import com.fmning.service.dao.impl.ResultsOrderType;
import com.fmning.service.domain.User;
import com.fmning.service.domain.WcAppVersion;
import com.fmning.service.domain.WcArticle;
import com.fmning.service.domain.WcReport;
import com.fmning.service.exceptions.NotFoundException;
import com.fmning.service.manager.ErrorManager;
import com.fmning.service.manager.HelperManager;
import com.fmning.service.manager.UserManager;
import com.fmning.util.Util;
import com.fmning.wcservice.utils.Utils;

@CrossOrigin(origins = "*", maxAge = 3600)
@Controller
public class AboutController {
//	@Autowired private SgDao sgDao;
	@Autowired private WcReportDao wcReportDao;
	@Autowired private WcArticleDao wcArticleDao;
	@Autowired private WcAppVersionDao wcAppVersionDao;
	@Autowired private UserManager userManager;
	@Autowired private ErrorManager errorManager;
	@Autowired private HelperManager helperManager;
	
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
			errorManager.logError(e, request);
			respond.put("error", "Version does not exist");
		}catch(Exception e){
			errorManager.logError(e, request);
			respond.put("error", "Unknown error");
		}
		return new ResponseEntity<Map<String, Object>>(respond, HttpStatus.OK);
	}
	
	@RequestMapping(value = "/create_sg_report", method = RequestMethod.POST)
    public ResponseEntity<Map<String, Object>> addSgReport(@RequestBody Map<String, Object> request) {
		Map<String, Object> respond = new HashMap<String, Object>();
		try{
			int menuId = (int)request.get("menuId");
			String accessToken = (String)request.get("accessToken");
			String email = (String)request.get("email");
			String report = (String)request.get("report");
			int userId = Util.nullInt;
			
			if (email.length() > 50 || report.length() > 500)
				throw new IllegalStateException("Input too long");
			
			if (accessToken != null) {
				try {
					User user = userManager.validateAccessToken(accessToken);
					userId = user.getId();
					email = user.getUsername();
				} catch (Exception e) {}
			}
			
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
			if (Utils.prodMode) {
				try {
					helperManager.sendEmail("admin@fmning.com", emailList, "WPI CSA app user report", emailContent);
				} catch (Exception e) {
					errorManager.logError(e);
				}
			} else {
				System.out.println(emailContent);
			}
			respond.put("error", "");
		} catch (Exception e){
			respond = errorManager.createErrorRespondFromException(e, Utils.rootDir + "/create_sg_report", request);
		}
	
		return new ResponseEntity<Map<String, Object>>(respond, HttpStatus.OK);
	}
	
	@RequestMapping(value = "/create_sg_article", method = RequestMethod.POST)
    public ResponseEntity<Map<String, Object>> addSgArticle(@RequestBody Map<String, Object> request) {
		Map<String, Object> respond = new HashMap<String, Object>();
		try{
			User user = userManager.validateAccessToken(request);
			int userId = user.getId();
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
			if (user.isTokenUpdated()) {
				respond.put("accessToken", user.getAccessToken());
			}
		}catch(Exception e){
			respond = errorManager.createErrorRespondFromException(e, Utils.rootDir + "/create_sg_article", request);
		}
	
		return new ResponseEntity<Map<String, Object>>(respond, HttpStatus.OK);
	}

}

