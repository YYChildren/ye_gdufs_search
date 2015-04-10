package com.ye.gdufs.service;

import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.List;
import java.util.WeakHashMap;

import org.hibernate.Session;

import com.ye.gdufs.dao.WordDaoImpl;
import com.ye.gdufs.util.HibernateSql;
import com.ye.gdufs.util.HibernateUtil;
import com.ye.gdufs.util.SHFactory;
import com.ye.gdufs.util.SentenceHandler;

public class ResultCrt {
	static long urlCount;
	{
		HibernateSql hs = new HibernateSql(){

			@Override
			public Object execute(Session session) throws Exception {
				return session.createQuery("select count(p.uid) from Page p").uniqueResult();
			}};
		try {
			urlCount = (long) HibernateUtil.execute(hs);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	private String reqStr;
	SHFactory shf = SHFactory.getInstance();
	private List<SimpleEntry<String, SimpleEntry<String, String>>> urlTitleContents;
	private WeakHashMap<String,WordDaoImpl> wordWDI = new WeakHashMap<>();;

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
			ArrayList<WordDaoImpl> wdiArr = new ArrayList<WordDaoImpl>();
			SentenceHandler sh = shf.buildHandler().analyze(reqStr);
			for(String word : sh.getSegs()){
				if(word.length() > 64){
					continue;
				}
				WordDaoImpl wdi = null;
				if(wordWDI.containsKey(word)){
					wdi = wordWDI.get(word);
				}
				wdi = new WordDaoImpl();
				wdi.get(word);
				wdiArr.add(wdi);
			}
//			for(wdiArr)
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return this;
	}
}
