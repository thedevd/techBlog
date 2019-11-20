package com.thedevd.javaexamples.multithreading;

import java.util.Random;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.SynchronousQueue;

/*
 * java.util.concurrent.BlockingQueue has following implementations-
 * 1. ArrayBlockingQueue
 * 2. LinkedBlockingQueue
 * 3. PriorityBlockingQueue
 * 4. DelayQueue
 * 5. SynchronousQueue
 * 
 * In this, we will explain SynchronousQueue.
 * 
 * What is SynchronousQueue
 * #############################
 * SynchronousQueue is very interesting type of BlockingQueue, which has zero size. Yes you heard
 * right, it does not have even a single slot to store an item. Then question comes why to use this. Lets see that -
 * 
 * SynchronousQueue is a way to implement direct handoffs between producer and consumer, means
 * one thread is handing off an element, and another thread is taking that element.
 * 
 * SynchronousQueue supports only two methods put() and take(). It does not have any poll(), peek() api.
 * it has zero capacity. 
 * How put() is different here with respect to put() on other type of blockingQueue is that, calling put() by producer thread 
 * will be blocked until a consumer thread comes and calls take() method. After a consumer thread comes and call take()
 * then only producer will come out of blocking state and handoff the item directly to the consumer. So the item will 
 * not be stored anywhere, it is given directly to consumer thread. 
 * 
 * Therefore we can say that after calling put(), producer thread will have to wait for a consumer thread to come and call take().
 * 
 * If you pay attention to the name, you will also understand that it is named SynchronousQueue with a reason which is -
 * it passes data synchronously to other thread means 
 *   instead of just putting data and returning (asynchronous operation), it wait for the other party to come and request taking the data 
 * .
 * 
 * Difference b/w SynchronousQueue and LinkedBlockingQueue with size 1
 * ######################################################################
 * 1. Major difference is that SynchronousQueue has zero capacity(). 
 * 2. Calling put() on synchronousQueue does not return immediately, rather it wait until there is corresponding take() call on the queue,
 * where as calling put() on LinkedBlockingQueue with size 1 will not wait if the queue is empty.
 * 3. peek(), poll() and iteration is not possible on SynchronousQueue as it has nothing in it.
 * 
 * Note- SynchronousQueue is the default BlockingQueue used for the Executors.newCachedThreadPool() methods.
 * 
 * You can correlated SynchronousQueue with athletes (threads) running with Olympic torch, 
 * they run with torch (object need to be passed) and passes it to other athlete waiting at other end.
 */
public class SynchronousQueueDemo {

	public static void main( String[] args ) throws InterruptedException
	{

		// creating SynchronousQueue with fair policy enabled
		BlockingQueue<String> synchronousQueue = new SynchronousQueue<>(true); // true meand- fair policy used
		
		// starting producer first.
		Thread producer1 = new Thread(new SynchronousProducer(synchronousQueue), "producer-1");
		producer1.start();
		
		// first consumer starts after some time later 
		Thread.sleep(10000);
		Thread consumer1 = new Thread(new SynchronousConsumer(synchronousQueue), "consumer-1");
		consumer1.start();
		
		// second consumer starts some time later
		Thread.sleep(3000);
		Thread consumer2 = new Thread(new SynchronousConsumer(synchronousQueue), "consumer-2");
		consumer2.start();
		
		/*
		 * Sample output
		 * #################
		 * 
		 * consumer-1: recieved data=8263
		 * producer-1: data Handoff successful= 8263
		 * consumer-2: recieved data=897
		 * producer-1: data Handoff successful= 897
		 * consumer-1: recieved data=8926
		 * producer-1: data Handoff successful= 8926
		 * consumer-2: recieved data=2795
		 * producer-1: data Handoff successful= 2795
		 * 
		 * 
		 * Observations-
		 * #############
		 * We can see that we are running the producer-1 first, then after 10 second running first consumer (consumer-1).
		 * So we will see the very first two lines around 10 seconds later because producer-1
		 * calling put() immediately on queue will wait until one of the consumer (consumer-1) 
		 * comes and calls take(). 
		 * 
		 * So each time put() will wait until corresponding take() call is made by one of the consumer.
		 * 
		 * Also we are seeing fair behavior, means data handoff is done fairly one by one with consumer-1 and consumer-2.
		 * this is because of fairness boolean is used in the constructor of SynchronousQueue
		 * 
		 *    BlockingQueue<String> synchronousQueue = new SynchronousQueue<>(true); // fair policy used
		 *    
		 * Note- try running with only one consumer for more understanding, 
		 * you will see after calling put() producer is going to wait each time around 5 second for a consumer to call take().
		 */
	}
}

class SynchronousProducer implements Runnable {

	private BlockingQueue<String> synchronousQueue;
	private static Random random = new Random();

	public SynchronousProducer( BlockingQueue<String> synchronousQueue )
	{
		super();
		this.synchronousQueue = synchronousQueue;
	}

	@Override
	public void run()
	{
		while( true )
		{
			String data = String.valueOf(random.nextInt(10000));
			try
			{
				synchronousQueue.put(data); // will wait for a consumer thread to call take()
				System.out.println(Thread.currentThread().getName() + ": data Handoff successful= " + data);
				Thread.sleep(1000);
			}
			catch( InterruptedException e )
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
		}

	}

}

class SynchronousConsumer implements Runnable {

	private BlockingQueue<String> synchronousQueue;

	public SynchronousConsumer( BlockingQueue<String> synchronousQueue )
	{
		super();
		this.synchronousQueue = synchronousQueue;
	}

	@Override
	public void run()
	{
		while(true)
		{
			try
			{
				String data = synchronousQueue.take();
				System.out.println(Thread.currentThread().getName() + ": recieved data=" + data);
				
				Thread.sleep(5000);// consumer is slow in consuming item
			}
			catch( InterruptedException e )
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

}
