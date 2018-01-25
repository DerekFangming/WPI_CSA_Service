package com.fmning.wcservice.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.TimeZone;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.fmning.service.manager.HelperManager;
import com.fmning.util.Util;
import com.fmning.wcservice.scheduler.DatabaseBackupScheduler;

@Component
public class Utils {
	
	/*Test & Prod switch*/
	/*PROD & TEST Step 1 of 2. Controls db backup job, ticket directory mapping*/
	public static final boolean prodMode  = false;//TODO: Change this after 1.10 major update
	
	/*Email parameters*/
	public static final String emailVerificationPath = prodMode ? "https://wcservice.fmning.com/email_verification/"
			: "http://wc.fmning.com/email_verification/";
	
	/*Scheduler parameters*/
	public final static String dbBackupFolder = "/Users/Cyan/Documents/pg_backup";
	public final static String dbBackupFilePrefix = "db_backup_";
	public final static String dbBackupToolLocation = "/Library/PostgreSQL/9.5/bin";
	public final static String dbName = "WcServiceProd";
	public final static String dbUsername = "postgres";
	
	/*Google drive uploader parameters*/
	public final static String credentialFolder = "/Users/Cyan/Documents/pg_backup/credential";
	
	/*Ticket parameters*/
	public static final String ticketPath = prodMode ? "/Volumes/Data/tickets/" : "/Volumes/Data/testTickets/";
	
	@Value("${dsName}")
	private String datasourceName;
	
	@Autowired private HelperManager helperManager;
	
	public static String createVerificationEmail(String veriCode) {
		String message = "Hi there,";
		message += "\n";
		message += "Thank you for creating an account at fmning.com domain. Please click on the following link to confirm your email address.";
		message += "\n\n";
		message += emailVerificationPath;
		message += veriCode;
		message += "\n\n";
		message += "Thank you.";
		message += "\n";
		return message;
	}
	
	@PostConstruct
    public void runOnceOnlyOnStartup() {
		//Set up time zone
		TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
		
		//Set up image path
		if(datasourceName.equals("dataSourceTest")) {
			//prodMode = false;//TODO: Read in this way
			Util.imagePath = "/Volumes/Data/testImages/";
		}
		
		//System.out.println(myValues);
		
		ClassLoader classLoader = getClass().getClassLoader();
		DatabaseBackupScheduler.backupScriptPath = classLoader.getResource("dbBackup.sh").getFile();
        
		try {
			Process process = Runtime.getRuntime().exec("chmod 777 " + DatabaseBackupScheduler.backupScriptPath);
			
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
				String report = "Error during startup of the service:\n\n";
				helperManager.sendEmail("admin@fmning.com", "fning@wpi.edu,sxie@wpi.edu", 
						"WPI CSA scheduler error report", report + errors);
			}
			
		} catch (IOException | InterruptedException e) {
			String report = "Error during startup of the service:\n\n";
			helperManager.sendEmail("admin@fmning.com", "fning@wpi.edu,sxie@wpi.edu", 
					"WPI CSA scheduler error report", report + e.getMessage());
		}

    }

}
