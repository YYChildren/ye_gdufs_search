package com.ye.gdufs.model;

public class WordFreq implements java.io.Serializable{
	private static final long serialVersionUID = 4119287388935748488L;
	private int titleWordFrequecy;
	private int bodyWordFrequecy;
	public int getTitleWordFrequecy() {
		return titleWordFrequecy;
	}
	public void setTitleWordFrequecy(int titleWordFrequecy) {
		this.titleWordFrequecy = titleWordFrequecy;
	}
	public int getBodyWordFrequecy() {
		return bodyWordFrequecy;
	}
	public void setBodyWordFrequecy(int bodyWordFrequecy) {
		this.bodyWordFrequecy = bodyWordFrequecy;
	}
}