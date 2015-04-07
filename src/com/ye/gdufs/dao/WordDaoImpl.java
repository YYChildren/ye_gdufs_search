package com.ye.gdufs.dao;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.Map;

import org.hibernate.Session;

import com.ye.gdufs.GlobalArgs;
import com.ye.gdufs.model.Word;
import com.ye.gdufs.model.WordFreq;
import com.ye.gdufs.model.WordPos;
import com.ye.gdufs.util.HibernateSql;
import com.ye.gdufs.util.HibernateUtil;
import com.ye.gdufs.util.MPQ;
import com.ye.gdufs.util.Misc;

public class WordDaoImpl implements WordDao{
	private Word w = new Word();
	private Map<Long, WordFreq> oldUidFreq;
	private Map<Long, WordPos> oldUidPos;
	private Map<Long, WordFreq> uidFreq;
	private Map<Long, WordPos> uidPos;
	private File freqFile;
	private File posFile;
	private static String[] freqInfo;
	private static String[] posInfo;
	static{
		freqInfo = GlobalArgs.getWordFreqInfo();
		posInfo = GlobalArgs.getWordPosInfo();
	}
	
	public WordDaoImpl(){}
	public WordDaoImpl(String word, Map<Long, WordFreq> uidFreq,
			Map<Long, WordPos> uidPos) {
		this.uidFreq = uidFreq;
		this.uidPos = uidPos;
		//
		this.w.setWord(word);
		long urlCount = this.uidFreq.size();
		this.w.setUrlCount(urlCount );
		String serName = Long.toHexString(MPQ.getInstance().hash(word));
		this.w.setSerName(serName);
		setPath();
	}
	public Map<Long, WordFreq> getUidFreq() {
		return uidFreq;
	}
	public void setUidFreq(Map<Long, WordFreq> uidFreq) {
		this.uidFreq = uidFreq;
	}
	public Map<Long, WordPos> getUidPos() {
		return uidPos;
	}
	public void setUidPos(Map<Long, WordPos> uidPos) {
		this.uidPos = uidPos;
	}
	public Word getW() {
		return w;
	}
	public void setW(Word w) {
		this.w = w;
	}
	
	@Override
	public void save() throws Exception {
		try {
			HibernateSql hs = session -> {
				rsave(session);
				return null;
			};
			HibernateUtil.execute(hs);
		} catch (Exception e) {
			rrollback();
			throw e;
		}
	}
	@SuppressWarnings("unchecked")
	@Override
	public void rsave(Session session) throws IOException{
		//---------------begin ser-----------------------------
		try {
			this.oldUidFreq = (Map<Long, WordFreq>) Misc.readObject(freqFile);
			this.oldUidPos = (Map<Long, WordPos>) Misc.readObject(posFile);
		} catch (ClassNotFoundException | IOException e) {
			this.oldUidFreq = null;
			this.oldUidPos = null;
		}
		if(oldUidFreq != null && oldUidPos != null){
			uidFreq.putAll(oldUidFreq);
			uidPos.putAll(oldUidPos);
			w.setUrlCount(uidFreq.size());
		}
		Misc.writeObject(freqFile, (Serializable) uidFreq);
		Misc.writeObject(posFile, (Serializable) uidPos);
		//---------------end ser-----------------------------
		session.createSQLQuery("replace into word(word,urlcount,sername) values(:word,:urlCount,:serName)")
		.setString("word", w.getWord())
		.setLong("urlCount", w.getUrlCount())
		.setString("serName", w.getSerName()).executeUpdate();
	}
	@Override
	public void rrollback(){
		if(oldUidFreq != null && oldUidPos != null){
			try {
				Misc.writeObject(freqFile, (Serializable) oldUidFreq);
				Misc.writeObject(posFile, (Serializable) oldUidPos);
			} catch (IOException e) {
				e.printStackTrace();
				this.oldUidFreq = null;
				this.oldUidPos = null;
			}
		}
	}
	
	private void setPath() {
		String freqPath = freqInfo[0] + "/" + w.getSerName() + "." + freqInfo[1];
		String posPath = posInfo[0] + "/" + w.getSerName() + "." + posInfo[1];
		freqFile = new File(freqPath);
		posFile = new File(posPath);
	}
}
