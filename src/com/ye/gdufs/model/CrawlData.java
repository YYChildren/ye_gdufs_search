package com.ye.gdufs.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

//临时表
@Entity
@Table(name="crawldata")
public final class CrawlData{
	private long id;
	private long uid;
	private String url;
	private String contentMd5;
	private String serName;//uid的16进制
	public CrawlData() {
		super();
	}
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	public long getId() {
		return id;
	}


	public void setId(long id) {
		this.id = id;
	}


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


	public String getSerName() {
		return serName;
	}


	public void setSerName(String serName) {
		this.serName = serName;
	}
}