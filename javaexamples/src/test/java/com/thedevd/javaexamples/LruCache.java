package com.thedevd.javaexamples;

import org.junit.Assert;
import org.junit.Test;

import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Least Recently Used (LRU): This cache algorithm keeps recently used items near the top of cache.
 * Whenever a new item is accessed, the LRU places it at the top of the cache.
 * When the cache limit has been reached,
 * items that have been accessed less recently
 * will be removed starting from the bottom of the cache.
 */
public class LruCache {

	Map<Integer, String> dbmap = new Hashtable<>();
	Map<Integer, String> cahceMap = new Hashtable<>();
	List<Integer> recentIndexes = new LinkedList<>();
	int capaciy = 5;

	@Test
	public void testCase()
	{
		LruCache lruCache = new LruCache();
		lruCache.initalizeDbForTest();
		Assert.assertEquals(lruCache.solution(4), "four");
		Assert.assertEquals(1, lruCache.cahceMap.size());
		int intOnTop = lruCache.recentIndexes.get(lruCache.recentIndexes.size() - 1);
		Assert.assertEquals(4, intOnTop);

		Assert.assertEquals(lruCache.solution(4), "four");
		intOnTop = lruCache.recentIndexes.get(lruCache.recentIndexes.size() - 1);
		Assert.assertEquals(4, intOnTop);
		Assert.assertEquals(1, lruCache.cahceMap.size());

		Assert.assertEquals(lruCache.solution(2), "two");
		intOnTop = lruCache.recentIndexes.get(lruCache.recentIndexes.size() - 1);
		Assert.assertEquals(2, intOnTop);
		Assert.assertEquals(2, lruCache.cahceMap.size());

		Assert.assertEquals(lruCache.solution(1), "one");
		Assert.assertEquals(3, lruCache.cahceMap.size());
		intOnTop = lruCache.recentIndexes.get(lruCache.recentIndexes.size() - 1);
		Assert.assertEquals(1, intOnTop);

		Assert.assertEquals(lruCache.solution(4), "four");
		Assert.assertEquals(3, lruCache.cahceMap.size());
		intOnTop = lruCache.recentIndexes.get(lruCache.recentIndexes.size() - 1);
		Assert.assertEquals(4, intOnTop);

		Assert.assertEquals(lruCache.solution(5), "five");
		Assert.assertEquals(4, lruCache.cahceMap.size());
		intOnTop = lruCache.recentIndexes.get(lruCache.recentIndexes.size() - 1);
		Assert.assertEquals(5, intOnTop);

		Assert.assertEquals(lruCache.solution(6), "six");
		Assert.assertEquals(5, lruCache.cahceMap.size());
		intOnTop = lruCache.recentIndexes.get(lruCache.recentIndexes.size() - 1);
		Assert.assertEquals(6, intOnTop);

		Assert.assertEquals(lruCache.solution(0), "zero");
		Assert.assertEquals(5, lruCache.cahceMap.size());
		intOnTop = lruCache.recentIndexes.get(lruCache.recentIndexes.size() - 1);
		Assert.assertEquals(0, intOnTop);

		Assert.assertEquals(lruCache.solution(1), "one");
		Assert.assertEquals(5, lruCache.cahceMap.size());
		intOnTop = lruCache.recentIndexes.get(lruCache.recentIndexes.size() - 1);
		Assert.assertEquals(1, intOnTop);

		Assert.assertEquals(lruCache.solution(4), "four");
		Assert.assertEquals(5, lruCache.cahceMap.size());
		intOnTop = lruCache.recentIndexes.get(lruCache.recentIndexes.size() - 1);
		Assert.assertEquals(4, intOnTop);

		Assert.assertEquals(lruCache.solution(9), "nine");
		Assert.assertEquals(5, lruCache.cahceMap.size());
		intOnTop = lruCache.recentIndexes.get(lruCache.recentIndexes.size() - 1);
		Assert.assertEquals(9, intOnTop);

		Assert.assertEquals(lruCache.solution(3), "three");
		Assert.assertEquals(5, lruCache.cahceMap.size());
		intOnTop = lruCache.recentIndexes.get(lruCache.recentIndexes.size() - 1);
		Assert.assertEquals(3, intOnTop);

		Assert.assertEquals(lruCache.solution(8), "eight");
		Assert.assertEquals(5, lruCache.cahceMap.size());
		intOnTop = lruCache.recentIndexes.get(lruCache.recentIndexes.size() - 1);
		Assert.assertEquals(8, intOnTop);

		Assert.assertEquals(lruCache.solution(7), "seven");
		Assert.assertEquals(5, lruCache.cahceMap.size());
		intOnTop = lruCache.recentIndexes.get(lruCache.recentIndexes.size() - 1);
		Assert.assertEquals(7, intOnTop);

		Assert.assertEquals(lruCache.solution(7), "seven");
		Assert.assertEquals(5, lruCache.cahceMap.size());
		intOnTop = lruCache.recentIndexes.get(lruCache.recentIndexes.size() - 1);
		Assert.assertEquals(7, intOnTop);

	}

	public String solution( Integer index )
	{
		if( cahceMap.get(index) == null )
		{
			if( recentIndexes.size() < capaciy )
			{
				recentIndexes.add(index);

				cahceMap.put(index, dbmap.get(index));

			}
			else
			{
				int id = recentIndexes.get(0);
				recentIndexes.remove(0);
				cahceMap.remove(id);
				recentIndexes.add(index);
				cahceMap.put(index, dbmap.get(index));

			}

		}
		else
		{
			recentIndexes.remove(index);
			recentIndexes.add(index);
		}
		return cahceMap.get(index);
	}

	private void initalizeDbForTest()
	{
		dbmap.put(1, "one");
		dbmap.put(2, "two");
		dbmap.put(3, "three");
		dbmap.put(4, "four");
		dbmap.put(5, "five");
		dbmap.put(6, "six");
		dbmap.put(7, "seven");
		dbmap.put(8, "eight");
		dbmap.put(9, "nine");
		dbmap.put(0, "zero");
	}
}
