package com.ye.gdufs.dao;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.hibernate.Session;

import com.ye.gdufs.log.Logs;
import com.ye.gdufs.model.PageMd5;
import com.ye.gdufs.util.HibernateSql;
import com.ye.gdufs.util.HibernateUtil;

public class PageMd5DaoImpl implements PageMd5Dao {
	private PageMd5 pm;

	public PageMd5DaoImpl() {
		super();
	}

	public PageMd5DaoImpl(PageMd5 pm) {
		super();
		this.pm = pm;
	}
	

	public PageMd5DaoImpl(String contentMd5) {
		this( new PageMd5(contentMd5));
	}

	public PageMd5 getPm() {
		return pm;
	}

	public void setPm(PageMd5 pm) {
		this.pm = pm;
	}

	@Override
	public void save() throws Exception {
		HibernateSql hs = new HibernateSql(){
			@Override
			public Object execute(Session session) throws Exception {
				rsave(session);
				return null;
			}};
			try {
				HibernateUtil.execute(hs);
			} catch (Exception e) {
				 rrollback();
				 throw e;
			}
	}

	@Override
	public void rsave(Session session) throws FileNotFoundException,
			IOException {
		session.createSQLQuery("replace into pagemd5(contentMd5) values(:contentMd5)").setString("contentMd5",pm.getContentMd5()).executeUpdate();
	}

	@Override
	public void rrollback() {
	}

	public static PageMd5 get(String contentMd5) {
		try {
			return (PageMd5) HibernateUtil.get(PageMd5.class,contentMd5);
		} catch (Exception e) {
			Logs.printStackTrace(e);
			return null;
		}
	}
}
