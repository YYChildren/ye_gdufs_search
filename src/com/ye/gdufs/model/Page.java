package com.ye.gdufs.model;

import javax.persistence.Entity;
import javax.persistence.Id;
@Entity
public class Page {
	public final static int URL_MAX_SIZE = 2048;
	private long uid;//url的mpq
	private String url;
	private String contentMd5;
	private int titleFrequency;
	private int bodyFrequency;
	private String serName;//uid的16进制
	public Page(){}	
	@Id
	public long getUid() {
		return uid;
	}
	public void setUid(long uid) {
		this.uid = uid;
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
