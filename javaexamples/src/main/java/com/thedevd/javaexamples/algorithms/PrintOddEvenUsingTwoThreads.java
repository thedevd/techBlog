package com.thedevd.javaexamples.algorithms;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class PrintOddEvenUsingTwoThreads {

	public static void main( String[] args )
	{
		Printer printer = new Printer();
		Thread odd = new Thread(new OddThread(printer,100), "Odd");
		Thread even = new Thread(new EvenThread(printer, 100), "Even");

		odd.start();
		even.start();
	}
}

class EvenThread implements Runnable {

	private Printer printer;
	private int limit;

	public EvenThread( Printer printer, int limit )
	{
		super();
		this.printer = printer;
		this.limit = limit;
	}

	@Override
	public void run()
	{
		int i = 1;
		while( i <= limit )
		{
			try
			{
				printer.printEvenNumber(i);
			}
			catch( InterruptedException e )
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			i++;
		}

	}

}

class OddThread implements Runnable {

	private Printer printer;
	private int limit;

	public OddThread( Printer printer, int limit )
	{
		super();
		this.printer = printer;
		this.limit = limit;
	}

	@Override
	public void run()
	{
		int i = 1;
		while( i <= limit )
		{
			try
			{
				printer.printOddNumber(i);
			}
			catch( InterruptedException e )
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			i++;
		}

	}

}

class Printer {

	Lock lock = new ReentrantLock();
	Condition isEvenNeeded = lock.newCondition();
	Condition isOddNeeded = lock.newCondition();

	int currentNum;

	public void printOddNumber( int i ) throws InterruptedException
	{
		lock.lock();
		try
		{
			if( currentNum == i )
			{
				// If number already printed by other thread do nothing, go to next number
			}
			else if( i % 2 == 0 )
			{
				//System.out.println("isOddNeeded.await()");
				isOddNeeded.await();
			}
			else
			{
				currentNum = i;
				System.out.println(Thread.currentThread().getName() + ": " + i);
				isEvenNeeded.signal();
			}
		}
		finally
		{
			lock.unlock();
		}

	}

	public void printEvenNumber( int i ) throws InterruptedException
	{
		lock.lock();
		try
		{
			if( currentNum == i )
			{
				// If number already printed by other thread do nothing, go to next number
			}
			else if( i % 2 != 0 )
			{
				//System.out.println("isEvenNeeded.await()");
				isEvenNeeded.await();
			}
			else
			{
				currentNum = i;
				System.out.println(Thread.currentThread().getName() + ": " + i);
				isOddNeeded.signal();
			}
		}
		finally
		{
			lock.unlock();
		}

	}
}
