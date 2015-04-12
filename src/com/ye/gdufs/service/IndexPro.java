package com.ye.gdufs.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import org.hibernate.Session;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import com.ye.gdufs.GlobalArgs;
import com.ye.gdufs.dao.CrawlDataDao;
import com.ye.gdufs.dao.CrawlDataDaoImpl;
import com.ye.gdufs.dao.PageMd5Dao;
import com.ye.gdufs.dao.PageMd5DaoImpl;
import com.ye.gdufs.log.Logs;
import com.ye.gdufs.model.Word;
import com.ye.gdufs.util.HibernateSql;
import com.ye.gdufs.util.HibernateUtil;
import com.ye.gdufs.util.MPQ;
import com.ye.gdufs.util.Misc;
import com.ye.gdufs.util.Paragraph;
import com.ye.gdufs.util.SentenceHandler;

public final class IndexPro implements java.io.Serializable{
	private static final long serialVersionUID = -7940609119323972989L;
	private final static String key = "indexpro";
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
		IndexPro p = null;
		try {
			p = getDump();
			if(p == null){
				p =  new IndexPro();
			}
		} catch (Exception e) {
			p =  new IndexPro();
			Logs.printStackTrace(e);
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
			WordPro wordPro = new WordPro();
			int retryTime = 0;
			int nThreads = GlobalArgs.getPageThread();
			while (isStarted) {
				long endId = CrawlDataDaoImpl.getMaxId() + 1;
				if (startId < endId) {
					retryTime = 0;
				} else {
					save(wordPro);
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
					//test;
					//startId = 315;
					for (int i = 0; i < step; ++startId,++i) {
						System.out.println("---------------------id=" + startId+ "--------------------");
						new PageProThread(startId, wordPro).run();//多线程
						if(startId % 20000 == 0){
							save(wordPro);
						}
					}
				}
			}
			stop();
			dump();
		} catch (Exception e) {
			Logs.printStackTrace(e);
		}
	}
	
	private void save(WordPro wordPro) throws Exception {
		try {
			System.out.println("------------------------------word save begin-------------------------------------");
			wordPro.save();
			wordPro.clear();
			System.out.println("------------------------------word save end-------------------------------------");
		} catch (IOException e) {
			Logs.printStackTrace(e);
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
			Logs.printStackTrace(e);
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
		MPQ mpq =MPQ.getInstance();
		private long id;
		WordPro wordPro;
		PagePro pageProLocal;
		WordPro wordProLocal;
		String contentMd5;
		public PageProThread(long id,WordPro wordPro) {
			this.id = id;
			this.wordPro = wordPro;
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
				contentMd5= Misc.getContentMd5(html);
				if(PageMd5DaoImpl.get(contentMd5) != null){
					return;
				}
				String body = html.body().toString();
				List<String> para = Paragraph.extractSentence(body);
				if(para.isEmpty()){
					return;
				}
				
				//-----------------create----------------------------//
				create();
				String url = cdd.getUrl();
				long uid = mpq.hash(url);
				pageProLocal.setUid(uid);
				pageProLocal.setUrl(url);
				
				
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
					if(word.length() <= Word.WORD_MAX_SIZE){
						List<Integer> wordTitlePosL = wordTitleProMap.getOrDefault(word, new ArrayList<>());
						wordTitlePosL.add(i);
						wordTitleProMap.put(word, wordTitlePosL);
					}
					++i;
				}
				for(Entry<String,List<Integer>> e :wordTitleProMap.entrySet()){
					String word = e.getKey();
					List<Integer> wordTitlePosL = e.getValue();
					int titleWordFrequecy = wordTitlePosL.size();
					wordProLocal.putTitleWordPos(word, uid, wordTitlePosL);
					wordProLocal.putTitleWordFrequecy(word, uid, titleWordFrequecy );
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
						if(word.length() <= Word.WORD_MAX_SIZE){
							{
								List<Integer> wordBodyIPosL = wordBodyIProMap.getOrDefault(word, new ArrayList<>());
								wordBodyIPosL.add(i1);
								wordBodyIProMap.put(word, wordBodyIPosL);
							}
							{
								List<Integer> wordBodyJPosL = wordBodyJProMap.getOrDefault(word, new ArrayList<>());
								wordBodyJPosL.add(j1);
								wordBodyJProMap.put(word, wordBodyJPosL);
							}
							{
								Integer count = wordBodyFrequecyMap.getOrDefault(word, 0);
								wordBodyFrequecyMap.put(word, count+1);
							}
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
					List<Integer> bodyWordIPosL = wordBodyIProMap.get(word);
					List<Integer> bodyWordJPosL = wordBodyJProMap.get(word);
					wordProLocal.putBodyWordFrequecy(word, uid, bodyWordFrequecy);
					wordProLocal.putBodyWordIPos(word, uid, bodyWordIPosL);
					wordProLocal.putBodyWordJPos(word, uid, bodyWordJPosL);
				}
				{
					body = null;
					para = null;
					bodySegments = null;
					bodyPostags = null;
					bodyNers = null;
					wordBodyIProMap = null;
					wordBodyJProMap = null;
					wordBodyFrequecyMap = null;
				}
				System.out.println("-------------------------------------------------------------body end-------------------------------------------------------------\n");
				//-------------------------------------------------------end of body-----------------------------------------------------------//
			} catch (Exception e) {
				clean();
				Logs.printStackTrace(e);
			}
		}
		private void clean(){
			pageProLocal = null;
			contentMd5 = null;
			wordProLocal = null;
		}
		
		private void create(){
			pageProLocal = new PagePro();
			wordProLocal = new WordPro();
		}
		
		private void save(){
			UUID uuid = UUID.randomUUID();
			PageMd5Dao pmd = new PageMd5DaoImpl(contentMd5);
			try {
				if(pageProLocal == null){
					return;
				}
				HibernateSql hs = new HibernateSql(){
					@Override
					public Object execute(Session session) throws Exception {
						pageProLocal.rsave(session);
						pmd.rsave(session);
						wordPro.psave(uuid,wordProLocal);
						return null;
					}
				};
				HibernateUtil.execute(hs);
			} catch (Exception e) {
				pageProLocal.rrollback();
				pmd.rrollback();
				wordPro.prollback(uuid);
				Logs.printStackTrace(e);
			}
		}
	}
}
