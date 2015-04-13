package commtest;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.WeakHashMap;
import java.util.function.BiConsumer;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Entities;
import org.junit.Test;

import com.ye.gdufs.util.MPQ;
import com.ye.gdufs.util.MsgUtil;
import com.ye.gdufs.util.Paragraph;
import com.ye.gdufs.util.SHFactory;
import com.ye.gdufs.util.SentenceHandler;

public class ATest {
	@Test
	public void testDate(){
		System.out.println(LocalDateTime.now());
	}
	@Test
	public void testHashMap(){
		HashMap<Integer,Integer> kv = new HashMap<>();
		kv.put(1, 1);
		kv.put(2, 2);
		kv.put(3, 3);
		kv.put(4, 4);
		kv.put(5, 5);
		kv.put(6, 6);
		kv.put(7, 7);
		kv.put(8, 8);
		kv.put(9, 9);
		ArrayList<Integer> vvL = new ArrayList<>();
		BiConsumer<Integer, Integer> action = new BiConsumer<Integer, Integer>(){
			@Override
			public void accept(Integer t, Integer u) {
				vvL.add(u*t);
			}  
		};
		System.out.println(vvL);
		kv.forEach(action );
		System.out.println(vvL);
		vvL.forEach(v -> System.out.println(v));
	}
	@Test 
	public void testMd5() throws NoSuchAlgorithmException, UnsupportedEncodingException{
		String str = "asdjkfgyiabcv我是中国kbxkifdghvkjsudf";
		System.out.println(MsgUtil.msgDigest(str));
		System.out.println(MsgUtil.msgDigest(str));
		System.out.println(MPQ.getInstance().hash(str));
		System.out.println(MPQ.getInstance().hash(str));
		str = "sdjkfgyiaubcv我是中国人kbxkifdghvkjsudhgdyt";
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
