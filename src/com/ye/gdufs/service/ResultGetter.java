package com.ye.gdufs.service;

import java.util.List;
import java.util.WeakHashMap;

import com.ye.gdufs.model.Result;

public class ResultGetter {
	private static ResultGetter rgt = ResultGetterIn.rg;
	private WeakHashMap<String,List<Result>> reqRC;
	public static  ResultGetter getInstance(){
		return rgt;
	}
	private ResultGetter(){
		reqRC = new WeakHashMap<>();
	}
	private static class ResultGetterIn{
		private static ResultGetter rg = new ResultGetter();
	}
	public List<Result> getResult(String reqStr){
		ResultCrt rc= null;
		List<Result> resultL = null;
		if(!reqRC.containsKey(reqStr) || reqRC.get(reqStr) == null){
			rc = new ResultCrt(reqStr);
			resultL = rc.getResultL();
			reqRC.put(reqStr, resultL);
		}
		else{
			resultL = reqRC.get(reqStr);
		}
		return resultL;
	}
}
