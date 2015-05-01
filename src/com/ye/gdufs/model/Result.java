package com.ye.gdufs.model;

import java.util.List;

public class Result {
	private List<ResultPage> resultPageList;
	private List<String> ansList;
	public Result(){}
	public Result(List<ResultPage> resultPageList, List<String> ansList) {
		this.resultPageList = resultPageList;
		this.ansList = ansList;
	}
	public List<ResultPage> getResultPageList() {
		return resultPageList;
	}
	public void setResultPageList(List<ResultPage> resultPageList) {
		this.resultPageList = resultPageList;
	}
	public List<String> getAnsList() {
		return ansList;
	}
	public void setAnsList(List<String> ansList) {
		this.ansList = ansList;
	}
}
