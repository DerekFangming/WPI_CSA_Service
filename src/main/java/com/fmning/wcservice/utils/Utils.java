package com.fmning.wcservice.utils;

public class Utils {
	
	//************************* TEST
	//public final static String emailVerificationPath = "http://wc.fmning.com/email_verification/";
	//************************* PROD
	public final static String emailVerificationPath = "https://wcservice.fmning.com/email_verification/";
	
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
