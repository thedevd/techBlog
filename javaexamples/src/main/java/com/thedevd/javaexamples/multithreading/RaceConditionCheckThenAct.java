package com.thedevd.javaexamples.multithreading;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/*
 * What is race condition-
 * #########################
 * It is one of the problem that arise in multi-threaded environment where
 * multiple threads access shared variables and you get the wrong result due to thread interleaving,
 * means the value of the variable depends on execution order of the threads.
 * 
 * Two types of race condition=
 * 1. Check-then-act. (boolean condition)
 * 2. Read-modify-write. (counters)
 * 
 * Race condition can be solved by allowing only thread to change the status
 * of shared variable and this can be done either using synchronization technique (synchronized block
 * or use of Lock --> these are to solve check-then-act) or using Atomic Variable (in case when 
 * shared variable is counter) 
 */
public class RaceConditionCheckThenAct {

	public static void main( String[] args )
	{
		// Shared Resource
		SharedResource sharedResource = new SharedResource();
		
		// Creating threads of BadUserThread where you may end up with wrong output
		Thread user1AskingBook1 = new Thread(new BadUserThread(sharedResource, "user1", "book1"));
		Thread user2AskingBook1 = new Thread(new BadUserThread(sharedResource, "user2", "book1"));
		Thread user3AskingBook1 = new Thread(new BadUserThread(sharedResource, "user3", "book1"));

		user1AskingBook1.start();
		user2AskingBook1.start();
		user3AskingBook1.start();
		
		/*
		 * Sometimes you end up with book1 being assinged to more than one user which is wrong
		 * 
		 * book1 is assigned to user: user2
		 * book1 is already assinged, so can not be loaned to user: user1
		 * book1 is assigned to user: user3
		 */

		// Creating threads of GoodUserThread that solved the problem
		Thread user10AskingBook2 = new Thread(new GoodUserThread(sharedResource, "user10", "book2"));
		Thread user11AskingBook2 = new Thread(new GoodUserThread(sharedResource, "user11", "book2"));
		Thread user12AskingBook2 = new Thread(new GoodUserThread(sharedResource, "user12", "book2"));

		user10AskingBook2.start();
		user11AskingBook2.start();
		user12AskingBook2.start();
		
		/*
		 * GoodUserThread will always guarantee of book1 being assigned to only one user
		 * 
		 * book2 is assigned to user: user12
		 * book2 is already assinged, so can not be loaned to user: user11
		 * book2 is already assinged, so can not be loaned to user: user10
		 */
	}
}

class SharedResource {

	Map<String, String> loanedBookStatus = new ConcurrentHashMap<String, String>();

	public Map<String, String> getLoanedBookStatus()
	{
		return loanedBookStatus;
	}

}

class BadUserThread implements Runnable {

	SharedResource sharedResource;
	String userId;
	String bookId;

	public BadUserThread( SharedResource sharedResource, String userId, String bookId )
	{
		super();
		this.sharedResource = sharedResource;
		this.userId = userId;
		this.bookId = bookId;
	}

	@Override
	public void run()
	{
		try
		{
			Thread.sleep(1);
		}
		catch( InterruptedException e )
		{
			e.printStackTrace();
		}

		// Two thread might check this condition at same time and thus resulting wrong output
		if( !sharedResource.getLoanedBookStatus().containsKey(bookId) )
		{
			sharedResource.getLoanedBookStatus().put(bookId, userId);
			System.out.println(bookId + " is assigned to user: " + userId);
		}
		else
		{
			System.out.println(bookId + " is already assinged, so can not be loaned to user: " + userId);
		}

	}

}

class GoodUserThread implements Runnable {

	private SharedResource sharedResource;
	private String userId;
	private String bookId;

	public GoodUserThread( SharedResource sharedResource, String userId, String bookId )
	{
		super();
		this.sharedResource = sharedResource;
		this.userId = userId;
		this.bookId = bookId;
	}

	@Override
	public void run()
	{
		try
		{
			Thread.sleep(1);
		}
		catch( InterruptedException e )
		{
			e.printStackTrace();
		}
		
		// Only one thread is allowed to enter this block
		synchronized( sharedResource )
		{
			if( !sharedResource.getLoanedBookStatus().containsKey(bookId) )
			{

				sharedResource.getLoanedBookStatus().put(bookId, userId);
				System.out.println(bookId + " is assigned to user: " + userId);
			}
			else
			{
				System.out.println(bookId + " is already assinged, so can not be loaned to user: " + userId);
			}
		}
	}

}
