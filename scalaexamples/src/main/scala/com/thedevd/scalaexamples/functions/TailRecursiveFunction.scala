package com.thedevd.scalaexamples.functions

import scala.annotation.tailrec

/*
 * This example will demonstrate -
 * 1. What is tail recursive function
 * 2. How to create tail recursive function with use of
 *    scala annotation @tailrec
 *
 * First let see why we use recursion -
 * #####################################
 * Recursion in programming language means function calling itself. This approach
 * is generally used to break the problems into subproblems and call function to solve each subproblem.
 * Just think of the example of finding the factorial of a number.
 *
 * The problem with recursion -
 * ######################################
 * In recursion, for each function call there is separate stack frame created, so small amount of
 * memory is consumed by each frame in stack. The another major problem with recursion is,  if the recursion
 * is too deep then it can blow up stack and thus stackoverflow exception will arise.
 *
 * In order to avoid the problems of recursion, we can use tail recursion.
 * So now lets see what is tail recursion -
 * ##########################################
 * A recursive function is said to be tail recursive function if -
 * 1. the recursive call is the last operation performed by the function
 * 2. There is no need to keep result of the previous state.
 *
 * Scala Support of Tail Recursion-
 * #################################
 * To help developers, scala provides an annotation @tailrec that can be used to check
 * if the recursion is tail recursive or not. The annotation is available as a part of the
 *    scala.annotation._ package
 *
 * If the recursive function is not tail recursive and you used @tailrec annotation then
 * scala compiler will throw compile-time error saying-
 *
 *  "Could not optimize @tailrec annotated method <method_name>: it contains a recursive call not in tail position"
 *
 * So in single word -
 * ####################
 * @tailrec annotation tells the compiler that treat this function as tail-recursive and optimize it.
 *
 * Note-
 * Scala compiler forces to specify return type when defining recursive function.
 *
 */

// Scala program of factorial using tail recursion
object TailRecursiveFunction {

  def main(args: Array[String]): Unit = {
    val result = factorial(5)
    println(result)
  }

  def factorial(num: Int) = {
    tailRecursiveFactorial(num, 1)
  }

  @tailrec
  def tailRecursiveFactorial(num: Int, result: Int): Int = {
    if (num == 1) result // --> using result to avoid storing the result of previous state
    else tailRecursiveFactorial(num - 1, num * result) // --> see, last operation is purely a recursive call
  }

  // @tailrec
  // Can not use @tailrec here because the last operation is not recursive call
  def nonTailRecursiveFactorial(num: Int): Int = {
    if (num == 1) 1
    else num * nonTailRecursiveFactorial(num - 1) // --> last operation is not purely recursive call so can not be optimized with @tailrec
  }

}