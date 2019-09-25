package com.thedevd.javaexamples;

import javafx.util.Pair;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Problem :Find ms excel column number using column name Ex A is 1st column, Z is 26th column AA is 27 etc
 */
public class NumberToExcelColumns {

	private static int toNumber( String str )
	{
		return recursiveSolution1(str);
	}

	private static int recursiveSolution1( String input )
	{
		if( input.length() == 1 )
		{
			return input.codePointAt(0) - 64;
		}
		int placeFactor = (int) Math.pow(26, (input.length() - 1));
		int value = (input.codePointAt(0) - 64) * placeFactor;
		return value + recursiveSolution1(input.substring(1));
	}
	private static int iterativeSolution( String str )
	{
		char[] cols = str.toCharArray();
		int num = 0;

		for( int i = cols.length; i > 0; i-- )
		{
			int thisChar = cols[i - 1] - 64;
			int placeFactor = (int) Math.pow(26, (cols.length - i));
			int thisPlace = placeFactor * thisChar;
			num = num + thisPlace;
		}

		return num;
	}

	@Test
	public void testCase()
	{
		Assert.assertEquals(1, toNumber("A"));
		Assert.assertEquals(26, toNumber("Z"));
		Assert.assertEquals(27, toNumber("AA"));
		Assert.assertEquals(28, toNumber("AB"));
		Assert.assertEquals(52, toNumber("AZ"));
		Assert.assertEquals(28, toNumber("AB"));
		Assert.assertEquals(677, toNumber("ZA"));
		Assert.assertEquals(703, toNumber("AAA"));
		Assert.assertEquals(691, toNumber("ZO"));
		Assert.assertEquals(611, toNumber("WM"));
		Assert.assertEquals(700, toNumber("ZX"));
		Assert.assertEquals(701, toNumber("ZY"));
		Assert.assertEquals(676, toNumber("YZ"));
		Assert.assertEquals(702, toNumber("ZZ"));
		Assert.assertEquals(744, toNumber("ABP"));

	}



}
