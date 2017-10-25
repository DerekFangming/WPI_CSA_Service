package com.fmning.wcservice.utils;

public class Utils {
	
	/*Email parameters*/
	public final static String emailVerificationPath = "http://wc.fmning.com/email_verification/";//TEST
	//public final static String emailVerificationPath = "https://wcservice.fmning.com/email_verification/";//PROD
	
	/*Scheduler parameters*/
	public final static boolean schedulerEnabled = false;//TEST
	//public final static boolean schedulerEnabled = true;//PROD
	public final static String dbBackupFolder = "/Users/Cyan/Documents/pg_backup";
	public final static String dbBackupFilePrefix = "db_backup_";
	public final static String dbBackupToolLocation = "/Library/PostgreSQL/9.5/bin";
	public final static String dbName = "WcServiceProd";
	public final static String dbUsername = "postgres";
	
	/*Google drive uploader parameters*/
	public final static String credentialFolder = "/Users/Cyan/Documents/pg_backup/credential";
	
	/*Ticket parameters*/
	public final static String ticketPath = "/Volumes/Data/testTickets/";//TEST
	//public final static String ticketPath = "/Volumes/Data/tickets/";//PROD
	
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
