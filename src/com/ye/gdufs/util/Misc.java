package com.ye.gdufs.util;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;

import org.jsoup.nodes.Document;

import com.ye.gdufs.dao.DumpDao;
import com.ye.gdufs.dao.DumpDaoImpl;
import com.ye.gdufs.log.Logs;
import com.ye.gdufs.model.Dump;


public class Misc {
	private static SHFactory shf ;
	
	public static  void dumpObject(String key,Object obj) throws Exception {
		try {
			DumpDao dumpDao = new DumpDaoImpl(key, obj);
			dumpDao.save();
		} catch (IOException e) {
			Logs.printStackTrace(e);
		}
	}
	public static Object getDumpObject(String key) throws Exception{
		try {
			DumpDao dumpDao = new DumpDaoImpl();
			Dump dump = dumpDao.get(key);
			if (dump == null) {
				return null;
			}
			return dump.getObj();
		} catch (ClassNotFoundException | IOException e) {
			Logs.printStackTrace(e);
			return null;
		} 
	}
	public static void writeObject(File file, Object ser) throws FileNotFoundException, IOException{
		ObjectOutputStream oos = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(file))); 
		oos.writeObject(ser);
		oos.close();
	}
	public static Object readObject(File file) throws FileNotFoundException, IOException, ClassNotFoundException{
		ObjectInputStream ois = new ObjectInputStream(new BufferedInputStream(new FileInputStream(file)));
		Object ser =  ois.readObject();
		ois.close();
		return ser;
	}
	public static void writeObjects(File file, Object[] sers,int num) throws FileNotFoundException, IOException{
		ObjectOutputStream oos = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(file))); 
		for(int i=0;i<num;++i){
			oos.writeObject(sers[i]);
		}
		oos.close();
	}
	public static Object[] readObjects(File file,int num) throws FileNotFoundException, IOException, ClassNotFoundException{
		
		ObjectInputStream ois = new ObjectInputStream(new BufferedInputStream(new FileInputStream(file)));
		Object[] sers = new Object[num];
		for(int i=0;i<num;++i){
			sers[i] = ois.readObject();
		}
		ois.close();
		return sers;
	}
	
	public static String getContentMd5(Document html) throws NoSuchAlgorithmException, UnsupportedEncodingException {
		String bodyString = html.body().toString();
		return MsgUtil.msgDigest(bodyString);
	}
	public static SentenceHandler analyzeSentence(String sentence) throws Exception{
		if(shf == null){
			shf = SHFactory.getInstance();
		}
		return shf.buildHandler().analyze(sentence);
	}
}
