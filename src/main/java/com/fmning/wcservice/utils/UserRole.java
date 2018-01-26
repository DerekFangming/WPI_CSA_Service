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
	
}
