package com.rahulswaminathan.yarnapplicationstatistics;

public class CountObject {
	private String tag;
	private int value;

    /**
     * An object that holds both a tag and a value. Models the relationship between a tag and its value from StatsD.
     * @param tag
     *          Name of the object
     * @param value
     *          Value of the object
     */
	public CountObject(String tag, int value) {
		this.tag = tag;
		this.value = value;
	}
	
	public String getTag() {
		return tag;
	}
	
	public int getValue() {
		return value;
	}
	
}
