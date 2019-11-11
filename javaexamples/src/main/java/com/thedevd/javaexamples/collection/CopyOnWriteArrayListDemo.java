package com.thedevd.javaexamples.collection;

import java.util.ArrayList;
import java.util.Iterator;
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
		/*
		 * ######### ArrayList ######### 
		 */
		ArrayList<Integer> arrayList = new ArrayList<>();
		arrayList.add(1);
		arrayList.add(2);
		
		Iterator<Integer> iteratorOnArrayList = arrayList.iterator();
		
		/* After the Iterator is created on ArrayList, you can not modify the list using add or remove.
		 * Doing so will throw ConcurrentModificationException -
		 * 
		 * arrayList.add(3);
		 * arrayList.remove(0);
		 * 
		 * But update to an existing item is allowed using set method, because this does not
		 * change size of the arrayList
		 * 
		 * An elements from ArrayList can be removed using remove() method of Iterator, but 
		 * same is not possible using remove() of ArrayList collection itself.
		 */
		
		arrayList.set(1,4); 
		
		while(iteratorOnArrayList.hasNext()) {
			Integer item = iteratorOnArrayList.next();
			System.out.println(item);
			// arrayList.add(11); --> throws ConcurrentModificationException 
			// arrayList.remove(0) --> throws ConcurrentModificationException 
			// list.set(1, 4); --> Set is allowed as it does not change size of ArrayList (modCount variable)
			if (item % 2 == 0 ) {
				iteratorOnArrayList.remove(); // remove on Iterator is possible with ArrayList
			}
		}
		
		System.out.println("arrayList after iterator.remove():"+ arrayList);
		// arrayList after iterator.remove():[1]
		
		/*
		 * #########  CopyOnWriteArrayList ######### 
		 */
		CopyOnWriteArrayList<Integer> copyOnWriteAL = new CopyOnWriteArrayList<>();
		copyOnWriteAL.add(1);
		copyOnWriteAL.add(2);
		copyOnWriteAL.addIfAbsent(1); // addIfAbsent only adds item if it does not exist
		
		Iterator<Integer>  iteratorOnCOWAL = copyOnWriteAL.iterator(); // iterator on original list
		
		/*
		 * After iterator is created on original arrayList, doing add, remove operation on list
		 * is done on separate copy.
		 *  
		 */
		copyOnWriteAL.add(3); // allowed
		copyOnWriteAL.remove(0); // allowed
		while( iteratorOnCOWAL.hasNext())
		{
			Integer item = iteratorOnCOWAL.next();
			System.out.println(item); // 1,2 --> not showing items which were added after iterator is created, because items were added in copy
			// iteratorOnCOWAL.remove(); --> throws java.lang.UnsupportedOperationException
		}
		 
		System.out.println("copyOnWriteAL after modifications is:"+ copyOnWriteAL);
		// copyOnWriteAL after modifications is:[2, 3]]
	}
}
