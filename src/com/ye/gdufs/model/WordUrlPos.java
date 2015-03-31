package com.ye.gdufs.model;


import javax.persistence.EmbeddedId;
import javax.persistence.Entity;

@Entity
public final class WordUrlPos{
	private WordUrlPosPk wordUrlPk;
	private int titleWordFrequecy;
	private int bodyWordFrequecy;
	private String serName;
	@EmbeddedId
	public WordUrlPosPk getWordUrlPk() {
		return wordUrlPk;
	}
	public void setWordUrlPk(WordUrlPosPk wordUrlPk) {
		this.wordUrlPk = wordUrlPk;
	}
	public int getTitleWordFrequecy() {
		return titleWordFrequecy;
	}
	public void setTitleWordFrequecy(int titleWordFrequecy) {
		this.titleWordFrequecy = titleWordFrequecy;
	}
	public int getBodyWordFrequecy() {
		return bodyWordFrequecy;
	}
	public void setBodyWordFrequecy(int bodyWordFrequecy) {
		this.bodyWordFrequecy = bodyWordFrequecy;
	}
	public String getSerName() {
		return serName;
	}
	public void setSerName(String serName) {
		this.serName = serName;
	}
}
