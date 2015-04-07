package com.ye.gdufs.dao;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.hibernate.Session;

import com.ye.gdufs.model.PageMd5;
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
	

	public PageMd5 getPm() {
		return pm;
	}

	public void setPm(PageMd5 pm) {
		this.pm = pm;
	}

	@Override
	public void save() throws Exception {
		HibernateUtil.save(pm);
	}

	@Override
	public void rsave(Session session) throws FileNotFoundException,
			IOException {
		session.createSQLQuery("replace into pagemd5(contentMd5) values(:contentMd5)").setString("contentMd5",pm.getContentMd5()).executeUpdate();
	}

	@Override
	public void rrollback() {
	}
}
