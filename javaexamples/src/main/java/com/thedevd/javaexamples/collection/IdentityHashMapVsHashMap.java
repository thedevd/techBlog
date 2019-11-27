package com.thedevd.javaexamples.collection;

import java.util.HashMap;
import java.util.IdentityHashMap;

/*
 * IdentityHashMap is not a general purpose map, because it violates general Map contract and that
 * contract mandates the use of equals method when comparing objects.
 * 
 * So in IndentityHashMap, == operator is used to compare the keys and values, where as in HashMap
 * which is a general purpose map uses equals() to compare keys and values.
 * 
 * Means -
 * In IdentityHashMap 
 *     two keys are considered equal  if and only if (key1 == key2)
 *     
 * In HashMap
 *     two keys are considered equal   if and only if (key1==null ? key2==null  : key1.equals(key2))
 *     
 * IdentityHashMap is very rarely used. As per java docs-
 * typical use of this class is to maintain proxy objects. For example , a debugging facility might 
 * wish to maintain a proxy object for each object in the program being debugged.
 */
public class IdentityHashMapVsHashMap {
	
	public static void main( String[] args )
	{
		HashMap<String, Integer> hMap = new HashMap<>();
		IdentityHashMap<String, Integer> identityHMap = new IdentityHashMap<>();
		
		String key1 = new String("A");
		String key2 = new String("A");
		String key3 = new String("B");
		String key4 = key3;
		
		hMap.put(key1, 1);
		hMap.put(key2, 1); // key1.equals(key2) returns true
		hMap.put(key3, 2);
		hMap.put(key4, 2); // key4.equals(key3) returns true
		
		System.out.println(hMap); // --> {A=1, B=2}
		
		identityHMap.put(key1, 1);
		identityHMap.put(key2, 1); // key2 == key1 returns false
		identityHMap.put(key3, 2);
		identityHMap.put(key4, 2); // key4 == key3 returns true
		System.out.println(identityHMap); // --> {A=1, B=2, A=1}
		
		/*
		 * Observation
		 * #################
		 * So in case of HashMap, adding a new key - 'key2' is considered 
		 * same as key1 because thier content are same, and hmap uses equals() method to compare the key and values
		 * 
		 * Where as when we added the new key- 'key2' which is supposed to be same as key1 as per Map contract,
		 * is not treated same as key1 because identityHashMap uses == operator and both key1 and key2 are different
		 * object even though their content is same.
		 */

		
	}

	
}
