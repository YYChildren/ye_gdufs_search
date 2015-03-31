package com.ye.gdufs.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import org.hibernate.Session;

import com.ye.gdufs.dao.WordUrlsDao;
import com.ye.gdufs.dao.WordUrlsDaoImpl;
import com.ye.gdufs.model.WordUrls;
import com.ye.gdufs.model.WordUrlsSer;
import com.ye.gdufs.util.MsgUtil;

public class WordUrlsPro {
	private Map<String, List<String>> wuMd5sMap = new HashMap<>();
	private Map<UUID,WordUrlsPro> uuidWusMap = null;

	public Map<String, List<String>> getWuMd5sMap() {
		return wuMd5sMap;
	}
	private void putWord(String word) {
		if (!wuMd5sMap.containsKey(word)) {
			wuMd5sMap.put(word, new ArrayList<String>());
		}
	}
	public synchronized void putUrlMd5(String word, String urlMd5) {
		putWord(word);
		wuMd5sMap.get(word).add(urlMd5);
	}
	public synchronized void clear(){
		wuMd5sMap.clear();
	}
	public synchronized void psavel(UUID uuid, WordUrlsPro wusProLocal) {
		if(uuidWusMap == null){
			uuidWusMap = new HashMap<>();
		}
		uuidWusMap.put(uuid, wusProLocal);	
	}
	public synchronized void prollback(UUID uuid) {
		if(uuidWusMap == null){
			return;
		}
		uuidWusMap.remove(uuid);
	}

	public void rsave(Session session) throws Exception {
		operation(new Op(){
			@Override
			public void operation(WordUrlsDao wusd) throws Exception {
				wusd.rsave(session);
			}
		});		
	}
	public void rrollback() throws Exception{
		operation(new Op(){
			@Override
			public void operation(WordUrlsDao wusd) throws Exception {
				wusd.rrollback();
			}
		});		
	}
	public void save() throws Exception {
		operation(new Op(){
			@Override
			public void operation(WordUrlsDao wusd) throws Exception {
				wusd.save();
			}
		});		
	}
	
	public void operation(Op op) throws Exception{
		merge();
		for(Entry<String, List<String>> wuMd5s : wuMd5sMap.entrySet()){
			String word = wuMd5s.getKey();
			List<String> urlMd5s = wuMd5s.getValue();
			WordUrls wus = new WordUrls();
			wus.setWord(word);
			wus.setSerName(MsgUtil.msgDigest(word));
			WordUrlsSer wusSer = new WordUrlsSer();
			wusSer.setUrlMd5s(urlMd5s);
			WordUrlsDao wusd = new WordUrlsDaoImpl(wus,wusSer); 
			op.operation(wusd);
		}
	}
	
	private void merge(){
		if( uuidWusMap == null || uuidWusMap.isEmpty()){
			return;
		}
		for(Entry<UUID,WordUrlsPro> uuidWusE: uuidWusMap.entrySet()){
			WordUrlsPro wusPro = uuidWusE.getValue();
			merge(wusPro);
		}
		uuidWusMap = null;
	}
	private void merge(WordUrlsPro wusPro){
		Map<String, List<String>> wuMd5sMap = wusPro.getWuMd5sMap();
		for(Entry<String,List<String>> e : wuMd5sMap.entrySet()){
			String word = e.getKey();
			List<String> urlMd5s = e.getValue();
			putAllUrlAndMd5(word, urlMd5s);
		}
	}
	private void putAllUrlAndMd5(String word,List<String> urlMd5s) {
		putWord(word);
		wuMd5sMap.get(word).addAll(urlMd5s);
	}
	interface Op{
		void operation(WordUrlsDao wusd)  throws Exception;
	}
}
