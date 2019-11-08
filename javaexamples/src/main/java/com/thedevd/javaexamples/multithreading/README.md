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