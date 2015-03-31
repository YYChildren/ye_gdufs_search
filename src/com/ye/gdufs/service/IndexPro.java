package com.ye.gdufs.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;

import org.hibernate.Session;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import com.ye.gdufs.GlobalArgs;
import com.ye.gdufs.dao.CrawlDataDao;
import com.ye.gdufs.dao.CrawlDataDaoImpl;
import com.ye.gdufs.dao.PageDaoImpl;
import com.ye.gdufs.util.HibernateSql;
import com.ye.gdufs.util.HibernateUtil;
import com.ye.gdufs.util.Misc;
import com.ye.gdufs.util.MsgUtil;
import com.ye.gdufs.util.Paragraph;
import com.ye.gdufs.util.SentenceHandler;

public final class IndexPro implements java.io.Serializable{
	private static final long serialVersionUID = -7940609119323972989L;
	private final static String key = "createindexpro";
	private static IndexPro indexPro = null;
		
	private long startId;
	private Boolean isStarted = false;
		
	public static IndexPro getInstance() {
		if (indexPro == null) {
			syncInit();
		}
		return IndexPro.indexPro;
	}

	private static synchronized void syncInit() {
		if (IndexPro.indexPro == null) {
			IndexPro.indexPro = getIndexPro();
		}
	}

	private static IndexPro getIndexPro() {
		IndexPro p;
		try {
			p = getDump();
			if(p == null){
				p =  new IndexPro();
			}
		} catch (Exception e) {
			p =  new IndexPro();
			e.printStackTrace();
		}
		return p;
	}

	private IndexPro() {
		startId = 1;
	}
	
	public void start(){
		synchronized(this){
			if(this.isStarted){
				return;
			}else{
				this.isStarted = true;
			}
		}
		try {
			WordUrlsPro wusPro = new WordUrlsPro();
			int retryTime = 0;
			int nThreads = GlobalArgs.getPageThread();
			//			//test
			//			startId = 67;
			while (isStarted) {
				long endId = CrawlDataDaoImpl.getMaxId() + 1;
				//				//test
				//				endId=68;
				if (startId < endId) {
					retryTime = 0;
				} else {
					try {
						wusPro.save();
						wusPro.clear();
					} catch (IOException e) {
						e.printStackTrace();
					}
					++retryTime;
					if (retryTime < 5) {
						sleep();
					} else {
						stop();
					}
				}
				while (startId < endId) {
					long tempStep = endId - startId;
					int step = tempStep < (long) nThreads ? (int) tempStep:nThreads;
					for (int i = 0; i < step; ++startId,++i) {
						System.out.println("---------------------id=" + startId+ "--------------------");
						new PageProThread(startId, wusPro).run();
					}
				}
			}
			dump();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public synchronized void stop(){
		this.isStarted = false;
		System.out.println("---------------------stopping--------------------");
		sleep();
		System.out.println("---------------------stopped--------------------");
	}
	
	private static void sleep(){
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	private synchronized void dump() throws Exception{
		Boolean temp = this.isStarted;
		this.isStarted = false;
		dump(this);
		this.isStarted = temp;
	}
	
	private static void dump(java.io.Serializable o) throws Exception {
		Misc.dumpObject(key, o);
	}
	private static IndexPro getDump() throws Exception{
		return (IndexPro) Misc.getDumpObject(key);
	}

	class PageProThread implements Runnable{
		private long id;
		WordUrlsPro wusPro;
		
		PagePro pageProLocal;
		WordUrlPosPro wupProLocal;
		WordUrlsPro wusProLocal;
		public PageProThread(long id,WordUrlsPro wusPro) {
			this.id = id;
			this.wusPro = wusPro;
		}
		@Override
		public void run() {
			start(id);
			System.out.println("-------------------------------------------------------------page save begin-------------------------------------------------------------");
			save();
			System.out.println("-------------------------------------------------------------page save end-------------------------------------------------------------\n");
		}
		private void start(long id){
			try {
				//-----------------clean----------------------------//
				clean();
				CrawlDataDao cdd = new CrawlDataDaoImpl();
				if(cdd.get(id) == null){
					return;
				}
				String content = cdd.getContent();
				Document html = Jsoup.parse(content);
				String title = html.title().trim();
				if(title.equals("")){
					return;
				}
				String body = html.body().toString();
				List<String> para = Paragraph.extractSentence(body);
				if(para.isEmpty()){
					return;
				}
				String contentMd5 = Misc.getContentMd5(html);
				if(new PageDaoImpl().isExistContentMd5(contentMd5)){
					return;
				}
				
				
				//-----------------create----------------------------//
				create();
				String url = cdd.getUrl();
				String urlMd5 = MsgUtil.msgDigest(url);
				pageProLocal.setUrlMd5(urlMd5);
				pageProLocal.setUrl(url);
				pageProLocal.setContentMd5(contentMd5);
				pageProLocal.setSerName(urlMd5);
				
				Set<String> wordAreadyPro = new HashSet<String>();
				//-------------------------------------------------------begin of title-----------------------------------------------------------//
				System.out.println("-------------------------------------------------------------title begin-------------------------------------------------------------");
				
				SentenceHandler shTitle = Misc.analyzeSentence(title);
				int titleFrequency = shTitle.getSize();
				List<String> titleSegments = shTitle.getSegs();
				List<String> titlePostags = shTitle.getTags();
				List<String> titleNers = shTitle.getNers();
				pageProLocal.setTitleFrequency(titleFrequency);
				pageProLocal.setTitleSegments(titleSegments);
				pageProLocal.setTitlePostags(titlePostags);
				pageProLocal.setTitleNers(titleNers);
				
				Map<String,List<Integer>> wordTitleProMap = new HashMap<>();
				int i = 0;
				for(String word : titleSegments){
					if(!wordTitleProMap.containsKey(word)){
						List<Integer> wordTitlePosL = new ArrayList<>();
						wordTitlePosL.add(i);
						wordTitleProMap.put(word, wordTitlePosL);
					}else{
						wordTitleProMap.get(word).add(i);
					}
					if(!wordAreadyPro.contains(word)){
						wusProLocal.putUrlMd5(word, urlMd5);
						wordAreadyPro.add(word);
					}
					++i;
				}
				for(Entry<String,List<Integer>> e :wordTitleProMap.entrySet()){
					String word = e.getKey();
					List<Integer> titleWordPos = e.getValue();
					int titleWordFrequecy = titleWordPos.size();
					wupProLocal.putTitleWordPos(word, urlMd5, titleWordPos);
					wupProLocal.putTitleWordFrequecy(word, urlMd5, titleWordFrequecy );
				}
				wordTitleProMap = null;
				System.out.println("-------------------------------------------------------------title end-------------------------------------------------------------\n");
				//-------------------------------------------------------end of title-----------------------------------------------------------//
				
				//-------------------------------------------------------begin of body-----------------------------------------------------------//
				System.out.println("-------------------------------------------------------------body begin-------------------------------------------------------------");
				int bodyFrequency = 0;
				List<List<String>> bodySegments = new ArrayList<>();
				List<List<String>> bodyPostags = new ArrayList<>();
				List<List<String>> bodyNers = new ArrayList<>();
				
				Map<String,List<Integer>> wordBodyIProMap = new HashMap<>();
				Map<String,List<Integer>> wordBodyJProMap = new HashMap<>();
				Map<String,Integer> wordBodyFrequecyMap = new HashMap<>();
				int i1 = 0;
				for(String sentence :para){
					SentenceHandler shSentence = Misc.analyzeSentence(sentence);
					bodyFrequency += shSentence.getSize();
					bodySegments.add(shSentence.getSegs());
					bodyPostags.add(shSentence.getTags());
					bodyNers.add(shSentence.getNers());
					
				    int j1 = 0;
					for(String word : shSentence.getSegs()){
						if(!wordBodyIProMap.containsKey(word)){
							List<Integer> wordBodyIPosL = new ArrayList<>();
							wordBodyIPosL.add(i);
							wordBodyIProMap.put(word, wordBodyIPosL);
						}else{
							wordBodyIProMap.get(word).add(i1);
						}
						if(!wordBodyJProMap.containsKey(word)){
							List<Integer> wordBodyJPosL = new ArrayList<>();
							wordBodyJPosL.add(i);
							wordBodyJProMap.put(word, wordBodyJPosL);
						}else{
							wordBodyJProMap.get(word).add(j1);
						}
						{
							Integer count = wordBodyFrequecyMap.containsKey(word)?wordBodyFrequecyMap.get(word):0;
							wordBodyFrequecyMap.put(word, count+1);
						}
						if(!wordAreadyPro.contains(word)){
							wusProLocal.putUrlMd5(word, urlMd5);
							wordAreadyPro.add(word);
						}
						++j1;
					}
					++i1;
				}
				pageProLocal.setBodyFrequency(bodyFrequency);
				pageProLocal.setBodySegments(bodySegments);
				pageProLocal.setBodyPostags(bodyPostags);
				pageProLocal.setBodyNers(bodyNers);
			    
				for(Entry<String,Integer> bodyWordFrequecyEntry : wordBodyFrequecyMap.entrySet()){
					String word = bodyWordFrequecyEntry.getKey();
					Integer bodyWordFrequecy = bodyWordFrequecyEntry.getValue();
					List<Integer> bodyWordIPos = wordBodyIProMap.get(word);
					List<Integer> bodyWordJPos = wordBodyJProMap.get(word);
					wupProLocal.putBodyWordFrequecy(word, urlMd5, bodyWordFrequecy);
					wupProLocal.putBodyWordIPos(word, urlMd5, bodyWordIPos);
					wupProLocal.putBodyWordJPos(word, urlMd5, bodyWordJPos);
				}
				body = null;
				para = null;
				bodySegments = null;
				bodyPostags = null;
				bodyNers = null;
				wordBodyIProMap = null;
				wordBodyJProMap = null;
				wordBodyFrequecyMap = null;
				System.out.println("-------------------------------------------------------------body end-------------------------------------------------------------\n");
				//-------------------------------------------------------end of body-----------------------------------------------------------//
			} catch (Exception e) {
				clean();
				e.printStackTrace();
			}
		}
		private void clean(){
			pageProLocal = null;
			wupProLocal = null;
			wusProLocal = null;
		}
		private void create(){
			pageProLocal = new PagePro();
			wupProLocal = new WordUrlPosPro();
			wusProLocal = new WordUrlsPro();
		}
		private void save(){
			UUID uuid = UUID.randomUUID();
			try {
				if(pageProLocal == null){
					return;
				}
				HibernateSql hs = new HibernateSql(){
					@Override
					public Object execute(Session session) throws Exception {
						pageProLocal.rsave(session);
						wupProLocal.rsave(session);
						wusPro.psavel(uuid,wusProLocal);
						return null;
					}
				};
				HibernateUtil.execute(hs);
			} catch (Exception e) {
				pageProLocal.rrollback();
				try {
					wupProLocal.rrollback();
				} catch (Exception e1) {
					e1.printStackTrace();
				}
				wusPro.prollback(uuid);
				e.printStackTrace();
			}
		}
	}
}
