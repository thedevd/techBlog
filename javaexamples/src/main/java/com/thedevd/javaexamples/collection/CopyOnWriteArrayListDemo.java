package com.thedevd.javaexamples.collection;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.IntStream;

/*
 * CopyOnWriteArraList is a concurrent and thread-safe implementation of List interface.
 * It was added in Java 1.5.
 * 
 * 1. The word CopyOnWrite means, every update operations (add, remove ,set) on underlying arraylist
 * is done on separate fresh cloned copy. This makes it thread-safe as thread which are reading the 
 * collection do not get affected by modification operation.
 * After some certain point the original list and copies are synch automatically by the JVM.
 * 
 * 2. In the case if there are too many write operations are needed, then CopyOnWriteArrayList
 * becomes very poor in performance because for every updated operation a cloned copy is created.
 * Hence CopyOnWriteArrayList is only good choice if write operations are very less.
 * 
 * 
 * It differs from ArrayList on these areas -
 * 1. ArraList is not thread-safe so can not be used in multi-threaded environment,
 *    where as CopyOnWriteArrayList is thread-safe collection.
 * 2. Iterator on ArralyList is fail-fast means doing modification (except set operation) directly on ArrayList 
 *    after iterator is created will throw ConcurrentModificationException, where as Iterator on CopyOnWriteArrayList
 *    is fail-safe, since modification is done on fresh copy of original list. (see point 3 for Gotchas)
 *    
 *    Lets see why exception is thrown on ArrayList when size is changed- 
 *    you will notice that ConcurrentModificationException is thrown by Iterator next() method. If you will look 
 *    into the ArrayList source code, following method is called every time we invoke next() on iterator -
 *    
 *    final void checkForComodification() {
 *        if (modCount != expectedModCount)
 *           throw new ConcurrentModificationException();
 *     }
 *     
 *     Here modCount is the ArrayList variable that holds the modification count and every time we use add, remove method, 
 *     it increments. 
 *     expectedModCount is the iterator variable that is initialized when we create iterator with same value as modCount. 
 *     This explains why we don’t get exception if we use set method to replace any existing element (because set method
 *     does not change modCount value)
 *    
 * 3. The another important point about CopyOnWriteArrayList is that Iterator of CopyOnWriteArrayList 
 *    can not perform remove operation otherwise we get Run-time exception saying UnsupportedOperationException.
 *    
 *    Where as remove() operation on ArrayList's iterator is allowed.
 *    
 */
public class CopyOnWriteArrayListDemo {

	public static void main( String[] args )
	{
		List<Integer> list = new ArrayList<>();
		IntStream.rangeClosed(1, 10).forEach(n -> list.add(n));
		
		Iterator<Integer> iteratorOnAL = list.iterator();
		while(iteratorOnAL.hasNext()) {
			Integer item = iteratorOnAL.next();
			System.out.println(item);
			// list.add(11); --> throws ConcurrentModificationException 
			// list.remove(1) --> throws ConcurrentModificationException 
			list.set(1, 20); // Set is allowed as it does not change size of ArrayList (modCount variable)
			if (item % 2 == 0 ) {
				iteratorOnAL.remove(); // remove on Iterator is possible with ArrayList
			}
		}
		
		System.out.println("list after operations:"+ list);
		// list after operations:[1, 20, 5, 7, 9]
		
		List<Integer> copyOnWriteAL = new CopyOnWriteArrayList<>();
		IntStream.rangeClosed(1, 10).forEach(n -> copyOnWriteAL.add(n));
		
		Iterator<Integer>  iteratorOnCOWAL = copyOnWriteAL.iterator();
		while( iteratorOnCOWAL.hasNext())
		{
			Integer item = iteratorOnCOWAL.next();
			System.out.println(item);
			copyOnWriteAL.add(11); // allowed , no exception
			// iteratorOnCOWAL.remove(); --> throws java.lang.UnsupportedOperationException
		}
		 
		System.out.println("copyOnWriteAL after modifications is:"+ copyOnWriteAL);
		// copyOnWriteAL after modifications is:[1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11]
	}
}
