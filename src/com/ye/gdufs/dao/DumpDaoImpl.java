package com.ye.gdufs.dao;

import java.io.File;

import com.ye.gdufs.global.GlobalArgs;
import com.ye.gdufs.model.Dump;
import com.ye.gdufs.util.Misc;

public class DumpDaoImpl implements DumpDao{
	private Dump dump;
	private File dumpFile;
	private static String[] dumpInfo;
	static{
		dumpInfo = GlobalArgs.getDumpInfo(); 
	}
	public DumpDaoImpl() {	}
	public DumpDaoImpl(String key,Object obj){
		dump = new Dump();
		dump.setKeyStr(key);
		dump.setObj(obj);
	}
	@Override
	public void save() throws Exception{
		Misc.writeObject(dumpFile, dump);
	}
	@Override
	public Dump get(String key) throws Exception{
		this.dump = (Dump) Misc.readObject(dumpFile);
		return dump;
	}
	public Dump getDump() {
		return dump;
	}
	public void setDump(Dump dump) {
		this.dump = dump;
	}
	protected void setPath(){
		String path = dumpInfo[0] + "/" + dump.getKeyStr() + "." + dumpInfo[1];
		dumpFile = new File(path);
	}
}
