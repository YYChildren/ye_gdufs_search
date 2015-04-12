package commtest;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.WeakHashMap;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Entities;
import org.junit.Test;

import com.ye.gdufs.log.Logs;
import com.ye.gdufs.util.MPQ;
import com.ye.gdufs.util.MsgUtil;
import com.ye.gdufs.util.Paragraph;
import com.ye.gdufs.util.SHFactory;
import com.ye.gdufs.util.SentenceHandler;

public class ATest {
	@Test 
	public void testMd5() throws NoSuchAlgorithmException, UnsupportedEncodingException{
		String str = "asdjkfgyiaubcv我是中国人kbxkifdghvkjsudf";
		System.out.println(MsgUtil.msgDigest(str));
		System.out.println(MsgUtil.msgDigest(str));
		System.out.println(MPQ.getInstance().hash(str));
		System.out.println(MPQ.getInstance().hash(str));
		str = "asdjkfgyiaubcv我是中国人kbxkifdghvkjsud";
		System.out.println(MsgUtil.msgDigest(str));
		System.out.println(MsgUtil.msgDigest(str));
		System.out.println(MPQ.getInstance().hash(str));
		System.out.println(MPQ.getInstance().hash(str));
	}
	@Test
	public void test_byte(){
		byte b = (byte)0x0FFF;
		
		System.out.println(b);
		System.out.println(b & 0xff);
		System.out.println(b+1);
	}
	@Test
	public void testWeakHashMap(){
		WeakHashMap<Integer,Integer> w = new WeakHashMap<Integer,Integer>();
		int i = 1000;
		while(i-->0){
			w.put(i, i);
		}
		System.gc();
		System.out.println(w.size());
	}
	
	@Test
	public void testArray(){
		ArrayList<String> a = new ArrayList<String>();
		a.add(null);
		a.add(null);
		System.out.println(a);
	}
	
	@Test
	public void testHex(){
		long seed1 = -123456789;
	    System.out.println(Long.toUnsignedString(seed1, 1));
	}
	@Test
	public void testNum(){
		long x = Long.MAX_VALUE;
		x = x + x;
		x = x + x;
		System.out.println(x);
	}
	
	@Test
	public void test() throws Exception{
		SHFactory.getInstance().buildHandler().analyze("                ");
	}

	public static void main(String[] args) throws Exception {
		File f = new File("G:/mysql 二进制(1K)_百度搜索.html");
		Entities.EscapeMode.base.getMap().clear();
		Document doc = Jsoup.parse(f, "UTF-8");
		doc = Jsoup.connect("http://epaper.southcn.com/nfdaily/html/2015-03/14/content_7407770.htm")
				.timeout(5000).get();
		for(String txt: Paragraph.extractSentence(doc)){
			SentenceHandler sh = SHFactory.getInstance().buildHandler().analyze(txt);
			System.out.println(sh.getSegs());
			continue;
		}
	}
}
