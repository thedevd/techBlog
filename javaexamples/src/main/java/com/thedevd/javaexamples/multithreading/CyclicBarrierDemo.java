package com.thedevd.javaexamples.multithreading;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/*
 * CyclicBarrier is a synchronizer that allows set of threads to wait for each other
 * to reach a common execution point (aka barrier) before continuing further execution.
 * 
 * So CyclicBarries is used in the situation where we have a fixed number of 
 * threads (each thread basically process a part of computation) and they must wait for each other to 
 * reach a common point so that final output can be produced by combining each thread's output.
 * 
 * Take an example that we have two threads, one thread's task is to compute sum of two numbers and 
 * other thread's task is to compute product of the same numbers. Now we want to calculate the sum of output
 * of these two threads. It means final output depends on both threads, if one completes first, it has
 * to wait for other thread to complete then only final output is produced.
 * So this situation can be solved by CyclicBarrier.
 * 
 * Working of CyclicBarrier
 * ###################################
 * 1. When we create object of CyclicBarrier, we specify how many threads that the common execution
 * point aka barrier should wait.
 *      CyclicBarrier cyclicBarrier = new CyclicBarrier(numberOfThreads);
 * 
 * 2. Each thread must call await() methods after it completes it task i.e.
 *      public void run() {
 *      	// thread does the job
 *       	cyclicBarrier.await();
 *      }
 * 
 * Once the number of threads that called await() equals numberOfThreads given at the time of creating cyclic barrier object, 
 * we can execute further action.
 * 
 * 3. The CyclicBarrier can also be initialized with some action that need to be performed once all the threads have reached the barrier. 
 * This action can combine/utilize the result of computation of individual thread waiting at the barrier.
 *     Runnable action = ...
 *     //action to be performed when all threads reach the barrier;
 *     CyclicBarrier newBarrier = new CyclicBarrier(numberOfThreads, action);
 *     
 * Important methods of CyclicBarrier
 * #######################################
 * 1. await(): A Thread waits until all parties have invoked await on this barrier, or the specified waiting time elapses.
 * 2. getParties(): Returns the numberOfThreads required to trip this barrier.(This is equal to what we give at time of creating object)
 *        public int getParties()
 * 3. getNumberWaiting(): Returns the number of parties currently waiting at the barrier. 
 *    This method is primarily useful for debugging and assertions.
 *        public int getNumberWaiting()
 * 4. reset(): Resets the barrier to its initial state.
 *        public void reset()
 * 5. isBroken(): Queries if this barrier is in a broken state.
 *        public boolean isBroken()
 *        true if one or more parties broke out of this barrier due to interruption or timeout.
 */
public class CyclicBarrierDemo {

	public static void main( String[] args ) throws InterruptedException, BrokenBarrierException
	{
		CyclicBarrier barrier = new CyclicBarrier(3);

		int a = 10;
		int b = 20;

		// creating worker threads and starting them.
		ComputeSum task1 = new ComputeSum(a, b, barrier);
		ComputeProduct task2 = new ComputeProduct(a, b, barrier);
		Thread t1 = new Thread(task1, "ComputeSum"); // party 1
		Thread t2 = new Thread(task2, "ComputeProduct"); // party 2

		t1.start();
		t2.start();

		System.out.println("Main thread is also waiting at barrier..."); // party 3
		barrier.await(); // main thread is also one of the party so it is also awaiting at barrier.

		// barrier is broken as the number of thread waiting for the barrier at this point = 3
		System.out.println(
				"barrier is broken because no of threads waiting at barrier now:  " + barrier.getNumberWaiting());

		int finalOutput = task1.getResult() + task2.getResult();
		System.out.println("Combined result: " + finalOutput);

		/* overall output ##################
		 * 
		 * Main thread is also waiting at barrier... 
		 * barrier is broken because no of threads waiting at barrier now: 0 
		 * Combined result: 230 
		 * */
	}

}

class ComputeSum implements Runnable {

	private CyclicBarrier barrier;
	private int x;
	private int y;
	private int result = 0;

	public ComputeSum( int x, int y, CyclicBarrier barrier )
	{
		super();
		this.barrier = barrier;
		this.x = x;
		this.y = y;
	}

	@Override
	public void run()
	{
		try
		{
			result = x + y;
			Thread.sleep(3000);

			barrier.await(10, TimeUnit.SECONDS); // after completing, wait at barrier
		}
		catch( InterruptedException | BrokenBarrierException | TimeoutException e )
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

	private CyclicBarrier barrier;
	private int x;
	private int y;
	private int result;

	public ComputeProduct( int x, int y, CyclicBarrier barrier )
	{
		super();
		this.barrier = barrier;
		this.x = x;
		this.y = y;
	}

	@Override
	public void run()
	{
		try
		{
			result = x * y;
			Thread.sleep(5000);

			barrier.await(10, TimeUnit.SECONDS); // after completing, wait at barrier
			
			// Some other task to do, but thread wont be able to continue from this until barrier is broken.
			// So this is another difference b/w CountDownLatch and CyclicBarrier.
		}
		catch( InterruptedException | BrokenBarrierException | TimeoutException e )
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
