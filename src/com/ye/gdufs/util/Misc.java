package com.ye.gdufs.util;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
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
	private final static int CONTENT_SUB_LEN = 1000;
	
	public static  void dumpObject(String key,Serializable o) throws Exception {
		try {
			byte[] b = MsgUtil.object2Bytes(o);
			DumpDao dumpDao = new DumpDaoImpl(key, b);
			dumpDao.save();
		} catch (IOException e) {
			Logs.printStackTrace(e);
		}
	}
	public static Serializable getDumpObject(String key) throws Exception{
		try {
			DumpDao dumpDao = new DumpDaoImpl();
			Dump dump = dumpDao.get(key);
			if (dump == null) {
				return null;
			}
			byte[] b = dump.getObjByte();
			Serializable obj = (Serializable) MsgUtil.bytes2Object(b);
			return obj;
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
		int length = bodyString.length();
		StringBuilder sb = new StringBuilder();
		sb.append(html.title());
		int[] starts = {0,(length - CONTENT_SUB_LEN )/2,length - CONTENT_SUB_LEN };
		for(int start : starts){
			if(start < 0 ){
				break;
			}
			int endT = start + CONTENT_SUB_LEN ;
			int end = endT < length ? endT : length;
			sb.append(bodyString.substring(start, end));
		}
		return MsgUtil.msgDigest(sb.toString());
	}
	public static SentenceHandler analyzeSentence(String sentence) throws Exception{
		if(shf == null){
			shf = SHFactory.getInstance();
		}
		return shf.buildHandler().analyze(sentence);
	}
}
