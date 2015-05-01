package com.ye.gdufs.global;


import com.ye.gdufs.model.Result;
import com.ye.gdufs.service.ResultGetter;

public class GdufsSearch {
	private String reqStr;
	private Result result; 
	
	public GdufsSearch(){}
	
	public GdufsSearch(String reqStr) {
		super();
		this.reqStr = reqStr.trim();
	}
	
	public void search(){
		result = ResultGetter.getInstance().getResult(reqStr);
	}
	public String getReqStr() {
		return reqStr;
	}
	public void setReqStr(String reqStr) {
		this.reqStr = reqStr.trim();
	}
	public Result getResult() {
		return result;
	}
}
