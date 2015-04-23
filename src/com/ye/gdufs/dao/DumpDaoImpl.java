package com.ye.gdufs.dao;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

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
		setPath();
	}
	@Override
	public void save() throws FileNotFoundException, IOException{
		Misc.writeObject(dumpFile, dump);
	}
	@Override
	public Dump get(String key){
		Dump  dump1 = new Dump();
		dump1.setKeyStr(key);
		this.setDump(dump1);
		try {
			this.dump = (Dump) Misc.readObject(dumpFile);
		} catch (ClassNotFoundException | IOException e) {
			this.dump = null;
		}
		return dump;
	}
	public Dump getDump() {
		return dump;
	}
	public void setDump(Dump dump) {
		this.dump = dump;
		setPath();
	}
	protected void setPath(){
		String path = dumpInfo[0] + "/" + dump.getKeyStr() + "." + dumpInfo[1];
		dumpFile = new File(path);
	}
}
