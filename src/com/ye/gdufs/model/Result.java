package com.ye.gdufs.model;

import java.util.List;

public class Result {
	private List<ResultPage> resultPageList;
	public Result(){}
	public Result(List<ResultPage> resultPageList) {
		this.resultPageList = resultPageList;
	}
	public List<ResultPage> getResultPageList() {
		return resultPageList;
	}
	public void setResultPageList(List<ResultPage> resultPageList) {
		this.resultPageList = resultPageList;
	}
}
