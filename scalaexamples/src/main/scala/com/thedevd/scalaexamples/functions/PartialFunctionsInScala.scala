package com.thedevd.scalaexamples.functions

/*
 * There are some situation where a function is not able to produce the output
 * for some specific range of input values. For example a function defined to
 * return square root of a number is not able to produce output for negative numbers.
 * In scala such functions are called partial functions because they can accept partial range of inputs.
 * 
 * So partial function is a unary function that is not able to producer output for every possible value of input type,
 * rather it can return the output for a certain range of input's value. 
 * for example -function that returns the Square root of the input number will not work if the input number is negative.
 * 
 * PartialFunction API is used to define partial functions in scala, where you can use any of these ways to define them -
 *  - isDefinedAt() and apply() - isDefinedAt() defines the condition and apply() performs the operation if condition is satisfied.
 *  - case statement (RECOMMENDED)
 *  
 * Partial Functions can also be chained together using 
 *    - andThen
 *    - orElse
 *  
 *  Simple example using case- 
 *     val squareRoot: PartialFunction[Double, Double] = { // [Double, Double] means taking Double type input and returning Double
 *           case x if x > 0 => Math.sqrt(x)
 *     }
 *
 *    it can also be written simply using anonymous type -
 *    val squareRootPf = (number: Double) => number match {
 *      case x if x > 0 => Math.sqrt(x)
 *    }
 *     
 * Note- PartialFunction is basically a trait which has two unimplemented methods isDefinedAt() and apply().
 * So when implementing it using new, you need to provide those implementations.
 *
 * Calling partialFunction on the input it does not supposed to work, will throw MatchError.
 *
 * Lifting
 * -----------
 * PartialFunction can be converted to a full function by applying .lift which transform it to return Option[T]
 * it means in case where PartialFunction was returning MatchError, it will return None.
 * Example -
 *    val squareRootPf = (number: Double) => number match {
 *      case x if x > 0 => Math.sqrt(x)
 *    }
 *
 *    val fullFunction: (Double) => Option[Double] = squareRootPf.lift
 *    val applyOnNegative = fullFunction(-4) ----> this will return None
 */
object PartialFunctionsInScala {
  
  def main(args: Array[String]): Unit = {
    
    /*
     * ############################################################################################
     * Creating partial function using anonymous implementation of isDefinedAt() and apply() methods 
     * of PartialFunction trait 
     *  
     */
    val squareRoot  = new PartialFunction[Double,Double] { // this is a way to create anonymous implementation of trait
      
      // defining isDefinedAt() to specify the condition
      def isDefinedAt(number: Double) = number > 0
      
      // defining apply() to perform the operation only if input is valid
      def apply(number: Double) = Math.sqrt(number)
    }
    
    println(squareRoot(4)) //--> 2
    // so here squareRoot is able to return the output as it was within business domain according to isDefinedAt()
    
    println(squareRoot(-4)) // --> NaN
    // so here squareRoot is not able to return the output as it was out of business domain according to isDefinedAt()
    
    
    /*
     * #################################################
     * Partial function using case statement -
     * 
     * Below function is defined to return output for only positive numbers,
     * In case of negative numbers, the function returns scala.MatchError runtime exception.
     */
    val squareRootPFUsingCase :  PartialFunction[Double, Double] = {
      case number if (number > 0) => Math.sqrt(number)
    }
    /* val squareRootPFUsingCase = (x: Double) => x match {
     *    case number if number>0 => Math.sqrt(number)
     * }
     */
    
    println(squareRootPFUsingCase(4))
    // println(squareRootPFUsingCase(-4)) // Exception in thread "main" scala.MatchError: -4.0 (of class java.lang.Double)
    
    // We can also make use of isDefinedAt() method to test if the input value is in the business domain  of the function
    println(squareRootPFUsingCase.isDefinedAt(2)) // --> true
    println(squareRootPFUsingCase.isDefinedAt(-4)) // --> false
    
    /*
     *#############################################
     * Lifting partialFunction using .lift
     */
    val liftedSquareRootPFUsingCase: Function1[Double, Option[Double]] = squareRootPFUsingCase.lift // Double => Option[Double]
    println("liftedSquareRootPFUsingCase(4): " + liftedSquareRootPFUsingCase(4)) // --> Some(2.0)
    println("liftedSquareRootPFUsingCase(-4): " + liftedSquareRootPFUsingCase(-4)) // --> None

    /*
     * #######################################################
     * Chaining of partial Functions using andThen, orElse
     * 
     * This feature of Partial function becomes very useful when you want to implement a validation system. I mean
     * If there are series of check implemented and you want to verify if the input data meets certain guidelines, then
     * chaining them will allow you to do so.
     */
    
    val positiveCheck : PartialFunction[Int, Int]  = {
      case number if (number >=0) => number
    }
    
    val evenCheck: PartialFunction[Int,Boolean] = {
      case number if(number%2 == 0) => true
    }
    
    val oddCheck: PartialFunction[Int, Boolean] = {
      case number if(number%2 == 1) => true
    }
    
    val completeEvenCheck : PartialFunction[Int, Boolean] = positiveCheck andThen evenCheck
    val completeOddCheck: PartialFunction[Int, Boolean] = positiveCheck andThen oddCheck
    
    // Since the resulting chained function is also Partial, isDefined can be used to 
    // check if input is in business domain
    println(completeEvenCheck.isDefinedAt(4)) // --> true
    println(completeEvenCheck.isDefinedAt(-4)) // --> false
    
    
    /*
     * ######################################################
     * Chaining partial function using orElse
     */
    val tellMeEven: PartialFunction[Int,String] = {
      case number if(number >=0 && number%2 == 0) => s"$number is even"
    }
    
    val tellMeOdd: PartialFunction[Int, String] = {
      case number if(number >=0 && number%2 == 1) => s"$number is odd"
    }
    
    val whatIsNumber: PartialFunction[Int, String] = tellMeEven orElse tellMeOdd
    println(whatIsNumber(2)) // --> 2 is even
    println(whatIsNumber(3)) // --> 3 is odd
    
    // partial functions can also be used in map or flatmap
    val list = List(1,2,3)
    println(list.map(whatIsNumber(_))) // --> List(1 is odd, 2 is even, 3 is odd)
  }
}