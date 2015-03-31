package com.ye.gdufs.dao;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.hibernate.Session;

import com.ye.gdufs.GlobalArgs;
import com.ye.gdufs.model.WordUrls;
import com.ye.gdufs.model.WordUrlsSer;
import com.ye.gdufs.util.HibernateSql;
import com.ye.gdufs.util.HibernateUtil;
import com.ye.gdufs.util.Misc;

public class WordUrlsDaoImpl extends SearchPath implements WordUrlsDao{

	WordUrls wus;
	WordUrlsSer wusSer;
	private static String[] info;
	static{
		info = GlobalArgs.getWordUrlsInfo(); 
	}
	
	public WordUrlsDaoImpl(){}
	public WordUrlsDaoImpl(WordUrls wus,WordUrlsSer wusSer) {
		this.wus = wus;
		this.wusSer = wusSer;
		setPath();
	}
	
	public WordUrls getWus() {
		return wus;
	}
	public void setWus(WordUrls wus) {
		this.wus = wus;
		setPath();
	}
	public WordUrlsSer getWusSer() {
		return wusSer;
	}
	public void setWusSer(WordUrlsSer wusSer) {
		this.wusSer = wusSer;
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
		boolean isNotExisted = session.createQuery("select wus.word from WordUrls wus where wus.word=:word")
			.setString("word", wus.getWord())
			.list().isEmpty();
		if(isNotExisted){
			session.save(wus);
		}else{
			session.update(wus);
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
		Misc.writeObject(file, wusSer);
	}
	
	@Override
	protected void setPath() {
		path = info[0] + "/" + wus.getSerName() + "." + info[1];
		file = new File(path);
	}
}
