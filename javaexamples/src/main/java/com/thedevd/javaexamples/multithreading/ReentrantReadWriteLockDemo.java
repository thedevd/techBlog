package com.thedevd.javaexamples.multithreading;

import java.util.concurrent.locks.ReentrantReadWriteLock;

/* ReentrantReadWriteLock is used to improve the performance if the use case it to have frequent
 * read operation and infrequent write operation.
 * 
 * The problem with ReentrantLock is that only one thread at a time is allowed to access the
 * resource, no matter if the operation is read or write. It means if there are 10 threads which
 * only want to read the resource, even in this case only one thread will be allowed at a time. So
 * this is not efficient.
 * 
 * So in such situation we can use ReentrantReadWriteLock to improve the performance, where more
 * than one reader thread can be allowed to read the shared resource simultaneously at the same
 * time. And at a time only one writer thread will be allowed to change the state.
 * 
 * It is to be noted that at a time either n number of reader threads or one writer thread is
 * allowed to access the resource. But never both at the same time
 * 
 * 
 * In ReentrantReadWriteLock, separate locks are created -
 * 1. ReadLock - is used by reader threads.
 * 2. WriteLock  - is used by writer threads.
 * */
public class ReentrantReadWriteLockDemo {

	public static void main( String[] args )
	{
		SeatChartsScreen2 sharedResource = new SeatChartsScreen2();

		// Reader threads - reader1, reader2, reader3
		new Thread(() -> sharedResource.readResource(), "reader1").start();
		new Thread(() -> sharedResource.readResource(), "reader2").start();
		new Thread(() -> sharedResource.readResource(), "reader3").start();

		// Writer threads - writer1, writer2, writer3
		new Thread(() -> sharedResource.writeResource(), "writer1").start();
		new Thread(() -> sharedResource.writeResource(), "writer2").start();
		new Thread(() -> sharedResource.writeResource(), "writer3").start();

		// Reader threads - reader4, reader5
		new Thread(() -> sharedResource.readResource(), "reader4").start();
		new Thread(() -> sharedResource.readResource(), "reader5").start();

		// @formatter:off
		/*
		 * 		readLock held by: reader1
		 * 		readLock held by: reader2
		 * 		readLock held by: reader3
		 * 		## readLock released by: reader1
		 * 		## readLock released by: reader3
		 * 		## readLock released by: reader2
		 * 		writeLock held by: writer1
		 * 		## writeLock released by: writer1
		 * 		writeLock held by: writer2
		 * 		## writeLock released by: writer2
		 * 		writeLock held by: writer3
		 * 		## writeLock released by: writer3
		 * 		readLock held by: reader4
		 * 		readLock held by: reader5
		 * 		## readLock released by: reader5
		 * 		## readLock released by: reader4
		 * 		
		 * Just observer the above output. You can clearly see, three readers threads
		 * are allowed to hold the readLock same time. 
		 * Then only one writer thread is allowed (first writer1 then --> writer2) to hold the writeLock.
		 * Then reader4 and reader5 allowed to hold the lock same time.
		 * 
		 * So this is going to improve the performance, as more number of threads will be running at the same time.
		 * */
		// @formatter:on

	}
}

class SeatChartsScreen2 {

	ReentrantReadWriteLock readWriteLock;
	ReentrantReadWriteLock.ReadLock readLock;
	ReentrantReadWriteLock.WriteLock writeLock;

	public SeatChartsScreen2()
	{
		readWriteLock = new ReentrantReadWriteLock();
		readLock = readWriteLock.readLock();
		writeLock = readWriteLock.writeLock();
	}

	public void readResource()
	{
		try
		{
			readLock.lock();
			System.out.println("readLock held by: " + Thread.currentThread().getName());

			Thread.sleep(2000); // putting some sleeping time before releasing lock
		}
		catch( InterruptedException e )
		{
			e.printStackTrace();
		}
		finally
		{
			readLock.unlock();
			System.out.println("## readLock realeased by: " + Thread.currentThread().getName());
		}
	}

	public void writeResource()
	{

		try
		{
			writeLock.lock();
			System.out.println("writeLock held by: " + Thread.currentThread().getName());

			Thread.sleep(2000); // putting some sleeping time before releasing lock
		}
		catch( InterruptedException e )
		{
			e.printStackTrace();
		}
		finally
		{
			writeLock.unlock();
			System.out.println("## writeLock realeased by: " + Thread.currentThread().getName());
		}
	}

}
