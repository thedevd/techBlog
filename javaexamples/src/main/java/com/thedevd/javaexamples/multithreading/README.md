## Reentrant Lock
This is a general purpose locking mechanism provided by java concurrent api to implement thread synhronization over a shared resource. 
```java
ReentrantLock lock = new ReentrantLock(true); // fairness flag is true
public void accessResource() {
  try {
     lock.lock(); // first acquire lock
     // access the shared resource and do operation on it
  }finally{
    lock.unlock(); // release the lock
  }
}
```

Taking above code snippet, only one thread at a time is allowed to acquire the lock and do the operation on shared resource.
Means if one of the thread is already acquired the lock and during this any other thread tries to acquire the the same lock it will go on waiting state. After lock is released by the current thread then one of the waiting thread will be given the lock to do its operation on shared resource. (which thread from the waiting queue is given the lock next, is decided by fairness flag. If the fairness flag is set to true then long waiting thread will be given chance to acquire the lock next.)

Now why this lock is called reentrant, because the same lock can be acquired by same running thread mutliple times (means running thread can reenter to critical section again by acquiring the same lock again and again - this can usually happen in case of recursive call). When the thread has acquired the lock multiple times (i.e. Reentrant), it is must to call unlock() same number of times on the same lock. We can check how many locks a thread hold on a lock by calling getHoldCount() method on the lock object.
```java
ReentrantLock lock = new ReentrantLock(true); // fairness flag is true
public void accessResource() {
  try {
     lock.lock(); // first acquire lock
     System.out.println("tolal hold count: " + lock.getHoldCount());
     if(!someCondition())  {
      accessResource();
     }
     // access the shared resource and do operation on it
  }finally{
    lock.unlock(); // release the lock
  }
}
```

Full source code is available here - [ReentrantLockDemo.java](https://github.com/thedevd/techBlog/blob/master/javaexamples/src/main/java/com/thedevd/javaexamples/multithreading/ReentrantLockDemo.java)

## ReentrantReadWriteLock
ReentrantReadWriteLock is used to improve the performance if the use case it to have frequent read operation and infrequent write operation.

The problem with ReentrantLock is that only one thread at a time is allowed to access the resource, no matter if the operation is read or write. It means if there are 10 threads which only want to read the resource, even in this case only one thread will be allowed at a time. So this is not efficient.

So in such situation we can use ReentrantReadWriteLock to improve the performance, where more than one reader thread can be allowed to read the shared resource simultaneously at the same time. And at a time only one writer thread will be allowed to change the state.

It is to be noted that at a time, either n number of reader threads or one writer thread is allowed to access the resource. But never both at the same time.

In ReentrantReadWriteLock, separate locks are created for read and write operation-
1. ReadLock - is used by reader threads.
2. WriteLock  - is used by writer threads.

```java
ReentrantReadWriteLock readWriteLock = new ReentrantReadWriteLock();
ReentrantReadWriteLock.ReadLock readLock = readWriteLock.readLock();
ReentrantReadWriteLock.WriteLock writeLock = readWriteLock.writeLock();

public void readResource() {
  try {
    readLock.lock(); // more than one threads are allowed to acquire this readLock at the same time.
    // read the shared resource
  }finally{
    readLock.unlock();  
  }
}

public void writeResource() {
  try{
    writeLock.lock(); // only one thread is allowed to hold this lock.
    // update state of shared resource
  }finally{
    writeLock.unlock();
  }
}
```

Full source code is available here - [ReentrantReadWriteLockDemo.java](https://github.com/thedevd/techBlog/blob/master/javaexamples/src/main/java/com/thedevd/javaexamples/multithreading/ReentrantReadWriteLockDemo.java)

## Java BlockingQueue - java.util.concurrent.BlockingQueue
**java.util.concurrent.BlockingQueue Interface** represents a Queue which is thread safe, means multiple threads can put and take elements concurrently from blocking queue without any concurrency issues. 
   * The term Blocking means the Queue is capable to block the threads if Queue is full or empty. For instance, if a thread tries to put an item to the queue and there is no room left in queue (queue is full), then this thread will be blocked until an item is consumed by other thread and makes some room in the queue. Similarly if a thread tries to take an item and there is no item in the queue (Queue is empty) then the thread will be blocked until an item is produced by a thread. 
   * However whether the thread will be blocked is totally depends on what method you call on the Blocking Queue (put()/offer()/add() for inserting item and poll()/peek()/take() for consuming item). 

**BlockingQueue implementations** - Since BlockingQueue is an interface, you need to use one of its implementations. The java.util.concurrent package has the following implementations of the BlockingQueue interface - 
1. ArrayBlockingQueue
   ```java
   BlockingQueue queue = new ArrayBlockingQueue(1024);
   ```
2. LinkedBlockingQueue
   ```java
   BlockingQueue unbounded = new LinkedBlockingQueue();
   BlockingQueue bounded = new LinkedBlockingQueue(1024);
   ```
3. PriorityBlockingQueue
   ```java
   BlockingQueue queue   = new PriorityBlockingQueue();
   ```
4. SynchronousQueue
5. DelayQueue
   ```java
   DelayQueue queue = new DelayQueue();
   ```

These Queues are categerized in these categories - **Bounded, Optionally Bounded and Unbounded**.
   * Bounded Queue means queue has a specific capacity. So you can not add items more than the specified capacity. If you try to put more, the put operation wiill be blocked until another thread takes something out and makes a room in the queue. Example of Bounded Queue is ArrayBlockingQueue. 
      ```java
      BlockingQueue queue = new ArrayBlockingQueue(1024); // must to specify maximum capacity
      ```
   * Unbounded Queue means queue can expand its size as needed. PriorityBlockingQueue is a bounded Queue.
      ```java
      BlockingQueue queue   = new PriorityBlockingQueue(); // no upperbound of the max capacity, Default is Integer.MAX_VALUE
      ```
   * Optionally Bounded Queue means specifying the capacity upfront is optional. So these queue can be made either bounded or unbounded.
   LinkedBlockingQueue is example of optionally bounded queue.
      ```java
      BlockingQueue<String> unbounded = new LinkedBlockingQueue<String>(); // no upperbound of the max capacity, Default is Integer.MAX_VALUE
      BlockingQueue<String> bounded   = new LinkedBlockingQueue<String>(1024); // optinally upper bounded queue.
      ```
      
 **BlockingQueue Methods** - As we discussed earlier that when a thread tries to insert or retrieve an element from the BlockingQueue, whether that thread will be blocked or not is totally depends on what method you call on the Blocking Queue. The java.util.concurrent.BlockingQueue Inteface has 4 sets of methods for inserting, removing and examininig an item of the Queue. Each set of methods behaves differenlty when the requested operation can not be performed. These 4 different set of behaviours are -
1. **Throws Exception** - If attempted opeation is not possible immediately, an exception is thrown. (fail-fast in nature).
2. **Return special value** - If attempted opertaion is not possible immediately, a special value is returned (basically true/false).
3. **Blocks** - If attempted operation is not possible immediately, then method call blocks until some certain condition met.
4. **TimeOut** - If attempted operation is not possible immediately. then method call blocks during the specified time duration only. After time expires, returns special value either true/false or item itself.
 
    |    |Insert|Remove|Examine|
    |----|------|------|-------|
    |**Throws Exception**| add(item) | remove(item) | element() |
    |**Returns Special Value**| offer(item) | poll() | peek() |
    |**Blocks**| **put(item)** | **take()** | |
    |**TimeOut**| offer(item, timeout, timeUnit) | poll(timeout, timeUnit) | |
 
 Therefor, from the above table we can answer these intersting differences -
 1. **offer() vs put()** - offer(item) just try to offer an item to queue and if item can not be inserted due to full queue then it either does not wait or wait for specified time. But put(item) will wait forever until space is available in the queue. So if you can not afford to loose an item, then use put(), otherwise offer().
 2. **peek() vs poll()** - poll() removes an item from the queue and returns the item itself, whereas peek() does not remove it just return the item. So peek() is only used to examine the item without removing it.
 3. **add() vs offer() vs put()** - If item can not be inserted into queue then- add() method will throw exception immediately, offer() will return either true/false and does not wait. And put() will wait forever.
 
 ``Note- It is not possible to insert null into a BlockingQueue. If you try to insert null, the BlockingQueue will throw a NullPointerException.``
      
 **ArrayBlockingQueue vs LinkedBlockingQueue**
1. ArrayBlockingQueue is always bounded, but LinkedBlockingQueue is optinally bounded Queue.
2. ArrayBlockingQueue uses a fixed size array internally. So ArrayBlockingQueue pre-allocates the memory at the time of creation, which will not be good in-terms of memory usage. Also if capacity is given very high then this is going to be problem with non-fragmented memory. Where as LinkedBlocking queue creates the node dynamically.
3. So whevever you are unsure about capacity upfront, then use LinkedBlockingQueue.
