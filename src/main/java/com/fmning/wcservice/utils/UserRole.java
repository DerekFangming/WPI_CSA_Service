package com.fmning.wcservice.utils;

public class UserRole {
	
	public static final int SYS_ADMIN = 1;
	public static final int SITE_ADMIN = 2;
	
	public static boolean isAdmin(int roleId) {
		return roleId <= SITE_ADMIN;
	}
	
	public static boolean isSystemAdmin(int roleId) {
		return roleId == SYS_ADMIN;
	}
	
	public static String getRoleName(int roleId) {
		if (roleId == SYS_ADMIN) {
			return "System Admin";
		} else if (roleId == SITE_ADMIN) {
			return "Site Admin";
		} else {
			return "User";
		}
	}
	
}
