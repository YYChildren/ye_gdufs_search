package com.ye.gdufs.service;

import java.util.WeakHashMap;

import com.ye.gdufs.model.Result;

public class ResultGetter {
	private static ResultGetter rgt = ResultGetterIn.rg;
	private WeakHashMap<String,Result> reqRC;
	public static  ResultGetter getInstance(){
		return rgt;
	}
	private ResultGetter(){
		reqRC = new WeakHashMap<>();
	}
	private static class ResultGetterIn{
		private static ResultGetter rg = new ResultGetter();
	}
	public Result getResult(String reqStr){
		ResultCrt rc= null;
		Result rs = null;
		
		if(!reqRC.containsKey(reqStr) || reqRC.get(reqStr) == null){
			rc = new ResultCrt(reqStr);
			rc.crtResult();
			rs = rc.getResult();
			reqRC.put(reqStr, rs);
		}
		else{
			rs = reqRC.get(reqStr);
		}
		return rs;
	}
}
