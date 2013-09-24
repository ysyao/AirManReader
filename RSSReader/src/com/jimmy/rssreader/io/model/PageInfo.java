package com.jimmy.rssreader.io.model;

public class PageInfo {
	private int currentNum = 0;
	private int everyLoadNum = 10;
	public int getCurrentNum() {
		return currentNum;
	}
	public int getEveryLoadNum() {
		return everyLoadNum;
	}
	public void setEveryLoadNum(int everyLoadNum) {
		this.everyLoadNum = everyLoadNum;
	}
	public int getLoadNum(){
		return everyLoadNum; 
	}
}
