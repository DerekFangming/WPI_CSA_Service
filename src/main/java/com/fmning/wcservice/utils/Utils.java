package com.fmning.wcservice.utils;

public class Utils {
	
	//************************* TEST
	//public final static String emailVerificationPath = "http://wc.fmning.com/email_verification/";
	//************************* PROD
	public final static String emailVerificationPath = "https://wcservice.fmning.com/email_verification/";
	
	/*Scheduler parameters*/
	//Database backup script parameters
	public final static String dbBackupFolder = "/Users/Cyan/Documents/pg_backup";
	public final static String dbBackupFilePrefix = "db_backup_";
	public final static String dbBackupToolLocation = "/Library/PostgreSQL/9.5/bin";
	public final static String dbName = "WcServiceProd";
	public final static String dbUsername = "postgres";
	
	//Google drive uploader parameters
	public final static String credentialFolder = "/Users/Cyan/Documents/pg_backup/credential";
	
	//Pass output location
	public final static String passPath = "/Volumes/Data/passes/";
	
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

}
