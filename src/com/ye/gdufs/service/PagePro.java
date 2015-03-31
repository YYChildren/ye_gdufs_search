package com.ye.gdufs.service;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import org.hibernate.Session;

import com.ye.gdufs.dao.PageDao;
import com.ye.gdufs.dao.PageDaoImpl;
import com.ye.gdufs.model.Page;
import com.ye.gdufs.model.PageSer;

public class PagePro {
	Page page = new Page();
	PageSer pageSer = new PageSer();
	PageDao pageDao;
	public PagePro(){}
	public void setUrlMd5(String urlMd5) {
		page.setUrlMd5(urlMd5);
	}
	public void setUrl(String url) {
		page.setUrl(url);
	}
	public void setContentMd5(String contentMd5) {
		page.setContentMd5(contentMd5);
	}
	public void setTitleFrequency(int titleFrequency) {
		page.setTitleFrequency(titleFrequency);
	}
	public void setBodyFrequency(int bodyFrequency) {
		page.setBodyFrequency(bodyFrequency);
	}
	public void setSerName(String serName) {
		page.setSerName(serName);
	}
	public void setTitleSegments(List<String> titleSegments) {
		pageSer.setTitleSegments(titleSegments);
	}
	public void setTitlePostags(List<String> titlePostags) {
		pageSer.setTitlePostags(titlePostags);
	}
	public void setTitleNers(List<String> titleNers) {
		pageSer.setTitleNers(titleNers);
	}
	public void setBodySegments(List<List<String>> bodySegments) {
		pageSer.setBodySegments(bodySegments);
	}
	public void setBodyPostags(List<List<String>> bodyPostags) {
		pageSer.setBodyPostags(bodyPostags);
	}
	public void setBodyNers(List<List<String>> bodyNers) {
		pageSer.setBodyNers(bodyNers);
	}
	private void initPageDao(){
		if(pageDao == null){
			pageDao = new PageDaoImpl(page,pageSer);
		}
	}
	public void rsave(Session session) throws FileNotFoundException, IOException {
		initPageDao();
		pageDao.rsave(session);
	}
	public void rrollback(){
		initPageDao();
		pageDao.rrollback();
	}
}
