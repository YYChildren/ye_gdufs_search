package com.ye.gdufs.dao;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import com.ye.gdufs.model.CrawlData;

public interface CrawlDataDao {
	void save() throws Exception;
	void get(long id) throws UnsupportedEncodingException, Exception;
	void delete() throws Exception;
	long getId();
	String getUrl();
	String getContent() throws IOException;
	void setContent(String content);
	CrawlData getCrawlData();
	void setCrawlData(CrawlData crawlData);
}
