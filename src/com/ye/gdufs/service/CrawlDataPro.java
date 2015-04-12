package com.ye.gdufs.service;

import java.util.AbstractMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.ye.gdufs.GlobalArgs;
import com.ye.gdufs.dao.CrawlDataDao;
import com.ye.gdufs.dao.CrawlDataDaoImpl;
import com.ye.gdufs.dao.DumpDao;
import com.ye.gdufs.dao.DumpDaoImpl;
import com.ye.gdufs.log.Logs;
import com.ye.gdufs.model.Dump;
import com.ye.gdufs.util.MsgUtil;

public class CrawlDataPro implements java.io.Serializable {
	private static final long serialVersionUID = 2423023121219596833L;
	public static final int defaultTimeout = 5000;
	private static final int retryTime = 12;
	private static CrawlDataPro crawl = null;
	private final static String key = "crawl";

	private int nThreads;
	private int maxDepth;
	// url -- 深度
	private Queue<AbstractMap.SimpleEntry<String, Integer>> urlsReady;
	private Set<String> urlsFinished;
	private Boolean isStarted = false;

	public int getnThreads() {
		return nThreads;
	}

	public Queue<AbstractMap.SimpleEntry<String, Integer>> getUrlsReady() {
		return urlsReady;
	}

	public Set<String> getUrlsFinished() {
		return urlsFinished;
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
		nThreads = GlobalArgs.getCrawlThread();
		maxDepth = GlobalArgs.getSeedDepth();
		urlsReady = new ConcurrentLinkedQueue<AbstractMap.SimpleEntry<String, Integer>>();
		String[] seeds = GlobalArgs.getSeeds();
		for (String seed : seeds) {
			urlsReady.offer(new AbstractMap.SimpleEntry<String, Integer>(seed,
					1));
		}
		urlsFinished = new TreeSet<String>();
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
			urlsFinished.clear();
		}
		if (GlobalArgs.isUrlFinishUpdate()) {
			urlsFinished.clear();
		}
	}

	private void updateThread() {
		nThreads = GlobalArgs.getCrawlThread();
	}

	private void updateSeeds() {
		String[] seeds = GlobalArgs.getSeeds();
		urlsReady.clear();
		for (String seed : seeds) {
			urlsReady.offer(new AbstractMap.SimpleEntry<String, Integer>(seed,
					1));
		}
		maxDepth = GlobalArgs.getSeedDepth();
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
			AbstractMap.SimpleEntry<String, Integer> urlDepth = null;
			do {
				AbstractMap.SimpleEntry<String, Integer> urlDepthTmp = urlsReady
						.poll();
				if (urlDepthTmp == null) {
					urlDepth = urlDepthTmp;
					break;
				}
				String url = urlDepthTmp.getKey();
				Integer depth = urlDepthTmp.getValue();
				if (depth <= maxDepth) {
					if (!urlsFinished.contains(url)) {
						urlDepth = urlDepthTmp;
						break;
					}
				} else {
					System.err.println("MaxDepth:" + url);
				}
			} while (true);
			if (urlDepth != null) {
				CrawlRun command = new CrawlRun(urlDepth, urlsReady, maxDepth);
				executor.execute(command);
				urlsFinished.add(urlDepth.getKey());
				readyEmptyTime = 0;
			} else {
				++readyEmptyTime;
			}
			if (readyEmptyTime >= retryTime) {
				readyEmptyTime = 0;
				// stop();
			}
		}
		dump();
		System.err.println("Crawl stopped");
	}

	private void sleep() {
		try {
			Thread.sleep(CrawlDataPro.defaultTimeout);
		} catch (InterruptedException e) {
			Logs.printStackTrace(e);
		}
	}

	private void dump() {
		// save to mysql
		synchronized (this) {
			Boolean tempStarted = this.isStarted;
			this.isStarted = false;
			putCrawl(this);
			this.isStarted = tempStarted;
		}
	}

	public void stop() {
		synchronized (this) {
			System.err.println("Crawl stopping");
			isStarted = false;
			sleep();
		}
	}

	private static CrawlDataPro getCrawl() {
		// 从数据库读出对象的二进制流
		try {
			DumpDao dumpDao = new DumpDaoImpl();
			Dump dump = dumpDao.get(key);
			if (dump == null) {
				return null;
			}
			byte[] b = dump.getObjByte();
			CrawlDataPro obj = (CrawlDataPro) MsgUtil.bytes2Object(b);
			return obj;
		} catch (Exception e) {
			Logs.printStackTrace(e);
			return null;
		}
	}

	private static void putCrawl(Object obj) {
		try {
			byte[] b = MsgUtil.object2Bytes(obj);
			DumpDao dumpDao = new DumpDaoImpl(key, b);
			dumpDao.save();
		} catch (Exception e) {
			Logs.printStackTrace(e);
		}
	}
}

class CrawlRun implements Runnable {
	// private urlsReadyueue<String> urlsReady;
	AbstractMap.SimpleEntry<String, Integer> urlDepth;
	Queue<AbstractMap.SimpleEntry<String, Integer>> urlsReady;
	int maxDepth;
	private final String regex = "^https?.*";

	public CrawlRun(AbstractMap.SimpleEntry<String, Integer> urlDepth,
			Queue<AbstractMap.SimpleEntry<String, Integer>> urlsReady,
			int maxDepth) {
		this.urlDepth = urlDepth;
		this.urlsReady = urlsReady;
		this.maxDepth = maxDepth;
	}

	@Override
	public void run() {
		try {
			String url = urlDepth.getKey();
			int depth = urlDepth.getValue();
			Document doc;
			doc = Jsoup.connect(url).timeout(CrawlDataPro.defaultTimeout).get();
			Elements links = doc.select("a[href]");
			String content = doc.toString();
			CrawlDataDao crd = new CrawlDataDaoImpl(url, content);
			crd.save();
			List<AbstractMap.SimpleEntry<String, Integer>> urls = new LinkedList<>();
			// if (++depth > maxDepth) {
			// return;
			// }
			++depth;
			for (Element link : links) {
				String newUrl = link.attr("abs:href").trim();
				if (newUrl.matches(regex)) {// 只要http和https协议
					urls.add(new AbstractMap.SimpleEntry<String, Integer>(
							newUrl, depth));
				}
			}
			urlsReady.addAll(urls);
		} catch (Exception e) {
			Logs.printStackTrace(e);
		}
	}
}