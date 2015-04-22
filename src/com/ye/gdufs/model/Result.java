package com.ye.gdufs.model;

public class Result {
	private String url;
	private String title;
	private String body;
	public Result(){}
	public Result(String url, String title, String body) {
		super();
		this.url = url;
		this.title = title;
		this.body = body;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getBody() {
		return body;
	}
	public void setBody(String body) {
		this.body = body;
	}
}
