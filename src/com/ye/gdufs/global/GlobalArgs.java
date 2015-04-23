package com.ye.gdufs.global;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.URL;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public final class GlobalArgs {
	private static Document doc;
	static {
		try {
			update();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void update()
			throws IOException {
		URL gdufsSearchXmlUrl = GlobalArgs.class.getResource("/gdufs_search.xml");
		BufferedInputStream bs = new BufferedInputStream(gdufsSearchXmlUrl.openStream());
		StringBuilder htmlBuilder = new StringBuilder();
		int nbytes = 0;
		byte[]  b = new byte[8192];
		String charSet = "UTF-8";
		while((nbytes = bs.read(b)) != -1){
			htmlBuilder.append(new String(b,0,nbytes,charSet));
		}
		doc = Jsoup.parse(htmlBuilder.toString());
	}

	public static Document getDoc() {
		return doc;
	}

	public static boolean isSeedsUpdate() {
		return !doc.select("seeds[update=true]").isEmpty();
	}
	
	public static String seedsUrlFilter() {
		return doc.select("seeds").first().attr("urlfilter");
	}
	
	public static String[] getSeeds() {
		Elements els = doc.select("seeds").first().getElementsByTag("value");
		String[] result = new String[els.size()];
		int i = 0;
		for (Element e : els) {
			result[i++] = e.text().trim();
		}
		return result;
	}

	public static boolean isThreadUpdate() {
		return !doc.select("thread[update=true]").isEmpty();
	}

	public static int getCrawlThread() {
		return Integer.parseInt(doc.select("crawlthread").first().select("value")
				.first().text().trim());
	}
	
	public static int getPageThread() {
		return Integer.parseInt(doc.select("pagethread").first().select("value")
				.first().text().trim());
	}

	public static boolean isUrlFinishUpdate() {
		return Boolean.parseBoolean(doc.select("urlfinishupdate").first().text().trim());
	}

	public static String getCwsModelPath() {
		return doc.select("model[type=cws]").first().getElementsByTag("value")
				.text().trim();
	}

	public static String getPosModelPath() {
		return doc.select("model[type=pos]").first().getElementsByTag("value")
				.text().trim();
	}

	public static String getNerModelPath() {
		return doc.select("model[type=ner]").first().getElementsByTag("value")
				.text().trim();
	}

	public static int getSeedDepth() {
		// TODO Auto-generated method stub
		return Integer.parseInt(doc.select("seeds").first().attr("depth"));
	}


	public static String[] getCrawlDataInfo() {
		return getDataInfo("crawlData");
	}
	public static String[] getDumpInfo() {
		return getDataInfo("dump");
	}
	public static String[] getPageInfo(){
		return getDataInfo("page");
	}
	public static String[] getWordPosInfo() {
		return getDataInfo("wordpos");
	}
	public static String[] getWordFreqInfo(){
		return getDataInfo("wordfreq");
	}
	private static String[] getDataInfo(String type){
		Element data = doc.select("data[basepath]").first();
		String basePath = data.attr("basepath"); 
		Element subData =  data.getElementsByTag(type).first();
		String path = basePath + "/" + subData.attr("dir");
		String extend = subData.attr("extend");
		String[] result = {path,extend};
		return result;
	}
	public static String[] getErrorLogsinfo(){
		Element logs = doc.select("logs[base]").first();
		String basePath = logs.attr("base");
		String extend = logs.attr("extend");
		String errorName = logs.getElementsByTag("error").first().text();
		String[] result =  {basePath,errorName,extend};
		return result;
	}
}