package com.fmning.wcservice.model;

import com.fmning.service.domain.Event;

public class EventModel {
	private Event event;
	private int coverImageId;
	private int registedUserCount;
	
	public Event getEvent() {
		return event;
	}
	
	public void setEvent(Event event) {
		this.event = event;
	}

	public int getCoverImageId() {
		return coverImageId;
	}

	public void setCoverImageId(int coverImageId) {
		this.coverImageId = coverImageId;
	}

	public int getRegistedUserCount() {
		return registedUserCount;
	}

	public void setRegistedUserCount(int registedUserCount) {
		this.registedUserCount = registedUserCount;
	}

}
