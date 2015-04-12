package com.ye.gdufs.model;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Page {
	private long uid;//url的mpq
	private String url;
	private int titleFrequency;
	private int bodyFrequency;
	private String serName;//pid的16进制
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
