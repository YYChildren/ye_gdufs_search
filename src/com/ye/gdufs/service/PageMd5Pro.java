package com.ye.gdufs.service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hibernate.Session;

import com.ye.gdufs.dao.PageMd5DaoImpl;
import com.ye.gdufs.model.PageMd5;
import com.ye.gdufs.util.HibernateSql;
import com.ye.gdufs.util.HibernateUtil;

public class PageMd5Pro {
	private static PageMd5Pro pmPro = PageMd5ProIn.pmPro;
	private Set<String> md5s;
	private Set<String> oldMd5s;
	public static PageMd5Pro getInstance(){
		return pmPro;
	}
	public static  PageMd5Pro updateInstance(){
		return PageMd5ProIn.updateInstance();
	}
	private static class PageMd5ProIn{
		private static PageMd5Pro pmPro = new PageMd5Pro();
		private static PageMd5Pro updateInstance(){
			synchronized(pmPro){
				pmPro.rsave();
				pmPro.init();
			}
			return pmPro;
		}
	}
	private PageMd5Pro(){
		init();
	}
	@SuppressWarnings("unchecked")
	private void init(){
		oldMd5s = new HashSet<>();
		md5s = new HashSet<>();
		HibernateSql  hs = new HibernateSql(){
			@Override
			public Object execute(Session session) throws Exception {
				return session.createQuery("from PageMd5").list();
			}
		};
		try {
			List<PageMd5> pmL = (List<PageMd5>) HibernateUtil.execute(hs);
			for( PageMd5 pm : pmL ){
				oldMd5s.add(pm.getContentMd5());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public synchronized void psave(String md5){
		md5s.add(md5);
	}
	public synchronized void prollback(String md5){
		md5s.remove(md5);
	}
	public synchronized boolean contains(String md5){
		return oldMd5s.contains(md5) || md5s.contains(md5);
	}
	public synchronized void save(){
		rsave();
	}
	private void rsave(){
		if(md5s == null){
			return;
		}
		HibernateSql hs = new HibernateSql(){
			@Override
			public Object execute(Session session) throws Exception {
				int i = 100;
				for( String md5: md5s){
					new PageMd5DaoImpl(new PageMd5(md5)).rsave(session);
					if(--i == 0){
						session.flush();
						session.clear();
						i = 20;
					}
				}
				return null;
			}
		};
		try {
			HibernateUtil.execute(hs);
		} catch (Exception e) {
			for( String md5: md5s){
				new PageMd5DaoImpl(new PageMd5(md5)).rrollback();
			}
			e.printStackTrace();
		}
	}
}
