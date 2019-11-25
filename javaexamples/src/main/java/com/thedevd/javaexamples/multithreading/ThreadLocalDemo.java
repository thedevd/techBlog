package com.thedevd.javaexamples.multithreading;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

/*
 * In concurrent environment, critical aspect is to maintain the consistency/integrity of the shared data,
 * means if one thread change the value of shared data, then this change should be visible to all other
 * threads before they do further operations on it. Most of the time we want this behavior to make sure
 * thread should use updated value of the shared data. But sometimes we want to ensure that every
 * thread MUST work on its own copy and does not affect others. ThreadLocal is for that only.
 * 
 * As the name indicates, ThreadLocal is a way to create variable that will be local to a particular thread only.
 * So even if two threads are executing the same code and the code has a reference to that variable, then each 
 * thread will basically work with its own variable, and other thread wont be able to see that. 
 * 
 * Usecases where ThreadLocal is widely used-
 * ##########################################
 * 1. A very common use case, where developers use ThreadLocal is in the webapplication.
 * Consider you are working in eCommerce application, and you have a servlet or controller that 
 * received the request and generate unique transaction id to that request. Further the controller
 * calls list of business or DAO methods where transaction id is needed might be for logging purpose.
 * So one solution could be passing the transaction id to each method, But this does not seem to 
 * be a good solution as the code is going to be redundant. 
 * 
 * Solution of this could be using ThreadLocal class. So what we can do after generating unique
 * transaction id of the request in the controller, we can set the transaction id in the ThreadLocal.
 * After this whatever methods is called by the controller to serve that request, those methods can
 * access that transaction id (specific to particular request or thread) from ThreadLocal instance directly.
 * 
 * Assumption is here that - Since each request is processed in separate thread, 
 * the transaction id will be unique and local to corresponding thread (local) and will be accessible in the entire
 * the thread life cycle of its execution. (This whole use case can be considered as creating a RequestScope or
 * RequestContextHandler in terms of spring)
 * 
 * 2. ThreadLocal is sometime used with SimpleDateFormat. DateFormat implementation is not thread safe. 
 * Meaning that in the case, if two threads simultaneously use the DataFormat's format() method, then you 
 * may get very strange result or even exception. THis is what I am taking about ->
 * 
 *       private DateFormat df = new SimpleDateFormat("MM/dd/yy");
 *       
 *       public String formatCurrentDate() {
 *       	return df.format(new Date()); // It is not safe to use from different threads at the same time!	
 *       }
 *       
 *       public String formatFirstOfJanyary1970() {
 *       	return df.format(new Date(0)); // It is not safe to use from different threads at the same time!	
 *       }
 *       
 * To avoid such problem, we can wrap the SimpleDateFormat in a ThreadLocal, so that there will be a 
 * separate instance of SimpleDateFormat for each thread:
 * 
 * public class Something {
 *     public static final ThreadLocal<DateFormat> DATE_FORMAT = new ThreadLocal<DateFormat>() {
 *         @Override
 *         public DateFormat initialValue() {
 *             return new SimpleDateFormat("MM/dd/yy");
 *         }
 *     };
 * }
 * 
 * A very beautiful story of this usecase is available here - https://plumbr.io/blog/java/when-and-how-to-use-a-threadlocal.
 * 
 * How to create ThreadLocal and its method
 * ##########################################
 * ThreadLocal<T> threadId = new ThreadLocal<T>()
 * 
 * This class has following methods:
 * 1. get() : Returns the value for this thread-local variable.
 * 2. initialValue() : Returns the current thread’s “initial value” for this thread-local variable.
 * 3. remove() : Removes the current thread’s value for this thread-local variable.
 * 4. set(T value) : Sets the specified value to current thread’s local variable.
 * 
 * Watch a good video resource of ThreadLocal from DeFog tech - https://youtu.be/sjMe9aecW_A
 */
public class ThreadLocalDemo {

	public static void main( String[] args ) throws InterruptedException
	{
		
		new Thread(new TransactionService(), "thread-1").start();
		Thread.sleep(2000);
		new Thread(new TransactionService(), "thread-2").start();
		Thread.sleep(3000);
		new Thread(new TransactionService(), "thread-3").start();
		
		/*
		 * Output-
		 * #########
		 * thread-1: txnId=292198
		 * thread-1: startDate= 11/25/2019, date pattern= MM/dd/yyyy
		 * Changed SimpleDateFormat pattern to yyyy/MM/dd for thread-1
		 * thread-2: txnId=203489
		 * thread-2: startDate= 11/25/2019, date pattern= MM/dd/yyyy
		 * Changed SimpleDateFormat pattern to yyyy/MM/dd for thread-2
		 * thread-3: txnId=836795
		 * thread-3: startDate= 11/25/2019, date pattern= MM/dd/yyyy
		 * Changed SimpleDateFormat pattern to yyyy/MM/dd for thread-3
		 * After some time ... thread-1: txnId=292198
		 * After some time ... thread-1: date pattern= yyyy/MM/dd
		 * After some time ... thread-2: txnId=203489
		 * After some time ... thread-2: date pattern= yyyy/MM/dd
		 * After some time ... thread-3: txnId=836795
		 * After some time ... thread-3: date pattern= yyyy/MM/dd
		 * 
		 * 
		 * Observation-
		 * ################
		 * So you can see even if you changed SimpleDateFormat pattern to yyyy/MM/dd for thread-1,
		 * it does not affect the same in thread-2 and thread-3 because each thread is now working
		 * on their own local copy of threadLocal variable.
		 */
		
	}
}

class PerThreadLocalVariables {
	/*public static ThreadLocal<SimpleDateFormat> threadSafeFormatter = new ThreadLocal<SimpleDateFormat>() {
		@Override
        protected SimpleDateFormat initialValue()
        {
            return new SimpleDateFormat("MM/dd/yyyy");
        }
	};
	*/
	public static final ThreadLocal<SimpleDateFormat> THREAD_LOCAL_FORMATTER = ThreadLocal
			.withInitial(() -> new SimpleDateFormat("MM/dd/yyyy"));
	
	private static Random random = new Random();
	public static final ThreadLocal<Integer> THREAD_LOCAL_TXN_ID = ThreadLocal.withInitial(() -> random.nextInt(1000000));

}

class TransactionService implements Runnable {

	@Override
	public void run()
	{
		// get() method is used to read ThreadLocal variable.
		System.out.println(
				Thread.currentThread().getName() + ": txnId=" + PerThreadLocalVariables.THREAD_LOCAL_TXN_ID.get());
		System.out.println(Thread.currentThread().getName() + ": startDate= "
				+ PerThreadLocalVariables.THREAD_LOCAL_FORMATTER.get().format(new Date()) + ", date pattern= "
				+ PerThreadLocalVariables.THREAD_LOCAL_FORMATTER.get().toPattern());

		changeFormatterToYYYYMMDD(); // this will not affect other's simpleDateFormat pattern
		System.out.println("Changed SimpleDateFormat pattern to yyyy/MM/dd for " + Thread.currentThread().getName());

		try
		{
			Thread.sleep(5000);
			System.out.println("After some time ... " + Thread.currentThread().getName() + ": txnId="
					+ PerThreadLocalVariables.THREAD_LOCAL_TXN_ID.get());
			System.out.println("After some time ... " + Thread.currentThread().getName() + ": date pattern= "
					+ PerThreadLocalVariables.THREAD_LOCAL_FORMATTER.get().toPattern());

		}
		catch( InterruptedException e )
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private void changeFormatterToYYYYMMDD()
	{
		// set() method is used to set a value to current thread's threadLocal variable.
		// Changing the thread local variable will not affect variable of other thread.
		PerThreadLocalVariables.THREAD_LOCAL_FORMATTER.set(new SimpleDateFormat("yyyy/MM/dd"));
	}

}
