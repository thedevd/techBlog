package com.thedevd.javaexamples.multithreading;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ProducerConsumerWithLockAndCondition {

	public static void main( String[] args )
	{
		MyBlockingQueue sharedResource = new MyBlockingQueue(5);
		Thread producer = new Thread(new MyProducerThread(sharedResource));
		Thread consumer = new Thread(new MyConsumerThread(sharedResource));

		producer.start();
		consumer.start();
	}
}

class MyBlockingQueue {

	private int maxSize;
	private Queue<Integer> queue;

	Lock lock = new ReentrantLock(true);
	Condition notFull = lock.newCondition();
	Condition notEmpty = lock.newCondition();

	public MyBlockingQueue( int maxSize )
	{
		queue = new LinkedList<>();
		this.maxSize = maxSize;
	}

	public void put( int item ) throws InterruptedException
	{
		lock.lock();
		try
		{
			while( queue.size() == maxSize )
			{
				// waiting for some one to say queue is notFull, and you can proceed to producer.
				System.out.println("#### Queue is full, waiting for empty space....");
				notFull.await();
			}
			queue.add(item);
			notEmpty.signalAll(); // sending signal to inform consumer that queue has an item now means queue is notEmpty.
		}
		finally
		{
			lock.unlock();
		}
	}

	public int get() throws InterruptedException
	{
		lock.lock();
		try
		{
			while( queue.size() == 0 )
			{
				// waiting for some one to say queue is notEmpty, and you can proceed consuming
				System.out.println("#### Queue is empty, waiting for an item....");
				notEmpty.await();
			}
			int item = queue.poll();
			notFull.signalAll(); // sending signal to inform producer that queue has some space now means queue is notFull

			return item;
		}
		finally
		{
			lock.unlock();
		}
	}

}

class MyProducerThread implements Runnable {

	private MyBlockingQueue sharedResource;

	public MyProducerThread( MyBlockingQueue sharedResource )
	{
		super();
		this.sharedResource = sharedResource;
	}

	@Override
	public void run()
	{
		int i = 0;
		while( true )
		{
			try
			{
				sharedResource.put(i);
				System.out.println("Produced Item: " + i);
				i++;

				Thread.sleep(1000); // producer is little bit fast than consumer
			}
			catch( InterruptedException e )
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}
}

class MyConsumerThread implements Runnable {

	private MyBlockingQueue sharedResource;

	public MyConsumerThread( MyBlockingQueue sharedResource )
	{
		super();
		this.sharedResource = sharedResource;
	}

	@Override
	public void run()
	{
		while( true )
		{
			int item;
			try
			{
				item = sharedResource.get();
				System.out.println("Consumed item: " + item);
				Thread.sleep(1500); // Consumer is little bit slow than producer
			}
			catch( InterruptedException e )
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

	}

}
