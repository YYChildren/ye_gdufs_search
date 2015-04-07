package com.ye.gdufs.dao;

public interface PageDao extends Dao{
	boolean isExistContentMd5(String Contentmd5) throws Exception;
}