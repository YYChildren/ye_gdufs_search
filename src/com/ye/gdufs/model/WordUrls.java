package com.ye.gdufs.model;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public final class WordUrls {
	private String word;
	private String serName;//word 的 md5
	@Id
	public String getWord() {
		return word;
	}
	public void setWord(String word) {
		this.word = word;
	}
	public String getSerName() {
		return serName;
	}
	public void setSerName(String serName) {
		this.serName = serName;
	}
	
}
