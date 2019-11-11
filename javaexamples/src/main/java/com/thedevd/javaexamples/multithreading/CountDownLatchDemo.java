package com.thedevd.javaexamples.multithreading;

import java.util.concurrent.CountDownLatch;

/* Think, you are developing Chess game which requires at-least two players to join to get
 * start, So you want to make sure that the main game task should wait for two players to join before it
 * starts. This is the situation where countDownLatch can be used.
 * 
 * So CountDownLatch is used to make sure that the main task should wait for specific number of other threads to complete,
 * then only the  main task will start.
 * 
 * Working of CountDownLatch 
 * ########################## 
 * When we create object of countDownLatch, we specify a number of threads that task should wait for. 
 * All those threads are required to down the count by one by calling CountDownLatch.countDown() once they are completed. 
 * As soon as the count becomes zero, the waiting task starts running.
 * 
 *    CountDownLatch latch = new CountDownLatch(2); 
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
			}
			catch( InterruptedException e )
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
	}

}
