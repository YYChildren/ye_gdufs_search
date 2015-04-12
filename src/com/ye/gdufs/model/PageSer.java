package com.ye.gdufs.model;

import java.util.List;


public final class PageSer implements java.io.Serializable{
	private static final long serialVersionUID = 8334900536464924008L;
	private List<String> titleSegments;
	private List<String> titlePostags;
	private List<String> titleNers;
	private List<List<String>> bodySegments;//分词分段，List<List<String>>的二进制
	private List<List<String>> bodyPostags;//词性标注，List<List<String>>的二进制
	private List<List<String>> bodyNers;//实体标注
	public PageSer() {
		super();
	}
	public List<String> getTitleSegments() {
		return titleSegments;
	}
	public void setTitleSegments(List<String> titleSegments) {
		this.titleSegments = titleSegments;
	}
	public List<String> getTitlePostags() {
		return titlePostags;
	}
	public void setTitlePostags(List<String> titlePostags) {
		this.titlePostags = titlePostags;
	}
	public List<String> getTitleNers() {
		return titleNers;
	}
	public void setTitleNers(List<String> titleNers) {
		this.titleNers = titleNers;
	}
	public List<List<String>> getBodySegments() {
		return bodySegments;
	}
	public void setBodySegments(List<List<String>> bodySegments) {
		this.bodySegments = bodySegments;
	}
	public List<List<String>> getBodyPostags() {
		return bodyPostags;
	}
	public void setBodyPostags(List<List<String>> bodyPostags) {
		this.bodyPostags = bodyPostags;
	}
	public List<List<String>> getBodyNers() {
		return bodyNers;
	}
	public void setBodyNers(List<List<String>> bodyNers) {
		this.bodyNers = bodyNers;
	}
}
