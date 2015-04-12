package com.ye.gdufs.model;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import org.hibernate.Session;
import org.junit.Test;

import com.ye.gdufs.util.HibernateUtil;


public class DumpTest {
	@Test
    public void test(){
    	Dump misc = new Dump();
    	misc.setKeyStr("test1");
    	String[] a = {"aaa","bbb","cccc"};
    	ByteArrayOutputStream b = new ByteArrayOutputStream(); 
    	try {
			new ObjectOutputStream( b ).writeObject(a);
		} catch (IOException e) {
			e.printStackTrace();
		}
    	byte[] objByte = b.toByteArray();
    	misc.setObjByte(objByte);
		Session session = HibernateUtil.getSessionFactory().openSession();
		session.beginTransaction();
		session.save(misc);
		session.getTransaction().commit();
    }
}
