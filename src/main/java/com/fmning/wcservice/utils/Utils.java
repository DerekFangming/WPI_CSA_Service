package com.fmning.wcservice.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.TimeZone;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.braintreegateway.BraintreeGateway;
import com.braintreegateway.Environment;
import com.fmning.service.manager.HelperManager;
import com.fmning.util.Util;
import com.fmning.wcservice.scheduler.DatabaseBackupScheduler;

@Component
public class Utils {
	
	/*Test & Prod switch*/
	public static boolean prodMode  = false;
	
	/*Email parameters*/
	public static String emailVerificationPath = "";
	public static String emailChangePwdPath = "";
	
	/*Scheduler parameters*/
	public final static String dbBackupFolder = "/Users/Cyan/Documents/pg_backup";
	public final static String dbBackupFilePrefix = "db_backup_";
	public final static String dbBackupToolLocation = "/Library/PostgreSQL/9.5/bin";
	public final static String dbName = "WcServiceProd";
	public final static String dbUsername = "postgres";
	
	/*Braintree service*/
	public static BraintreeGateway gateway;
	
	/*Google drive uploader parameters*/
	public final static String credentialFolder = "/Users/Cyan/Documents/pg_backup/credential";
	
	/*Ticket parameters*/
	public static String ticketPath = "";
	
	@Autowired private HelperManager helperManager;
	
	public static String createVerificationEmail(String veriCode) {
		String message = "Hi there,";
		message += "\n";
		message += "Thank you for creating an account at fmning.com domain. Please click on the following link to confirm your email address.";
		message += "\n\n";
		message += emailVerificationPath;
		message += veriCode.replace(".", "=");
		message += "\n\n";
		message += "Thank you.";
		message += "\n";
		return message;
	}
	
	public static String createChangePwdEmail(String veriCode) {
		String message = "Hi there,";
		message += "\n";
		message += "Here is the link to reset your password. It will expire in one day. Please report to CSA if you didn't make this request.";
		message += "\n\n";
		message += emailChangePwdPath;
		message += veriCode.replace(".", "=");
		message += "\n\n";
		message += "Thank you.";
		message += "\n";
		return message;
	}
	
	public static String createRoleChangeEmail(int roleId) {
		String message = "Hi there,";
		message += "\n";
		message += "The role of your account is changed to " + UserRole.getRoleName(roleId) + ". Please report to CSA if you believe this is not correct.";
		
		if (UserRole.isAdmin(roleId)) {
			message += "\n\n";
			message += "Here is the demo about how to access Admin Portal and manager events and users.\n";
			message += "SOME HTTP PATH HERE";
		} else {
			message += "\n\n";
			message += "You will no longer have access to Admin Portal.\n";
		}
		
		message += "\n\n";
		message += "Thank you.";
		message += "\n";
		return message;
	}
	
	@Value("${prodMode}") private boolean prodModeProp;
	@Value("${emailVerificationPath}") private String emailVerificationPathProp;
	@Value("${emailChangePwdPath}") private String emailChangePwdPathProp;
	@Value("${ticketPath}") private String ticketPathProp;

	@Value("${env}") private String env;
	@Value("${merchantId}") private String merchantId;
	@Value("${publicKey}") private String publicKey;
	@Value("${privateKey}") private String privateKey;
	
	@PostConstruct
    public void runOnceOnlyOnStartup() {
		//Set up time zone
		TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
		
		//Set up parameters from properties file
		prodMode = prodModeProp;
		emailVerificationPath = emailVerificationPathProp;
		emailChangePwdPath = emailChangePwdPathProp;
		ticketPath = ticketPathProp;
		
		Environment btEnv = env.equals("sandbox") ? Environment.SANDBOX : Environment.PRODUCTION;
		
		gateway = new BraintreeGateway(
				  btEnv,
				  merchantId,
				  publicKey,
				  privateKey
				);
		
		
		//Set up image path
		if(!prodMode) {
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
