package com.ye.gdufs.dao;

import com.ye.gdufs.model.Dump;

public interface DumpDao {
	public void save() throws Exception;
	public Dump get(String key) throws Exception;
}
