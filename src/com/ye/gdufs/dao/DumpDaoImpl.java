package com.ye.gdufs.dao;

import com.ye.gdufs.model.Dump;
import com.ye.gdufs.util.HibernateUtil;

public class DumpDaoImpl implements DumpDao{
	private Dump dump;
	public DumpDaoImpl() {	}
	public DumpDaoImpl(String key,byte[] b){
		dump = new Dump();
		dump.setKeyStr(key);
		dump.setObjByte(b);
	}
	@Override
	public void save() throws Exception{
		HibernateUtil.saveOrUpdate(dump);
	}
	@Override
	public Dump get(String key) throws Exception{
		dump = (Dump) HibernateUtil.get(Dump.class, key);
		return dump;
	}
	
	public Dump getDump() {
		return dump;
	}
	public void setDump(Dump dump) {
		this.dump = dump;
	}
}
