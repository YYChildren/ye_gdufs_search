package com.ye.gdufs.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.WeakHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadPoolExecutor;

import com.ye.gdufs.dao.PageDaoImpl;
import com.ye.gdufs.dao.WordDaoImpl;
import com.ye.gdufs.log.Logs;
import com.ye.gdufs.model.Page;
import com.ye.gdufs.model.PageSer;
import com.ye.gdufs.model.ResultPage;
import com.ye.gdufs.model.WordPos;
import com.ye.gdufs.service.prepare.RuleClassifier;
import com.ye.gdufs.util.SearchExecutor;

public class Extractor {
	private static WeakHashMap<Long, PageDaoImpl> uidPdiMap = new WeakHashMap<>();
	private final static String END_CHARS = "。?!;";// 结束符号。
	private static ThreadPoolExecutor executor = SearchExecutor.getInstance().getExecutor();
	private final int MAX_BODY_SIZE=200;
	private ArrayList<WordDaoImpl> wdiArr;
	private Map<String, Double> wordWeightMap;
	private List<Entry<Long, Double>> uidWeightList;
	private List<ResultPage> resultL = new ArrayList<>();

	public Extractor() {
		super();
	}

	public Extractor(ArrayList<WordDaoImpl> wdiArr,
			Map<String, Double> wordWeightMap, 
			List<Entry<Long, Double>> uidWeightList) {
		this.wdiArr = wdiArr;
		this.wordWeightMap = wordWeightMap;
		this.uidWeightList = uidWeightList;
	}
	
	public List<ResultPage> getResultList() {
		return resultL;
	}

	public void setUidWeightList(List<Entry<Long, Double>> uidWeightList) {
		this.uidWeightList = uidWeightList;
	}

	public void extract() {
		CountDownLatch runningThreadNum = new CountDownLatch(uidWeightList.size());
		uidWeightList.forEach(uidWeightMap -> {
			executor.execute(new Runnable(){
				@Override
				public void run() {
					try {
						long uid = uidWeightMap.getKey();
						double uidWeight = uidWeightMap.getValue();
						PageDaoImpl pd = null;
						if (uidPdiMap.containsKey(uid)) {
							pd = uidPdiMap.get(uid);
						} else {
							pd = new PageDaoImpl();
							pd.get(uid);
							uidPdiMap.put(uid, pd);
						}
						Page page = pd.getPage();
						PageSer pageSer = pd.getPageSer();
						if (page != null && pageSer != null) {
							List<String> titleSegs = pageSer.getTitleSegments();
							List<String> titleTags = pageSer.getTitlePostags();
							List<String> titleNers = pageSer.getTitleNers();
							List<List<String>> bodySegs = pageSer
									.getBodySegments();
							List<List<String>> bodyTags = pageSer
									.getBodyPostags();
							List<List<String>> bodyNers = pageSer.getBodyNers();
							ResultPage rs = new ResultPage();
							String url = page.getUrl();
							rs.setUrl(url);
							extractTitle(uid, uidWeight, titleSegs, titleTags,
									titleNers, rs);
							extractBody(uid, uidWeight, bodySegs, bodyTags,
									bodyNers, rs);
							synchronized (resultL) {
								resultL.add(rs);
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
						Logs.printStackTrace(e);
					}finally{
						runningThreadNum.countDown();
					}
				}
			});
		});
		try {
            runningThreadNum.await();
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
	}
	
	private void extractTitle(long uid,double uidWeight,List<String> titleSegs,List<String>titleTags,List<String> titleNers,ResultPage rs) {
		boolean emBegin = false;
		StringBuilder titleBuilder = new StringBuilder();
		for (String word : titleSegs) {
			if (wordWeightMap.containsKey(word)) {
				if (!emBegin) {
					titleBuilder.append("<em>");
					emBegin = true;
				}
			} else {
				if (emBegin) {
					titleBuilder.append("</em>");
					emBegin = false;
				}
			}
			titleBuilder.append(word);
		}
		String sentence = titleBuilder.toString();
		rs.setTitle(sentence);
	}

	private void extractBody(long uid, double uidWeight,
			List<List<String>> bodySegs, List<List<String>> bodyTags,
			List<List<String>> bodyNers,ResultPage rs) {
		//计算每段的权重
		Map<Integer, Double> paraWeightMap = new HashMap<>();
		wdiArr.forEach(wdi -> {
			String word = wdi.getW().getWord();
			WordPos wordPos = wdi.getUidPos().get(uid);
			if (wordPos != null) {
				List<Integer> wordIPos = wordPos.getBodyWordIPos();
				if (wordIPos != null) {
					int length = wordIPos.size();
					for (int i = 0; i < length; ++i) {
						int ipara = wordIPos.get(i);
						double weight = paraWeightMap.getOrDefault(i, 0.0) 
								+ uidWeight* wordWeightMap.getOrDefault(word,RuleClassifier.LEAST_WEIGHT);
						paraWeightMap.put(ipara, weight);
					}
				}
			}
		});
		// -------------------取出权值最大的段-------------------------------//
		int maxIpara = -1;
		double maxWeight = Double.MIN_VALUE;
		for (Entry<Integer, Double> e : paraWeightMap.entrySet()) {
			int i = e.getKey();
			double weight = e.getValue();
			if (weight > maxWeight) {
				maxWeight = weight;
				maxIpara = i;
			}
		}
		// --------------------取出段中关键词位置---------------------------//
		if(maxIpara != -1){
			List<Integer> emJPos = new ArrayList<>();
			for (WordDaoImpl wdi : wdiArr) {
				WordPos wordPos = wdi.getUidPos().get(uid);
				if (wordPos != null) {
					List<Integer> wordIPos = wordPos.getBodyWordIPos();
					List<Integer> wordJPos = wordPos.getBodyWordJPos();
					if (wordIPos != null) {
						int[]  be = getBE(wordIPos,maxIpara);
						if( be != null){
							for(int i = be[0];i<=be[1];++i){
								emJPos.add(wordJPos.get(i));
							}
						}
					}
				}
			}
			Collections.sort(emJPos);
			List<String> lbodySegs = bodySegs.get(maxIpara);
			String maxWStr =  subSentence(emJPos, lbodySegs);
			rs.setBody(maxWStr);
		}
	}
	
	private int[] getBE(List<Integer> wordIPos,int value){
		int len = wordIPos.size();
		int begin = 0;
		int end = len - 1;
		int middle = -1;
		int middleValue;
		boolean isContained = false;
		while(begin <= end){
			middle = (begin + end)/2;
			middleValue = wordIPos.get(middle);
			if(value > middleValue){
				begin = middle + 1;
			}else if(value < middleValue){
				end = middle-1;
			}else{
				isContained = true;
				break;
			}
		}
		if(!isContained){//如果不存在
			return null;
		}
		begin = middle;
		while(--begin>=0 && wordIPos.get(begin) == value);
		end = middle;
		while(++end < len && wordIPos.get(end) == value);
		int[] result = {++begin,--end};
		return result;
	}

	private String subSentence(List<Integer> emJPos, List<String> lbodySegs) {
		StringBuilder bodyBuilder = new StringBuilder();
		if (emJPos.isEmpty()) {
			for (int i = 0; i < 100; i++) {
				try {
					bodyBuilder.append(lbodySegs.get(i));
				} catch (Exception e) {
					break;
				}
			}
		} else {
			int begin = emJPos.get(0);
			while (--begin >= 0 && !END_CHARS.contains(lbodySegs.get(begin)));// 提取一句话
			++begin;
			int length = lbodySegs.size();
			int end = emJPos.get(emJPos.size() - 1);
			while (++end < length && !END_CHARS.contains(lbodySegs.get(end)));
			--end;
			
			boolean emBegin = false;
			for (int currentSize = 0, j = begin;currentSize < MAX_BODY_SIZE && j <= end; currentSize++,j++) {
				
				String word = lbodySegs.get(j++);
				if (wordWeightMap.containsKey(word)) {
					if (!emBegin) {
						bodyBuilder.append("<em>");
						emBegin = true;
					}
				} else {
					if (emBegin) {
						bodyBuilder.append("</em>");
						emBegin = false;
					}
				}
				bodyBuilder.append(word);
			}
			if(emBegin){
				bodyBuilder.append("</em>");
				emBegin = false;
			}
			bodyBuilder.append("......");
		}
		return bodyBuilder.toString();
	}
}
