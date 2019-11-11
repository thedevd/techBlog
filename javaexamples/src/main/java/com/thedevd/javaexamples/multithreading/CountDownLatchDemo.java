package com.thedevd.javaexamples.multithreading;

import java.util.concurrent.CountDownLatch;

/* Think, you are developing Chess game which requires at-least two players to join to get
 * start, So you want to make sure that the main game task should wait for two players to join before it
 * starts. This is the situation where countDownLatch can be used.
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
		// Two players are needed to join to start the game
		CountDownLatch latch = new CountDownLatch(2);
		
		// lets create two players and start them
		Thread player1 = new Thread(new ChessPlayer(latch, "pid1"));
		Thread player2 = new Thread(new ChessPlayer(latch, "pid2"));
		player1.start();
		player2.start();
		
		// let the main task wait for above two players to join. Call await() on latch
		System.out.println("Main task is waiting....");
		latch.await(); // Main task is waiting
		
		// Main task has started only after both players have joined (ie. count of latch is zero)
		System.out.println("Starting main game...");
		
		/*
		 * Overall output- 
		 * ########################
		 * 
		 * Main task is waiting....
		 * Player: pid2 is getting ready...
		 * Player: pid1 is getting ready...
		 * Player: pid2 has joined.
		 * Player: pid1 has joined.
		 * Starting main game...
		 * 
		 */
		
	}
}

class ChessPlayer implements Runnable {

	private CountDownLatch latch;
	private String playerId;
	
	public ChessPlayer( CountDownLatch latch, String playerId )
	{
		super();
		this.latch = latch;
		this.playerId = playerId;
	}


	@Override
	public void run()
	{
			try
			{
				System.out.println("Player: " + playerId + " is getting ready...");
				// work to do
				Thread.sleep(3000);
				
				System.out.println("Player: " + playerId + " has joined.");
				latch.countDown(); // down the count by each player
				
				// Some other task, the player can continue
			}
			catch( InterruptedException e )
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
	}

}
