package com.ye.gdufs.model;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;

import org.hibernate.Session;
import org.junit.Test;

import com.ye.gdufs.util.HibernateUtil;

public class CrawlDataTest {

	@Test
	public void test() throws NoSuchAlgorithmException, UnsupportedEncodingException {
		CrawlData cw = new CrawlData();
		cw.setUrl("abcdabcdabcdabcdabcd");
		cw.setContent("abcdabcd".getBytes());
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.beginTransaction();
		session.save(cw);
		session.getTransaction().commit();
	}
}
