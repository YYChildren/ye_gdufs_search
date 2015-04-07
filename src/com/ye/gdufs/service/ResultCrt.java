package com.ye.gdufs.service;

import java.util.AbstractMap.SimpleEntry;
import java.util.List;

import com.ye.gdufs.util.SHFactory;
import com.ye.gdufs.util.SentenceHandler;

public class ResultCrt {
	private String reqStr;
	// url -> {title, content}
	private List<SimpleEntry<String, SimpleEntry<String, String>>> urlTitleContents;

	public ResultCrt() {
	}

	public ResultCrt(String reqStr) {
		setReqStr(reqStr);
		crtResult();
	}

	public String getReqStr() {
		return reqStr;
	}

	public ResultCrt setReqStr(String reqStr) {
		this.reqStr = reqStr;
		return this;
	}

	public List<SimpleEntry<String, SimpleEntry<String, String>>> getUrlTitleContents() {
		return urlTitleContents;
	}

	private ResultCrt crtResult() {
		try {
			SentenceHandler sh = SHFactory.getInstance().buildHandler().analyze(reqStr);
			for(String word : sh.getSegs()){
				
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return this;
	}
}
