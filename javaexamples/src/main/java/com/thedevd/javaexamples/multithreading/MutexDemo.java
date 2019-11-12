package com.thedevd.javaexamples.multithreading;

import java.util.concurrent.Semaphore;

/*
 * Mutex is basically a Semaphore but with only one permit. So at a time only one thread is allowed to access
 * the critical section or shared resource. 
 *      Semaphore semaphore = new Semaphore(1);
 *      
 * Real life example-
 * Consider a situation of using lockers in the bank. Usually the rule is that 
 * only one person is allowed to enter the locker room.
 * 
 * Now you may be wondering that the same thing can also be achieved using ReentrantLock. Yes, mutex can be 
 * replaced with Reentrant lock. Then the question is when to use ReentrantLock and mutex, the answer is-
 * If all you need is reentrant mutual exclusion, then ReentrantLock can be used over Mutex.
 * 
 * In simple word Mutex = Mutually Exclusive Semaphore.
 * 
 * Note- ReentrantLock and synchronized are like mutexes in Java.
 */
public class MutexDemo {

	public static void main( String[] args ) throws InterruptedException
	{

		// Mutex- a semaphore with one permit.
		Semaphore mutex = new Semaphore(1);
		
		new Thread(new MyBankLocker(mutex, 10000), "Customer-1").start();
		new Thread(new MyBankLocker(mutex, 5000), "Customer-2").start();
		
		/*
		 * Output
		 * #########
		 * 
		 * Customer-1: is accessing Locker
		 * Customer-2: waiting as available permit is 0
		 * Customer-1: is going out of locker room
		 * Customer-2: is accessing Locker
		 * Customer-2: is going out of locker room
		 * 
		 * When customer-1 is accessing locker, customer-2 is waiting.
		 * So you can see at one time only on customer is allowed inside LockerRoom.
		 * 
		 */
	}
}

class MyBankLocker implements Runnable {

	private Semaphore mutex;
	private long delayInMs; // this is the time a customer spends inside the lockerRoom

	public MyBankLocker( Semaphore mutex, long delayInMs )
	{
		super();
		this.mutex = mutex;
		this.delayInMs = delayInMs;
	}

	@Override
	public void run()
	{
		try
		{
			if( mutex.availablePermits() == 0 )
			{
				System.out.println(Thread.currentThread().getName() + ": waiting as available permit is 0");
			}

			mutex.acquire();
			
			System.out.println(Thread.currentThread().getName() + ": is accessing Locker");
			Thread.sleep(delayInMs); // time spent by customer in locker room, during this time no one allowed to enter
		}
		catch( InterruptedException e )
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally
		{
			System.out.println(Thread.currentThread().getName() + ": is going out of locker room");
			mutex.release();

		}

	}

}
