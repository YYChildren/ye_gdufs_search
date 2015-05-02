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
		setPath();
	}

	@Override
	public void setContent(String content) {
		this.content = content;
	}

	@Override
	public String getContent() throws IOException {
		return this.content;
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
			throw e;
		}
	}

	private void rsave(Session session) throws FileNotFoundException,
			IOException, ClassNotFoundException {
		session.save(crawlData);
		try {
			oldContent = Misc.readString(crawlDataFile);
		} catch (Exception e) {
			oldContent = null;
		}
		Misc.writeString(crawlDataFile, content);
	}

	private void rrollback() {
		if (oldContent != null) {
			try {
				Misc.writeString(crawlDataFile, oldContent);
			} catch (IOException e) {
				Logs.printStackTrace(e);
			}
		}
	}

	@Override
	public void get(long id) throws Exception {
		Class<?> c = CrawlData.class;
		this.crawlData = (CrawlData) HibernateUtil.get(c, id);
		setPath();
		this.content = Misc.readString(crawlDataFile);
	}

	public static  boolean isExistMd5(String contentMd5) {
		try {
			HibernateSql hs = session -> 
			!session.createQuery("select cd.contentMd5 from CrawlData cd where cd.contentMd5 = :md5")
					.setString("md5", contentMd5).list().isEmpty();
			return (boolean) HibernateUtil.execute(hs);
		} catch (Exception e) {
			return false;
		}
	}

	@Override
	public void delete() throws Exception {
		HibernateUtil.delete(this.crawlData);
		try {
			crawlDataFile.delete();
		} catch (Exception e) {
		}
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
		String path = crawlDataInfo[0] + "/" + data.getSerName() + "."
				+ crawlDataInfo[1];
		File crawlDataFile = new File(path);
		try {
			crawlDataFile.delete();
		} catch (Exception e) {
		}
	}

	public void setPath() {
		String path = crawlDataInfo[0] + "/" + crawlData.getSerName() + "."
				+ crawlDataInfo[1];
		crawlDataFile = new File(path);
	}
}
