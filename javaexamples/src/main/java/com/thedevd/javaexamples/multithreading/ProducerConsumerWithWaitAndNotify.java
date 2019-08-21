package com.thedevd.javaexamples.multithreading;

public class ProducerConsumerWithWaitAndNotify {

	public static void main( String[] args )
	{
		MyBlockingQ queue = new MyBlockingQ();

		Producer producer = new Producer(queue);
		Consumer consumer = new Consumer(queue);
		producer.start();
		consumer.start();
	}
}

class MyBlockingQ {

	int item;
	boolean isProduced = false;

	public synchronized void put( int item )
	{
		if( isProduced )
		{
			try
			{
				wait(); // wait for consumer to consume it first
			}
			catch( InterruptedException e )
			{
				System.err.print(e);
			}
		}
		this.item = item;
		System.out.println("Produced item: " + this.item);
		isProduced = true;
		notify();
	}

	public synchronized void get()
	{
		if( !isProduced )
		{
			try
			{
				wait(); // if no item produced then wait for Producer to produce it first
			}
			catch( InterruptedException e )
			{
				System.err.print(e);
			}
		}
		System.out.println("Consumed item: " + this.item);
		isProduced = false;
		notify();

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
			queue.put(i++);
			try
			{
				Thread.sleep(1000);
			}
			catch( InterruptedException e )
			{
				System.err.print(e);
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
			queue.get();
			try
			{
				Thread.sleep(1000);
			}
			catch( InterruptedException e )
			{
				System.err.print(e);
			}
		}
	}
}
