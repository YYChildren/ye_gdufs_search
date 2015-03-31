package commtest;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Entities;

import com.ye.gdufs.util.Paragraph;
import com.ye.gdufs.util.SHFactory;
import com.ye.gdufs.util.SentenceHandler;

public class Test {

	public static void main(String[] args) throws Exception {
		File f = new File("G:/mysql 二进制(1K)_百度搜索.html");
		Entities.EscapeMode.base.getMap().clear();
		Document doc = Jsoup.parse(f, "UTF-8");
		doc = Jsoup.connect("http://id.gdufs.edu.cn/")
				.timeout(5000).get();
		for(String txt: Paragraph.extractSentence(doc)){
			SentenceHandler sh = SHFactory.getInstance().buildHandler().analyze(txt);
			System.out.println(sh.getSegs());
			continue;
		}
	}
}
