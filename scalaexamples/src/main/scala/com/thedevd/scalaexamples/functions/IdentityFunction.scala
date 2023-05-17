package com.thedevd.scalaexamples.functions

/*
 * In scala, an identity function is type of pure function which takes only one argument and returns same argument without
 * any transformation.
 *
 * Scala provides a predefined function called `identity` which is defined in Scala Predef object. This is how it looks -
 *    def identity[A](a: A): a
 *
 * Now the question is where can we use such function which does nothing with supplied argument.
 * Very common use of identity function is to use as a higher order function where a function is expected to be passed
 * as an argument but no actual transformation is needed on values.
 *
 * Lets take a very straightforward example of flatMap which expects a function as an argument.
 * We have a List[List[Int]] and we want to flatten this list to expect List[Int]
 *    val listOfList: List[List[Int]]= List(List(1), List(2, 3), List(4, 5))
 *
 * We can use flatMap function on this list of list, but we do not want any transformation applied to actual values here,
 * so this is classic usecase of using an identity function.
 *    val flattendList: List[Int] = listOfList.flatMap(identity)
 */
object IdentityFunction extends App {

  // Example #1
  val listOfList: List[List[Int]] = List(List(1), List(2, 3), List(4, 5))
  val flattenedList: List[Int] = listOfList.flatMap(identity)
  // Although in scala we can simply use listOfList.flatten to achieve the same outcome

  println(flattenedList) // List(1, 2, 3, 4, 5)

  // Example #2 - map requires a function as argument
  println(List(1, 2, 3, 4, 5).map(identity)) // List(1, 2, 3, 4, 5)
}
