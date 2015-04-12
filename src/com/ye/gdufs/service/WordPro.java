package com.ye.gdufs.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import org.hibernate.Session;

import com.ye.gdufs.dao.WordDao;
import com.ye.gdufs.dao.WordDaoImpl;
import com.ye.gdufs.model.WordPos;

public class WordPro {
	//word -> uid -> .....
	private Map<String,Map<Long,WordPos>>  wordPosMap= new HashMap<>();
	private Map<String,Map<Long,Integer>> wordTitleFreqMap = new HashMap<>();
	private Map<String,Map<Long,Integer>> wordBodyFreqMap = new HashMap<>();
	private Map<UUID,WordPro> uuidWordProMap;
	
	public Map<String, Map<Long, Integer>> getWordTitleFreqMap() {
		return wordTitleFreqMap;
	}
	public Map<String, Map<Long, Integer>> getWordBodyFreqMap() {
		return wordBodyFreqMap;
	}
	public Map<String, Map<Long, WordPos>> getWordPosMap() {
		return wordPosMap;
	}
	public void putTitleWordPos(String word, long uid,
			List<Integer> titleWordPos) {
		Map<Long, WordPos> uidWPosMap = wordPosMap.getOrDefault(word, new HashMap<>());
		WordPos wPos =uidWPosMap.getOrDefault(uid, new WordPos());
		wPos.setTitleWordPos(titleWordPos);
		uidWPosMap.put(uid, wPos);
		wordPosMap.put(word, uidWPosMap);
	}
	public void putBodyWordIPos(String word, long uid,
			List<Integer> bodyWordIPos) {
		Map<Long, WordPos> uidWPosMap = wordPosMap.getOrDefault(word, new HashMap<>());
		WordPos wPos =uidWPosMap.getOrDefault(uid, new WordPos());
		wPos.setBodyWordIPos(bodyWordIPos);
		uidWPosMap.put(uid, wPos);
		wordPosMap.put(word, uidWPosMap);
		
	}
	public void putBodyWordJPos(String word, long uid,
			List<Integer> bodyWordJPos) {
		Map<Long, WordPos> uidWPosMap = wordPosMap.getOrDefault(word, new HashMap<>());
		WordPos wPos =uidWPosMap.getOrDefault(uid, new WordPos());
		wPos.setBodyWordJPos(bodyWordJPos);
		uidWPosMap.put(uid, wPos);
		wordPosMap.put(word, uidWPosMap);
	}
	public void putTitleWordFrequecy(String word, long uid,
			int titleWordFrequecy) {
		Map<Long, Integer> uidWTFreqMap = wordTitleFreqMap.getOrDefault(word, new HashMap<>());
		uidWTFreqMap.put(uid, titleWordFrequecy);
		wordTitleFreqMap.put(word, uidWTFreqMap);
	}
	public void putBodyWordFrequecy(String word, long uid,
			Integer bodyWordFrequecy) {
		Map<Long, Integer> uidWBFreqMap = wordBodyFreqMap.getOrDefault(word, new HashMap<>());
		uidWBFreqMap.put(uid, bodyWordFrequecy);
		wordBodyFreqMap.put(word, uidWBFreqMap);
	}

	public void clear() {
		wordPosMap.clear();
		wordTitleFreqMap.clear();
		wordBodyFreqMap.clear();
		uuidWordProMap = null;
	}
	
	private void initUuidWordProMap(){
		if(uuidWordProMap == null){
			uuidWordProMap = new HashMap<>();
		}
	}
	
	public synchronized void psave(UUID uuid, WordPro wordProLocal) {
		initUuidWordProMap();
		uuidWordProMap.put(uuid, wordProLocal);
	}
	public synchronized void prollback(UUID uuid) {
		if(uuidWordProMap != null){
			uuidWordProMap.remove(uuid);
		}
	}	
	public void save() throws Exception {
		operation(new Op(){
			@Override
			public void operation(WordDao wupd) throws Exception {
				wupd.save();
			}
		});
	}
	public void rsave(Session session) throws Exception {
		operation(new Op(){
			@Override
			public void operation(WordDao wupd) throws Exception {
				wupd.rsave(session);
			}
		});
	}

	public void rrollback() throws Exception {
		operation(new Op(){
			@Override
			public void operation(WordDao wupd) throws Exception {
				wupd.rrollback();
			}
		});
	}
	//需要合并，再计算
	public synchronized void operation(Op op) throws Exception {
		merge();
		for(Entry<String, Map<Long, WordPos>> e : wordPosMap.entrySet()){
			String word = e.getKey();
			Map<Long, WordPos> uidPos = e.getValue();
			Map<Long,Integer> uidTitleFreq = wordTitleFreqMap.getOrDefault(word, new HashMap<>());
			Map<Long, Integer> uidBodyFreq = wordBodyFreqMap.getOrDefault(word, new HashMap<>());
			WordDao wupd = new WordDaoImpl(word, uidPos,uidTitleFreq,uidBodyFreq);
			op.operation(wupd);
		}
	}
	private void merge(){
		if( uuidWordProMap == null || uuidWordProMap.isEmpty()){
			return;
		}
		for(Entry<UUID, WordPro> uuidWordPro: uuidWordProMap.entrySet()){
			WordPro wordPro = uuidWordPro.getValue();
			merge(wordPro);
		}
		uuidWordProMap = null;
	}
	private void merge(WordPro wordPro){
		Map<String,Map<Long, WordPos>> wordUidPosMap = wordPro.getWordPosMap();
		Map<String, Map<Long, Integer>> wordUidTitleFreqMap = wordPro.getWordTitleFreqMap();
		Map<String, Map<Long, Integer>> wordUidBodyFreqMap = wordPro.getWordBodyFreqMap();
		for(Entry<String, Map<Long, WordPos>> e :  wordUidPosMap.entrySet()){
			String word = e.getKey();
			Map<Long, WordPos> uidPosMap = e.getValue();
			Map<Long, Integer> uidTitleFreqMap = wordUidTitleFreqMap.getOrDefault(word, new HashMap<>());
			Map<Long, Integer> uidBodyFreqMap = wordUidBodyFreqMap.getOrDefault(word, new HashMap<>());
			putAll(word,uidPosMap,uidTitleFreqMap,uidBodyFreqMap);
		}
	}
	private void putAll(String word,Map<Long, WordPos> uidPosMap,Map<Long, Integer> uidTitleFreqMap,Map<Long, Integer> uidBodyFreqMap) {
		Map<Long, WordPos> wordUidPosMap = wordPosMap.getOrDefault(word, new HashMap<>());
		Map<Long, Integer> wordUidTitleFreqMap = wordTitleFreqMap.getOrDefault(word, new HashMap<>());
		Map<Long, Integer> wordUidBodyFreqMap = wordBodyFreqMap.getOrDefault(word, new HashMap<>());
		wordUidPosMap.putAll(uidPosMap);
		wordUidTitleFreqMap.putAll(uidTitleFreqMap);
		wordUidBodyFreqMap.putAll(uidBodyFreqMap);
		wordPosMap.put(word, wordUidPosMap);
		wordTitleFreqMap.put(word, wordUidTitleFreqMap);
		wordBodyFreqMap.put(word, wordUidBodyFreqMap);
	}
	interface Op{
		void operation(WordDao wupd)  throws Exception;
	}
}
