package com.thedevd.javaexamples.multithreading;

import java.util.concurrent.CountDownLatch;

/* Take an example that we have two threads, one thread's task is to compute sum of two numbers and 
 * other thread's task is to compute product of the same numbers. Now we want to calculate the sum of output
 * of these two threads. It means final output depends on both threads, i.e. main thread has
 * to wait for those two threads to first complete the required task then only final output is produced.
 * 
 * So CountDownLatch is used to make sure that the main task should wait for specific number of other threads to complete,
 * then only the  main task will start.
 * 
 * CountDownLatch vs CyclicBarrier
 * #####################################
 * A better real world example of CountDownLatch would be an exam prompter who waits patiently for each student 
 * to hand in their test. Students don't wait once they complete their exams and are free to leave. 
 * Once the last student hands in the test to prompter (or the time limit expires), 
 * the prompter stops waiting and leaves with the tests.
 * 
 * So With a CountDownLatch, the waiter thread (exam prompter) wait for the last arriving thread (student) to arrive, 
 * but those arriving threads don't do any waiting themselves after they countDown the latch.
 * Where As with a CyclicBarrier, each thread arrive at barrier and then wait for the last thread to arrive, and
 * then only all these threads continue themselves
 * 
 * Working of CountDownLatch 
 * ########################## 
 * When we create object of countDownLatch, we specify a number of threads that task should wait for. 
 * All those threads are required to down the count by one by calling CountDownLatch.countDown() once they are completed. 
 * As soon as the count becomes zero, the waiting task starts running.
 * 
 *    CountDownLatch latch = new CountDownLatch(2); 
 * 
 * 
 * */
public class CountDownLatchDemo {

	public static void main( String[] args ) throws InterruptedException
	{
		// Final output depends on two threads, to passing 2 as initial count in CoundDownLatch
		CountDownLatch latch = new CountDownLatch(2);
		
		int a = 10;
		int b = 20;

		// creating worker threads and starting them.
		ComputeSum task1 = new ComputeSum(a, b, latch, 5000);
		ComputeProduct task2 = new ComputeProduct(a, b, latch, 10000);
		Thread t1 = new Thread(task1, "ComputeSum"); // Thread 1
		Thread t2 = new Thread(task2, "ComputeProduct"); // Thread 2

		t1.start();
		t2.start();
		
		// let the main task wait for above two threads to complete required computation. 
		// to wait main thread, Call await() on latch
		System.out.println("Main task is waiting....");
		latch.await(); // Main task is waiting
		
		// Main task has started only after both threads has completed (ie. count of latch is zero)
		System.out.println("Main task can proceed, as the count of latch is now: " + latch.getCount());
		int finalResult = task1.getResult() + task2.getResult();
		System.out.println("Producing final result: " + finalResult);
		
		/*
		 * Overall output- 
		 * ########################
		 * 
		 * Main task is waiting....
		 * ComputeSum can do any other stuffs..
		 * ComputeProduct can do any other stuffs..
		 * Main task can proceed, as the count of latch is now: 0
		 * Producing final result: 230
		 * 
		 */
		
	}
}

class ComputeSum implements Runnable {

	private int x;
	private int y;
	private CountDownLatch latch;
	private long delayInMs;
	private int result = 0;

	public ComputeSum( int x, int y, CountDownLatch latch, long delayInMs )
	{
		super();
		this.x = x;
		this.y = y;
		this.latch = latch;
		this.delayInMs = delayInMs;
	}

	@Override
	public void run()
	{
		try
		{
			result = x + y;
			Thread.sleep(delayInMs);

			latch.countDown(); // down the count after required computation is done
			
			// Some other task to do, thread need not do wait after calling countDown().
			// So this is another difference b/w CountDownLatch and CyclicBarrier.
			System.out.println(Thread.currentThread().getName() + " can do any other stuffs..");
		}
		catch( InterruptedException e )
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public int getResult()
	{
		return result;
	}

}

class ComputeProduct implements Runnable {

	private int x;
	private int y;
	private CountDownLatch latch;
	private long delayInMs;
	private int result;

	public ComputeProduct( int x, int y, CountDownLatch latch, long delayInMs )
	{
		super();
		this.x = x;
		this.y = y;
		this.latch = latch;
		this.delayInMs = delayInMs;
	}

	@Override
	public void run()
	{
		try
		{
			result = x * y;
			Thread.sleep(delayInMs);

			latch.countDown(); // down the count after required computation is done
			
			// Some other task to do, thread need not do wait after calling countDown().
			// So this is another difference b/w CountDownLatch and CyclicBarrier.
			System.out.println(Thread.currentThread().getName() + " can do any other stuffs..");
		}
		catch( InterruptedException e )
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public int getResult()
	{
		return result;
	}

}
