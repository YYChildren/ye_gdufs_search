package com.ye.gdufs.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import org.hibernate.Session;

import com.ye.gdufs.dao.WordDao;
import com.ye.gdufs.dao.WordDaoImpl;
import com.ye.gdufs.model.WordFreq;
import com.ye.gdufs.model.WordPos;

public class WordPro {
	//word -> uid -> .....
	private Map<String,Map<Long,WordFreq>> wordFreqMap = new HashMap<>();
	private Map<String,Map<Long,WordPos>>  wordPosMap= new HashMap<>();
	private Map<UUID,WordPro> uuidWordProMap;
	
	

	public Map<String, Map<Long, WordFreq>> getWordFreqMap() {
		return wordFreqMap;
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
		Map<Long, WordFreq> uidWFreqMap = wordFreqMap.getOrDefault(word, new HashMap<>());
		WordFreq wFreq =uidWFreqMap.getOrDefault(uid, new WordFreq());
		wFreq.setTitleWordFrequecy(titleWordFrequecy);
		uidWFreqMap.put(uid, wFreq);
		wordFreqMap.put(word, uidWFreqMap);
	}
	public void putBodyWordFrequecy(String word, long uid,
			Integer bodyWordFrequecy) {
		Map<Long, WordFreq> uidWFreqMap = wordFreqMap.getOrDefault(word, new HashMap<>());
		WordFreq wFreq =uidWFreqMap.getOrDefault(uid, new WordFreq());
		wFreq.setBodyWordFrequecy(bodyWordFrequecy);
		uidWFreqMap.put(uid, wFreq);
		wordFreqMap.put(word, uidWFreqMap);
	}

	public void clear() {
		wordFreqMap.clear();
		wordPosMap.clear();
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
	/*private Map<String,Map<Long,WordFreq>> wordFreqMap = new HashMap<>();
	private Map<String,Map<Long,WordPos>>  wordPosMap= new HashMap<>();
	private Map<UUID,WordPro> uuidWordProMap;
	*/
	//需要合并，再计算
	public synchronized void operation(Op op) throws Exception {
		merge();
		for(Entry<String, Map<Long, WordFreq>> e : wordFreqMap.entrySet()){
			String word = e.getKey();
			Map<Long, WordFreq> uidFreq = e.getValue();
			Map<Long, WordPos> uidPos = wordPosMap.get(word);
			WordDao wupd = new WordDaoImpl(word,uidFreq,uidPos);
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
		Map<String, Map<Long, WordFreq>> wordUidFreqMap = wordPro.getWordFreqMap();
		Map<String,Map<Long, WordPos>> wordUidPosMap = wordPro.getWordPosMap();
		for(Entry<String, Map<Long, WordFreq>> e : wordUidFreqMap.entrySet()){
			String word = e.getKey();
			Map<Long, WordFreq> uidFreqMap = e.getValue();
			Map<Long, WordPos> uidPosMap = wordUidPosMap.get(word);
			putAllUrlAndMd5(word, uidFreqMap,uidPosMap);
		}
	}
	private void putAllUrlAndMd5(String word,Map<Long, WordFreq> uidFreqMap,Map<Long, WordPos> uidPosMap) {
		{
			Map<Long, WordFreq> uidWFreqMap = wordFreqMap.getOrDefault(word, new HashMap<>());
			uidWFreqMap.putAll(uidFreqMap);
			wordFreqMap.put(word, uidWFreqMap);
		}
		{
			Map<Long, WordPos> uidWPosMap = wordPosMap.getOrDefault(word, new HashMap<>());
			uidWPosMap.putAll(uidPosMap);
			wordPosMap.put(word, uidWPosMap);
		}
	}
	interface Op{
		void operation(WordDao wupd)  throws Exception;
	}
}
