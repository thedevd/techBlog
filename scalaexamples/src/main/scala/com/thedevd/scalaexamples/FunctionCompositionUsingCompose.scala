package com.thedevd.scalaexamples

/*
 * Calling compose will take the result from the second function and pass it as input parameter to the first function.
 * Keep in mind the difference in ordering between andThen and compose - 
 *   Ordering using andThen: f(x) andThen g(x) = g(f(x)) // f(x) is called first then g(x)
 *   Ordering using compose: f(x) compose g(x) = f(g(x)) // g(x) is called firs then f(x)
 */
object FunctionCompositionUsingCompose {
  
  def main(args: Array[String]): Unit = {
    val composeValFunction = increamentByOne compose square
    val andThenValFunction = increamentByOne andThen square
    println("(increamentByOne compose square)(1): " + composeValFunction(1)) // 2
    println("(increamentByOne andThen square)(1): " + andThenValFunction(1)) // 4
    
    /*
     * So (increamentByOne compose square)(1) will call second function first i.e. square(1)
     * and then output is passed to input of first function i.e. increamentByOne(1) -- > 2
     */
  }
  
  val increamentByOne = (num: Int) => num + 1
  val square = (num:Int) => num * num
}