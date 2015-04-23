package com.ye.gdufs.dao;

import java.io.FileNotFoundException;
import java.io.IOException;

import com.ye.gdufs.model.Dump;

public interface DumpDao {
	public void save() throws FileNotFoundException, IOException;
	public Dump get(String key);
}
