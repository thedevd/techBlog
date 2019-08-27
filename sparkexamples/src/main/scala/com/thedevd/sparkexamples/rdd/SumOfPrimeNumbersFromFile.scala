package com.thedevd.sparkexamples.rdd

import org.apache.log4j.Level
import org.apache.log4j.Logger
import org.apache.spark.sql.SparkSession

/* Create a Spark program to read the first 100 prime numbers from prime_nums.txt,
 * print the sum of those numbers to console.
 * Note- Each row of the input file contains prime numbers separated by more than one spaces.
 *       Some row of the input file having string not number so need to discard them.
 *       There are some numbers which are not prime so need to discard them.
 *
 */
object SumOfNumbersFromFile {

  def main(args: Array[String]): Unit = {
    Logger.getLogger("org").setLevel(Level.OFF)

    val spark = SparkSession.builder()
      .appName("SumOfPrimeNumbersOnly")
      .master("local[*]")
      .getOrCreate()

    val linesFromFile = spark.sparkContext.textFile(getClass.getResource("/rdd/prime_nums.txt").getPath)
    val validLinesFromFile = linesFromFile.filter(_.trim().length() > 0)

    val sum = validLinesFromFile
      .flatMap(_.trim().split("\\s+")) // First trim the line as some line start with space
      .filter(isAllDigits(_)) // filter in only numeric elements
      .map(_.toInt) // convert string to int
      .filter(isPrime(_)) // filter in only prime numbers
      .reduce(_ + _) // reduce to get sum of prime numbers

    println("Sum of 1 to 100 prime numbers: " + sum)
  }

  // Use Character.isDigit utility to check if all the character in string is digit
  def isAllDigits(x: String) = x forall Character.isDigit

  // To check prime, try to check if number is not divisible by 2 to half of number itself
  def isPrime(num: Int) = (2 to math.sqrt(num).toInt) forall (x => num % x != 0)
}