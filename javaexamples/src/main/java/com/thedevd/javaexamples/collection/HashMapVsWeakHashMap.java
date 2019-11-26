package com.thedevd.javaexamples.collection;

import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;

/*
 * HashMaps are hash based implementation which stores data in key-value pair.
 * 
 * If we have used Object as key then -
 * In HashMap, if the key does not have any reference, it will not be eligible for garbage collection until hashMap object 
 * has a reference, where as in WeakHashMap the key will be eligible for garbage collection even-though weakHashMap
 * object has a reference.
 *       MyObject key1 = new MyObject("obj1");
 *       HashMap<MyObject, String> hmap = new HashMap<>();
 *       WeakHashMap<MyObject, String> wmap = new WeakHashMap<>();
 *       
 *       hmap.put(key1, "object-1");
 *       wmap.put(key1, "object-1");
 *       
 *       key1 = null;
 *       System.gc(); // calling garbage collector
 *       
 *       System.out.println(hmap); // Eventhough key1 is pointing to null, the GC will not be able to reclaim the key1 object.
 *       System.out.println(wmap); // will be empty, since in weakHashMap, key uses WeakReference and after making key1 point to null
 *                                 // the key1 pointed object will have only a weakReference and that is eligible of garbage collection.
 * 
 * So we can say if object is used as key, then in WeakHashMap, unused keys will be garbage collected automatically, thus
 * WeakHashMap is good choice for making memory cache with efficient memory usage.
 * 
 * Example- 
 * ############
 * Let's say that we want to build a cache that keeps big image objects as values, and image names as keys.
 * Our goal is to make the cache memory efficient. So need to choose proper Map implementation. 
 * Using a simple HashMap will not be a good choice because even though some keys are not in use (means pointing to null),
 * the garbage collector will not be able to reclaim the memory of that key-value pair. So clearly waste of memory using Simple HashMap.
 * 
 * Ideally what we want, if keys are unused then they should be garbage collected by GC. Means if a key of a big image Object
 * is not in use in the application, then that entry should be deleted from memory. This can be achieved using
 * WeakHashMap implementation as it uses weakReferenced Keys in Entry.
 * 
 *     private static class Entry<K,V> extends WeakReference<Object> implements Map.Entry<K,V> {}
 * 
 */
public class HashMapVsWeakHashMap {

	public static void main( String[] args ) throws InterruptedException
	{

		/*
		 * Simple HashMap, where keys do not have WeakReference
		 */
		Map<MyObject, String> hashMap = new HashMap<>();
		
		MyObject key1 = new MyObject("dev");
		hashMap.put(key1, "vishwakarma");
		System.out.println(hashMap); // --> {MyObject [name=dev]=vishwakarma}
		
		key1=null; // Key has now no Strong Reference
		System.gc(); // Calling GC, GC will not be able to collect object which is pointed by key1
		
		Thread.sleep(5000);
		System.out.println(hashMap); // --> {MyObject [name=dev]=vishwakarma}
		
		/*
		 * WeakHashMap, where Keys are also pointed by WeakReference
		 */
		Map<MyObject, String> weakHashMap = new WeakHashMap<>();
		
		MyObject key101 = new MyObject("ravi");
		weakHashMap.put(key101, "chaudhary");
		System.out.println(weakHashMap); // --> {MyObject [name=ravi]=chaudhary}
		
		key101 = null; // Key has now only the WeakReference, meaning GC will be able to garbage collect it.
		System.gc(); // Calling GC, GC will be able to collect object pointed by WeakReference
		
		Thread.sleep(5000);
		System.out.println(weakHashMap); // --> {}
		
		
	}

	static class MyObject {

		private String name;

		public MyObject( String name )
		{
			super();
			this.name = name;
		}

		@Override
		public String toString()
		{
			return "MyObject [name=" + name + "]";
		}
		
		@Override
		protected void finalize() throws Throwable
		{
			System.out.println("Finalize called for: " + this);
		}
		
	}
}
