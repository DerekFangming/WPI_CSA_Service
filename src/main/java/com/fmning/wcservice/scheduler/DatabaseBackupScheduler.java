package com.fmning.wcservice.scheduler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Calendar;
import java.util.Collections;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.fmning.service.manager.ErrorManager;
import com.fmning.service.manager.HelperManager;
import com.fmning.wcservice.controller.rest.TestController;
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
@EnableScheduling
@Component
public class DatabaseBackupScheduler {
	
	public static String backupScriptPath;
	
	private HttpTransport httpTransport;
	private FileDataStoreFactory dataStoreFactory;
	private JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
	private static Drive drive;
	
	@Autowired private HelperManager helperManager;
	@Autowired private ErrorManager errorManager;

	//@Scheduled(cron = "*/5 * * * * *") //Every 5 seconds, for testing only
	@Scheduled(cron = "0 0 6 * * ?") //6 am in est time to simulate 1 am in UTC time
    public void dbDailyBackup() {
		if(Utils.prodMode) {
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
					try {
						helperManager.sendEmail("admin@fmning.com", "fning@wpi.edu,sxie@wpi.edu", 
								"WPI CSA scheduler error report", report + errors);
					} catch (Exception e) {
						errorManager.logError(e);
					}
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
						errorManager.logError(e);
						try {
							String report = "Error during google drive backup of the database:\n\n";
							helperManager.sendEmail("admin@fmning.com", "fning@wpi.edu,sxie@wpi.edu", 
									"WPI CSA scheduler error report", report + e.getMessage());
						} catch (Exception e1) {
							errorManager.logError(e1);
						}
					}
				}
				
			} catch (IOException | InterruptedException e) {
				errorManager.logError(e);
				try {
					String report = "Error during backup of the database:\n\n";
					helperManager.sendEmail("admin@fmning.com", "fning@wpi.edu,sxie@wpi.edu", 
							"WPI CSA scheduler error report", report + e.getMessage());
				} catch (Exception e1) {
					errorManager.logError(e1);
				}
			}
		} else {
			System.out.println("Scheduler tested, working");
		}
        
    }

}
