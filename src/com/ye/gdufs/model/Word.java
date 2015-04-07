package com.ye.gdufs.model;


import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public final class Word{
	private String word;
	private long urlCount;
	private String serName;//频率或者位置文件的名字，wid的16进制  -》 urlid -> (WordUrlFreq，WordUrlPos)
	
	@Id
	public String getWord() {
		return word;
	}
	public void setWord(String word) {
		this.word = word;
	}
	public long getUrlCount() {
		return urlCount;
	}
	public void setUrlCount(long urlCount) {
		this.urlCount = urlCount;
	}
	public String getSerName() {
		return serName;
	}
	public void setSerName(String serName) {
		this.serName = serName;
	}
}
