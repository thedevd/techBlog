package com.thedevd.javaexamples.algorithms;

// @formatter:off
// Problem
/*
Given an array arr[] of N integers, 
the task is to find the maximum absolute difference between any two elements of the array.

	Examples:

	Input: arr[] = {2, 1, 5, 3}
	Output: 4
	|5 – 1| = 4
	
	Input: arr[] = {-10, 4, -9, -5}
	Output: 14
	|4 - (-10)| = 14
	 */
// @formatter:on

public class MaxAbsoluteDifferenceInArray {

	public static void main( String[] args )
	{
		int[] array = { 2, 1, 5, 3 };
		System.out.println(maxAbsDiff(array));

		int[] array1 = { -10, 4, -9, -5 };
		System.out.println(maxAbsDiff(array1));
	}

	static int maxAbsDiff( int[] array )
	{
		// Max diff in array is always difference of maximum no and minimum no
		// so first we find max and min in array and return their diff as maxAbsDiff

		int minNumber = array[0];
		int maxNumber = array[0];
		int arraySize = array.length;
		
		for( int i = 1; i < arraySize; i++ )
		{
			minNumber = Math.min(minNumber, array[i]);
			maxNumber = Math.max(maxNumber, array[i]);
		}

		return maxNumber - minNumber;
	}
}
