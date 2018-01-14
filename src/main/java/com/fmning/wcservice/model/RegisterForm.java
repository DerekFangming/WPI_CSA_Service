package com.fmning.wcservice.model;

public class RegisterForm {
	private String newUsername;
	private String newName;
	private String newPassword;
	private String newConfirm;
	
	public String getNewUsername() {
		return newUsername;
	}
	
	public void setNewUsername(String newUsername) {
		this.newUsername = newUsername;
	}

	public String getNewName() {
		return newName;
	}

	public void setNewName(String newName) {
		this.newName = newName;
	}

	public String getNewPassword() {
		return newPassword;
	}

	public void setNewPassword(String newPassword) {
		this.newPassword = newPassword;
	}

	public String getNewConfirm() {
		return newConfirm;
	}

	public void setNewConfirm(String newConfirm) {
		this.newConfirm = newConfirm;
	}

}
