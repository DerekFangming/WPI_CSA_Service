package com.fmning.wcservice.model;

import java.time.Instant;

public class PartiListModel {
	private String name;
	private String email;
	private Instant regiTime;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Instant getRegiTime() {
		return regiTime;
	}

	public void setRegiTime(Instant regiTime) {
		this.regiTime = regiTime;
	}

}
