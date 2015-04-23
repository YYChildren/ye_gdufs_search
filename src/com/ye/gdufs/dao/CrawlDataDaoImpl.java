package com.ye.gdufs.dao;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.hibernate.Session;

import com.ye.gdufs.global.GlobalArgs;
import com.ye.gdufs.log.Logs;
import com.ye.gdufs.model.CrawlData;
import com.ye.gdufs.util.HibernateSql;
import com.ye.gdufs.util.HibernateUtil;
import com.ye.gdufs.util.Misc;

public class CrawlDataDaoImpl implements CrawlDataDao {
	CrawlData crawlData;
	String oldContent;
	String content;
	File crawlDataFile;
	static String[] crawlDataInfo;
	static {
		crawlDataInfo = GlobalArgs.getCrawlDataInfo();
	}

	public CrawlDataDaoImpl() {
	}

	@Override
	public CrawlData getCrawlData() {
		return crawlData;
	}

	@Override
	public void setCrawlData(CrawlData crawlData) {
		this.crawlData = crawlData;
	}

	@Override
	public void setContent(String content) {
		this.content = content;
	}

	@Override
	public String getContent() throws IOException {
		return this.getContent();
	}

	@Override
	public void save() throws Exception {
		HibernateSql hs = session -> {
			rsave(session);
			return null;
		};
		try {
			HibernateUtil.execute(hs);
		} catch (Exception e) {
			rrollback();
		}
	}

	private void rsave(Session session) throws FileNotFoundException,
			IOException, ClassNotFoundException {
		session.save(crawlData);
		try {
			oldContent = (String) Misc.readObject(crawlDataFile);
		} catch (Exception e) {
			oldContent = null;
		}
		Misc.writeObject(crawlDataFile, content);
	}

	private void rrollback() {
		if (oldContent != null) {
			try {
				Misc.writeObject(crawlDataFile, oldContent);
			} catch (IOException e) {
				Logs.printStackTrace(e);
			}
		}
	}

	@Override
	public void get(long id) throws Exception {
		setPath();
		Class<?> c = CrawlData.class;
		this.crawlData = (CrawlData) HibernateUtil.get(c, id);
		this.content = (String) Misc.readObject(crawlDataFile);
	}

	public static  boolean get(String contentMd5) {
		try {
			HibernateSql hs = session -> 
			!session.createQuery("select cd.contentMd5 from CrawlDara cd where cd.contentMd5 = :md5")
					.setString("md5", contentMd5).list().isEmpty();
			return (boolean) HibernateUtil.execute(hs);
		} catch (Exception e) {
			return false;
		}
	}

	@Override
	public void delete() throws Exception {
		HibernateUtil.delete(this.crawlData);
	}

	@Override
	public long getId() {
		return crawlData.getId();
	}

	@Override
	public String getUrl() {
		return crawlData.getUrl();
	}

	public static long getMaxId() throws Exception {
		HibernateSql hs = session -> session
				.createQuery("select max(id) from CrawlData cd").list().get(0);
		Object result = HibernateUtil.execute(hs);
		return (Long) result;
	}

	public static void delete(long id) throws Exception {
		CrawlData data = new CrawlData();
		data.setId(id);
		HibernateUtil.delete(data);
	}

	public void setPath() {
		String path = crawlDataInfo[0] + "/" + crawlData.getSerName() + "."
				+ crawlDataInfo[1];
		crawlDataFile = new File(path);
	}
}
