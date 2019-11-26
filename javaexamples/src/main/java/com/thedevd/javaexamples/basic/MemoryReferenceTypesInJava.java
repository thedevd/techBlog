package com.thedevd.javaexamples.basic;

import java.lang.ref.PhantomReference;
import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/*
 * There are 4 kind of memory references in Java-
 * 1. Strong reference
 * 2. WeakReference
 * 3. SoftReference
 * 4. PhantomReference
 * 
 * How GC is going to work on the objects is dependent on type of these references. Means
 * which objects are eligible for garbage collection is dependent on which type of 
 * references are pointing to that object.
 * 
 * 1. Strong reference -
 * ########################
 * This is default, simple and straightforward reference as we you this in our day to day programming i.e
 *    String ename = new String("devendra");
 *    Myclass obj = new MyClass();
 * 
 * Strong reference means any object which has atleast one strong reference will not be eligible for GC.
 * The strongly referenced object is eligible for garbage collected only when reference variable starts pointing to null.
 *    MyClass obj = new MyClass(); // at this point object is strongly referenced
 *    obj = null; // now the object is eligible for GC.
 *    
 * 2. and 3. WeakReference and SoftReference-
 * #####################################
 * To create weak reference you have to explicitly use java.lang.ref.WeakReference, i.e.
 *    MyClass obj = new MyClass(); // strong reference - line 1
 *    WeakReference<MyClass> weakRefObj = new WeakReference<MyClass>(obj) // weak reference to object obj
 *    obj = null; // now MyClass object pointed by obj is eligible for GC.
 *    
 * So as soon as strong reference starts point to null, then the MyClass object created at line 1 will 
 * be marked eligible for GC and memory will be reclaimed in next GC cycle. But if it was a SoftReference 
 * then although the object will be marked for garbage collection but it would be reclaimed only when JVM is 
 * about to run out of memory. 
 *     MyClass obj2 = new MyClass(); // strong reference - line 2
 *     SoftReference<MyClass> softRefObj = new SoftReference<MyClass>(obj2); // softreference to object obj2
 *     obj2 = null; //now MyClass object obj2 is eligible for garbage collection but only be collected when JVM absolutely needs memory
 * 
 * So these special behavior of SoftReference and WeakReference makes them useful in certain cases e.g. 
 * SoftReference looks perfect for implementing caches, where we want to store object for longer time and 
 * want to remove them only when JVM need memory badly. If we keep strong references in the cache, then they will never be collected
 * even if main reference to cache object points to null, hence it would be wasting memory.
 * 
 * Where as WeakReference is great for storing meta data of class, meaning if actual class in unloaded, then there 
 * is not point of keeping its meta data. It should be garbage collected.
 * 
 * Java has a collection called WeahHashMap, which uses WeakReference for keys, Means if a object is used as key 
 * and that object has no reference anymore, then that object will be eligible for GC even-though there is a reference 
 * to main WeakHashMap object. See HashMapVsWeakHashMap.java
 * 
 * 4. PhantomReference - 
 * ######################
 * This is least used reference, which is similar to weak reference, i.e any object referenced by
 * only a phantom reference is eligible for garbage collection, but difference is that calling get()
 * method on phatomReference always return null where as calling the same on weakReference returns the referent.
 * 
 * Important to note that we need ReferenceQueue to work with PhantomReference. 
 * The Garbage Collector adds a phantom reference to a reference queue after the its referent starts pointing to null (means
 * referent is finalized).
 *      ReferenceQueue refQueue = new ReferenceQueue();
 *      MyClass obj3 = new MyClass();
 *      PhantomReference<MyClass> phantomRef = new PhantomReference<MyClass>(obj3, refQueue);
 *      
 *      you can use ReferenceQueue as to keep track of list of objects garbage collected and perform any clean-up by polling ReferenceQueue.
 * 
 * Common Usecase of PhantomReference is to use them as alternative of finalize() method which is expensive and even we 
 * do not know whey finalize() will be called by GC.
 * 
 * Summary --
 * #############
 * Strong reference - If an object has strong reference, then the object can never be eligible for GC and
 * 					  reclaimed by garbage Collector.
 * 
 * Weak reference - If an object has only weak reference, then the object will be garbage collected and reclaimed
 *                  eagerly in next GC cycle. So GC will not wait for running-out-of-memory situation to come first.
 *                  
 * Soft reference - If an object has only soft reference, then the object wont be Garbage collected until
 *                  the JVM absolutely needs memory. So GC will wait for running-out-of-memory situation to come.
 *                  
 * Phantom reference - If an object has only Phantom reference, then object is directly eligible for GC.
 *                     It is used to keep track of objects being garbage collected and do some cleanup.
 */
public class MemoryReferenceTypesInJava {

	public static void main( String[] args )
	{
		// Integer i = new Integer(100);
		Integer x = 100; // Strong reference

		Integer y = 200;
		WeakReference<Integer> weakRef = new WeakReference<Integer>(y);
		y = null;
		// when we make strong reference to null, then object pointed by weakRef will be garbage collected in next GC cycle.
		System.out.println(weakRef.get()); // --> 200
		// calling get() on weakRef returns reference object's referent or returns null If this reference object has been cleared by GC

		Integer z = 300;
		SoftReference<Integer> softRef = new SoftReference<Integer>(z);
		z = null; // when we make strong reference to null, then object pointed by softRef will be eligible for GC but 
					// will be collected only when JVM absolutely needs memory
		System.out.println(softRef.get()); // --> 300
		// calling get() on softRef returns reference object's referent or returns null If this reference object has been cleared by GC

		/* PhantomReference Demo */
		ReferenceQueue<Employee> refQueue = new ReferenceQueue<>();
		List<EmployeeFinalizer> phantomReferences = new ArrayList<>();
		List<Employee> largeObjects = new ArrayList<>(); //Strong Reference 

		for( int i = 1; i <= 10; i++ )
		{
			Employee e = new Employee(i);
			largeObjects.add(e);
			phantomReferences.add(new EmployeeFinalizer(e, refQueue));
		}

		// making strong reference to null, at this point each object has now only a phantom reference
		// so they are all eligible for garbage collection now. But they are kept in refQueue before reclaimed by GC.
		largeObjects = null;
		System.gc();

		// since the referent of a phantom reference is always inaccessible, get() on phantom ref always returns null.
		System.out.println(phantomReferences.get(0).get()); // --> null

		Reference<?> referenceFromRefQueue;
		while( (referenceFromRefQueue = refQueue.poll()) != null )
		{
			// see here we have now full control to finalize the objects.
			((EmployeeFinalizer) referenceFromRefQueue).cleanup();
		}
	}

	static class Employee {

		private int id;

		public Employee( int id )
		{
			super();
			this.id = id;
		}
	}

	static class EmployeeFinalizer extends PhantomReference<Employee> {

		public EmployeeFinalizer( Employee referent, ReferenceQueue<? super Employee> q )
		{
			super(referent, q);
		}

		public void cleanup()
		{
			// free resources
			System.out.println("Finilizing person resource");
		}
	}

}

