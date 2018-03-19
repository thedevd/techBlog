import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

class MyThread extends Thread {

	public void run()
	{
		System.out.println("MyThread task done...... by: " + Thread.currentThread().getName());
	}
}

class MyCallableThread implements Callable<Boolean> {

	boolean isDone = false;

	@Override
	public Boolean call() throws Exception
	{
		System.out.println("MyCallableThread task done......by: " + Thread.currentThread().getName());
		isDone = true;
		return isDone;
	}

}

public class ExecutorServiceAndCallableExample {

	public static void main( String[] args ) throws InterruptedException, ExecutionException
	{
		ExecutorService executorService = Executors.newFixedThreadPool(5);
		MyThread task1 = new MyThread();
		MyThread task2 = new MyThread();
		MyThread task3 = new MyThread();
		MyThread task4 = new MyThread();
		MyThread task5 = new MyThread();
		MyThread task6 = new MyThread();
		MyThread task7 = new MyThread();
		MyThread task8 = new MyThread();
		MyThread task9 = new MyThread();
		MyThread task10 = new MyThread();
		executorService.submit(task1);
		executorService.submit(task2);
		executorService.submit(task3);
		executorService.submit(task4);
		executorService.submit(task5);
		executorService.submit(task6);
		executorService.submit(task7);
		executorService.submit(task8);
		executorService.submit(task9);
		executorService.submit(task10);

		MyCallableThread mct = new MyCallableThread();
		Future<Boolean> futureFromCallable = executorService.submit(mct);
		System.out.println("Return value from callable: " + futureFromCallable.get());
		
		/*output
		MyThread task done...... by: pool-1-thread-1
		MyThread task done...... by: pool-1-thread-2
		MyThread task done...... by: pool-1-thread-2
		MyThread task done...... by: pool-1-thread-1
		MyThread task done...... by: pool-1-thread-2
		MyThread task done...... by: pool-1-thread-3
		MyThread task done...... by: pool-1-thread-3
		MyThread task done...... by: pool-1-thread-1
		MyThread task done...... by: pool-1-thread-4
		MyThread task done...... by: pool-1-thread-5
		MyCallableThread task done......by: pool-1-thread-3
		Return value from callable: true*/


		executorService.shutdown();
	}
}
