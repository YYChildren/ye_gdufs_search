package com.ye.gdufs.service;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.hibernate.Session;

import com.ye.gdufs.dao.WordUrlPosDao;
import com.ye.gdufs.dao.WordUrlPosDaoImpl;
import com.ye.gdufs.dao.WordUrlsDao;
import com.ye.gdufs.model.WordUrlPos;
import com.ye.gdufs.model.WordUrlPosPk;
import com.ye.gdufs.model.WordUrlPosSer;
import com.ye.gdufs.util.MsgUtil;

public class WordUrlPosPro {
	private Map<WordUrlPosPk,WordUrlPos> wupMap = new HashMap<>();
	private Map<WordUrlPosPk,WordUrlPosSer>  wupSerMap= new HashMap<>();
	
	public WordUrlPosPk getKey(String word,String urlMd5){
		return new WordUrlPosPk(word,urlMd5);
	}
	
	private void putKey(WordUrlPosPk pk) throws NoSuchAlgorithmException, UnsupportedEncodingException{
		if(wupMap.containsKey(pk)){
			return;
		}
		WordUrlPos wup = new WordUrlPos(); 
		wup.setWordUrlPk(pk);
		String serName = MsgUtil.msgDigest(pk.getWord() + pk.getUrlMd5()); 
		wup.setSerName(serName);
		wupMap.put(pk, wup);
		if(wupSerMap.containsKey(pk)){
			return;
		}
		WordUrlPosSer wupSer = new WordUrlPosSer(); 
		wupSerMap.put(pk, wupSer);
	}
	
	public synchronized void putTitleWordFrequecy(String word,String urlMd5,int titleWordFrequecy) throws NoSuchAlgorithmException, UnsupportedEncodingException{
		WordUrlPosPk pk = getKey(word,urlMd5);
		putTitleWordFrequecy(pk,titleWordFrequecy);
	}
	private void putTitleWordFrequecy(WordUrlPosPk pk,int titleWordFrequecy) throws NoSuchAlgorithmException, UnsupportedEncodingException{
		putKey(pk);
		wupMap.get(pk).setTitleWordFrequecy(titleWordFrequecy);
	}
	
	public synchronized void putBodyWordFrequecy(String word,String urlMd5,int bodyWordFrequecy) throws NoSuchAlgorithmException, UnsupportedEncodingException {
		WordUrlPosPk pk = getKey(word,urlMd5);
		putBodyWordFrequecy(pk,bodyWordFrequecy);
	}
	private void putBodyWordFrequecy(WordUrlPosPk pk,int bodyWordFrequecy) throws NoSuchAlgorithmException, UnsupportedEncodingException {
		putKey(pk);
		wupMap.get(pk).setBodyWordFrequecy(bodyWordFrequecy);
	}
	
	public synchronized void putBodyWordSerName(String word,String urlMd5,String serName) throws NoSuchAlgorithmException, UnsupportedEncodingException {
		WordUrlPosPk pk = getKey(word,urlMd5);
		putBodyWordSerName(pk,serName);
	}
	private void putBodyWordSerName(WordUrlPosPk pk,String serName) throws NoSuchAlgorithmException, UnsupportedEncodingException {
		putKey(pk);
		wupMap.get(pk).setSerName(serName);
	}
	
	public synchronized void putTitleWordPos(String word,String urlMd5,List<Integer> titleWordPos) throws NoSuchAlgorithmException, UnsupportedEncodingException {
		WordUrlPosPk pk = getKey(word,urlMd5);
		putTitleWordPos(pk,titleWordPos);
	}
	private void putTitleWordPos(WordUrlPosPk pk,List<Integer> titleWordPos) throws NoSuchAlgorithmException, UnsupportedEncodingException {
		putKey(pk);
		wupSerMap.get(pk).setTitleWordPos(titleWordPos);
	}
	
	public synchronized void putBodyWordIPos(String word,String urlMd5,List<Integer> bodyWordIPos) throws NoSuchAlgorithmException, UnsupportedEncodingException {
		WordUrlPosPk pk = getKey(word,urlMd5);
		putBodyWordIPos(pk,bodyWordIPos);
	}
	private void putBodyWordIPos(WordUrlPosPk pk,List<Integer> bodyWordIPos) throws NoSuchAlgorithmException, UnsupportedEncodingException {
		putKey(pk);
		wupSerMap.get(pk).setBodyWordIPos(bodyWordIPos);
	}
	
	public synchronized void putBodyWordJPos(String word,String urlMd5,List<Integer> bodyWordJPos) throws NoSuchAlgorithmException, UnsupportedEncodingException {
		WordUrlPosPk pk = getKey(word,urlMd5);
		putBodyWordJPos(pk,bodyWordJPos);
	}
	private void putBodyWordJPos(WordUrlPosPk pk,List<Integer> bodyWordJPos) throws NoSuchAlgorithmException, UnsupportedEncodingException {
		putKey(pk);
		wupSerMap.get(pk).setBodyWordJPos(bodyWordJPos);
	}

	public void clear() {
		wupMap.clear();
		wupSerMap.clear();
	}

	public void save() throws Exception {
		operation(new Op(){
			@Override
			public void operation(WordUrlPosDao wupd) throws Exception {
				wupd.save();
			}
		});
	}
	public void rsave(Session session) throws Exception {
		operation(new Op(){
			@Override
			public void operation(WordUrlPosDao wupd) throws Exception {
				wupd.rsave(session);
			}
		});
	}

	public void rrollback() throws Exception {
		operation(new Op(){
			@Override
			public void operation(WordUrlPosDao wupd) throws Exception {
				wupd.rrollback();
			}
		});
	}
	public void operation(Op op) throws Exception {
		for(Entry<WordUrlPosPk,WordUrlPos> e : wupMap.entrySet()){
			WordUrlPosPk pk = e.getKey();
			WordUrlPos wup = e.getValue();
			WordUrlPosSer wupSer = wupSerMap.get(pk);
			WordUrlPosDao wupd = new WordUrlPosDaoImpl(wup,wupSer);
			op.operation(wupd);
		}
	}
	interface Op{
		void operation(WordUrlPosDao wupd)  throws Exception;
	}
}
