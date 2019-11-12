package com.thedevd.javaexamples.multithreading;

import java.util.Random;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

/* 
 * Look at CyclicBarrierDemo.java to know about what is CyclicBarrier.
 * 
 * In this Demo we will look, how barrier can be used repeatedly in cycles.
 * Here we are creating a NeverEndingGame where infinite game levels are there and 
 * multiple players are allowed to join the game
 * 
 * After a player completes a level, it has to wait for all other joined players to finish the same level first,
 * then the player sends a signal to report their individual score for that level.
 * 
 * So here each player has to wait at some common point before reporting their individual score.
 * So this is truely the usecase of CyclicBarrier, where we need to use the same barrier at each level.
 */

public class CyclicBarrierDemo2 {

	public static void main( String[] args )
	{
		// As of now, three players are playing
		CyclicBarrier barrier = new CyclicBarrier(3);

		Thread player1 = new Thread(new NeverEndingGamePlayer("player-1", barrier, 5000)); // party 1
		Thread player2 = new Thread(new NeverEndingGamePlayer("player-2", barrier, 10000)); // party 2
		Thread player3 = new Thread(new NeverEndingGamePlayer("player-3", barrier, 2000)); // party 3

		player1.start();
		player2.start();
		player3.start();

		/*
		 * Sample output
		 * ##################
		 * 
		 * player-2: score at level-1 =40
         * player-3: score at level-1 =19
         * player-1: score at level-1 =769
         * 
         * player-2: score at level-2 =646
         * player-1: score at level-2 =501
         * player-3: score at level-2 =938
         * 
         * player-2: score at level-3 =483
         * player-1: score at level-3 =985
         * player-3: score at level-3 =426
         * 
         * player-2: score at level-4 =357
         * player-1: score at level-4 =372
         * player-3: score at level-4 =334
         * 
         * player-2: score at level-5 =126
         * player-1: score at level-5 =702
         * player-3: score at level-5 =176
         * 
         * player-2: score at level-6 =86
         * player-3: score at level-6 =276
         * player-1: score at level-6 =92
		 */
	}
}

class NeverEndingGamePlayer implements Runnable {

	private String id;
	private CyclicBarrier barrier;
	private long delayInMs;

	public NeverEndingGamePlayer( String id, CyclicBarrier barrier, long delayInMs )
	{
		super();
		this.id = id;
		this.barrier = barrier;
		this.delayInMs = delayInMs;
	}

	@Override
	public void run()
	{
		Random random = new Random();
		int level = 1;
		
		while( true ) // --> infinite cycle
		{
			try
			{
				Thread.sleep(delayInMs); // each player completes the level in different time

				barrier.await();
				// Sending score to each player only after they completes the  same level
				System.out.println(id + ": score at level-" + level + " =" + random.nextInt(1000));
				
				level++;
			}
			catch( InterruptedException | BrokenBarrierException e )
			{
				e.printStackTrace();
			}
		}

	}

}
