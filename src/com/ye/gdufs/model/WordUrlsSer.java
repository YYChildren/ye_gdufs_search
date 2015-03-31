package com.ye.gdufs.model;

import java.util.List;

public final class WordUrlsSer implements java.io.Serializable{
	private static final long serialVersionUID = -8726191928454844155L;
	private List<String> urlMd5s;
	public List<String> getUrlMd5s() {
		return urlMd5s;
	}
	public void setUrlMd5s(List<String> urlMd5s) {
		this.urlMd5s = urlMd5s;
	}
}
