package com.thedevd.javaexamples.multithreading;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Exchanger;

/*
 * The java.util.concurrent package contains several classes known as Java Synchronizers that help set of 
 * threads to collaborate with each other. Some of those classes are -
 * 1. CountDownLatch 
 * 2. CyclicBarrier
 * 3. Semaphore And Mutex 
 * 4. SynchronousQueue (direct handoff)
 * 5. Phaser
 * 6. Exchanger
 * 
 * In this demo we will explain Exchanger Synchronizer. But before that read about SynchronousQueue
 * in SynchronousQueueDemo.java
 * 
 * So let see what is an Exchanger
 * ##################################
 * The Exchanger Class provides a synchronization point for two threads, where those threads can exchange their 
 * data with the other thread. Whenever a thread arrives at the exchange point, it must wait for 
 * the other thread to arrive. When the other pairing thread arrives, the two threads then proceed to exchange their objects.

 * An Exchanger is just like SynchronousQueue, but difference is that in Exchanger handoff is done 
 * from both direction. (in case of Synchronous, data handoff is done in from one end only ie. one direction only,
 * where as in case of Exchanger threads at both end can handoff data to each other, it is like
 * threads are exchanging objects with each other). Exchanger is a kind of bidirectional SynchronousQueue
 * 
 * For example- we have two threads, thread-1 and thread-2.
 * thread-1 has object A to be exchange with thread-2 and thread-2 has object B to be exchanged with thread-1.
 * So here both threads will have to wait for other thread availability to exchange the objects.
 * Meaning if thread-2 is not available to consume object A then thread-1 will wait, similarly
 * if thread-1 is not available to consumer object B then thread-2 will wait.
 * 
 * Working of Exchanger
 * #######################
 * Exchanger provides a synchronization point at which two threads can pair and swap elements. 
 * Exchanger waits until two separate threads call its exchange() method. When two threads have called the exchange() method, 
 * Exchanger will swap the objects provided by the threads.
 * 
 * Usage of Exchanger in Java
 * ############################
 * Exchanger can be used in a Producer-Consumer scenarios where one thread will produce the data, fill the buffer and 
 * exchange it with the consumer thread. Consumer thread in turn will pass the empty buffer to the producer thread.
 * 
 * Analogy for this- We have two persons, person-1 and person-2.
 * person-1 has the tank-1 fully filled with water and person-2 has empty tank-2.
 * There is only one bucket available. Now the problem is they have to transfer the water from tank-1 to tank-2 using single bucket.
 * Solutions will be person-1 will fill the bucket and give it to person-2 and wait for person-2 to return empty bucket and 
 * the same process will go on. So here person-1 has to wait for person-2 availability and person-2 has to wait for person-1 availability
 * So this is a very good example of Exchanger.
 * 
 * Another good example is - before sending another list of items, waiting for other party to send an ACKNOWLEGMENT of data received.
 * Here ACK can be sent in the form of empty list.
 */
public class ExchangerDemo {

	public static void main( String[] args )
	{
		Exchanger<List<Integer>> exchanger = new Exchanger<>();
		
		Thread party1 = new Thread(new ExchangerParty1(exchanger), "person-1");
		Thread party2 = new Thread(new ExchangerParty2(exchanger), "person-2");
		
		party1.start();
		party2.start();
		
		/*
		 * Sample output
		 * ######################
		 * person-1: Got empty bucket from other end= 0
		 * person-2: Got filled bucket from other end=5
		 * person-1: Got empty bucket from other end= 0
		 * person-2: Got filled bucket from other end=5
		 * person-1: Got empty bucket from other end= 0
		 * person-2: Got filled bucket from other end=5
		 * 
		 * Observation-
		 * #############
		 * According to the person-2 implementation, person-2 reach to Exchange point first and wait for person-1.
		 * As soon as person-1 reaches the Exchange point, person-2 come out of waiting state  and both
		 * person-1 and person-2 exchange the filled and empty bucket respectively. 
		 * 
		 *  And the same process goes no....
		 */
	}

}

class ExchangerParty1 implements Runnable {

	private Exchanger<List<Integer>> exchanger;

	public ExchangerParty1( Exchanger<List<Integer>> exchanger )
	{
		super();
		this.exchanger = exchanger;
	}

	@Override
	public void run()
	{
		List<Integer> bucket = new ArrayList<>(); // initially empty bucket
		while( true )
		{
			// filling the bucket
			bucket.add(1);
			bucket.add(2);
			bucket.add(3);
			bucket.add(4);
			bucket.add(5);

			try
			{
				Thread.sleep(10000); // time taken to fill the bucket
				bucket = exchanger.exchange(bucket); // exchange the filled bucket with empty bucket
				System.out.println(
						Thread.currentThread().getName() + ": Got empty bucket from other end= " + bucket.size());
			}
			catch( InterruptedException e )
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

	}

}

class ExchangerParty2 implements Runnable {

	private Exchanger<List<Integer>> exchanger;

	public ExchangerParty2( Exchanger<List<Integer>> exchanger )
	{
		super();
		this.exchanger = exchanger;
	}

	@Override
	public void run()
	{
		while( true )
		{

			try
			{
				List<Integer> bucket = exchanger.exchange(new ArrayList<>()); // exchange the empty bucket with filled bucket
				System.out.println(
						Thread.currentThread().getName() + ": Got filled bucket from other end=" + bucket.size());

				// Time taken to fill the tank using bucket and after some time give the empty bucket back.
				Thread.sleep(5000);
			}
			catch( InterruptedException e )
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

	}

}
