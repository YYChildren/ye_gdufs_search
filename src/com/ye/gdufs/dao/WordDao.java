package com.ye.gdufs.dao;

import com.ye.gdufs.model.Word;


public interface WordDao extends Dao{
	void get(String word) throws Exception;
}
