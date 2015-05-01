package com.ye.gdufs.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Map.Entry;
import java.util.Set;
import java.util.WeakHashMap;

import com.ye.gdufs.dao.PageDaoImpl;
import com.ye.gdufs.dao.WordDaoImpl;
import com.ye.gdufs.model.Page;
import com.ye.gdufs.model.PageSer;
import com.ye.gdufs.model.ResultPage;
import com.ye.gdufs.model.WordPos;
import com.ye.gdufs.service.prepare.RuleClassifier;

public class Extractor {
	private static WeakHashMap<Long, PageDaoImpl> uidPdiMap = new WeakHashMap<>();
	private final static int DEFAULT_COUNT = 1;
	private final static int DEFAULT_ANSWER_SIZE = 4;
	private final static int MAX_ANS = 5;
	private final static String END_CHARS = "。?!;";// 结束符号。
	private ArrayList<WordDaoImpl> wdiArr;
	private Map<String, Double> wordWeightMap;
	private String quesType;
	private List<Entry<Long, Double>> uidWeightList;
	private Op op;
	Map<String, Double> ansWeightMap;
	// ------------------To return ----------------------------//
	private List<String> ansList;
	private List<ResultPage> resultL = new ArrayList<>();

	public Extractor() {
		super();
	}

	public Extractor(ArrayList<WordDaoImpl> wdiArr,
			Map<String, Double> wordWeightMap, String quesType,
			List<Entry<Long, Double>> uidWeightList) {
		this.wdiArr = wdiArr;
		this.wordWeightMap = wordWeightMap;
		this.quesType = quesType;
		this.uidWeightList = uidWeightList;
		decideDoFun();
	}

	private void decideDoFun() {
		switch (quesType) {
		case "person":
			op = new Op() {
				@Override
				public Map<String, Double> doFun(String sentence,
						List<String> segs, List<String> tags,
						List<String> ners, double uidWeight) {
					return doPeople(sentence, segs, tags, ners, uidWeight);
				}
			};
			ansWeightMap = new HashMap<>();
			ansList = new ArrayList<>();
			break;
		case "time":
		case "number":
			op = new Op() {
				@Override
				public Map<String, Double> doFun(String sentence,
						List<String> segs, List<String> tags,
						List<String> ners, double uidWeight) {
					return doNumber(sentence, segs, tags, ners, uidWeight);
				}
			};
			ansWeightMap = new HashMap<>();
			ansList = new ArrayList<>();
			break;
		case "location":
			op = new Op() {
				@Override
				public Map<String, Double> doFun(String sentence,
						List<String> segs, List<String> tags,
						List<String> ners, double uidWeight) {
					return doLocation(sentence, segs, tags, ners, uidWeight);
				}
			};
			ansWeightMap = new HashMap<>();
			ansList = new ArrayList<>();
			break;
		case "definition":
		case "reason":
			op = new Op() {
				@Override
				public Map<String, Double> doFun(String sentence,
						List<String> segs, List<String> tags,
						List<String> ners, double uidWeight) {
					return doOtherFun(sentence, segs, tags, ners, uidWeight);
				}
			};
			ansWeightMap = new HashMap<>();
			ansList = new ArrayList<>();
			break;
		case "other":
		default:
			op = null;
		}
	}

	
	public List<String> getAnsList() {
		return ansList;
	}

	public List<ResultPage> getResultList() {
		return resultL;
	}

	public void setUidWeightList(List<Entry<Long, Double>> uidWeightList) {
		this.uidWeightList = uidWeightList;
	}

	public void extract() {
		uidWeightList.forEach(uidWeightMap -> {
			long uid = uidWeightMap.getKey();
			double uidWeight = uidWeightMap.getValue();
			PageDaoImpl pd = null;
			if (uidPdiMap.containsKey(uid)) {
				pd = uidPdiMap.get(uid);
			} else {
				pd = new PageDaoImpl();
				pd.get(uid);
				uidPdiMap.put(uid, pd);
			}
			Page page = pd.getPage();
			PageSer pageSer = pd.getPageSer();
			if (page != null && pageSer != null) {
				List<String> titleSegs = pageSer.getTitleSegments();
				List<String> titleTags = pageSer.getTitlePostags();
				List<String> titleNers = pageSer.getTitleNers();
				List<List<String>> bodySegs = pageSer.getBodySegments();
				List<List<String>> bodyTags = pageSer.getBodyPostags();
				List<List<String>> bodyNers = pageSer.getBodyNers();
				ResultPage rs = new ResultPage();
				String url = page.getUrl();
				rs.setUrl(url);
				extractTitle(uid,uidWeight,titleSegs,titleTags,titleNers, rs);
				extractBody(uid, uidWeight, bodySegs, bodyTags,bodyNers, rs);
				resultL.add(rs);
			}
		});
		exetactHeadAns();
	}

	//提取最佳的前五答案
	private void exetactHeadAns() {
		if(op != null){
			PriorityQueue<Entry<String, Double>> ansWeight = new PriorityQueue<Entry<String, Double>>(MAX_ANS, new Comparator<Entry<String, Double>>(){
				@Override
				public int compare(Entry<String, Double> o1, Entry<String, Double> o2) {
					return o1.getValue().compareTo(o2.getValue());
				}});
			Set<Entry<String, Double>> enSet = ansWeightMap.entrySet();
			enSet.forEach( en -> {
				if(ansWeight.size() < MAX_ANS){
					ansWeight.offer(en);
				}else{
					if(ansWeight.peek().getValue() < en.getValue()){
						ansWeight.poll();
						ansWeight.offer(en);
					}
				}
			});
			int i = ansWeight.size();
			while(--i >=0 ){
				ansList.add(ansWeight.poll().getKey());
			}
			Collections.reverse(ansList);
		}
	}

	private void extractTitle(long uid,double uidWeight,List<String> titleSegs,List<String>titleTags,List<String> titleNers,ResultPage rs) {
		double titleWeight = 0.0;
		boolean emBegin = false;
		StringBuilder titleBuilder = new StringBuilder();
		for (String word : titleSegs) {
			if (wordWeightMap.containsKey(word)) {
				titleWeight +=  wordWeightMap.get(word);
				if (!emBegin) {
					titleBuilder.append("<em>");
					emBegin = true;
				}
			} else {
				if (emBegin) {
					titleBuilder.append("</em>");
					emBegin = false;
				}
			}
			titleBuilder.append(word);
		}
		titleWeight *= uidWeight;
		String sentence = titleBuilder.toString();
		if(op!=null && titleWeight != 0.0){
			Map<String, Double> ansWeightMapT = op.doFun(sentence,titleSegs, titleTags, titleNers, titleWeight);
			ansWeightMapT.forEach((ans, weight) -> {ansWeightMap.put(ans,ansWeightMap.getOrDefault(ans, 0.0)+ weight);});
		}
		rs.setTitle(sentence);
	}

	private void extractBody(long uid, double uidWeight,
			List<List<String>> bodySegs, List<List<String>> bodyTags,
			List<List<String>> bodyNers,ResultPage rs) {
		//计算每段的权重
		Map<Integer, Double> paraWeightMap = new HashMap<>();
		wdiArr.forEach(wdi -> {
			String word = wdi.getW().getWord();
			WordPos wordPos = wdi.getUidPos().get(uid);
			if (wordPos != null) {
				List<Integer> wordIPos = wordPos.getBodyWordIPos();
				if (wordIPos != null) {
					int length = wordIPos.size();
					for (int i = 0; i < length; ++i) {
						int ipara = wordIPos.get(i);
						double weight = paraWeightMap.getOrDefault(i, 0.0) 
								+ uidWeight* wordWeightMap.getOrDefault(word,RuleClassifier.LEAST_WEIGHT);
						paraWeightMap.put(ipara, weight);
					}
				}
			}
		});
		//计算结果
		if (op != null) {
			Set<Integer> emIPosSet = new HashSet<>();
			for (WordDaoImpl wdi : wdiArr) {
				WordPos wordPos = wdi.getUidPos().get(uid);
				if (wordPos != null) {
					List<Integer> wordIPos = wordPos.getBodyWordIPos();
					if (wordIPos != null) {
						emIPosSet.addAll(wordIPos);
					}
				}
			}
			if (!emIPosSet.isEmpty()) {
				emIPosSet.forEach(maxIpara -> {
					// --------------------取出段中关键词位置---------------------------//
						List<Integer> emJPos = new ArrayList<>();
						for (WordDaoImpl wdi : wdiArr) {
							WordPos wordPos = wdi.getUidPos().get(uid);
							if (wordPos != null) {
								List<Integer> wordIPos = wordPos.getBodyWordIPos();
								List<Integer> wordJPos = wordPos.getBodyWordJPos();
								if (wordIPos != null) {
									int[]  be = getBE(wordIPos,maxIpara);
									if( be != null){
										for(int i = be[0];i<=be[1];++i){
											emJPos.add(wordJPos.get(i));
										}
									}
								}
							}
						}
						Collections.sort(emJPos);
						List<String> lbodySegs = bodySegs.get(maxIpara);
						List<String> lbodyTags = bodyTags.get(maxIpara);
						List<String> lbodyNers = bodyNers.get(maxIpara);
						String sentence = subSentence(emJPos, lbodySegs);
						double paraWeight = paraWeightMap.get(maxIpara);
						Map<String, Double> ansWeightMapT = op.doFun(sentence,lbodySegs, lbodyTags, lbodyNers, paraWeight);
						ansWeightMapT.forEach((ans, weight) -> {ansWeightMap.put(ans,ansWeightMap.getOrDefault(ans, 0.0)+ weight);});
					});
			}
		} 
		
		// -------------------取出权值最大的段-------------------------------//
		int maxIpara = -1;
		double maxWeight = Double.MIN_VALUE;
		for (Entry<Integer, Double> e : paraWeightMap.entrySet()) {
			int i = e.getKey();
			double weight = e.getValue();
			if (weight > maxWeight) {
				maxWeight = weight;
				maxIpara = i;
			}
		}
		// --------------------取出段中关键词位置---------------------------//
		if(maxIpara != -1){
			List<Integer> emJPos = new ArrayList<>();
			for (WordDaoImpl wdi : wdiArr) {
				WordPos wordPos = wdi.getUidPos().get(uid);
				if (wordPos != null) {
					List<Integer> wordIPos = wordPos.getBodyWordIPos();
					List<Integer> wordJPos = wordPos.getBodyWordJPos();
					if (wordIPos != null) {
						int[]  be = getBE(wordIPos,maxIpara);
						if( be != null){
							for(int i = be[0];i<=be[1];++i){
								emJPos.add(wordJPos.get(i));
							}
						}
					}
				}
			}
			Collections.sort(emJPos);
			List<String> lbodySegs = bodySegs.get(maxIpara);
			String maxWStr =  subSentence(emJPos, lbodySegs);
			rs.setBody(maxWStr);
		}
	}
	
	private int[] getBE(List<Integer> wordIPos,int value){
		int len = wordIPos.size();
		int begin = 0;
		int end = len - 1;
		int middle = -1;
		int middleValue;
		boolean isContained = false;
		while(begin <= end){
			middle = (begin + end)/2;
			middleValue = wordIPos.get(middle);
			if(value > middleValue){
				begin = middle + 1;
			}else if(value < middleValue){
				end = middle-1;
			}else{
				isContained = true;
				break;
			}
		}
		if(!isContained){//如果不存在
			return null;
		}
		begin = middle;
		while(--begin>=0 && wordIPos.get(begin) == value);
		end = middle;
		while(++end < len && wordIPos.get(end) == value);
		int[] result = {++begin,--end};
		return result;
	}

	private String subSentence(List<Integer> emJPos, List<String> lbodySegs) {
		StringBuilder bodyBuilder = new StringBuilder();
		if (emJPos.isEmpty()) {
			for (int i = 0; i < 100; i++) {
				try {
					bodyBuilder.append(lbodySegs.get(i));
				} catch (Exception e) {
					break;
				}
			}
		} else {
			int begin = emJPos.get(0);
			while (--begin >= 0 && !END_CHARS.contains(lbodySegs.get(begin)));// 提取一句话
			++begin;
			int length = lbodySegs.size();
			int end = emJPos.get(emJPos.size() - 1);
			while (++end < length && !END_CHARS.contains(lbodySegs.get(end)));
			--end;
			int j = begin;
			boolean emBegin = false;
			while (j <= end) {
				String word = lbodySegs.get(j++);
				if (wordWeightMap.containsKey(word)) {
					if (!emBegin) {
						bodyBuilder.append("<em>");
						emBegin = true;
					}
				} else {
					if (emBegin) {
						bodyBuilder.append("</em>");
						emBegin = false;
					}
				}
				bodyBuilder.append(word);
			}
		}
		return bodyBuilder.toString();
	}

	private Map<String, Double> doPeople(String sentence, List<String> segs,
			List<String> tags, List<String> ners, double sentenceWeight) {// 问人
		HashMap<String, Integer> nameCount = new HashMap<>();
		int size = segs.size();
		for (int i = 0; i < size; ++i) {
			String name = null;
			if (ners.get(i).contains("B-Nh")) {// 人名
				StringBuilder sb = new StringBuilder(segs.get(i));
				do {
					try {
						sb.append(segs.get(++i));
					} catch (Exception ex) {
						break;
					}
				} while (ners.get(i).contains("E-Nh"));
				name = sb.toString();
			} else if (ners.get(i).contains("S-Nh")) {
				name = segs.get(i);
			}// 提取人名
			if (name != null && !name.matches("\\s*")) {
				int count = nameCount.getOrDefault(name, 0) + 1;
				nameCount.put(name, count);
			}
		}
		Map<String, Double> nameWeightMap = new HashMap<>();
		for (Entry<String, Integer> en : nameCount.entrySet()) {
			nameWeightMap.put(en.getKey(), en.getValue() * sentenceWeight);
		}
		return nameWeightMap;
	}

	private Map<String, Double> doLocation(String sentence, List<String> segs,
			List<String> tags, List<String> ners, double sentenceWeight) {// 返回地名或者地址的下一段
		List<Integer> addressPos = new ArrayList<>();
		int i = 0;
		int size = segs.size();
		for (i = 0; i < size; ++i) {
			if (ners.get(i).matches("B-N[siz]")) {// 地名 机构名 专有名词
				addressPos.add(i);
				try{
					while (!ners.get(++i).matches("E-N[siz]"));
				}catch(Exception e){
					break;
				}
			} else if (ners.get(i).contains("S-N[siz]")) {
				addressPos.add(i);
			}
		}
		return getAns(sentence, segs, sentenceWeight, addressPos);
	}

	private Map<String, Double> doNumber(String sentence, List<String> segs,
			List<String> tags, List<String> ners, double sentenceWeight) {// 返回一个时间串
		List<Integer> mPos = new ArrayList<>();// 数词位置
		int i;
		int size = segs.size();
		for (i = 0; i < size; ++i) {
			if (tags.get(i).charAt(0) == 'm') {// 是数词
				mPos.add(i);
			}
		}
		return getAns(sentence, segs, sentenceWeight, mPos);
	}

	private Map<String, Double> getAns(String sentence, List<String> segs,
			double sentenceWeight, List<Integer> posEs) {

		HashMap<String, Integer> answer = new HashMap<String, Integer>();
		Map<String, Double> result = new HashMap<>();
		if (!posEs.isEmpty()) {// 返回句子
			int length = posEs.size();
			int currentPos = 0;
			for (int i = 0; i < length; ++i) {
				if (posEs.get(i) < currentPos) {
					continue;
				}
				currentPos = posEs.get(i);

				int begin = currentPos;
				while (--begin >= 0 && !END_CHARS.contains(segs.get(begin)));// 提取一句话
				++begin;

				int end = currentPos;
				while (++end < length && !END_CHARS.contains(segs.get(end)));
				--end;
				StringBuilder sentenceSb = new StringBuilder();
				int j = begin;
				while (j <= end) {
					sentenceSb.append(segs.get(j++));
				}
				String shortSentence = sentenceSb.toString();
				int count = answer.getOrDefault(shortSentence, 0) + 1;
				if (shortSentence.length() >= DEFAULT_ANSWER_SIZE) {
					answer.put(shortSentence, count);
				}
				currentPos = end;
			}
		} else {
			answer.put(sentence, DEFAULT_COUNT);
		}
		for (Entry<String, Integer> en : answer.entrySet()) {
			result.put(en.getKey(), sentenceWeight * en.getValue());
		}// 这里是打分
		return result;
	}

	private Map<String, Double> doOtherFun(String sentence, List<String> segs,
			List<String> tags, List<String> ners, double sentenceWeight) {
		Map<String, Double> result = new HashMap<>();
		result.put(sentence, sentenceWeight);
		return result;// 直接返回那一段
	}

	interface Op {
		Map<String, Double> doFun(String sentence, List<String> segs,List<String> tags, List<String> ners, double uidWeight);
	}
}
