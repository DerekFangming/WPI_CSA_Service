package com.fmning.wcservice.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.fmning.util.Util;
import com.fmning.wcservice.utils.Utils;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.FileContent;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.Drive.Files.Create;
import com.google.api.services.drive.Drive.Files.Delete;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;

@Controller
public class TestController {
	
private String backupScriptPath;

	
	private HttpTransport httpTransport;
	private FileDataStoreFactory dataStoreFactory;
	private JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
	private static Drive drive;
	
	@RequestMapping(value = "/ttt", method = RequestMethod.GET)
	public ResponseEntity<String> testMethods(HttpServletRequest request) {
		
		ClassLoader classLoader = getClass().getClassLoader();
		backupScriptPath = classLoader.getResource("dbBackup.sh").getFile();

		try {
			Calendar calendar = Calendar.getInstance();
			int dayOfWeek = calendar.get(Calendar.DAY_OF_MONTH);
			
			Process process = Runtime.getRuntime().exec(backupScriptPath + " " 
														+ Utils.dbBackupFolder + " "
														+ Utils.dbBackupFilePrefix + dayOfWeek + " "
														+ Utils.dbBackupToolLocation + " "
														+ Utils.dbName + " "
														+ Utils.dbUsername);
			
			BufferedReader in = new BufferedReader(new InputStreamReader(process.getErrorStream()));
			String errors = "";
			String line;
			while ((line = in.readLine()) != null) {
			    errors += line + "\n";
			}
			
			int exitValue = process.waitFor();
			if (exitValue != 0)
				errors += "Execution of script failed!";
			
			if (errors.length() > 0){
				String report = "Error during backup of the database:\n\n";
				sendScheduleErrorReportEmail(report + errors);
			} else {
				try {
					httpTransport = GoogleNetHttpTransport.newTrustedTransport();
					dataStoreFactory = new FileDataStoreFactory(new java.io.File(Utils.credentialFolder));
					
					GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(jsonFactory,
							new InputStreamReader(TestController.class.getResourceAsStream("/client_secret.json")));
					GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
							httpTransport, jsonFactory, clientSecrets,
					        Collections.singleton(DriveScopes.DRIVE_FILE)).setDataStoreFactory(dataStoreFactory)
					        .build();
					
					Credential credential = new AuthorizationCodeInstalledApp(flow, new LocalServerReceiver())
							.authorize("user");
					drive = new Drive.Builder(httpTransport, jsonFactory, credential)
							.setApplicationName("WPI_CSA").build();
					
					FileList list = drive.files().list()
				    		  .setQ("name contains '" + Utils.dbBackupFilePrefix + dayOfWeek + "'")
				    		  .execute();

					for(File file: list.getFiles()) {
						Delete delete = drive.files().delete(file.getId());
					    delete.execute();
					}
					
					java.io.File UPLOAD_FILE = new java.io.File(Utils.dbBackupFolder + "/" + Utils.dbBackupFilePrefix + dayOfWeek + ".gz" );
					
					File fileMetadata = new File();
				    fileMetadata.setName(UPLOAD_FILE.getName());

				    FileContent mediaContent = new FileContent("application/gzip", UPLOAD_FILE);
				    Create insert = drive.files().create(fileMetadata, mediaContent);
				    //MediaHttpUploader uploader = insert.getMediaHttpUploader();
				    //uploader.setDirectUploadEnabled(useDirectUpload);
				    //uploader.setProgressListener(new FileUploadProgressListener());
				    insert.execute();
					
				} catch (Exception e) {
					String report = "Error during google drive backup of the database:\n\n";
					sendScheduleErrorReportEmail(report + e.getMessage());
				}
			}
			
		} catch (IOException | InterruptedException e) {
			String report = "Error during backup of the database:\n\n";
			sendScheduleErrorReportEmail(report + e.getMessage());
		}
		
		return new ResponseEntity<String>("ok", HttpStatus.OK);
	}
	
	@RequestMapping(value = "/get", method = RequestMethod.GET)
	public ResponseEntity<Map<String, Object>> getFeedPreviewImage(HttpServletRequest request) {
		Map<String, Object> respond = new HashMap<String, Object>();
		try{
			String a = (String)request.getParameter("haha");
			respond.put("haha", a);
		}catch(Exception e){
			respond = Util.createErrorRespondFromException(e);
		}

		
		return new ResponseEntity<Map<String, Object>>(respond, HttpStatus.OK);
	}
	
	@RequestMapping(value="/build", method = RequestMethod.GET)
	public String goToBuilder(HttpServletRequest request, ModelMap model){
		model.addAttribute("message", "Hello Spring MVC Framework!");
		return "emailConfirm";
	}
	
	@RequestMapping("/test")
	public ResponseEntity<String> test(@RequestBody Map<String, Object> request) {
	
		
		
		
		
		
		return new ResponseEntity<String>("", HttpStatus.OK);
	}
	
	@RequestMapping(value = "/files", method = RequestMethod.GET)
	public void getFile(){
		DateTimeFormatter formatter =
				  DateTimeFormatter.ofPattern("yyMMddHHmmss").withZone(ZoneId.systemDefault());
		System.out.println(formatter.format(Instant.now()));
	}
	
	@RequestMapping(value = "/haha", method = RequestMethod.GET)
	public void gethaha(){
		System.out.println(Date.from(Instant.now()));
	}

	private void sendScheduleErrorReportEmail(String report){
		//String emailList = "fning@wpi.edu,sxie@wpi.edu";
		//System.out.println(report);
		//helperManager.sendEmail("admin@fmning.com", emailList, 
		//		"WPI CSA scheduler error report", report);
	}
	
}
