package com.thedevd.javaexamples.algorithms;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/* Problem- ############ You have a list of words {"cat","dog","god"}, 
 * your task is to find the anagrams of each word and return them in one group i.e. 
 *      [ [cat], [dog, god] ] 
 *      since dog and god are anagrams so they are together 
 */
public class FindAnagrams {

	public static void main( String[] args )
	{
		List<String> listOfWords = Arrays.asList("cat", "dog", "god");

		Map<String, List<String>> sortedWordToListOfAnagrams = new HashMap<String, List<String>>();

		for( String word : listOfWords )
		{
			char[] temp = word.toCharArray();
			Arrays.sort(temp);

			String key = new String(temp);
			if( !sortedWordToListOfAnagrams.containsKey(key) )
			{
				sortedWordToListOfAnagrams.put(key, new ArrayList<>());
			}
			sortedWordToListOfAnagrams.get(key).add(word);
		}

		System.out.println(sortedWordToListOfAnagrams.entrySet()); // --> 	[act=[cat], dgo=[dog, god]]
		System.out.println(sortedWordToListOfAnagrams.values()); // --> [[cat], [dog, god]]
	}
}
