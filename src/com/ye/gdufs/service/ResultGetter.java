package com.ye.gdufs.service;

import java.util.List;
import java.util.WeakHashMap;

import com.ye.gdufs.model.Result;

public class ResultGetter {
	private static ResultGetter rgt = ResultGetterIn.rg;
	private WeakHashMap<String,List<Result>> reqResL;
	public static  ResultGetter getInstance(){
		return rgt;
	}
	private ResultGetter(){
		reqResL = new WeakHashMap<>();
	}
	private static class ResultGetterIn{
		private static ResultGetter rg = new ResultGetter();
	}
	public List<Result> getResult(String reqStr){
		ResultCrt rc= null;
		List<Result> resultL = null;
		if(!reqResL.containsKey(reqStr) || reqResL.get(reqStr) == null){
			rc = new ResultCrt(reqStr);
			resultL = rc.getResultL();
			reqResL.put(reqStr, resultL);
		}
		else{
			resultL = reqResL.get(reqStr);
		}
		return resultL;
	}
}
