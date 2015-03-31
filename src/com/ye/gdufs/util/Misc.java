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
import com.ye.gdufs.model.Dump;


public class Misc {
	private static SHFactory shf = SHFactory.getInstance();
	
	public static  void dumpObject(String key,Serializable o) throws Exception {
		try {
			byte[] b = MsgUtil.object2Bytes(o);
			DumpDao dumpDao = new DumpDaoImpl(key, b);
			dumpDao.save();
		} catch (IOException e) {
			e.printStackTrace();
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
			e.printStackTrace();
			return null;
		} 
	}
	public static void writeObject(File file, Serializable ser) throws FileNotFoundException, IOException{
		ObjectOutputStream oos = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(file))); 
		oos.writeObject(ser);
		oos.close();
	}
	public static Serializable readObject(File file) throws FileNotFoundException, IOException, ClassNotFoundException{
		ObjectInputStream ois = new ObjectInputStream(new BufferedInputStream(new FileInputStream(file)));
		Serializable ser =  (Serializable) ois.readObject();
		ois.close();
		return ser;
	}
	
	public static String getContentMd5(Document html) throws NoSuchAlgorithmException, UnsupportedEncodingException {
		int subLen = 100;
		String bodyString = html.body().toString();
		int length = bodyString.length();
		StringBuilder sb = new StringBuilder();
		sb.append(html.title());
		int[] starts = {0,(length - subLen)/2,length - subLen};
		for(int start : starts){
			if(start <=0 ){
				break;
			}
			int endT = start + subLen;
			int end = endT < length ? endT : length;
			sb.append(bodyString.substring(start, end));
		}
		return MsgUtil.msgDigest(sb.toString());
	}
	public static SentenceHandler analyzeSentence(String sentence) throws Exception{
		return shf.buildHandler().analyze(sentence);
	}
}
