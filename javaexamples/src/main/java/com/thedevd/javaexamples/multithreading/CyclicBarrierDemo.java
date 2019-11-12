package com.thedevd.javaexamples.multithreading;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

/* 
 * CyclicBarrier is a synchronizer that allows set of threads to wait for each other
 * to reach a common execution point (aka barrier) before continuing further execution.
 * 
 * So CyclicBarries is used in the situation where we have a fixed number of 
 * threads and they must wait for each other to reach a common point. After required no 
 * of thread reach the barrier, then they all can proceed further.
 * 
 * Real time example  - 
 * Think of, you are developing Online game which requires at-least two players to join to start the game
 * So here each player has to wait for its opponent to join the game first, then only game will start.
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
 * Once the number of threads that called await() equals numberOfThreads which was given at the time of creating cyclic barrier object, 
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
 *        
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
 */

public class CyclicBarrierDemo {

	public static void main( String[] args )
	{
		CyclicBarrier barrier = new CyclicBarrier(3);

		Thread player1 = new Thread(new GamePlayer("earth007", barrier, 5000)); // party 1
		Thread player2 = new Thread(new GamePlayer("mars007", barrier, 10000)); // party 2

		player1.start();
		player2.start();

		try
		{
			System.out.println("Main: Main thread is also waiting at barrier..."); // party 3
			barrier.await(); // main thread is also one of the party so it is also awaiting at barrier.
		}
		catch( InterruptedException | BrokenBarrierException e )
		{
			e.printStackTrace();
		}

		// barrier is broken as the number of thread waiting for the barrier at this point = 0
		System.out.println(
				"Main: barrier is broken because no of threads waiting at barrier now:  " + barrier.getNumberWaiting());
		System.out.println("Main: Both Players have joined... starting the game 3, 2, 1..... GO!");
		
		/*
		 * Overall Output -
		 * ##################
		 * Main: Main thread is also waiting at barrier...
		 * 
		 * mars007: Welcome, Please wait we are connecting you to the online game portal..
		 * earth007: Welcome, Please wait we are connecting you to the online game portal..
		 * 
		 * earth007: You are Connected successfully... waiting for an opponent to join...
		 * mars007: You are Connected successfully... waiting for an opponent to join...
		 * mars007: Found an opponent, Lets play
		 * earth007: Found an opponent, Lets play
		 * 
		 * Main: barrier is broken because no of threads waiting at barrier now:  0
		 * Main: Both Players have joined... starting the game 3, 2, 1..... GO!
		 */
	}
}

class GamePlayer implements Runnable {

	private String id;
	private CyclicBarrier barrier;
	private long delayInMs;

	public GamePlayer( String id, CyclicBarrier barrier, long delayInMs )
	{
		super();
		this.id = id;
		this.barrier = barrier;
		this.delayInMs = delayInMs;
	}

	@Override
	public void run()
	{
		try
		{
			// Steps to Initialize player
			System.out.println(id + ": Welcome, Please wait we are connecting you to the online game portal..");
			Thread.sleep(delayInMs); // doing required resource initialization

			System.out.println(id + ": You are Connected successfully... waiting for an opponent to join...");
			barrier.await(); // player waiting for an opponent to join the game. (Waiting at barrier) 

			// Some other tasks to do, but thread wont be able to continue from this until barrier is broken.
			// So this is another difference b/w CountDownLatch and CyclicBarrier.
			System.out.println(id + ": Found an opponent, Lets play");
		}
		catch( InterruptedException | BrokenBarrierException e )
		{
			e.printStackTrace();
		}

	}

}
