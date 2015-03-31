package com.ye.gdufs.model;
import org.hibernate.cfg.Configuration;
import org.hibernate.tool.hbm2ddl.SchemaExport;
import org.junit.AfterClass;
import org.junit.Test;

public class HibernateTest {

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Test
	public void test() {
		new SchemaExport(new Configuration().configure()).create(true, false);
	}

}
