package com.ye.gdufs.service;

import java.util.Map.Entry;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

import org.hibernate.Session;

import com.ye.gdufs.dao.WordDaoImpl;
import com.ye.gdufs.log.Logs;
import com.ye.gdufs.model.Result;
import com.ye.gdufs.model.Word;
import com.ye.gdufs.util.HibernateSql;
import com.ye.gdufs.util.HibernateUtil;
import com.ye.gdufs.util.SHFactory;
import com.ye.gdufs.util.SentenceHandler;

public class ResultCrt {
	private String reqStr;
	private SHFactory shf = SHFactory.getInstance();
	private List<Result> resultL;
	private WeakHashMap<String,WordDaoImpl> wordWDI = new WeakHashMap<>();;
	static long urlCount;
	static{
		update();
	}
	public static void update(){
		HibernateSql hs = new HibernateSql(){
			@Override
			public Object execute(Session session) throws Exception {
				return session.createQuery("select count(p.uid) from Page p").uniqueResult();
			}};
		try {
			urlCount = (long) HibernateUtil.execute(hs);
		} catch (Exception e) {
			Logs.printStackTrace(e);
		}
		System.gc();//垃圾回收，清理WeakHashMap缓存。
	}

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
		crtResult();
		return this;
	}

	public List<Result> getResultL() {
		return resultL;
	}

	private void crtResult() {
		try {
			ArrayList<WordDaoImpl> wdiArr = new ArrayList<WordDaoImpl>();
			SentenceHandler sh = shf.buildHandler().analyze(reqStr);
			for(String word : sh.getSegs()){
				if(word.length() > Word.WORD_MAX_SIZE){
					continue;
				}
				WordDaoImpl wdi = null;
				if(wordWDI.containsKey(word)){
					wdi = wordWDI.get(word);
				}else{
					wdi = new WordDaoImpl();
					wdi.get(word);
				}
				wdiArr.add(wdi);
			}
			 Analyzer uidAnalyze =  new Analyzer(wdiArr);
			 uidAnalyze.analyze();
			 Map<String,Double> wordWeight = uidAnalyze.getWordWeight();
		     List<Entry<Long, Double>> uidWeightList = uidAnalyze.getUidWeightList();
		     Extractor extractor = new Extractor(wordWeight,uidWeightList);
		     extractor.extract();
		     resultL =extractor.getResultL();
		} catch (Exception e) {
			Logs.printStackTrace(e);
		}
	}
}
