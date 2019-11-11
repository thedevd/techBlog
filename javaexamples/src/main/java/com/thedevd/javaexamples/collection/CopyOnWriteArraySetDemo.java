package com.thedevd.javaexamples.collection;

import java.util.Iterator;
import java.util.concurrent.CopyOnWriteArraySet;

/*
 * 1. CopyOnWriteArraySet is concurrent and thread-safe implementation of Set interface,
 * which internally uses CopyOnWriteArrayList. 
 * 
 * 2. Insertion order is preserved and Duplicates are not allowed.
 * 
 * 3. Similar to CopyOnWriteArrayList, if one thread is iterating, other thread can update the collection.
 *    Because each updated is done on fresh cloned copy of underlying collection.
 *    
 * 4. Similar to CopyOnWriteArrayList, Iterator can not perform remove() operation. (Because it causes messed up indexes
 *    in collection, and JVM wont be not able to combine the original collection with all other copies.)
 */
public class CopyOnWriteArraySetDemo {

	public static void main( String[] args )
	{
		CopyOnWriteArraySet<String> cowas = new CopyOnWriteArraySet<>();
		cowas.add("A");
		cowas.add("B");
		cowas.add("C");
		cowas.add("A"); // this is duplicate so won't be added
		System.out.println(cowas); // --> [A, B, C]

		Iterator<String> iterator = cowas.iterator(); // iterator is created
		
		cowas.add("D"); // allowed, no ConcurrentModificationException

		while( iterator.hasNext() )
		{
			String item = iterator.next(); // Can only read from Iterator of CopyOnWriteArraySet, not remove
			System.out.print( item + " "); 
			// iterator.remove(); --> java.lang.UnsupportedOperationException
		} // --> A B C
		
		System.out.println();
		System.out.println(cowas); // [A, B, C, D]
	}
}
