package com.ye.gdufs.dao;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.hibernate.Session;

import com.ye.gdufs.GlobalArgs;
import com.ye.gdufs.model.WordUrlPos;
import com.ye.gdufs.model.WordUrlPosPk;
import com.ye.gdufs.model.WordUrlPosSer;
import com.ye.gdufs.util.HibernateSql;
import com.ye.gdufs.util.HibernateUtil;
import com.ye.gdufs.util.Misc;

public class WordUrlPosDaoImpl extends SearchPath implements WordUrlPosDao{

	WordUrlPos wup;
	WordUrlPosSer wupSer;
	private static String[] info;
	static{
		info = GlobalArgs.getWordUrlPosInfo(); 
	}
	
	public WordUrlPosDaoImpl(){}
	public WordUrlPosDaoImpl(WordUrlPos wup,WordUrlPosSer wupSer) {
		this.wup = wup;
		this.wupSer = wupSer;
		setPath();
	}
	public WordUrlPos getWup() {
		return wup;
	}
	public void setWup(WordUrlPos wup) {
		this.wup = wup;
		setPath();
	}
	public WordUrlPosSer getWupSer() {
		return wupSer;
	}
	public void setWupSer(WordUrlPosSer wupSer) {
		this.wupSer = wupSer;
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
	@Override
	public void rsave(Session session) throws FileNotFoundException, IOException{
		WordUrlPosPk pk = wup.getWordUrlPk();
		boolean isNotExisted = session.createQuery("select wup.wordUrlPk "
				+ "from WordUrlPos wup where wup.wordUrlPk.word=:word and "
				+ "wup.wordUrlPk.urlMd5=:md5")
			.setString("word", pk.getWord())
			.setString("md5", pk.getUrlMd5())
			.list().isEmpty();
		if(isNotExisted){
			session.save(wup);
		}else{
			session.update(wup);
		}
		serSave();
	}
	@Override
	public void rrollback(){
		if(file.exists()){
			file.delete();
		}
	}
	@Override
	public void serSave() throws FileNotFoundException, IOException {
		Misc.writeObject(file, wupSer);
	}
	
	@Override
	protected void setPath() {
		path = info[0] + "/" + wup.getSerName() + "." + info[1];
		file = new File(path);
	}
}
