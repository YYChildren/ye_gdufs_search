package com.ye.gdufs.model;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class PageMd5 {
	private String contentMd5;//标题，部分内容的md5

	public PageMd5() {
		super();
	}

	public PageMd5(String contentMd5) {
		this.contentMd5 = contentMd5;
	}

	@Id
	public String getContentMd5() {
		return contentMd5;
	}

	public void setContentMd5(String contentMd5) {
		this.contentMd5 = contentMd5;
	}
}
