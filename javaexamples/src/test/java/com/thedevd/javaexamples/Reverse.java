package com.thedevd.javaexamples;

import org.junit.Assert;
import org.junit.Test;

/**
 * Reverse an integer without converting to string
 */
public class Reverse {

	@Test
	public void testCase()
	{
		Reverse reverse = new Reverse();
		Assert.assertEquals(4321, reverse.solution(1234));
		Assert.assertEquals(41, reverse.solution(14));
		Assert.assertEquals(1, reverse.solution(1));
		Assert.assertEquals(1, reverse.solution(10000));

	}

	public int solution( int N )
	{
		int reversed = 0;
		while( N > 0 )
		{
			int digit = N % 10;
			reversed = 10 * reversed + digit;
			N = N / 10;
		}
		return reversed;
	}

}
