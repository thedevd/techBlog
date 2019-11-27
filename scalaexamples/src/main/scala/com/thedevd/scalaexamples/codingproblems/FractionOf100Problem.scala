package com.thedevd.scalaexamples.codingproblems

import scala.annotation.tailrec

/*
 * Problem -
 * ###########
 * We have list of no as - 111, 143, 233, 350, 90, 410 and we want output like - 
 * 100, 11, 100, 43, 100, 100, 33, 100, 100, 100, 50, 90, 100, 100, 100, 100, 10.
 * 
 * If you notice each number is combination of possible 100's and then remaining number
 * for example 
 *      233 can be written as 100 + 100 + 33 
 *      350 can be written as 100 + 100 + 100 + 50
 *      90 can be written as 90 only since it is less than 100
 *      
 * Solution- this can be done using recursive approach or fill() method of List collection
 */
object FractionOf100Problem {

  def main(args: Array[String]): Unit = {

    val numbers = List(111, 143, 233, 350, 90, 410)
    
    /*
     * Solution-1: Using tailRecursive approach
     */
    val result = numbers.flatMap(multipleOf100(_, List()))
    println(result)
    // List(100, 11, 100, 43, 100, 100, 33, 100, 100, 100, 50, 90, 100, 100, 100, 100, 10)
    
    /*
     * Solution-2: Using fill() method of List collection
     */
    val resultUsingListFill = numbers.flatMap(populateListWithFill(_))
    println(resultUsingListFill)
    // List(100, 11, 100, 43, 100, 100, 33, 100, 100, 100, 50, 90, 100, 100, 100, 100, 10)

  }

  @tailrec
  def multipleOf100(number: Int, list: List[Int]): List[Int] = {
    number > 100 match {
      case true => multipleOf100(number - 100, list :+ 100)
      case false => list :+ number // simply add the remaining number and return
    }
  }
  
  def populateListWithFill(number: Int): List[Int] = {
    val hundreds = List.fill(number/100)(100) // ex - 233/100 = 2 so fill 100 two times
    val remainder = number%100 // ex- 233%100 = 33, just add this in above list
    
    hundreds :+ remainder
  }
}