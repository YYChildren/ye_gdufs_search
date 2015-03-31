package com.ye.gdufs.dao;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.hibernate.Session;

public interface Dao {

	public abstract void save() throws Exception;

	public abstract void rsave(Session session) throws FileNotFoundException,
			IOException;

	public abstract void rrollback();

	public abstract void serSave() throws FileNotFoundException, IOException;

}