package com.ye.gdufs;

import java.io.File;
import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public final class GlobalArgs {
	private static Document doc;
	static {
		try {
			update("gdufs_search.xml", "UTF-8");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void update(String xmlPath, String charsetName)
			throws IOException {
		File f = new File(xmlPath);
		doc = Jsoup.parse(f, charsetName);
	}

	public static Document getDoc() {
		return doc;
	}

	// public static boolean isUpdate(){
	// return Boolean.parseBoolean(doc.select("update").first().text().trim());
	// }
	public static boolean isSeedsUpdate() {
		return !doc.select("seeds[update=true]").isEmpty();
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

	
	public static String[] getPageInfo(){
		return getDataInfo("page");
	}
	public static String[] getWordUrlsInfo(){
		return getDataInfo("wordurls");
	}
	public static String[] getWordUrlPosInfo(){
		return getDataInfo("wordurlpos");
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
}