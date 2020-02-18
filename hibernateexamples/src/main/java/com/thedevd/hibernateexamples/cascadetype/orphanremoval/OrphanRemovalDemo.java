package com.thedevd.hibernateexamples.cascadetype.orphanremoval;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.query.Query;

import com.thedevd.hibernateexamples.utils.SessionFactoryUtil;

/*
 * Apart from JPA/Hibernate provided cascade types, there is one more cascading operation in hibernate 
 * which is not part of the CascadeType enum, and it is called orphanRemoval. Lets understand this by 
 * an example- 
 * In our Author class, I have mentioned orphanRemoval=true on OneToMany relationship i.e.
 * 		@OneToMany(orphanRemoval = true, mappedBy = "author")
 * 		private List<Book> book = new ArrayList<>();
 * 
 * orphanRemoval=true means whenever I will remove a book object from the List<Book>, then the
 * book entity which is not associated now with any other Author object (i.e. orphan) should be 
 * deleted.
 * 
 * In the code we can see we have three books in the list - book1,book2 and book3. And I have
 * tried to remove the first book from the list using list.remove(index) method. So book1 is now
 * considered as orphan object so it will be deleted.
 * 
 * So conclusion is that - orphanRemoval is a very good way of removing the item from the db in case
 * of OneToMany or ManyToOne relationship. Here we just need to remove the object from the collection and
 * hibernate will take care of rest of the things for you.
 */
class OrphanRemovalDemo {
	
	public static void main(String[] args) {
		
		Configuration configuration = SessionFactoryUtil.bootstrapHibernateConfiguration();
		configuration.addAnnotatedClass(Author.class).addAnnotatedClass(Book.class);
		
		SessionFactory factory = SessionFactoryUtil.createSessionFactory(configuration);
		
		/*
		 * First save the testing data in db using separate session.
		 */		
		setUpInitialData(factory);
		
		
		/*
		 * Now lets remove one of the book from the author object
		 */
		Session session2 = factory.openSession();
		session2.beginTransaction();
		
		Author author100 = session2.load(Author.class, 100);
		List<Book> books = author100.getBook();
		System.out.println("Step-1 ##### no of books at beginning: " + books.size()); // --> 3
		
		// remove the first book from the list
		books.remove(0); // so orphanRemoval=true should do the trick here.
		// verify there are 2 books now in list
		System.out.println("Step-2 ##### no of books after removing first: " + books.size()); // --> 2
		
		SessionFactoryUtil.commitAndCloseSession(session2);
		/*
		 * So as soon as you commit the transaction, then a delete query is fired at background
		 * 		Hibernate: delete from Book where id=?	
		 * 
		 * So we can say so orphanRemoval=true does the trick here.
		 */
		
		
		/*
		 * Now lets open another session and see if author is now having 2 books not 3
		 */
		Session session3 = factory.openSession();
		session3.beginTransaction();
		
		Author author = session3.load(Author.class, 100);
		List<Book> newBookList = author.getBook(); // this should be 2
		System.out.println("Step-3 ##### no of books now ( due to orphanRemoval=true): " + newBookList.size()); // --> 2
		
		// verify there are two books in the book table
		@SuppressWarnings("rawtypes")
		Query query = session3.createQuery("from Book b");
		List<Book> fromBookTable = query.list();
		System.out.println("Step-4 ##### no of books from table: " + fromBookTable.size());
		
		SessionFactoryUtil.commitAndCloseSession(session3);
		
	}
	
	private static void setUpInitialData(SessionFactory factory) {
		
		Session session1 = factory.openSession();
		session1.beginTransaction();
		
		// Save the data first in database using separate session
		Author author = new Author();
		author.setId(100);
		author.setName("Dev");
		
		Book book1 = new Book();
		book1.setId(1);
		book1.setName("book1");
		book1.setAuthor(author);
		
		Book book2 = new Book();
		book2.setId(2);
		book2.setName("book2");
		book2.setAuthor(author);
		
		Book book3 = new Book();
		book3.setId(3);
		book3.setName("book3");
		book3.setAuthor(author);
		
		author.getBook().add(book1);
		author.getBook().add(book2);
		author.getBook().add(book3);

		session1.save(author);
		session1.save(book1);
		session1.save(book2);
		session1.save(book3);
		
		SessionFactoryUtil.commitAndCloseSession(session1);
	}

}
