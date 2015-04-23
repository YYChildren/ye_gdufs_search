package com.ye.gdufs.service;

import java.util.AbstractMap.SimpleEntry;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.ye.gdufs.dao.CrawlDataDao;
import com.ye.gdufs.dao.CrawlDataDaoImpl;
import com.ye.gdufs.dao.DumpDao;
import com.ye.gdufs.dao.DumpDaoImpl;
import com.ye.gdufs.global.GlobalArgs;
import com.ye.gdufs.log.Logs;
import com.ye.gdufs.model.CrawlData;
import com.ye.gdufs.model.Dump;
import com.ye.gdufs.model.Page;
import com.ye.gdufs.util.MPQ;
import com.ye.gdufs.util.MsgUtil;

public class CrawlDataPro implements java.io.Serializable {
	private static final long serialVersionUID = 2423023121219596833L;
	private final static String KEY = "CrawlDataPro";
	public static final int TIMEOUT = 5000;
	private static final int RETRY_TIME = 12;
	private static final int NOT_MATCH_ADD = 1;
	private static final String URL_regex = "^https?.*";
	private static MPQ mpq = MPQ.getInstance();
	private static CrawlDataPro crawl = null;

	private int nThreads;
	private int maxDepth;
	private int notMatxhDepth;
	private String urlFilter;
	// url -- 深度
	private Queue<SimpleEntry<String, Integer>> urlsReady;
	private Boolean isStarted = false;

	public int getnThreads() {
		return nThreads;
	}

	public Queue<SimpleEntry<String, Integer>> getUrlsReady() {
		return urlsReady;
	}

	public static CrawlDataPro getInstance() {
		if (crawl == null) {
			syncInit();
		}
		return CrawlDataPro.crawl;
	}

	static synchronized void syncInit() {
		if (CrawlDataPro.crawl == null) {
			CrawlDataPro crawlT = getCrawl();
			if (crawlT != null) {
				CrawlDataPro.crawl = crawlT;
			} else {
				CrawlDataPro.crawl = new CrawlDataPro();
			}
		}
	}

	private CrawlDataPro() {
		updateThread();
		updateSeeds();
	}

	public void start() {
		synchronized (isStarted) {
			if (isStarted) {
				return;
			} else {
				isStarted = true;
			}
		}
		// 在这里可以把线程分裂出去
		doUpdate();
		loopCrawl();
		CrawlDataPro.crawl = null;
	}

	private void doUpdate() {
		if (GlobalArgs.isThreadUpdate()) {
			updateThread();
		}
		if (GlobalArgs.isSeedsUpdate() || urlsReady.isEmpty()) {
			updateSeeds();
		}
	}

	private void updateThread() {
		nThreads = GlobalArgs.getCrawlThread();
	}

	private void updateSeeds() {
		String[] seeds = GlobalArgs.getSeeds();
		urlsReady = new ConcurrentLinkedQueue<SimpleEntry<String, Integer>>();
		for (String seed : seeds) {
			urlsReady.offer(new SimpleEntry<String, Integer>(seed,
					1));
		}
		maxDepth = GlobalArgs.getSeedDepth();
		notMatxhDepth = maxDepth - NOT_MATCH_ADD; 
		urlFilter = GlobalArgs.seedsUrlFilter();
	}

	private void loopCrawl() {
		ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors
				.newFixedThreadPool(nThreads);
		int readyEmptyTime = 0;
		while (isStarted) {
			// 没有空闲线程
			if (executor.getActiveCount() == this.nThreads
					|| urlsReady.isEmpty()) {
				dump();
				sleep();
				continue;
			}
			SimpleEntry<String, Integer> urlDepth = null;
			do {
				SimpleEntry<String, Integer> urlDepthTmp = urlsReady.poll();
				if (urlDepthTmp == null) {
					urlDepth = urlDepthTmp;
					break;
				}
				String url = urlDepthTmp.getKey();
				Integer depth = urlDepthTmp.getValue();
				if (depth <= maxDepth) {
					urlDepth = urlDepthTmp;
					break;
				} else {
					Logs.info_msg("MaxDepth:" + url);
					System.err.println("MaxDepth:" + url);
				}
			} while (true);
			if (urlDepth != null) {
				readyEmptyTime = 0;
				CrawlRun command = new CrawlRun(urlDepth);
				executor.execute(command);
			} else {
				++readyEmptyTime;
			}
			if (readyEmptyTime >= RETRY_TIME) {
				readyEmptyTime = 0;
				break;
			}
		}
		dump();
		System.err.println("Crawl stopped");
	}

	private void sleep() {
		try {
			Thread.sleep(CrawlDataPro.TIMEOUT);
		} catch (InterruptedException e) {
			Logs.printStackTrace(e);
		}
	}

	private void dump() {
		synchronized (this) {
			Boolean tempStarted = this.isStarted;
			this.isStarted = false;
			putCrawl(this);
			this.isStarted = tempStarted;
		}
	}

	public void stop() {
		synchronized (this) {
			Logs.info_msg("Crawl stopping");
			System.err.println("Crawl stopping");
			isStarted = false;
			sleep();
		}
	}

	private static CrawlDataPro getCrawl() {
		try {
			DumpDao dumpDao = new DumpDaoImpl();
			Dump dump = dumpDao.get(KEY);
			if (dump == null) {
				return null;
			}
			return (CrawlDataPro) dump.getObj();
		} catch (Exception e) {
			Logs.printStackTrace(e);
			return null;
		}
	}

	private static void putCrawl(Object obj) {
		try {
			DumpDao dumpDao = new DumpDaoImpl(KEY, obj);
			dumpDao.save();
		} catch (Exception e) {
			Logs.printStackTrace(e);
		}
	}

	private class CrawlRun implements Runnable {
		SimpleEntry<String, Integer> urlDepth;
		public CrawlRun(SimpleEntry<String, Integer> urlDepth) {
			this.urlDepth = urlDepth;
		}

		@Override
		public void run() {
			try {
				String url = urlDepth.getKey();
				int depth = urlDepth.getValue();
				Document doc;
				doc = Jsoup.connect(url).timeout(CrawlDataPro.TIMEOUT).get();
				String content = doc.toString();
				String contentMd5 = MsgUtil.msgDigest(content);
				if(CrawlDataDaoImpl.get(contentMd5)){
					return;
				}
				CrawlDataDao crd = new CrawlDataDaoImpl();
				CrawlData data = new CrawlData();
				long uid = mpq.hash(url);
				String serName = Long.toHexString(uid);
				data.setUid(uid);
				data.setUrl(url);
				data.setContentMd5(contentMd5);
				data.setSerName(serName);
				crd.setCrawlData(data);
				crd.setContent(content);
				crd.save();
				Elements links = doc.select("a[href]");
				List<SimpleEntry<String, Integer>> urls = new LinkedList<>();
				++depth;
				for (Element link : links) {
					String newUrl = link.attr("abs:href").trim();
					if(newUrl.length() > Page.URL_MAX_SIZE || newUrl.length() == 0){
						continue;
					}
					if (newUrl.matches(URL_regex)) {// 只要http和https协议
						int newDepth;
						if(newUrl.contains(urlFilter)){
							newDepth = depth;
						}else{
							newDepth = notMatxhDepth > depth ? notMatxhDepth : depth;
						}
						urls.add(new SimpleEntry<String, Integer>(newUrl, newDepth));
					}
				}
				urlsReady.addAll(urls);
			} catch (Exception e) {
				Logs.printStackTrace(e);
			}
		}
	}
}
