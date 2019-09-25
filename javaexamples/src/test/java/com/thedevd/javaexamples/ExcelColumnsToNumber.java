package com.thedevd.javaexamples;

import org.junit.Assert;
import org.junit.Test;

/**
 *
 * Problem :Find ms excel column name using column number Ex A is 1st column, Z is 26th column AA is 27 etc
 *
 */
public class ExcelColumnsToNumber {

	private static String toExcelColumn( int N )
	{
		StringBuilder stringBuilder = new StringBuilder();

		int div = N;
		while( div >= 27 )
		{
			int ch = div % 26;
			div = div / 26;
			if( ch == 0 )
			{
				ch = 26;
				div = div - 1;
			}
			stringBuilder.append((char) (ch + 64));
		}
		stringBuilder.append((char) (div + 64));
		return stringBuilder.reverse().toString();
	}

	@Test
	public void testCase()
	{
		Assert.assertEquals("AA", toExcelColumn(27));
		Assert.assertEquals("A", toExcelColumn(1));
		Assert.assertEquals("Z", toExcelColumn(26));
		Assert.assertEquals("AZ", toExcelColumn(52));
		Assert.assertEquals("AB", toExcelColumn(28));
		Assert.assertEquals("ZA", toExcelColumn(677));
		Assert.assertEquals("AAA", toExcelColumn(703));
		Assert.assertEquals("ZO", toExcelColumn(691));
		Assert.assertEquals("WM", toExcelColumn(611));
		Assert.assertEquals("ZX", toExcelColumn(700));
		Assert.assertEquals("ZY", toExcelColumn(701));
		Assert.assertEquals("YZ", toExcelColumn(676));
		Assert.assertEquals("ZZ", toExcelColumn(702));
		Assert.assertEquals("ABP", toExcelColumn(744));

	}

}
