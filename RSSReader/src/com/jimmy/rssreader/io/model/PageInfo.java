package com.jimmy.rssreader.io.model;

public class PageInfo {
	
	private int current_num;
	private int load_num = 5;
	public int getCurrent_num() {
		return current_num;
	}
	public void setCurrent_num(int current_num) {
		this.current_num = current_num;
	}
	public int getLoad_num() {
		return load_num;
	}
	public void setLoad_num(int load_num) {
		this.load_num = load_num;
	}

}
