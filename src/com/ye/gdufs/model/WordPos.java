package com.ye.gdufs.model;

import java.util.List;

public final class WordPos implements java.io.Serializable{
	private static final long serialVersionUID = 4822587779643626762L;
	private List<Integer> titleWordPos;
	private List<Integer> bodyWordIPos;
	private List<Integer> bodyWordJPos;
	public List<Integer> getTitleWordPos() {
		return titleWordPos;
	}
	public void setTitleWordPos(List<Integer> titleWordPos) {
		this.titleWordPos = titleWordPos;
	}
	public List<Integer> getBodyWordIPos() {
		return bodyWordIPos;
	}
	public void setBodyWordIPos(List<Integer> bodyWordIPos) {
		this.bodyWordIPos = bodyWordIPos;
	}
	public List<Integer> getBodyWordJPos() {
		return bodyWordJPos;
	}
	public void setBodyWordJPos(List<Integer> bodyWordJPos) {
		this.bodyWordJPos = bodyWordJPos;
	}
}
