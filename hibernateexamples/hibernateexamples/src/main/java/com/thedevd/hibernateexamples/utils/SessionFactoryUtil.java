package com.thedevd.hibernateexamples.utils;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class SessionFactoryUtil {
	
	private static SessionFactory sessionFactory;
	
	
	public static SessionFactory createSessionFactory(Configuration configuration) {
		
		sessionFactory = configuration.buildSessionFactory();
		return sessionFactory;
		
	}
	
	public static Configuration bootstrapHibernateConfiguration() {
		Configuration configuration = new Configuration();
		
		/*
		 * configuration.setProperty("hibernate.dialect", "org.hibernate.dialect.H2Dialect");
		 * configuration.setProperty("hibernate.connection.driver_class", "org.h2.Driver");
		 * configuration.setProperty("hibernate.connection.url", "jdbc:h2:mem:test");
		 */
		
		configuration.setProperty("hibernate.dialect", "org.hibernate.dialect.MySQL5Dialect");
		configuration.setProperty("hibernate.connection.driver_class", "com.mysql.jdbc.Driver");
		configuration.setProperty("hibernate.connection.url", "jdbc:mysql://localhost:3306/test");
		configuration.setProperty("hibernate.connection.username", "root");
		configuration.setProperty("hibernate.connection.password", "root");
		
		configuration.setProperty("hibernate.hbm2ddl.auto", "create");
		configuration.setProperty("hibernate.show_sql", "true");
		//configuration.setProperty("hibernate.format_sql", "true");
		
		//configuration.addAnnotatedClass(Author.class).addAnnotatedClass(Book.class);
		
		return configuration;
	}
	
	public static void commitAndCloseSession(Session session) {
		session.getTransaction().commit();
		session.close();
	}
	

}
