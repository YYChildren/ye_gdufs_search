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
import com.ye.gdufs.global.GlobalArgs;
import com.ye.gdufs.log.Logs;
import com.ye.gdufs.model.CrawlData;
import com.ye.gdufs.model.Page;
import com.ye.gdufs.util.MPQ;
import com.ye.gdufs.util.Misc;
import com.ye.gdufs.util.MsgUtil;

public class CrawlDataPro implements java.io.Serializable {
	private static final long serialVersionUID = 2423023121219596833L;
	private final static String KEY = "CrawlDataPro";
	public static final int TIMEOUT = 10000;
	private static final int NOT_MATCH_ADD = 0;
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
			CrawlDataPro crawlT = (CrawlDataPro) Misc.getDumpObject(KEY);
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
			urlsReady.offer(new SimpleEntry<String, Integer>(seed, 1));
		}
		maxDepth = GlobalArgs.getSeedDepth();
		notMatxhDepth = maxDepth - NOT_MATCH_ADD;
		urlFilter = GlobalArgs.seedsUrlFilter();
	}

	private void loopCrawl() {
		ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors
				.newFixedThreadPool(nThreads);
		while (isStarted) {
			// 没有空闲线程
			if (executor.getActiveCount() == this.nThreads){
				Logs.info_msg("Waiting: Threads are all used");
				System.err.println("Waiting: Threads are all used");
				dump();
				sleep();
				continue;
			}
			if(executor.getActiveCount() != 0 && urlsReady.isEmpty()){
				Logs.info_msg("Waiting: Threads are running");
				System.err.println("Waiting: Threads are running");
				dump();
				sleep();
				continue;
			}
			if (executor.getActiveCount() == 0 && urlsReady.isEmpty()) {
				System.out.println(executor.getActiveCount());
				System.out.println(executor.getPoolSize());
				stop();
				break;
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
					Logs.info_msg("MaxDepth: "+ depth + " Url:"+ url);
					System.err.println("MaxDepth: "+ depth + " Url:"+ url);
				}
			} while (true);
			if (urlDepth != null) {
				CrawlRun command = new CrawlRun(urlDepth);
				executor.execute(command);
			} 
		}
		stopped();
		
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
			Misc.dumpObject(KEY, this);
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
	private void stopped() {
		dump();
		Logs.info_msg("Crawl stopped");
		System.err.println("Crawl stopped");
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
				Document doc = null;
				try {
					doc = Jsoup
							.connect(url)
							.userAgent(
									"Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 6.1)")
							.timeout(CrawlDataPro.TIMEOUT).get();
				} catch (java.net.SocketTimeoutException e) {
					throw new java.net.SocketTimeoutException("Url: "+url+"  Msg:"+e.getMessage());
				}
				String content = doc.toString();
				String contentMd5 = MsgUtil.msgDigest(content);
				if (CrawlDataDaoImpl.isExistMd5(contentMd5)) {
					System.err.println("contentMd5: "+contentMd5);
					return;
				}
				System.out.println(url);
				CrawlDataDao crd = new CrawlDataDaoImpl();
				CrawlData data = new CrawlData();
				long uid = mpq.hash(url);
				String serName = Long.toHexString(uid);
				data.setUid(uid);
				data.setUrl("url: " +url+" depth: "+depth);
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
					if (newUrl.length() > Page.URL_MAX_SIZE
							|| newUrl.length() == 0) {
						continue;
					}
					if (newUrl.matches(URL_regex)) {// 只要http和https协议
						int newDepth;
						if (newUrl.matches(urlFilter)) {
							newDepth = depth;
						} else {
							newDepth = notMatxhDepth > depth ? notMatxhDepth
									: depth;
						}
						urls.add(new SimpleEntry<String, Integer>(newUrl,
								newDepth));
					}
				}
				urlsReady.addAll(urls);
			} catch (Exception e) {
				Logs.printStackTrace(e);
			}
		}
	}
}
