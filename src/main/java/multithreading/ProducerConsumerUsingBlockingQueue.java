package default;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ProducerConsumerUsingBlockingQueue {

	public static void main( String args[] )
	{

		//Creating shared object
		BlockingQueue<Integer> sharedQueue = new LinkedBlockingQueue<Integer>();

		//Creating Producer and Consumer Thread
		Thread prodThread = new Thread(new BQProducer(sharedQueue));
		Thread consThread = new Thread(new BQConsumer(sharedQueue));

		//Starting producer and Consumer thread
		prodThread.start();
		consThread.start();
	}

}

//Producer Class in java
class BQProducer implements Runnable {

	private final BlockingQueue<Integer> sharedQueue;

	public BQProducer( BlockingQueue<Integer> sharedQueue )
	{
		this.sharedQueue = sharedQueue;
	}

	@Override
	public void run()
	{
		int i=1;
		while(true)
		{
			try
			{
				sharedQueue.put(i);
				System.out.println("Produced: " + i);
				i++;
				Thread.sleep(1000);
			}
			catch( InterruptedException ex )
			{
				Logger.getLogger(Producer.class.getName()).log(Level.SEVERE, null, ex);
			}
		}
	}

}

//Consumer Class in Java
class BQConsumer implements Runnable {

	private final BlockingQueue<Integer> sharedQueue;

	public BQConsumer( BlockingQueue<Integer> sharedQueue )
	{
		this.sharedQueue = sharedQueue;
	}

	@Override
	public void run()
	{
		while( true )
		{
			try
			{
				System.out.println("Consumed: " + sharedQueue.take());
				Thread.sleep(1000);
			}
			catch( InterruptedException ex )
			{
				Logger.getLogger(Consumer.class.getName()).log(Level.SEVERE, null, ex);
			}
		}
	}

}
