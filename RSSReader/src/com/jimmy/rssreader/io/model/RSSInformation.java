package com.jimmy.rssreader.io.model;
import java.util.Date;

import android.net.Uri;

public class RSSInformation {
	private String title;
	private long id;
	private String description;
	private Date pubDate;
	private Uri link;
	
	
	
	public Uri getLink() {
		return link;
	}
	public void setLink(Uri link) {
		this.link = link;
	}
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
	public Date getPubDate() {
		return pubDate;
	}
	public void setPubDate(Date pubDate) {
		this.pubDate = pubDate;
	}
	
	
}
