package com.ye.gdufs.dao;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;

import org.hibernate.Session;

import com.ye.gdufs.global.GlobalArgs;
import com.ye.gdufs.log.Logs;
import com.ye.gdufs.model.Page;
import com.ye.gdufs.model.PageSer;
import com.ye.gdufs.util.HibernateSql;
import com.ye.gdufs.util.HibernateUtil;
import com.ye.gdufs.util.Misc;

public class PageDaoImpl implements PageDao {
	private Page page;
	private PageSer oldPageSer;
	private PageSer pageSer;
	private File pageFile;
	private static String[] pageInfo;
	static{
		pageInfo = GlobalArgs.getPageInfo(); 
	}

	public PageDaoImpl() {
	}
	public PageDaoImpl(Page page, PageSer pageSer) {
		super();
		this.page = page;
		this.pageSer = pageSer;
		setPath();
	}
	public Page getPage() {
		return page;
	}
	public void setPage(Page page) {
		this.page = page;
		setPath();
	}
	public PageSer getPageSer() {
		return pageSer;
	}
	public void setPageSer(PageSer pageSer) {
		this.pageSer = pageSer;
	}
	
	@Override
	public void rsave(Session session) throws FileNotFoundException, IOException{
		session.createSQLQuery("replace into page(uid,url,contentMd5,titlefrequency,bodyfrequency,sername)"
				+ " values(:uid,:url,:contentMd5,:titlefrequency,:bodyfrequency,:sername)")
				.setLong("uid", page.getUid())
				.setString("url", page.getUrl())
				.setString("contentMd5", page.getContentMd5())
				.setInteger("titlefrequency", page.getTitleFrequency())
				.setInteger("bodyfrequency", page.getBodyFrequency())
				.setString("sername", page.getSerName()).executeUpdate();
		//-----------------begin ser------------------------
		try {
			this.oldPageSer = (PageSer) Misc.readObject(pageFile);
		} catch (ClassNotFoundException | IOException e) {
			this.oldPageSer = null;
		}
		Misc.writeObject(pageFile, pageSer);
		//-----------------end ser------------------------
	}
	@Override
	public void rrollback(){
		if(oldPageSer!=null){
			try {
				Misc.writeObject(pageFile, oldPageSer);
			} catch (IOException e) {
				Logs.printStackTrace(e);
			}
		}
	}
	public void save() throws Exception {
		HibernateSql hs = new HibernateSql(){
			@Override
			public Object execute(Session session) throws Exception{
				rsave(session);
				return null;
			}
		};
		try {
			HibernateUtil.execute(hs);
		} catch (Exception e) {
			 rrollback();
			 throw e;
		}
	}
	
	public static boolean isExistContentMd5(String contentMd5) throws Exception {
		HibernateSql hs = 
				session -> !session.createQuery("select p.contentMd5 from Page p where p.contentMd5 = :md5")
				.setString("md5", contentMd5).list().isEmpty();
		return (boolean) HibernateUtil.execute(hs);
	}
	protected void setPath(){
		String path = pageInfo[0] + "/" + page.getSerName() + "." + pageInfo[1];
		pageFile = new File(path);
	}
	@Override
	public void get(Serializable uid) {
		try {
			page = (Page) HibernateUtil.get(Page.class, uid);
			setPath();
			pageSer = (PageSer) Misc.readObject(pageFile);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public static long getPageCount() {
		try {
			return (long) HibernateUtil.execute(session -> session.createQuery("select count(p.uid) from Page p"));
		} catch (Exception e) {
			Logs.printStackTrace(e);
			return 0;
		}
		
	}
}
