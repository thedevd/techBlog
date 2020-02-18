package com.thedevd.hibernateexamples.cascadetype;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import com.thedevd.hibernateexamples.utils.SessionFactoryUtil;

public class CascadeTypeDemo {
	
	public static void main(String[] args) {
		
		Configuration configuration = SessionFactoryUtil.bootstrapHibernateConfiguration();
		configuration.addAnnotatedClass(Author.class).addAnnotatedClass(Book.class);
		
		SessionFactory sessionFactory = SessionFactoryUtil.createSessionFactory(configuration);
		
		Session session1 = sessionFactory.openSession();
		session1.beginTransaction();
		
		Author author101 = new Author();
		author101.setId(101);
		author101.setName("author101");
		
		Book book1 = new Book();
		book1.setId(1);
		book1.setName("book1");
		
		Book book2 = new Book();
		book2.setId(2);
		book2.setName("book2");
		
		Book book3 = new Book();
		book3.setId(3);
		book3.setName("book3");
		
		author101.getBook().add(book1);
		author101.getBook().add(book2);
		author101.getBook().add(book3);
		
		// session1.save(author101);
		session1.persist(author101); // using persist() as CascadeType is set to PERSIST which is javax.persistent specific
		
		// If not using CascadeType=PERSIST, need to persist all the books of author individually.
		//session1.persist(book1);
		//session1.persist(book2);
		//session1.persist(book3);
		
		
		SessionFactoryUtil.commitAndCloseSession(session1);
	}

}
