package com.ye.gdufs.util;

import java.io.Serializable;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class HibernateUtil {
     
    private static final SessionFactory sessionFactory = buildSessionFactory();

    @SuppressWarnings("deprecation")
	private static SessionFactory buildSessionFactory() {
        try {
        	return new Configuration().configure().buildSessionFactory();
        }
        catch (Throwable ex) {
            System.err.println("Initial SessionFactory creation failed." + ex);
            throw new ExceptionInInitializerError(ex);
        }
    }

    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }
    
    public static void save(Object data) throws Exception{
		HibernateSql hs = session -> {session.save(data);return null;};
		execute(hs);
    }
    
    public static void saveOrUpdate(Object data) throws Exception{
		HibernateSql hs = session -> {session.saveOrUpdate(data);return null;};
		execute(hs);
    }

	public static Object get(Class<?> c,Serializable id) throws Exception {
		HibernateSql hs = session -> session.get(c, id);
		return execute(hs);
	}
	
	public static Object load(Class<?> c,Serializable id) throws Exception {
		HibernateSql hs = session -> session.load(c, id);
		return execute(hs);
	}
	
	public static void delete(Object o) throws Exception{
		HibernateSql hs = session -> {session.delete(o);return null;};
		execute(hs);
	}
	
	public static Object execute(HibernateSql hs) throws Exception{
		Session session = null;
		try {
			session =  HibernateUtil.getSessionFactory().openSession();
			session.beginTransaction();
			Object result = hs.execute(session);
			session.getTransaction().commit();
			return result;
		} catch (Exception e) {
			session.getTransaction().rollback();
			throw e;
		}finally{
			session.close();
		}
	}
}