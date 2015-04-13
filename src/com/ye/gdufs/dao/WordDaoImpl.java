package com.ye.gdufs.dao;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.util.Map;

import org.hibernate.Session;

import com.ye.gdufs.GlobalArgs;
import com.ye.gdufs.log.Logs;
import com.ye.gdufs.model.Word;
import com.ye.gdufs.model.WordPos;
import com.ye.gdufs.util.HibernateSql;
import com.ye.gdufs.util.HibernateUtil;
import com.ye.gdufs.util.MPQ;
import com.ye.gdufs.util.Misc;

public class WordDaoImpl implements WordDao {
	private Map<Long, WordPos> oldUidPos;
	private Map<Long, Integer> oldUidTitleFreq;
	private Map<Long, Integer> oldUidBodyFreq;

	private Word w = new Word();
	private Map<Long, WordPos> uidPos;
	private Map<Long, Integer> uidTitleFreq;
	private Map<Long, Integer> uidBodyFreq;
	private File freqFile;
	private File posFile;
	private static String[] freqInfo;
	private static String[] posInfo;
	static {
		freqInfo = GlobalArgs.getWordFreqInfo();
		posInfo = GlobalArgs.getWordPosInfo();
	}

	public WordDaoImpl() {
	}

	public WordDaoImpl(String word, Map<Long, WordPos> uidPos,
			Map<Long, Integer> uidTitleFreq, Map<Long, Integer> uidBodyFreq) {
		this.uidPos = uidPos;
		this.w.setWord(word);
		this.w.setUidTitleCount(uidTitleFreq.size());
		this.w.setUidBodyCount(uidBodyFreq.size());
		this.uidTitleFreq = uidTitleFreq;
		this.uidBodyFreq = uidBodyFreq;
		String serName = Long.toHexString(MPQ.getInstance().hash(word));
		this.w.setSerName(serName);
		setPath();
	}

	public Word getW() {
		return w;
	}

	public void setW(Word w) {
		this.w = w;
	}

	public Map<Long, WordPos> getUidPos() {
		return uidPos;
	}

	public void setUidPos(Map<Long, WordPos> uidPos) {
		this.uidPos = uidPos;
	}

	public Map<Long, Integer> getUidTitleFreq() {
		return uidTitleFreq;
	}

	public void setUidTitleFreq(Map<Long, Integer> uidTitleFreq) {
		this.uidTitleFreq = uidTitleFreq;
	}

	public Map<Long, Integer> getUidBodyFreq() {
		return uidBodyFreq;
	}

	public void setUidBodyFreq(Map<Long, Integer> uidBodyFreq) {
		this.uidBodyFreq = uidBodyFreq;
	}

	@Override
	public void save() throws Exception {
		HibernateSql hs = session -> {
			rsave(session);
			return null;
		};
		try {
			HibernateUtil.execute(hs);
		} catch (Exception e) {
			rrollback();
			throw e;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public void rsave(Session session) throws IOException {
		session.createSQLQuery(
				"replace into word(word,uidtitlecount,uidbodycount,sername) values(:word,:uidtitlecount,:uidbodycount,:serName)")
				.setString("word", w.getWord())
				.setLong("uidtitlecount", w.getUidTitleCount())
				.setLong("uidbodycount", w.getUidBodyCount())
				.setString("serName", w.getSerName()).executeUpdate();
		// ---------------begin ser-----------------------------
		try {
			this.oldUidPos = (Map<Long, WordPos>) Misc.readObject(posFile);
			Object[] sers = Misc.readObjects(freqFile, 2);
			this.oldUidTitleFreq = (Map<Long, Integer>) sers[0];
			this.oldUidBodyFreq = (Map<Long, Integer>) sers[1];
		} catch (ClassNotFoundException | IOException e) {
			this.oldUidPos = null;
			this.oldUidTitleFreq = null;
			this.oldUidBodyFreq = null;
		}
		if (oldUidPos != null && this.oldUidTitleFreq != null
				&& this.oldUidBodyFreq != null) {
			uidPos.putAll(oldUidPos);
			uidTitleFreq.putAll(oldUidTitleFreq);
			uidBodyFreq.putAll(oldUidBodyFreq);
			w.setUidTitleCount(uidTitleFreq.size());
			w.setUidBodyCount(uidBodyFreq.size());
		}

		Misc.writeObject(posFile, (Serializable) uidPos);
		Object[] sers = { uidTitleFreq, uidBodyFreq };
		Misc.writeObjects(freqFile, sers, 2);
		// ---------------end ser-----------------------------
	}

	@Override
	public void rrollback() {
		if (oldUidPos != null && this.oldUidTitleFreq != null
				&& this.oldUidBodyFreq != null) {
			try {
				Misc.writeObject(posFile, (Serializable) oldUidPos);
				Object[] oldSers = { oldUidTitleFreq, oldUidBodyFreq };
				Misc.writeObjects(freqFile, oldSers, 2);
			} catch (IOException e) {
				Logs.printStackTrace(e);
				this.oldUidPos = null;
				this.oldUidTitleFreq = null;
				this.oldUidBodyFreq = null;
			}
		}
	}

	private void setPath() {
		String freqPath = freqInfo[0] + "/" + w.getSerName() + "."
				+ freqInfo[1];
		String posPath = posInfo[0] + "/" + w.getSerName() + "." + posInfo[1];
		freqFile = new File(freqPath);
		posFile = new File(posPath);
	}

	@Override
	public void get(String word) throws Exception {
		w = (Word) HibernateUtil.get(Word.class, word);
		setPath();
		loadUidPos();
		loadUidFreq();
	}

	@SuppressWarnings("unchecked")
	private synchronized void loadUidFreq() throws FileNotFoundException,
			ClassNotFoundException, IOException {
		if (this.uidTitleFreq == null || this.uidBodyFreq == null) {
			Object[] sers = Misc.readObjects(freqFile, 2);
			this.uidTitleFreq = (Map<Long, Integer>) sers[0];
			this.uidBodyFreq = (Map<Long, Integer>) sers[1];
		}
	}

	@SuppressWarnings("unchecked")
	private synchronized void loadUidPos() throws FileNotFoundException,
			ClassNotFoundException, IOException {
		if (this.uidPos == null) {
			this.uidPos = (Map<Long, WordPos>) Misc.readObject(posFile);
		}
	}
}
