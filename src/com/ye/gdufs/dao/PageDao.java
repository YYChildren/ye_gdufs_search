package com.ye.gdufs.dao;

import java.io.Serializable;

public interface PageDao extends Dao{
	void get(Serializable id);
}