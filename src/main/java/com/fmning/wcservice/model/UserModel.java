package com.fmning.wcservice.model;

import com.fmning.service.domain.User;

public class UserModel {
	private User user;
	private String userName;
	private String userBirthday;
	private String userClassof;
	private String userMajor;
	private int userAvatarId;

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getUserBirthday() {
		return userBirthday;
	}

	public void setUserBirthday(String userBirthday) {
		this.userBirthday = userBirthday;
	}

	public String getUserClassof() {
		return userClassof;
	}

	public void setUserClassof(String userClassof) {
		this.userClassof = userClassof;
	}

	public String getUserMajor() {
		return userMajor;
	}

	public void setUserMajor(String userMajor) {
		this.userMajor = userMajor;
	}

	public int getUserAvatarId() {
		return userAvatarId;
	}

	public void setUserAvatarId(int userAvatarId) {
		this.userAvatarId = userAvatarId;
	}

}
