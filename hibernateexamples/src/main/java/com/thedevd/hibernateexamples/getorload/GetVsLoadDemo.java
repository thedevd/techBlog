package com.thedevd.hibernateexamples.getorload;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import com.thedevd.hibernateexamples.utils.SessionFactoryUtil;


/*
 * This demo explains the difference b/w session.get() and session.load() method in hibernate.
 * (For this example I am using Mysql8 running locally)
 */

public class GetVsLoadDemo {
	
	public static void main(String[] args) {
		
		// setup hibernate session with mysql
		Configuration configuration = SessionFactoryUtil.bootstrapHibernateConfiguration();
		configuration.addAnnotatedClass(Author.class);
		
		SessionFactory sessionFactory = SessionFactoryUtil.createSessionFactory(configuration);
		
		Session session1 = sessionFactory.openSession();
		session1.beginTransaction();
		
		
		// lets save some records for testing get() and load()
		Author author1 = new Author();
		author1.setId(101);
		author1.setName("author101");
		
		Author author2 = new Author();
		author2.setId(102);
		author2.setName("author102");
		
		session1.save(author1);
		session1.save(author2);
		
		SessionFactoryUtil.commitAndCloseSession(session1);
		
		/*
		 * Lets open another session and try to fetch object using get and load
		 * and see the behavior internally.
		 */
		Session session2 = sessionFactory.openSession();
		session2.beginTransaction();
		
		System.out.println("#### Calling session.get()");
		Author authorFetchedUsingGet = session2.get(Author.class, 101);
		System.out.println("authorFetchedUsingGet: [" + authorFetchedUsingGet.getId() + ", " + authorFetchedUsingGet.getName() + "]");
		
		/*
		 * #### Calling session.get()
		 * Hibernate: select author0_.id as id1_0_0_, author0_.name as name2_0_0_ from Author author0_ where author0_.id=?
		 * authorFetchedUsingGet: [101, author101]
		 * 
		 * Observation-
		 * ###################
		 * As you can see calling get() always hit the database and  fetch the record.
		 */
		
		System.out.println("#### Calling session.load()");
		Author authorFetchedUsingLoad = session2.load(Author.class, 102);
		System.out.println(authorFetchedUsingLoad.getId());
		//System.out.println(authorFetchedFromLoad.getName());
		
		/*
		 * #### Calling session.load()
		 * 102
		 * 
		 * Observation-
		 * #############
		 * As you can see we do not see any query ran by hibernate to fetch record from database using load(), instead
		 * load() will simply return the fake object having only the primary key field. 
		 * 
		 * But fetching the other fields instead of primary key field, will cause database to hit. 
		 * (to see this uncomment the line no 67 where we are trying to get name field from the fake object returned by load().
		 * you will see a query is being fired to fetch name field of the requested record -
		 * 
		 *    Hibernate: select author0_.id as id1_0_0_, author0_.name as name2_0_0_ from Author author0_ where author0_.id=?
		 * )
		 * 
		 * So we can say load() is like a lazy way of fetching the record.
		 */
		
		
		SessionFactoryUtil.commitAndCloseSession(session2);
		
		/*
		 * There is another difference b/w get and load and i.e. the return value if object does not exist.
		 * For this we will try to fetch an object with id 103 which does not exist in our table. 
		 * 
		 * get() returns null
		 * load() returns ObjectNotFoundException if try to get other field from database;
		 * 
		 * It means if you not sure if record exist or not, prefer get() otherwise using load() will
		 * end up throwing the exception and if you have not handle it, program will terminate.
		 */
		
		Session session3 = sessionFactory.openSession();
		session3.beginTransaction();
		
		Author author103UsingGet = session3.get(Author.class, 103);
		System.out.println(author103UsingGet == null); // --> true
		
		Author author104UsingLoad = session3.load(Author.class, 104);
		try {
			// load will hit the db if we try to get any non-primary field from database and if object does not exist then
			// objectNotFoundException is thrown
			System.out.println(author104UsingLoad.getName());
		} catch (Exception e) {
			System.out.println(e); // --> org.hibernate.ObjectNotFoundException: No row with the given identifier
									// exists: [com.thedevd.hibernateexamples.getorload.Author#103]
		}
		
		
		SessionFactoryUtil.commitAndCloseSession(session3);
	}

}
