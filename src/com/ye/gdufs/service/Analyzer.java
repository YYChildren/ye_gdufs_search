package com.ye.gdufs.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.PriorityQueue;

import com.ye.gdufs.dao.PageDaoImpl;
import com.ye.gdufs.dao.WordDaoImpl;
import com.ye.gdufs.model.Word;

public class Analyzer {
	private int maxHeads = 40;
	ArrayList<WordDaoImpl> wdiArr ;
	private Map<String,Double> wordWeight = new HashMap<>();
	private  List<Entry<Long, Double>> uidWeightList;
	private static long urlCount;
	static{
		updateUrlCount();
	}
	public Analyzer(){}
	public Analyzer(ArrayList<WordDaoImpl> wdiArr){ this.wdiArr = wdiArr; }
	public static void updateUrlCount(){
		urlCount = PageDaoImpl.getPageCount();
	}
	public List<Entry<Long, Double>> getUidWeightList() {
		return uidWeightList;
	}
	public Map<String, Double> getWordWeight() {
		return wordWeight;
	}
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
			
			//计算词在每个uid的tf-idf
			Map<Long,Double> uidTitleWeight = countTfIdf(tidf,uidTitleFreq);
			Map<Long,Double> uidBodyWeight = countTfIdf(bidf,uidBodyFreq);
			
			//计算词在每个uid的权重
			Map<Long,Double> uidWeight = countWordWeight(uidTitleWeight ,uidBodyWeight);
			wordUidWeight.put(word, uidWeight);
			
			
			//计算词的平均权重
			Double countWeight = 0.0;
			for(Double weight : uidWeight.values()){
				countWeight += weight;
			}
			wordWeight.put(word, uidWeight.size()>0 ? countWeight/uidWeight.size() : 0.0 );
		});
		Map<Long,Double> sentenceUidWeight =  countSentenceWeight(wordUidWeight);
		 uidWeightList = exetactHead(sentenceUidWeight,maxHeads);
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
	private Map<Long, Double> countWordWeight(Map<Long, Double> uidTitleWeight,
			Map<Long, Double> uidBodyWeight) {
		Map<Long,Double> uidWeight  = new HashMap<>();
		uidBodyWeight.forEach( (uid,bw) -> uidWeight.put(uid,  bw + uidTitleWeight.getOrDefault(uid, 0.0))); 
		return uidWeight;
	}
	private Map<Long, Double> countSentenceWeight(Map<String,Map<Long,Double>> wordUidWeight){
		Map<Long,Double> sentenceUidWeight = new HashMap<>();
		wordUidWeight.forEach(  (word,uidWeight) -> {
			 uidWeight.forEach((uid,weight) ->{
				 Double oldWeight = sentenceUidWeight.getOrDefault(uid, 0.0);
				 sentenceUidWeight.put(uid, oldWeight +weight );
			 });
		 });
		return sentenceUidWeight;
	}
	private List<Entry<Long, Double>> exetactHead(Map<Long, Double> uidWeight,int capacity){
		PriorityQueue<Entry<Long, Double>> pq = new PriorityQueue<Entry<Long, Double>>(capacity, new Comparator<Entry<Long, Double>>(){
			@Override
			public int compare(Entry<Long, Double> o1, Entry<Long, Double> o2) {
				return o1.getValue().compareTo(o2.getValue());
			}});
		Set<Entry<Long, Double>> enSet = uidWeight.entrySet();
		enSet.forEach( en -> {
			if(pq.size() < capacity){
				pq.offer(en);
			}else{
				if(pq.peek().getValue() < en.getValue()){
					pq.poll();
					pq.offer(en);
				}
			}
		});
		List<Entry<Long,Double>> result = new ArrayList<>();
		int i = pq.size();
		while(--i >=0 ){
			result.add(pq.poll());
		}
		Collections.reverse(result);
		return result;
	}
}
