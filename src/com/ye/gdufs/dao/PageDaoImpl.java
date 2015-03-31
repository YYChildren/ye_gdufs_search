package com.ye.gdufs.dao;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.hibernate.Session;

import com.ye.gdufs.GlobalArgs;
import com.ye.gdufs.model.Page;
import com.ye.gdufs.model.PageSer;
import com.ye.gdufs.util.HibernateSql;
import com.ye.gdufs.util.HibernateUtil;
import com.ye.gdufs.util.Misc;

public class PageDaoImpl extends SearchPath implements PageDao {
	private Page page;
	private PageSer pageSer;
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
		if(session.createQuery("select urlMd5 from Page p where p.urlMd5 = :md5").setString("md5", page.getUrlMd5()).list().isEmpty()){
			session.save(page);
		}else{
			session.update(page);
		}
		serSave();
	}
	@Override
	public void rrollback(){
		if(file.exists()){
			file.delete();
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
		HibernateUtil.execute(hs);
	}
	@Override
	public boolean isExistContentMd5(String contentMd5) throws Exception {
		HibernateSql hs = 
				session -> !session.createQuery("select p.contentMd5 from Page p where p.contentMd5 = :md5")
				.setString("md5", contentMd5).list().isEmpty();
		return (boolean) HibernateUtil.execute(hs);
	}
	@Override
	public void serSave() throws FileNotFoundException, IOException{
		Misc.writeObject(file, pageSer);
	}
	
	@Override
	protected void setPath(){
		path = pageInfo[0] + "/" + page.getSerName() + "." + pageInfo[1];
		file = new File(path);
	}
	
}
