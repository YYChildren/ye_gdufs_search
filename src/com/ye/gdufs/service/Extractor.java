package com.ye.gdufs.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import com.ye.gdufs.dao.PageDaoImpl;
import com.ye.gdufs.model.Page;
import com.ye.gdufs.model.PageSer;
import com.ye.gdufs.model.Result;

public class Extractor {
	private Map<String,Double> wordWeight;
	private List<Entry<Long, Double>> uidWeightList;
	private List<Result> resultL = new ArrayList<>();

	public Extractor() {
		super();
	}

	public Extractor(Map<String,Double> wordWeight,List<Entry<Long, Double>> uidWeightList) {
		super();
		this.wordWeight = wordWeight;
		this.uidWeightList = uidWeightList;
	}
	
	public List<Result> getResultL() {
		return resultL;
	}

	public void setUidWeightList(List<Entry<Long, Double>> uidWeightList) {
		this.uidWeightList = uidWeightList;
	}

	public void extract() {
		uidWeightList.forEach(uidWeight -> {
			long uid = uidWeight.getKey();
			PageDaoImpl pd = new PageDaoImpl();
			pd.get(uid);
			Page page = pd.getPage();
			PageSer pageSer = pd.getPageSer();
			if (page != null && pageSer != null) {
				List<String> titleSegs = pageSer.getTitleSegments();
				List<List<String>> bodySegs = pageSer.getBodySegments();
				String url = page.getUrl();
				String title = extractTitle(titleSegs); 
				String body = extractBody(bodySegs);
				Result rs = new Result();
				rs.setUrl(url);
				rs.setTitle(title);
				rs.setBody(body);
				resultL.add(rs);
			}
		});
	}

	private String extractTitle(List<String> titleSegs) {
		boolean emBegin = false;
		StringBuilder titleBuilder = new StringBuilder();
		for (String word : titleSegs) {
			if(wordWeight.containsKey(word)){
				if (!emBegin) {
					titleBuilder.append("<em>");
					emBegin = true;
				} 
			}else {
				if (emBegin) {
					titleBuilder.append("</em>");
					emBegin = false;
				}
			}
			titleBuilder.append(word);
		}
		return titleBuilder.toString();
	}
	
	private String extractBody(List<List<String>> bodySegs) {
		List<String> paras = new ArrayList<>();
		List<Double> weights = new ArrayList<>();
		for(List<String> lbodySegs : bodySegs){
			boolean emBegin = false;
			StringBuilder bodyBuilder = new StringBuilder();
			Double tempWeight = 0.0;
			for(String word : lbodySegs){
				if(wordWeight.containsKey(word)){
					if (!emBegin) {
						bodyBuilder.append("<em>");
						emBegin = true;
						tempWeight += wordWeight.get(word);
					} 
				}else {
					if (emBegin) {
						bodyBuilder.append("</em>");
						emBegin = false;
					}
				}
				bodyBuilder.append(word);
			}
			paras.add(bodyBuilder.toString());
			weights.add(tempWeight);
		}
		Double maxW = 0.0;
		int maxIndex = 0;
		int i = 0;
		for(Double weight : weights){
			if(weight > maxW){
				weight = maxW;
				maxIndex = i;
			}
			++i;
		}
		return paras.get(maxIndex);
	}
}
