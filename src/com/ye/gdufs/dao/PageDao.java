package com.ye.gdufs.dao;

import java.io.Serializable;

public interface PageDao extends Dao{
	boolean isExistContentMd5(String Contentmd5) throws Exception;
	void get(Serializable id);
}