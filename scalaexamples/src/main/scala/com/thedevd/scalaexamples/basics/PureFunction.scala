package com.thedevd.scalaexamples.basics

/*
 * 1. In Functional Programming, pure function is defined like this:
 *    a. The function's output depends only on its input variables.
 *    b. It doesn't mutate input parameters.
 *    c. It doesn't have any "back doors": It doesn't read data from the outside world 
 *    (including the console, web services, databases, files, etc.), or write data to the outside world.
 *    
 * As a result of this definition, any time you call a pure function with the same input value(s), 
 * you’ll always get the same result. This called idempotent. 
 * 
 * For example, you can call a double function an infinite number of times with the input value 2, and 
 * you’ll always get the result 4.
 * 
 * 2. Example of pure functions-
 * ##############################
 *    Scala has lots of pure function. scala.math._ package have these pure functions -
 *    max, min, abs
 *    
 *    Some String methods are also pure such as isEmpty(), length()
 *    Many methods in scala collections are also pure such as map(), filter().
 *    
 * 3. Examples of impure functions
 * #################################
 *    a. The foreach() method on collections classes or println() is impure because it violates some rules of being pure -
 *    Firstly it has Unit return type means it takes input and does not returns anything.
 *    Secondly it can be used to interacts with outer word means print to STDOUT.
 *    
 *    b. Date and time related methods like getDayOfWeek, getHour, and getMinute are all impure because their output depends
 *    on some type of hidden IO. 
 *    
 *    So we can say impure function do followings- 
 *    1. Read hidden inputs, i.e., they access variables and data that is not explicitly passed into the function as input parameters.
 *    2. Mutate the parameters they are given.
 *    3. Perform some sort of I/O with the outside world.
 *    
 */


object PureFunction {
  
  def main(args: Array[String]): Unit = {
    val doubleResult = double(2)
    println(doubleResult) // 4
  }
  
  // lets write pure function to return double of input parameter.
  def double(num: Int): Int = num * 2
}