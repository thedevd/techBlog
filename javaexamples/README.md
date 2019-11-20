## [Basics](https://github.com/thedevd/techBlog/tree/master/javaexamples/src/main/java/com/thedevd/javaexamples/basic)
1. **Static block vs Instance block** - [InstanceBlockAndStaticBlock.java](https://github.com/thedevd/techBlog/blob/master/javaexamples/src/main/java/com/thedevd/javaexamples/basic/InstanceBlockAndStaticBlock.java)
2. **Shallow copy vs Deep copy** - [ShallowAndDeepCloning.java](https://github.com/thedevd/techBlog/blob/master/javaexamples/src/main/java/com/thedevd/javaexamples/basic/ShallowAndDeepCloning.java)

## [Collections](https://github.com/thedevd/techBlog/tree/master/javaexamples/src/main/java/com/thedevd/javaexamples/collection)
1. **hashCode() and equal() role in Hash-based collection** - [HashSetExampleWithEqualsAndHashcode.java](https://github.com/thedevd/techBlog/blob/master/javaexamples/src/main/java/com/thedevd/javaexamples/collection/HashSetExampleWithEqualsAndHashcode.java)
2. **ArrayList vs CopyOnWriteArrayList** - [CopyOnWriteArrayListDemo.java](https://github.com/thedevd/techBlog/blob/master/javaexamples/src/main/java/com/thedevd/javaexamples/collection/CopyOnWriteArrayListDemo.java)
3. **CopyOnWriteArraySet vs CopyOnWriteArrayList** - [CopyOnWriteArraySetDemo.java](https://github.com/thedevd/techBlog/blob/master/javaexamples/src/main/java/com/thedevd/javaexamples/collection/CopyOnWriteArraySetDemo.java)

## [Multithreading and Concurrency](https://github.com/thedevd/techBlog/tree/master/javaexamples/src/main/java/com/thedevd/javaexamples/multithreading)
1. **Callable** (returning value from thread) - [ExecutorServiceAndCallableExample.java](https://github.com/thedevd/techBlog/blob/master/javaexamples/src/main/java/com/thedevd/javaexamples/multithreading/ExecutorServiceAndCallableExample.java)
2. **Locks** -
   * ReentrantLock - [ReentrantLockDemo.java](https://github.com/thedevd/techBlog/blob/master/javaexamples/src/main/java/com/thedevd/javaexamples/multithreading/ReentrantLockDemo.java)
   * ReentrantReadWriteLock - [ReentrantReadWriteLockDemo.java](https://github.com/thedevd/techBlog/blob/master/javaexamples/src/main/java/com/thedevd/javaexamples/multithreading/ReentrantReadWriteLockDemo.java)
3. **Race condition** -
   * Check-the-act - [RaceConditionCheckThenAct.java](https://github.com/thedevd/techBlog/blob/master/javaexamples/src/main/java/com/thedevd/javaexamples/multithreading/RaceConditionCheckThenAct.java)
   * Read-modify-write - [RaceConditionReadModifyWrite.java](https://github.com/thedevd/techBlog/blob/master/javaexamples/src/main/java/com/thedevd/javaexamples/multithreading/RaceConditionReadModifyWrite.java)
4. **Producer-Consumer Pattern problem** -
   * Using Lock and Conditions - [ProducerConsumerWithLockAndCondition.java](https://github.com/thedevd/techBlog/blob/master/javaexamples/src/main/java/com/thedevd/javaexamples/multithreading/ProducerConsumerWithLockAndCondition.java)
   * Using wait() and notify() - [ProducerConsumerWithWaitAndNotify.java](https://github.com/thedevd/techBlog/blob/master/javaexamples/src/main/java/com/thedevd/javaexamples/multithreading/ProducerConsumerWithWaitAndNotify.java)
   * Using LinkedBlockingQueue - [ProducerConsumerUsingBlockingQueue.java](https://github.com/thedevd/techBlog/blob/master/javaexamples/src/main/java/com/thedevd/javaexamples/multithreading/ProducerConsumerUsingBlockingQueue.java)
 5. **Java Concurrency - BlockingQueue**\
 java.util.concurrency.BlockingQueue Interface has following implementations-
    * ArrayBlockingQueue
    * LinkedBlockingQueue - [ProducerConsumerUsingBlockingQueue.java](https://github.com/thedevd/techBlog/blob/master/javaexamples/src/main/java/com/thedevd/javaexamples/multithreading/ProducerConsumerUsingBlockingQueue.java)
    * PriorityBlockingQueue - [PriorityBlockingQueueDemo.java](https://github.com/thedevd/techBlog/blob/master/javaexamples/src/main/java/com/thedevd/javaexamples/multithreading/PriorityBlockingQueueDemo.java)
    * DelayQueue- [DelayQueueDemo.java](https://github.com/thedevd/techBlog/blob/master/javaexamples/src/main/java/com/thedevd/javaexamples/multithreading/DelayQueueDemo.java)
    * SynchronousQueue (direct handoff) - [SynchronousQueueDemo.java](https://github.com/thedevd/techBlog/blob/master/javaexamples/src/main/java/com/thedevd/javaexamples/multithreading/SynchronousQueueDemo.java)
 6. **Java Concurrency – Synchronizers**\
The java.util.concurrent package contains several classes that help set of threads to collaborate with each other. Some of those classes are -
    * CountDownLatch - [CountDownLatchDemo.java](https://github.com/thedevd/techBlog/blob/master/javaexamples/src/main/java/com/thedevd/javaexamples/multithreading/CountDownLatchDemo.java)
    * CyclicBarrier - [CyclicBarrierDemo.java](https://github.com/thedevd/techBlog/blob/master/javaexamples/src/main/java/com/thedevd/javaexamples/multithreading/CyclicBarrierDemo.java),  [CyclicBarrierDemo2.java](https://github.com/thedevd/techBlog/blob/master/javaexamples/src/main/java/com/thedevd/javaexamples/multithreading/CyclicBarrierDemo2.java)
    * Semaphore And Mutex - [SemaphoreDemo.java](https://github.com/thedevd/techBlog/blob/master/javaexamples/src/main/java/com/thedevd/javaexamples/multithreading/SemaphoreDemo.java), [MutexDemo.java](https://github.com/thedevd/techBlog/blob/master/javaexamples/src/main/java/com/thedevd/javaexamples/multithreading/MutexDemo.java)
    * SynchronousQueue (direct handoff) - [SynchronousQueueDemo.java](https://github.com/thedevd/techBlog/blob/master/javaexamples/src/main/java/com/thedevd/javaexamples/multithreading/SynchronousQueueDemo.java)
    * Phaser
    * Exchanger (bidirection data handoff) - [ExchangerDemo.java](https://github.com/thedevd/techBlog/blob/master/javaexamples/src/main/java/com/thedevd/javaexamples/multithreading/ExchangerDemo.java)
    
 ## [Java8 Streams](https://github.com/thedevd/techBlog/tree/master/javaexamples/src/main/java/com/thedevd/javaexamples/streams)
 1. **Sort map by values problem** - [SortMapByValues.java](https://github.com/thedevd/techBlog/blob/master/javaexamples/src/main/java/com/thedevd/javaexamples/streams/SortMapByValues.java)
 
 ## [Coding problems](https://github.com/thedevd/techBlog/tree/master/javaexamples/src/main/java/com/thedevd/javaexamples/algorithms)
 1. Maximum absolute difference in array - [MaxAbsoluteDifferenceInArray.java](https://github.com/thedevd/techBlog/blob/master/javaexamples/src/main/java/com/thedevd/javaexamples/algorithms/MaxAbsoluteDifferenceInArray.java)
 2. Reverse single linked list - [ReverseSingleLinkedList.java](https://github.com/thedevd/techBlog/blob/master/javaexamples/src/main/java/com/thedevd/javaexamples/algorithms/ReverseSingleLinkedList.java)
 3. Print Odd and Even using two threads (Synchronization problem) - [PrintOddEvenUsingTwoThreads.java](https://github.com/thedevd/techBlog/blob/master/javaexamples/src/main/java/com/thedevd/javaexamples/algorithms/PrintOddEvenUsingTwoThreads.java)
