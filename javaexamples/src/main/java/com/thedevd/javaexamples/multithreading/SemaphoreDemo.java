package com.thedevd.javaexamples.multithreading;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

/*
 * Semaphore is another type of synchronizer that allows only fixed no of threads to - access the resource 
 * or perform an operation.
 * 
 * This is generally useful in the case 
 *  - when doing operation on a resource is very slow and at a time we do not want to put overload on
 *    that resource, so we allow only fixed no of threads to access the resource at a time.
 *    For ex- There is an operation or a webservice call, which is very very slow due to some IO operation,
 *    so to prevent large no of threads (say 100) to call that service/operation at the same time, we want to allow
 *    only few threads (say 5) to access the service at a time.
 *    
 *  - when we have limited no of resources and we want to allow fixed no of threads to access that set of limited resources.
 *    For ex- In a bank, three ATM machines are available in ATM room. So only 3 persons are allowed to go inside the ATM
 *    room, and withdraw money. When another person comes and all the ATM machines are acquired, then that
 *    person has to wait. As soon as the person inside the ATM room withdraws the money and comes out, then a person
 *    waiting outside of ATM room is allowed (as one ATM machine has become free to use).
 *    
 *    
 * Working of Semaphore
 * #######################
 * At the time of creating semaphore object, we specify the number representing maximum no of threads
 * that are allowed to enter the critical section.  This number is known as Permits or pass,
 *      Semaphore semaphore = new Semaphore(noOfPermits);
 *      
 * In order to access a shared resource or to enter the critical section, 
 * Current Thread must acquire a permit. If all permits is already acquired means no permit is available 
 * then the thread will wait until one of the thread (acquiring the permit) releases the permit. 
 * And as soon as a permit is released, it is available to be acquired by one of the waiting thread.
 * 
 * To acquire and release the permit, Semaphore has two main methods acquire() and release(). Two more variant are available - 
 *  1. acquire() - Acquires a permit, if one is available and reduces the number of available permits by one.
 *  2. acquire(int permits) - Acquires the given number of permits, if they are available and reduces the number of available 
 *     permits by the given amount. 
 *  3. release() - Releases the permit, returning them to the semaphore and increments permits number by one.
 *  4. release(int permits)
 *  
 * We have also nonblocking method of acquire called tryAquire(long ms, TimeUnit)- means wait for permit for specified time
 * if no permit is available during that time then  do not wait and do other stuffs.
 * 
 *  5. availablePermits() --> return available permits/pass at a time.
 * 
 * Note - There is no requirement that a thread that releases a permit has to be the one that it had acquired by calling acquire().
 * 
 */
public class SemaphoreDemo {

	public static void main( String[] args ) throws InterruptedException
	{
		// Creating a Semaphore with the given number of permits and the given fairness setting.
		Semaphore semaphore = new Semaphore(3, true);
		
		ExecutorService executor = Executors.newFixedThreadPool(10);
		
		executor.submit(new MyBigTask("MyBigTask-1", semaphore));
		Thread.sleep(1000);
		executor.submit(new MyBigTask("MyBigTask-2", semaphore));
		Thread.sleep(1000);
		executor.submit(new MyBigTask("MyBigTask-3", semaphore));
		Thread.sleep(1000);
		executor.submit(new MyBigTask("MyBigTask-4", semaphore));
		Thread.sleep(1000);
		executor.submit(new MyBigTask("MyBigTask-5", semaphore));
		
		executor.shutdown();
		
		/*
		 * Output
		 * #################
		 * 
		 * MyBigTask-1: Doing Operation 1
		 * MyBigTask-2: Doing Operation 1
		 * MyBigTask-1: Doing Operation 2
		 * MyBigTask-3: Doing Operation 1
		 * MyBigTask-2: Doing Operation 2
		 * MyBigTask-1: Permits available before acquiring= 3
		 * MyBigTask-1: Doing Operation 3 which is very slow...
		 * MyBigTask-3: Doing Operation 2
		 * MyBigTask-4: Doing Operation 1
		 * MyBigTask-2: Permits available before acquiring= 2
		 * MyBigTask-2: Doing Operation 3 which is very slow...
		 * MyBigTask-4: Doing Operation 2
		 * MyBigTask-3: Permits available before acquiring= 1
		 * MyBigTask-3: Doing Operation 3 which is very slow...
		 * MyBigTask-5: Doing Operation 1
		 * MyBigTask-5: Doing Operation 2
		 * MyBigTask-4: Permits available before acquiring= 0
		 * MyBigTask-4: $$$$$ is waiting because no permits are available
		 * MyBigTask-5: Permits available before acquiring= 0
		 * MyBigTask-5: $$$$$ is waiting because no permits are available
		 * MyBigTask-1: ##### released semaphore
		 * MyBigTask-4: Doing Operation 3 which is very slow...
		 * MyBigTask-2: ##### released semaphore
		 * MyBigTask-5: Doing Operation 3 which is very slow...
		 * MyBigTask-3: ##### released semaphore
		 * MyBigTask-4: ##### released semaphore
		 * MyBigTask-5: ##### released semaphore
		 * 
		 * 
		 * Explanation- As you can see MyBigTask-1, MyBigTask-2, MyBigTask-3 acquired the permits one
		 * by one. So at this point available permits are now 0, so when MyBigTask-4 and MyBigTask-5 comes
		 * they see available permits are zero so they go on waiting. As soon as permit is released by
		 * MyBigTask-1 and MyBigTask-2, then MyBigTask-5 and MyBigTask-4 resumes the execution.
		 */

	}
}

class MyBigTask implements Runnable {

	private String taskName;
	private Semaphore semaphore;

	public MyBigTask( String taskName, Semaphore semaphore )
	{
		super();
		this.taskName = taskName;
		this.semaphore = semaphore;
	}

	@Override
	public void run()
	{
		try
		{
			// Doing Operation 1
			System.out.println(taskName + ": Doing Operation 1");
			Thread.sleep(1000);

			// Doing Operation 2
			System.out.println(taskName + ": Doing Operation 2");
			Thread.sleep(1000);
			
			
			int permitsAvailableAtThisPoint = semaphore.availablePermits();
			System.out.println(taskName + ": Permits available before acquiring= " + permitsAvailableAtThisPoint);
			if(permitsAvailableAtThisPoint == 0) {
				System.out.println(taskName+ ": $$$$$ is waiting because no permits are available");
			}
			
			// Doing Operation 3 which is very very slow, 
			// so here we want to allow only fixed no of threads to access this at a time
			
			/* boolean isAcquired = semaphore.tryAcquire(10, TimeUnit.SECONDS); 
			 * if(isAcquired) { 
			 *    //Do slow operation 
			 *  } else { 
			 *    // do other work 
			 *  } 
			 */
			semaphore.acquire(); //--> acquire a permit if available otherwise go on waiting
			// call the slow operation or the service after acquiring the permit
			slowOperation();

		}
		catch( InterruptedException e )
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally
		{
			semaphore.release(); // Releases the given number of permits, returning them to the semaphore. 
			System.out.println(taskName + ": ##### released semaphore");
		}

	}
	
	public void slowOperation() throws InterruptedException 
	{
		System.out.println(taskName + ": Doing Operation 3 which is very slow...");
		Thread.sleep(5000);
	}

}
