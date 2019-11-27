package com.thedevd.javaexamples.multithreading;


/*
 * In java there are two level where locking is done -
 * 1. Object level
 * 2. Class level
 * 
 * # Object level locking is used when you want to synchronize non-static method/codeblock
 * such that only one thread will be allowed to execute the code block on the given instance of the class.
 * 
 * [See the class MyObjectLockingDemoClass in this demo]
 * For example if in runtime there are 4 threads , out of which first 3 are having the same MyObjectLockingDemoClass 
 * type object and last (4th) one is having separate MyObjectLockingDemoClass object, then as per Object level locking
 * first 3 threads will have to synchronize with each other when executing method1() as they are having same instance, 
 * while 4th thread can execute method1() concurrently because it has separate instance of MyObjectLockingDemoClass.
 * 
 * # Class level locking is used to synchronize static method/codeblock such that it can allow only one thread to enter 
 * the synchronized block, even if thread is operating on different separate instance.
 * 
 * [See the class MyClassLockingDemoClass in this demo]
 * For example if in runtime there are 4 threads, out of which first 3 threads are having the same MyClassLockingDemoClass
 * type object and last (4th) one is having separate MyClassLockingDemoClass object, then as per class level locking
 * even though 4th thread has separate object, it also has to synchronize with first 3 threads when executing method2().
 * Actually here method2() is basically treated like a static method internally.
 */
public class ClassLevelLockVsObjectLevelLock {

	public static void main( String[] args ) throws InterruptedException
	{
		/*
		 * Object level locking demo
		 */
		MyObjectLockingDemoClass myObj1 = new MyObjectLockingDemoClass();
		Thread t1 = new Thread(myObj1, "thread-1");
		Thread t2 = new Thread(myObj1, "thread-2");
		Thread t3 = new Thread(myObj1, "thread-3");
		
		MyObjectLockingDemoClass myObj2 = new MyObjectLockingDemoClass();
		Thread t4 = new Thread(myObj2, "thread-4");
		
		t1.start();t2.start();t3.start();t4.start();
		
		/*
		 * thread-1: I am OBJECT level locked code block
		 * thread-4: I am OBJECT level locked code block
		 * thread-4: Exiting from OBJECT level locked code block
		 * thread-1: Exiting from OBJECT level locked code block
		 * thread-3: I am OBJECT level locked code block
		 * thread-3: Exiting from OBJECT level locked code block
		 * thread-2: I am OBJECT level locked code block
		 * thread-2: Exiting from OBJECT level locked code block
		 * 
		 * Observation-
		 * ############### 
		 * so we can see thread-4 starts executing concurrently
		 * since it has a separate instance to work on. Where as thread-1,
		 * thread-2 and thread-3 have the same instance to work on so they all have 
		 * to synchronize with each other when executing synchronized block.
		 * 
		 */
		
		Thread.sleep(10000);
		
		/*
		 * Class level locking demo
		 */
		MyClassLockingDemoClass myClassObj1 = new MyClassLockingDemoClass();
		Thread t100 = new Thread(myClassObj1, "thread-100");
		Thread t200 = new Thread(myClassObj1, "thread-200");
		Thread t300 = new Thread(myClassObj1, "thread-300");
		
		MyClassLockingDemoClass myClassObj2 = new MyClassLockingDemoClass();
		Thread t400 = new Thread(myClassObj2, "thread-400");
		
		t100.start();t200.start();t300.start();t400.start();
		
		/*
		 * thread-100: #### I am CLASS level locked code block
		 * thread-100: #### Exiting from CLASS level locked code block
		 * thread-400: #### I am CLASS level locked code block
		 * thread-400: #### Exiting from CLASS level locked code block
		 * thread-300: #### I am CLASS level locked code block
		 * thread-300: #### Exiting from CLASS level locked code block
		 * thread-200: #### I am CLASS level locked code block
		 * thread-200: #### Exiting from CLASS level locked code block
		 * 
		 * Observation-
		 * ##############
		 * So we can see even though thread-400 has the separate instance to work on,
		 * it has to synchronize with all other available threads wanting to 
		 * execute the synchronized block. It means in class level lock
		 * only one thread is allowed to execute the critical section no matter of having 
		 * separate instance to work on.
		 */
		
	}
}

class MyObjectLockingDemoClass implements Runnable {

	@Override
	public void run()
	{
		try
		{
			method1();
		}
		catch( InterruptedException e )
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void method1() throws InterruptedException
	{
		synchronized( this ) // this means object level lock
		{
			System.out.println(Thread.currentThread().getName() + ": I am OBJECT level locked code block");
			Thread.sleep(2000);
			System.out.println(Thread.currentThread().getName() + ": Exiting from OBJECT level locked code block");
		}
	}
	
	/*
	 * method1() which is using object level lock can also be written like this - which is very straight-forward
	 * public synchronized method1() throws InterruptedException {
	 *     System.out.println(Thread.currentThread().getName() + ": I am OBJECT level locked code block");
	 *     Thread.sleep(5000);
	 *     System.out.println(Thread.currentThread().getName() + ": Exiting from OBJECT level locked code block");
	 * }
	 */

}

class MyClassLockingDemoClass implements Runnable {

	@Override
	public void run()
	{
		try
		{
			method2();
		}
		catch( InterruptedException e )
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void method2() throws InterruptedException
	{
		synchronized( MyClassLockingDemoClass.class )
		{
			System.out.println(Thread.currentThread().getName() + ": #### I am CLASS level locked code block");
			Thread.sleep(1000);
			System.out.println(Thread.currentThread().getName() + ": #### Exiting from CLASS level locked code block");
		}
	}
	
	/*
	 * method2() which is using Class level lock can also be written like this -
	 * public synchronized static void method2() throws InterruptedException {
	 *      System.out.println(Thread.currentThread().getName() + ": #### I am CLASS level locked code block");
	 *      Thread.sleep(5000);
	 *      System.out.println(Thread.currentThread().getName() + ": #### Exiting from CLASS level locked code block");
	 * }
	 */
}
