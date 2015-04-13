package com.ye.gdufs.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.AbstractMap.SimpleEntry;
import java.util.function.BiConsumer;

import com.ye.gdufs.dao.WordDaoImpl;
import com.ye.gdufs.model.Word;

public class Analyzer {
	ArrayList<WordDaoImpl> wdiArr ;
	private List<SimpleEntry<String, SimpleEntry<String, String>>> urlTitleContents;
	long urlCount;
	public void analyze(){
		Map<String,Map<Long,Double>> wordUidWeight = new HashMap<>();
		wdiArr.forEach( wdi -> {
			Word w = wdi.getW();
			String word = w.getWord();
			Long uidTCount = w.getUidTitleCount();
			Long uidBCount = w.getUidBodyCount();
			
			Double tidf =  uidTCount > 0 ? Math.log10(urlCount / uidTCount) : 0.0;
			Double bidf = uidBCount > 0 ? Math.log10(urlCount / uidBCount) : 0.0;
			
			Map<Long,Integer> uidTitleFreq = wdi.getUidTitleFreq();
			Map<Long,Integer> uidBodyFreq = wdi.getUidBodyFreq();
			
			Map<Long,Double> uidTitleWeight = countTfIdf(tidf,uidTitleFreq);
			Map<Long,Double> uidBodyWeight = countTfIdf(bidf,uidBodyFreq);
			
			Map<Long,Double> uidWeight = countWeight(uidTitleWeight ,uidBodyWeight);
			
			wordUidWeight.put(word, uidWeight);
		});
	}
	private Map<Long, Double> countWeight(Map<Long, Double> uidTitleWeight,
			Map<Long, Double> uidBodyWeight) {
		Map<Long,Double> uidWeight  = new HashMap<>();
		uidBodyWeight.forEach( (uid,bw) -> uidWeight.put(uid,  uidTitleWeight.getOrDefault(uid, 0.0))); 
		return uidWeight;
	}
	private Map<Long, Double> countTfIdf(Double idf,Map<Long,Integer> uidFreq){
		Map<Long,Double> uidWeight = new HashMap<>();
		BiConsumer<? super Long, ? super Integer> action = (uid,freq) ->{
			Double tf = Math.log10(1+ freq);
			Double tfIdf = tf *  idf;
			uidWeight.put(uid, tfIdf);
		};
		uidFreq.forEach(action);
		return uidWeight;
	}
}
