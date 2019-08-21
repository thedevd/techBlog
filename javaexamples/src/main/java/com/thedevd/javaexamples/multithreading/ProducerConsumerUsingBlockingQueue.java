package com.thedevd.javaexamples.multithreading;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class ProducerConsumerUsingBlockingQueue {

	public static void main( String args[] )
	{

		// Creating shared queue - LinkedBlockingQueue
		BlockingQueue<Integer> sharedQueue = new LinkedBlockingQueue<Integer>(10);

		//Creating Producer and Consumer Thread
		Thread prodThread = new Thread(new BQProducer(sharedQueue));
		Thread consThread = new Thread(new BQConsumer(sharedQueue));

		//Starting producer and Consumer thread
		prodThread.start();
		consThread.start();
	}

}

// Producer Class in java - put is used to put element in blocking Q
class BQProducer implements Runnable {

	private final BlockingQueue<Integer> sharedQueue;

	public BQProducer( BlockingQueue<Integer> sharedQueue )
	{
		this.sharedQueue = sharedQueue;
	}

	@Override
	public void run()
	{
		int i = 1;
		while( true )
		{
			try
			{
				sharedQueue.put(i);
				// The queue will block the put if total items in queue is 10
				System.out.println("Produced: " + i + ". total items in queue: " + sharedQueue.size());
				i++;
				Thread.sleep(1000);
			}
			catch( InterruptedException ex )
			{
				System.err.println(ex);
			}
		}
	}

}

//Consumer Class in Java - take is used to consume item from blocking Q
class BQConsumer implements Runnable {

	private final BlockingQueue<Integer> sharedQueue;

	public BQConsumer( BlockingQueue<Integer> sharedQueue )
	{
		this.sharedQueue = sharedQueue;
	}

	@Override
	public void run()
	{
		while( true )
		{
			try
			{
				System.out.println("Consumed: " + sharedQueue.take());
				Thread.sleep(2000);
			}
			catch( InterruptedException ex )
			{
				System.err.println(ex);
			}
		}
	}

}
