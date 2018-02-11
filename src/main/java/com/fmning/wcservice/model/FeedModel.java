package com.fmning.wcservice.model;

import com.fmning.service.domain.Event;
import com.fmning.service.domain.Feed;

public class FeedModel {
	private Feed feed;
	private int coverImageId;
	private String ownerName;
	private Event event;

	public Feed getFeed() {
		return feed;
	}

	public void setFeed(Feed feed) {
		this.feed = feed;
	}
	public int getCoverImageId() {
		return coverImageId;
	}

	public void setCoverImageId(int coverImageId) {
		this.coverImageId = coverImageId;
	}

	public String getOwnerName() {
		return ownerName;
	}

	public void setOwnerName(String ownerName) {
		this.ownerName = ownerName;
	}

	public Event getEvent() {
		return event;
	}

	public void setEvent(Event event) {
		this.event = event;
	}

}
