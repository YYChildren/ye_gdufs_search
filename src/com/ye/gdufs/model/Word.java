package com.ye.gdufs.model;


import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public final class Word{
	public final static int WORD_MAX_SIZE = 2048;
	private long wid;
	private String word;
	private long uidTitleCount;
	private long uidBodyCount;
	private String serName;//频率或者位置文件的名字，wid的16进制  -》 urlid -> (WordUrlFreq，WordUrlPos)
	
	public Word() {
		super();
	}
	@Id
	public void setWid(long wid) {
		this.wid = wid;
	}
	public void setWord(String word) {
		this.word = word;
	}
	public String getWord() {
		return word;
	}
	public long getWid() {
		return wid;
	}
	public long getUidTitleCount() {
		return uidTitleCount;
	}
	public void setUidTitleCount(long uidTitleCount) {
		this.uidTitleCount = uidTitleCount;
	}
	public long getUidBodyCount() {
		return uidBodyCount;
	}
	public void setUidBodyCount(long uidBodyCount) {
		this.uidBodyCount = uidBodyCount;
	}
	public String getSerName() {
		return serName;
	}
	public void setSerName(String serName) {
		this.serName = serName;
	}
}
