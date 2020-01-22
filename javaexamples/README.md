## [Basics](https://github.com/thedevd/techBlog/tree/master/javaexamples/src/main/java/com/thedevd/javaexamples/basic)
1. **Static block vs Instance block** - [InstanceBlockAndStaticBlock.java](https://github.com/thedevd/techBlog/blob/master/javaexamples/src/main/java/com/thedevd/javaexamples/basic/InstanceBlockAndStaticBlock.java)
2. **Shallow copy vs Deep copy** - [ShallowAndDeepCloning.java](https://github.com/thedevd/techBlog/blob/master/javaexamples/src/main/java/com/thedevd/javaexamples/basic/ShallowAndDeepCloning.java)
3. **Strong reference/WeakReference/SoftReference/PhantomReference** - [MemoryReferenceTypesInJava.java](https://github.com/thedevd/techBlog/blob/master/javaexamples/src/main/java/com/thedevd/javaexamples/basic/MemoryReferenceTypesInJava.java) 

## [Collections](https://github.com/thedevd/techBlog/tree/master/javaexamples/src/main/java/com/thedevd/javaexamples/collection)
1. **hashCode() and equal() role in Hash-based collection** - [HashSetExampleWithEqualsAndHashcode.java](https://github.com/thedevd/techBlog/blob/master/javaexamples/src/main/java/com/thedevd/javaexamples/collection/HashSetExampleWithEqualsAndHashcode.java)
2. **ArrayList vs CopyOnWriteArrayList** - [CopyOnWriteArrayListDemo.java](https://github.com/thedevd/techBlog/blob/master/javaexamples/src/main/java/com/thedevd/javaexamples/collection/CopyOnWriteArrayListDemo.java)
3. **CopyOnWriteArraySet vs CopyOnWriteArrayList** - [CopyOnWriteArraySetDemo.java](https://github.com/thedevd/techBlog/blob/master/javaexamples/src/main/java/com/thedevd/javaexamples/collection/CopyOnWriteArraySetDemo.java)
4. **WeakHashMap vs HashMap** - [HashMapVsWeakHashMap.java](https://github.com/thedevd/techBlog/blob/master/javaexamples/src/main/java/com/thedevd/javaexamples/collection/HashMapVsWeakHashMap.java)
5. **IdentityHashMap vs HashMap** - [IdentityHashMapVsHashMap.java](https://github.com/thedevd/techBlog/blob/master/javaexamples/src/main/java/com/thedevd/javaexamples/collection/IdentityHashMapVsHashMap.java)

## [Multithreading and Concurrency](https://github.com/thedevd/techBlog/tree/master/javaexamples/src/main/java/com/thedevd/javaexamples/multithreading)
* **Callable** (returning value from thread) - [ExecutorServiceAndCallableExample.java](https://github.com/thedevd/techBlog/blob/master/javaexamples/src/main/java/com/thedevd/javaexamples/multithreading/ExecutorServiceAndCallableExample.java)
* **Locking level (Class level and Object level)** - [ClassLevelLockVsObjectLevelLock.java](https://github.com/thedevd/techBlog/blob/master/javaexamples/src/main/java/com/thedevd/javaexamples/multithreading/ClassLevelLockVsObjectLevelLock.java)
* **Locks** -
   * ReentrantLock - [ReentrantLockDemo.java](https://github.com/thedevd/techBlog/blob/master/javaexamples/src/main/java/com/thedevd/javaexamples/multithreading/ReentrantLockDemo.java)
   * ReentrantReadWriteLock - [ReentrantReadWriteLockDemo.java](https://github.com/thedevd/techBlog/blob/master/javaexamples/src/main/java/com/thedevd/javaexamples/multithreading/ReentrantReadWriteLockDemo.java)
* **Race condition** -
   * Check-the-act - [RaceConditionCheckThenAct.java](https://github.com/thedevd/techBlog/blob/master/javaexamples/src/main/java/com/thedevd/javaexamples/multithreading/RaceConditionCheckThenAct.java)
   * Read-modify-write - [RaceConditionReadModifyWrite.java](https://github.com/thedevd/techBlog/blob/master/javaexamples/src/main/java/com/thedevd/javaexamples/multithreading/RaceConditionReadModifyWrite.java)
* **Producer-Consumer Pattern problem** -
   * Using Lock and Conditions - [ProducerConsumerWithLockAndCondition.java](https://github.com/thedevd/techBlog/blob/master/javaexamples/src/main/java/com/thedevd/javaexamples/multithreading/ProducerConsumerWithLockAndCondition.java)
   * Using wait() and notify() - [ProducerConsumerWithWaitAndNotify.java](https://github.com/thedevd/techBlog/blob/master/javaexamples/src/main/java/com/thedevd/javaexamples/multithreading/ProducerConsumerWithWaitAndNotify.java)
   * Using LinkedBlockingQueue - [ProducerConsumerUsingBlockingQueue.java](https://github.com/thedevd/techBlog/blob/master/javaexamples/src/main/java/com/thedevd/javaexamples/multithreading/ProducerConsumerUsingBlockingQueue.java)
 * **Java Concurrency - BlockingQueue**\
 java.util.concurrency.BlockingQueue Interface has following implementations-
    * ArrayBlockingQueue
    * LinkedBlockingQueue - [ProducerConsumerUsingBlockingQueue.java](https://github.com/thedevd/techBlog/blob/master/javaexamples/src/main/java/com/thedevd/javaexamples/multithreading/ProducerConsumerUsingBlockingQueue.java)
    * PriorityBlockingQueue - [PriorityBlockingQueueDemo.java](https://github.com/thedevd/techBlog/blob/master/javaexamples/src/main/java/com/thedevd/javaexamples/multithreading/PriorityBlockingQueueDemo.java)
    * DelayQueue- [DelayQueueDemo.java](https://github.com/thedevd/techBlog/blob/master/javaexamples/src/main/java/com/thedevd/javaexamples/multithreading/DelayQueueDemo.java)
    * SynchronousQueue (direct handoff) - [SynchronousQueueDemo.java](https://github.com/thedevd/techBlog/blob/master/javaexamples/src/main/java/com/thedevd/javaexamples/multithreading/SynchronousQueueDemo.java)
 * **Java Concurrency – Synchronizers**\
The java.util.concurrent package contains several classes that help set of threads to collaborate with each other. Some of those classes are -
    * CountDownLatch - [CountDownLatchDemo.java](https://github.com/thedevd/techBlog/blob/master/javaexamples/src/main/java/com/thedevd/javaexamples/multithreading/CountDownLatchDemo.java)
    * CyclicBarrier - [CyclicBarrierDemo.java](https://github.com/thedevd/techBlog/blob/master/javaexamples/src/main/java/com/thedevd/javaexamples/multithreading/CyclicBarrierDemo.java),  [CyclicBarrierDemo2.java](https://github.com/thedevd/techBlog/blob/master/javaexamples/src/main/java/com/thedevd/javaexamples/multithreading/CyclicBarrierDemo2.java)
    * Semaphore And Mutex - [SemaphoreDemo.java](https://github.com/thedevd/techBlog/blob/master/javaexamples/src/main/java/com/thedevd/javaexamples/multithreading/SemaphoreDemo.java), [MutexDemo.java](https://github.com/thedevd/techBlog/blob/master/javaexamples/src/main/java/com/thedevd/javaexamples/multithreading/MutexDemo.java)
    * SynchronousQueue (direct handoff) - [SynchronousQueueDemo.java](https://github.com/thedevd/techBlog/blob/master/javaexamples/src/main/java/com/thedevd/javaexamples/multithreading/SynchronousQueueDemo.java)
    * Phaser
    * Exchanger (bidirection data handoff) - [ExchangerDemo.java](https://github.com/thedevd/techBlog/blob/master/javaexamples/src/main/java/com/thedevd/javaexamples/multithreading/ExchangerDemo.java)
* **java.lang.ThreadLocal** - [ThreadLocalDemo.java](https://github.com/thedevd/techBlog/blob/master/javaexamples/src/main/java/com/thedevd/javaexamples/multithreading/ThreadLocalDemo.java)
    
 ## [Java8 Streams](https://github.com/thedevd/techBlog/tree/master/javaexamples/src/main/java/com/thedevd/javaexamples/streams)
 1. **Sort map by values problem** - [SortMapByValues.java](https://github.com/thedevd/techBlog/blob/master/javaexamples/src/main/java/com/thedevd/javaexamples/streams/SortMapByValues.java)
 2. **Find max or min from collection of object** - [Java8FindingMaxMin.java](https://github.com/thedevd/techBlog/blob/master/javaexamples/src/main/java/com/thedevd/javaexamples/streams/Java8FindingMaxMin.java)
 3. **Find max/min/second max/max salary by deptName of employee Problem** - [EmployeeSalaryProblem.java](https://github.com/thedevd/techBlog/blob/master/javaexamples/src/main/java/com/thedevd/javaexamples/streams/EmployeeSalaryProblem.java)
 
 ## [Design Patters using Java](https://github.com/thedevd/techBlog/tree/master/javaexamples/src/main/java/com/thedevd/javaexamples/designpatterns)
 1. [Creational](https://github.com/thedevd/techBlog/tree/master/javaexamples/src/main/java/com/thedevd/javaexamples/designpatterns/creational)
     * Factory Pattern - [FactoryPattern.java](https://github.com/thedevd/techBlog/blob/master/javaexamples/src/main/java/com/thedevd/javaexamples/designpatterns/creational/FactoryPattern.java), [FactoryPatternExp2.java](https://github.com/thedevd/techBlog/blob/master/javaexamples/src/main/java/com/thedevd/javaexamples/designpatterns/creational/FactoryPatternExp2.java)
     * Abstract Factory Pattern - [AbstractFactoryPattern.java](https://github.com/thedevd/techBlog/blob/master/javaexamples/src/main/java/com/thedevd/javaexamples/designpatterns/creational/AbstractFactoryPattern.java)
     * Builder Pattern - [BuilderPattern.java](https://github.com/thedevd/techBlog/blob/master/javaexamples/src/main/java/com/thedevd/javaexamples/designpatterns/creational/BuilderPattern.java)
     * Prototype Pattern - [PrototypePattern.java](https://github.com/thedevd/techBlog/blob/master/javaexamples/src/main/java/com/thedevd/javaexamples/designpatterns/creational/PrototypePattern.java)
     * Singleton Pattern - [SingletonPattern.java](https://github.com/thedevd/techBlog/blob/master/javaexamples/src/main/java/com/thedevd/javaexamples/designpatterns/creational/SingletonPattern.java)
2. [Structural](https://github.com/thedevd/techBlog/tree/master/javaexamples/src/main/java/com/thedevd/javaexamples/designpatterns/structural)
    * Adaptor pattern - [AdaptorPattern.java](https://github.com/thedevd/techBlog/blob/master/javaexamples/src/main/java/com/thedevd/javaexamples/designpatterns/structural/AdaptorPattern.java)
    * Brigde Pattern - [BridgePattern.java](https://github.com/thedevd/techBlog/blob/master/javaexamples/src/main/java/com/thedevd/javaexamples/designpatterns/structural/BridgePattern.java)
    * Decorator Pattern - [DecoratorPattern.java](https://github.com/thedevd/techBlog/blob/master/javaexamples/src/main/java/com/thedevd/javaexamples/designpatterns/structural/DecoratorPattern.java)
 
 ## [Coding problems](https://github.com/thedevd/techBlog/tree/master/javaexamples/src/main/java/com/thedevd/javaexamples/algorithms)
 1. Maximum absolute difference in array - [MaxAbsoluteDifferenceInArray.java](https://github.com/thedevd/techBlog/blob/master/javaexamples/src/main/java/com/thedevd/javaexamples/algorithms/MaxAbsoluteDifferenceInArray.java)
 2. Reverse single linked list - [ReverseSingleLinkedList.java](https://github.com/thedevd/techBlog/blob/master/javaexamples/src/main/java/com/thedevd/javaexamples/algorithms/ReverseSingleLinkedList.java)
 3. Print Odd and Even using two threads (Synchronization problem) - [PrintOddEvenUsingTwoThreads.java](https://github.com/thedevd/techBlog/blob/master/javaexamples/src/main/java/com/thedevd/javaexamples/algorithms/PrintOddEvenUsingTwoThreads.java)
