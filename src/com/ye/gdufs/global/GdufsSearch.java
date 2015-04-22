package com.ye.gdufs.global;

import java.util.List;

import com.ye.gdufs.model.Result;
import com.ye.gdufs.service.ResultGetter;

public class GdufsSearch {
	private String reqStr;
	private List<Result> resultL; 
	
	public GdufsSearch(){}
	
	public GdufsSearch(String reqStr) {
		super();
		this.reqStr = reqStr.trim();
	}
	
	public void search(){
		resultL = ResultGetter.getInstance().getResult(reqStr);
	}
	public String getReqStr() {
		return reqStr;
	}
	public void setReqStr(String reqStr) {
		this.reqStr = reqStr.trim();
	}
	public List<Result> getResultL() {
		return resultL;
	}
	public void setResultL(List<Result> resultL) {
		this.resultL = resultL;
	}
}

//resultL = new ArrayList<>();
//int count = 103;
//while(count-- > 0){
//	Result result = new Result();
//	result.setUrl("http://www.gdufs.edu.cn/info/1106/43754.htm");
//	result.setTitle("外交部部长助理<em>郑泽光</em>作国际形势报告");
//	result.setBody("本网讯 日前，外交部部长助理<em>郑泽光</em>作客我校广东国际战略研究院，就2015年的国际形势做了专题报告......");
//	resultL.add(result);
//}
