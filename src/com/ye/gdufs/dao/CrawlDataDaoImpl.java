package com.ye.gdufs.dao;

import java.io.IOException;
import com.ye.gdufs.model.CrawlData;
import com.ye.gdufs.util.HibernateSql;
import com.ye.gdufs.util.HibernateUtil;
import com.ye.gdufs.util.MsgUtil;

public class CrawlDataDaoImpl implements CrawlDataDao {
	CrawlData data;
	public CrawlDataDaoImpl(){}
	public CrawlDataDaoImpl(String url,String content) throws IOException{
		data = new CrawlData();
		data.setUrl(url);
		byte[] contentBytes = MsgUtil.compress(content);
        data.setContent(contentBytes);
	}
	public CrawlData getData() {
		return data;
	}
	public void setData(CrawlData data) {
		this.data = data;
	}
	@Override
	public void save() throws Exception {
		HibernateUtil.saveOrUpdate(data);
	}
	@Override
	public CrawlData get(long id) throws Exception{
		Class<?> c = CrawlData.class;
		this.data = (CrawlData) HibernateUtil.get(c,id);
		return this.data;
	}
	@Override
	public void delete() throws Exception{
		HibernateUtil.delete(this.data);
	}
	@Override
	public long getId(){
		return data.getId();
	}
	@Override
	public String getUrl(){
		return data.getUrl();
	}
	@Override
	public String getContent() throws IOException{
		return MsgUtil.uncompress(data.getContent());
	}
	
	public static long getMaxId() throws Exception{
		HibernateSql hs = session -> session.createQuery("select max(id) from CrawlData cd").list().get(0);
		Object result = HibernateUtil.execute(hs);
		return (Long) result;
	}
	
	public static void delete(long id) throws Exception{
		CrawlData data = new CrawlData();
		data.setId(id);
		HibernateUtil.delete(data);
	}
}
