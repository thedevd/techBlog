package com.thedevd.javaexamples.multithreading;

import java.util.concurrent.DelayQueue;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

/*
 * java.util.concurrent.BlockingQueue has following implementations-
 * 1. ArrayBlockingQueue
 * 2. LinkedBlockingQueue
 * 3. PriorityBlockingQueue
 * 4. DelayQueue
 * 5. SynchronousQueue
 * 
 * In this example we will explain DelayQueue.
 * 
 * What is DelayQueue and how elements are stored
 * ####################################################
 * 1. DelayQueue is type on unbounded BlockingQueue, where each item has a delayTime.
 * And only those items can be taken out from queue whole delay time expires. 
 * The item which expires first is taken out first, it means elements are ordered based on delayTime.
 * 
 * 2. DelayQueue can accepts only those items that belong to a type Delayed Interface. 
 *    So the item which we want to insert in DelayQueue must implement Delayed Interface. 
 *    In the implementing class, we have to override two methods - getDelay() and compareTo()
 *   1. compareTo() - this method is used to order the elements in the queue based on their delay time. 
 *   The item going to expire first will be first one to be taken out.
 *   2. getDelay()- this method is used to find the remaining delay of an item. And as soon as this return 0 or -ve number,
 *      that item becomes eligible to be taken out.
 *      
 * 3. So this queue is used in scenario where you want to deliver the message to consumers only after
 * a specific time.
 * 
 */
public class DelayQueueDemo {
	
	public static void main( String[] args ) throws InterruptedException
	{
		// create DelayQueue to store MyDelayObject of type Delayed
		DelayQueue<MyDelayObject> delayQueue = new DelayQueue<>(); // unbounded queue
		
		// putting 3 elements in the queue with delayInMs
		delayQueue.put(new MyDelayObject("item1", 10000)); // will expire after 10 second
		delayQueue.put(new MyDelayObject("item2", 5000)); // item with least delay, so expire very first
		delayQueue.put(new MyDelayObject("item3", 15000)); // will expire at very last
		
		// try to consume item using take(), take() will wait for some time until an item expires
		System.out.println(delayQueue.take());
		System.out.println(delayQueue.take());
		System.out.println(delayQueue.take());
		
		/*
		 * Output
		 * ############
		 * 
		 * MyDelayObject [data=item2, expiryTime=1573654554722]
		 * MyDelayObject [data=item1, expiryTime=1573654559722]
		 * MyDelayObject [data=item3, expiryTime=1573654564722]
		 * 
		 * So you can see item2 has least delayTime so it will expire first and so will be taken out first,
		 * then item1 and then item3. This clearly shows that items will be ordered based on delayTime.
		 */
	}

}

// Items being inserted in DelayQueue must implement Delayed Interface.
class MyDelayObject implements Delayed {
	
	private String data;
	private long expiryTime;
	
	public MyDelayObject( String data, long delayInMs )
	{
		super();
		this.data = data;
		this.expiryTime = System.currentTimeMillis() + delayInMs; // totalDelay in MilliS
	}

	@Override
	public int compareTo( Delayed o )
	{
		MyDelayObject other = (MyDelayObject)o;
		return new Long(this.expiryTime).compareTo(other.expiryTime);
		/*
		 *  if (this.expiryTime < ((MyDelayObject)o).expiryTime) {
		 *   return -1; 
		 *  } 
		 *  if (this.expiryTime > ((MyDelayObject)o).expiryTime) { 
		 *   return 1; 
		 *  } 
		 * return 0; 
		 */
	}

	@Override
	public long getDelay( TimeUnit unit )
	{
		long diff = expiryTime - System.currentTimeMillis(); // calculating remaining delay
		return unit.convert(diff, TimeUnit.MILLISECONDS); // returning remaining delay in the proper TimeUnit
	}

	@Override
	public String toString()
	{
		return "MyDelayObject [data=" + data + ", expiryTime=" + expiryTime + "]";
	}
	
}
