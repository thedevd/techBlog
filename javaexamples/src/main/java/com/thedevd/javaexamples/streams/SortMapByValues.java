package com.thedevd.javaexamples.streams;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

/* The problem is - you have an array of words and 
 * you want to store the count of them in descending
 * order of count using streams 
 * Ex - array = {"MP", "MP", "UP", "AP", "KA", "MP", "AP"} 
 * output -
 * ({"MP" -> 3}, {"AP" -> 2}, {"UP" -> 1}, {"KA" -> 1}) 
 * 
 * Solution-
 * 1. First of all we will store the count by words in a normal Map.
 *    This can be done by calling groupingBy on stream with Collectors.counting
 * 2. Once we have map of word->count, then we will sort the Map by value.
 *    This can be done by streaming the map's entry and then Call sorted() to sort
 *    map-entry by its value i.e. Map.Entry.comparingByValue().
 *    
 *    Use reversed() or Collections.reverseOrder() to achieve desc order.
 *    
 * 3. This is very important. Collect the sorted stream of Map's entry using
 * 	  LinkedHashMap to preserve the sorting order so far.
 * 
 * 	  To do this Provide LinkedHashMap::new to the last parameter in Collectors.toMap 
 *    to force it to return a LinkedHashMap,
 * 
 * */
public class SortMapByValues {

	public static void main( String[] args )
	{
		// High level Steps -
		// 1. Count Group by word (word -> count)
		// 2. Sort (word -> count) in reverse order by the value i.e. count
		// 3. Collect the output in a Map using LinkedHashMap to maintain counting the order
		String[] words = { "MP", "MP", "UP", "AP", "KA", "MP", "AP" };

		// 1. Count Group by word (word -> count)
		Map<String, Long> wordToCountMap = Arrays.asList(words)
				.stream()
				.collect(
						Collectors.groupingBy(word -> word, Collectors.counting()));

		// 2. Sort (word -> count) in reverse order by the value.
		// 3. Collect the output in LinkedHashMap to preserve sorting order
		Map<String, Long> sorted = wordToCountMap
				.entrySet()
				.stream()
				//.sorted(Map.Entry.<String, Long> comparingByValue().reversed())
				.sorted(Collections.reverseOrder(Map.Entry.comparingByValue()))
				.collect(
						Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, 
					   	  ( oldValue, newValue ) -> newValue, 
					   	  LinkedHashMap::new));

		
		sorted.entrySet().forEach(System.out::println);

	}
}
