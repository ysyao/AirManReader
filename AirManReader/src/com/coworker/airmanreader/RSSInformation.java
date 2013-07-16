package com.coworker.airmanreader;
import java.util.Date;

import android.net.Uri;

public class RSSInformation {
	private String title;
	private long id;
	private String description;
	private String pubDate;
	private Uri link;
	
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getPubDate() {
		return pubDate;
	}
	public void setPubDate(String pubDate) {
		this.pubDate = pubDate;
	}
	public Uri getLink() {
		return link;
	}
	public void setLink(Uri link) {
		this.link = link;
	}
	
	
}
