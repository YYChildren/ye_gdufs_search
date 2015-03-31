package com.ye.gdufs.model;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Page {
	private String urlMd5;
	private String url;
	private String contentMd5;//标题，部分内容的md5
	private int titleFrequency;
	private int bodyFrequency;
	private String serName;
	
	@Id
	public String getUrlMd5() {
		return urlMd5;
	}
	
	public void setUrlMd5(String urlMd5) {
		this.urlMd5 = urlMd5;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getContentMd5() {
		return contentMd5;
	}

	public void setContentMd5(String contentMd5) {
		this.contentMd5 = contentMd5;
	}

	public int getTitleFrequency() {
		return titleFrequency;
	}

	public void setTitleFrequency(int titleFrequency) {
		this.titleFrequency = titleFrequency;
	}

	public int getBodyFrequency() {
		return bodyFrequency;
	}

	public void setBodyFrequency(int bodyFrequency) {
		this.bodyFrequency = bodyFrequency;
	}

	public String getSerName() {
		return serName;
	}

	public void setSerName(String serName) {
		this.serName = serName;
	}
	
}
