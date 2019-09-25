package com.thedevd.javaexamples;

import org.junit.Assert;
import org.junit.Test;

/**
 * Problem : Write a program to check if a number is circular prime
 *
 * A prime number is called circular if it remains prime after any cyclic permutation of its digits.
 */
public class CircularPrimes {

	@Test
	public void testCase(){
		Assert.assertTrue(isCircularPrime(11));
		Assert.assertTrue(isCircularPrime(71));


	}

	public static void main( String[] args )
	{

		circularPrimes(10000);
	}

	private static void circularPrimes( int N )
	{
		while( N >= 1 )
		{
			if( isCircularPrime(N) )
				System.out.println(N);
			N--;
		}

	}

	private static boolean isCircularPrime( int number )
	{
		int n = number;
		int totalDigits = 0;
		while( n != 0 )
		{
			n = n / 10;
			totalDigits++;
		}
		n = number;
		while( isPrime(n) )
		{
			int rem = n % 10;
			int rest = n / 10;
			n = (int) (rem * Math.pow(10,totalDigits -1 )+ rest);

			if( number == n )
			{
				return true;
			}
		}
		return false;

	}

	private static boolean isPrime( int n )
	{
		for( int i = 2; i < (n / 2) + 1; i++ )
		{
			if( n % i == 0 )
				return false;

		}
		return true;
	}

}
