package com.thedevd.javaexamples.multithreading;

import java.util.concurrent.atomic.AtomicInteger;

/*
 * What is race condition-
 * #########################
 * It is one of the problem that arise in multi-threaded environment where
 * multiple threads access shared variables and you get the wrong result due to thread interleaving,
 * means the value of the variable depends on execution order of the threads.
 * 
 * Two types of race condition=
 * 1. Check-then-act. (boolean condition)
 * 2. Read-modify-write. (counters)
 * 
 * Race condition can be solved by allowing only thread to change the status
 * of shared variable and this can be done either using synchronization technique (synchronized block
 * or use of Lock --> these are to solve check-then-act) or using Atomic Variable (in case when 
 * shared variable is counter) 
 */
public class RaceConditionReadModifyWrite {
	
	public static void main( String[] args )
	{
	
		BadVotingCounter votingCounter = new BadVotingCounter();
		
		Thread t1 = new Thread(new BadVotingMachine(votingCounter), "t1");
		Thread t2 = new Thread(new BadVotingMachine(votingCounter), "t2");
		Thread t3 = new Thread(new BadVotingMachine(votingCounter), "t3");
		Thread t4 = new Thread(new BadVotingMachine(votingCounter), "t4");
		Thread t5 = new Thread(new BadVotingMachine(votingCounter), "t5");
		
		t1.start();
		t2.start();
		t3.start();
		t4.start();
		t5.start();
		
		/*
		 * Sometimes you may get wrong voting count when using BadVotingMachine -
		 * 
		 * %%%%% total votes now: 2
		 * %%%%% total votes now: 2
		 * %%%%% total votes now: 3
		 * %%%%% total votes now: 4
		 * %%%%% total votes now: 4
		 * 
		 * This should have 5.
		 * 
		 * The reason is, increment operation is basically three operations -
		 * read, modify and then update. So if increment is not done atomically
		 * then it may lead wrong result at the end when multiple thread access the same 
		 * counter variable. 
		 */
		
		
		GoodVotingCounter goodVotingCounter = new GoodVotingCounter();
		Thread t11 = new Thread(new GoodVotingMachine(goodVotingCounter), "t11");
		Thread t12 = new Thread(new GoodVotingMachine(goodVotingCounter), "t12");
		Thread t13 = new Thread(new GoodVotingMachine(goodVotingCounter), "t13");
		Thread t14 = new Thread(new GoodVotingMachine(goodVotingCounter), "t14");
		Thread t15 = new Thread(new GoodVotingMachine(goodVotingCounter), "t15");
		
		t11.start();
		t12.start();
		t13.start();
		t14.start();
		t15.start();
		
		/*
		 * Using GoodVotingMachine always guarantee the right voting counts
		 * As we have used AtomicInteger, so AtomicInteger will always perform the 
		 * read-modify-update operations atomically. 
		 * 
		 * ##### total votes now: 1
		 * ##### total votes now: 2
		 * ##### total votes now: 4
		 * ##### total votes now: 3
		 * ##### total votes now: 5
		 */
	}

}

/*
 * This may result wrong votingCount as increment is not done atomically
 */
class BadVotingCounter {
	private long votingCount = 0;
	
	public void increateCount() {
		votingCount = votingCount + 1;
	}

	
	public long getVotingCount()
	{
		return votingCount;
	}
	
}

class BadVotingMachine implements Runnable {

	// Shared variable
	private BadVotingCounter votingCounter;
	
	public BadVotingMachine( BadVotingCounter votingCounter )
	{
		super();
		this.votingCounter = votingCounter;
	}

	@Override
	public void run()
	{
		votingCounter.increateCount();
		System.out.println("%%%%% total votes now: " + votingCounter.getVotingCount());
	}
	
}

/*
 * Using AtomicInteger to ensure increment operation is done atomically by a thread.
 */
class GoodVotingCounter {
	private AtomicInteger votingCount = new AtomicInteger(0);
	
	public void increateCount() {
		votingCount.incrementAndGet();
	}
	
	public long getVotingCount()
	{
		return votingCount.get();
	}
}

class GoodVotingMachine implements Runnable {

	// Shared variable
	private GoodVotingCounter votingCounter;
	
	public GoodVotingMachine( GoodVotingCounter votingCounter )
	{
		super();
		this.votingCounter = votingCounter;
	}

	@Override
	public void run()
	{
		votingCounter.increateCount();
		System.out.println("##### total votes now: " + votingCounter.getVotingCount());
	}
	
}
