package com.ye.gdufs.service;

import java.util.WeakHashMap;

public class ResultGetter {
	private static ResultGetter rgt = ResultGetterIn.rg;
	private WeakHashMap<String,ResultCrt> reqRC;
	public ResultGetter getInstance(){
		return rgt;
	}
	private ResultGetter(){
		reqRC = new WeakHashMap<String, ResultCrt>();
	}
	private static class ResultGetterIn{
		private static ResultGetter rg = new ResultGetter();
	}
	public ResultCrt getResult(String reqStr){
		if(!reqRC.containsKey(reqStr) || reqRC.get(reqStr) == null){
			ResultCrt rc = new ResultCrt(reqStr); 
			reqRC.put(reqStr, rc);
			return rc;
		}
		else{
			return reqRC.get(reqStr);
		}
	}
}
