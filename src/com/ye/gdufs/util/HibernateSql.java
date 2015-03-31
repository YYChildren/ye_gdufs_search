package com.ye.gdufs.util;

import org.hibernate.Session;

public interface HibernateSql {
	Object execute(Session session) throws Exception;
}
