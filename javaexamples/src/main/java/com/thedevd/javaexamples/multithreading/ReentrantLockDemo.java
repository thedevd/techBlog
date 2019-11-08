package com.thedevd.javaexamples.multithreading;

import java.util.concurrent.locks.ReentrantLock;

public class ReentrantLockDemo {

	public static void main( String[] args )
	{
		SeatChartsScreen1 sharedResource = new SeatChartsScreen1();
		
		new Thread(() -> sharedResource.readResource(), "reader1").start();
		new Thread(() -> sharedResource.readResource(), "reader2").start();
		new Thread(() -> sharedResource.readResource(), "reader3").start();
		
		new Thread(() -> sharedResource.writeResource(), "writer1").start();
		new Thread(() -> sharedResource.writeResource(), "writer2").start();
		
		new Thread(() -> sharedResource.readResource(), "reader4").start();
		new Thread(() -> sharedResource.readResource(), "reader5").start();
		
		// @formatter:off
 
		/*
		 * 		readResource() - lock held by: reader1
		 * 		## readResource()- lock released by: reader1
		 * 		readResource() - lock held by: reader2
		 * 		## readResource()- lock released by: reader2
		 * 		readResource() - lock held by: reader3
		 * 		## readResource()- lock released by: reader3
		 * 		writeResource() - lock held by: writer1
		 * 		## writeResource() - lock realeased by: writer
		 * 		writeResource() - lock held by: writer2
		 * 		## writeResource() - lock realeased by: writer2
		 * 		readResource() - lock held by: reader4
		 * 		## readResource()- lock released by: reader4
		 * 		readResource() - lock held by: reader5
		 * 		## readResource()- lock released by: reader5
		 * 
		 * 
		 * Just observe the output, only one thread is allowed to access the resource at a time
		 * no matter of if the thread wants to only read or write.
		 * 
		 * Compare this ouput with ReentrantReadWriteLock.java. You will see the difference
		 * that ReentrantLock is not efficient if the use case is of frequent read operations and 
		 * infrequent write operation.
		 * 
		 * */
		
		// @formatter:on

		
	}
}

class SeatChartsScreen1 {

	ReentrantLock lock = new ReentrantLock(true);

	public void readResource()
	{
		try
		{
			lock.lock();
			System.out.println("readResource() - lock held by: " + Thread.currentThread().getName());
			Thread.sleep(2000);
		}
		catch( InterruptedException e )
		{
			e.printStackTrace();
		}
		finally
		{
			lock.unlock();
			System.out.println("## readResource()- lock released by: " + Thread.currentThread().getName());
		}
	}

	public void writeResource()
	{
		try
		{
			lock.lock();
			System.out.println("writeResource() - lock held by: " + Thread.currentThread().getName());
			Thread.sleep(2000);
		}
		catch( InterruptedException e )
		{
			e.printStackTrace();
		}
		finally
		{
			lock.unlock();
			System.out.println("## writeResource() - lock realeased by: " + Thread.currentThread().getName());
		}
	}
}
