package com.fmning.wcservice.model;

import com.fmning.service.domain.Feed;

public class FeedModel {
	private Feed feed;
	private int coverImageId;

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

}
