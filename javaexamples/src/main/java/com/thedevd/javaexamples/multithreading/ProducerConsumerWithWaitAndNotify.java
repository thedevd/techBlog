package com.thedevd.javaexamples.multithreading;

import java.util.LinkedList;
import java.util.Queue;

public class ProducerConsumerWithWaitAndNotify {

	public static void main( String[] args )
	{
		MyBlockingQ queue = new MyBlockingQ(5);

		Producer producer = new Producer(queue);
		Consumer consumer = new Consumer(queue);
		producer.start();
		consumer.start();
	}
}

class MyBlockingQ {

	private int maxSize;
	private Queue<Integer> queue;

	public MyBlockingQ( int maxSize )
	{
		queue = new LinkedList<Integer>();
		this.maxSize = maxSize;
	}

	public synchronized void put( int item ) throws InterruptedException
	{
		while( queue.size() == maxSize )
		{
			System.out.println("##### queue is full, waiting ...");
			wait(); // if queue is full, then wait for a consumer to consume an item first.
		}
		queue.add(item);
		notifyAll(); // sending signal to all consumers that queue has something to consumer.
	}

	public synchronized int get() throws InterruptedException
	{
		while( queue.size() == 0 )
		{
			System.out.println("##### queue is empty, waiting ...");
			wait(); // if no item in queue then wait for Producer to produce it first.
		}
		int item = queue.poll();
		notifyAll(); // sending signal to all producer that queue has some space now.

		return item;

	}
}

class Producer extends Thread {

	MyBlockingQ queue;

	public Producer( MyBlockingQ queue )
	{
		this.queue = queue;
	}

	public void run()
	{
		int i = 0;
		while( true )
		{
			try
			{
				queue.put(i);
				System.out.println("Produced item: " + i);
				i++;

				// producer is little bit slow than consumer.
				Thread.sleep(1500);
			}
			catch( InterruptedException e1 )
			{
				e1.printStackTrace();
			}
		}
	}
}

class Consumer extends Thread {

	MyBlockingQ queue;

	public Consumer( MyBlockingQ queue )
	{
		this.queue = queue;
	}

	public void run()
	{
		while( true )
		{
			try
			{
				int item = queue.get();
				System.out.println("Consumed item: " + item);

				/* Just for demo I have made consumer little bit faster than producer, It means
				 * after some point, queue empty condition will come, and consumer will go on
				 * waiting state.
				 * 
				 * So you will see this message in console - ##### queue is empty, waiting ... */
				Thread.sleep(1000);
			}
			catch( InterruptedException e1 )
			{
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
	}
}
