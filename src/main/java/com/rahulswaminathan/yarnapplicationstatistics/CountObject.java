package com.rahulswaminathan.yarnapplicationstatistics;

public class CountObject {
	private String tag;
	private int count;
	
	public CountObject(String tag, int count) {
		this.tag = tag;
		this.count = count;
	}
	
	public String getTag() {
		return tag;
	}
	
	public int getCount() {
		return count;
	}
	
}
