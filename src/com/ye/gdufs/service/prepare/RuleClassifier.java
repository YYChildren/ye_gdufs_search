/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ye.gdufs.service.prepare;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.ye.gdufs.log.Logs;
import com.ye.gdufs.util.SentenceHandler;

/**
 *
 * @author Administrator
 */
public class RuleClassifier {
	private static HashSet<String> common ;
	private static HashMap<String, String> commonstrType;// 通用疑问词+名词->问题类型
	private static HashMap<String, String> quesnameType;// 普通疑问词->问题类型
	private final static String DEFAULT_TYPE = "other";
	public final static double HIGH_WEIGHT = 7.64;//实体名字的权重  和 类型作为关键词的权重
	public final static double MEDIUM_WEIGHT = 6.18;//除了通用疑问词+问题类型外  最靠近介词或者动词的数词，缩略词的权重
	public final static double LOW_WEIGHT = 3.82;//其它名词的权重
	public final static double LEAST_WEIGHT = 1.0;//DropPos的权重
	static {
		try {// 初始化规则
			RuleClassifier.updateCommonRule();// 异常则规则无法导入
		} catch (ParserConfigurationException | SAXException | IOException ex) {
			Logs.printStackTrace(ex);
		}
	}

	private List<Integer> vPos;// 动词位置
	private List<Integer> pPos;// 介词位置
	private List<Integer> rPos;// 代词（疑问词位置）
	private List<Integer> nPos;// the position of noun
	private List<Integer> mPos;// 数词位置 可用于答案抽取
	private List<Integer> dropPos;// 应该丢弃的位置

	private String type = DEFAULT_TYPE;;// 问题分类
	Map<String, Double> wordWeightMap;// 返回关键词及其权重

	public RuleClassifier() {
	}

	public static void updateCommonRule() throws ParserConfigurationException,
			SAXException, IOException {
		common = new HashSet<>();
		commonstrType = new HashMap<>();// 通用疑问词+名词->问题类型
		quesnameType = new HashMap<>();// 普通疑问词->问题类型
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		InputStream in = RuleClassifier.class.getResourceAsStream("/questype.xml");
		Document doc = db.parse(in);
		in.close();
		Element root = doc.getDocumentElement();
		NodeList children = root.getChildNodes();
		int i;
		int length = children.getLength();
		for (i = 0; i < length; ++i) {
			Node node = children.item(i);
			if (node.getNodeName().equals("common")) {
				List<String> tmp = Arrays.asList(node.getFirstChild()
						.getNodeValue().split("\\s+"));
				common.addAll(tmp);
			} else if (node.getNodeName().equals("queslist")) {
				initQuesList((Element) node);
			}
		}
	}

	private static void initQuesList(Element element) {
		NodeList nl = element.getChildNodes();
		int length = nl.getLength();
		int i;
		for (i = 0; i < length; ++i) {
			Node node = nl.item(i);
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				NodeList nodechildren = node.getChildNodes();
				StringBuilder typesb = new StringBuilder();
				Set<String> quesname = new HashSet<>();
				Set<String> commonstr = new HashSet<>();
				int j;
				int templength = nodechildren.getLength();
				for (j = 0; j < templength; ++j) {
					Node temp = nodechildren.item((j));
					Node no = temp.getFirstChild();
					if (no == null) {
						continue;
					}
					String tempstr = temp.getFirstChild().getNodeValue().trim();
					if ("".equals(tempstr)) {
						continue;
					}
					switch (temp.getNodeName()) {
						case "type":
							typesb.append(tempstr).append(" ");
							break;
						case "quesname": {
							List<String> templist = Arrays.asList(tempstr.split("\\s+"));
							quesname.addAll(templist);
							break;
						}
						case "commonstr": {
							List<String> templist = Arrays.asList(tempstr.split("\\s+"));
							commonstr.addAll(templist);
							break;
						}
					}
				}
				String tempType = typesb.toString().trim();
				for (String str : quesname) {
					quesnameType.put(str, tempType);
				}
				for (String str : commonstr) {
					commonstrType.put(str, tempType);
				}
			}
		}
	}

	public void classifyAndExtract(SentenceHandler sh) {
		int i;
		vPos = new ArrayList<>(1);// 动词位置
		pPos = new ArrayList<>(1);// 介词位置
		rPos = new ArrayList<>(1);
		nPos = new ArrayList<>(1);// the position of noun
		mPos = new ArrayList<>(1);
		dropPos = new ArrayList<>(1);
		int size = sh.getSize();
		for (i = 0; i < size; ++i) {
			switch (sh.getTags().get(i).charAt(0)) {
				case 'r':
					rPos.add(i);
					break;
				case 'v':
					vPos.add(i);
					break;
				case 'p':
					pPos.add(i);// prep
					break;
				case 'j':
				case 'n':
					nPos.add(i);
					break;
				case 'm':
					mPos.add(i);
					break;
				}
		}
		// n,m的列表为关键字
		if (rPos.size() > 0) {// 代词
			int rp = rPos.get(rPos.size() - 1);//
			List<String> words = sh.getSegs();
			String word = words.get(rp);
			if (common.contains(word)) {
				if (words.size() > rp + 1) {
					String commonStr = words.get(rp + 1);
					if (commonstrType.containsKey(commonStr)) {
						type = commonstrType.get(commonStr);
						dropPos.add(rp);
						if (nPos.contains(rp + 1)) {
							try {
								nPos.remove(rp + 1);// 去除无意义名词
							} catch (Exception ex) {
							}
							dropPos.add(rp + 1);
						}
					} else if (quesnameType.containsKey(word)) {
						dropPos.add(rp);//
						type = quesnameType.get(word);
					}
				} else if (quesnameType.containsKey(word)) {
					dropPos.add(rp);//
					type = quesnameType.get(word);
				}
			} else if (quesnameType.containsKey(word)) {
				dropPos.add(rp);//
				type = quesnameType.get(word);
			}
		}// 问题分类
		extractKeyWord(sh);
	}

	private void extractKeyWord(SentenceHandler sh) {
		boolean testNotContained = true;
		wordWeightMap = new HashMap<>();

		int pos = -1;
		if (!pPos.isEmpty()) {
			pos = pPos.get(pPos.size() - 1);
		} else if (!vPos.isEmpty()) {
			pos = vPos.get(vPos.size() - 1);
		}
		int mostShortDis = Integer.MAX_VALUE;// 最短距离
		int i;
		int size = sh.getSize();
		List<String> words = sh.getSegs();
		for (i = 0; i < size; ++i) {
			if (sh.getNers().get(i).contains("B-N")) {
				StringBuilder tempStr = new StringBuilder(words.get(i));
				do {
					try {
						String ts = words.get(++i);
						tempStr.append(ts);
						wordWeightMap.put(ts, MEDIUM_WEIGHT);
					} catch (Exception ex) {
						break;
					}
				} while (!sh.getNers().get(i).contains("E-N"));
				wordWeightMap.put(tempStr.toString(), HIGH_WEIGHT);
			} else if (sh.getNers().get(i).contains("S-N")) {// 命名实体
				wordWeightMap.put(words.get(i), HIGH_WEIGHT);
			} else if (nPos.contains(i)
					|| mPos.contains(i)
					|| (vPos.contains(i) && rPos.contains(i + 1) && pPos.size() > 0)
					|| (vPos.contains(i) && nPos.contains(i - 1) && rPos.contains(i - 2))) {
				// 名词，缩略词，数词位置,如果有介词，动词后为介词，则添加动词，否则不添加
				if (pos != -1) {
					int tempShortDis = Math.abs(pos - i);
					if (tempShortDis < mostShortDis) {
						mostShortDis = tempShortDis;
					}
				}

				try {
					testNotContained = true;
					testNotContained = wordWeightMap.get(words.get(i)) < MEDIUM_WEIGHT;
				} catch (Exception ex) {// 异常则为不包含
				}
				if (testNotContained) {
					wordWeightMap.put(words.get(i), LOW_WEIGHT);
				}
				if (vPos.contains(i)) {// 动词
					wordWeightMap.put(words.get(i), HIGH_WEIGHT);
				}
			}
		}

		if (nPos.contains(pos - mostShortDis)) {
			try {
				testNotContained = true;
				testNotContained = wordWeightMap.get(words.get(pos- mostShortDis)) < MEDIUM_WEIGHT;
			} catch (Exception ex) {
			}
			if (testNotContained) {
				wordWeightMap.put(words.get(pos - mostShortDis),MEDIUM_WEIGHT);// 最靠近动词或者介词的名词、数词、缩略词
			}
		}

		if (nPos.contains(pos + mostShortDis)) {
			try {
				testNotContained = true;
				testNotContained = wordWeightMap.get(words.get(pos + mostShortDis)) < MEDIUM_WEIGHT;
			} catch (Exception ex) {
			}
			if (testNotContained) {
				wordWeightMap.put(words.get(pos + mostShortDis),MEDIUM_WEIGHT);// 最靠近动词或者介词的名词、数词、缩略词
			}
		}
		for (Integer pp : vPos) {// 动词后是代词
			if (rPos.contains(pp + 1)) {
				dropPos.add(pp);
				dropPos.add(pp + 1);
			}
		}
		for (Integer pp : pPos) {// 介词后是代词
			if (rPos.contains(pp + 1)) {
				dropPos.add(pp);
				dropPos.add(pp + 1);
			}
		}
		
		for(int j = 0;j<size;j++){
			String word=words.get(j);
			if(dropPos.contains(j)){
				wordWeightMap.put(word, LEAST_WEIGHT);
			}else if (!wordWeightMap.containsKey(word)){
				wordWeightMap.put(word, LOW_WEIGHT);
			}
		}
	}
	/**
	 *
	 * @return 问题类型
	 */
	public String getType() {
		return type;
	}

	public Map<String, Double> getwordWeightMap() {
		return wordWeightMap;
	}

}
