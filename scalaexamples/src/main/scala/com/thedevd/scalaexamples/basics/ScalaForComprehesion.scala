package com.thedevd.scalaexamples.basics

import scala.util.Try

/*
 * In Scala we have for-loop and for-comprehension.
 * for-loop is used for side effects (such as printing to STDOUT) where as for-comprehension
 * is used to create new collection from existing collection after applying an operation on each
 * elements of existing collection.
 *
 * How to create for-comprehension
 * ################################
 * Use for-loop with yield statement, for ex -
 *    val nums = Array(1,2,3,4,5)
 *    val doubles = for(i <- nums) yield i*2     ---> this is called for-comprehension
 *
 *    Note- In most cases, the type of output collection is same as of input collection.
 *    As seen in above example doubles is also Array type.
 */
object ScalaForComprehesion {

  def main(args: Array[String]): Unit = {

    example1_doubleTheNumber()
    example2_objectCreationAfterValidation()

  }

  def example1_doubleTheNumber() = {

    val inputArray = 1 to 10

    val double = for (i <- inputArray) yield i * 2 // using yield along with for-loop

    for (i <- double) print(i + " ") // --> 2 4 6 8 10 12 14 16 18 20

    /*
     * Instead of using for-comprehension, we can also use map here
     * so map is similar to for-comprehension here.
     * Most of the time, for-comprehension can be achieved using map function, but
     * there is a slight difference and that is for-comprehension allows using guards (See ScalaForComprehensionVsMap.scala)
     */
    inputArray.map(_ * 2)
  }

  case class Person(name: String, age: Int)
  
  def example2_objectCreationAfterValidation() = {

    def validateName(name: String): Either[String, String] = {
      if (name.trim().length() < 3) Left("Name should have atleast 3 characters")
      else Right(name.toUpperCase())
    }

    def validateAge(age: Int): Either[String, Int] = {
      if (age < 18) Left("Age is below 18, can not proceed")
      else Right(age)
    }

    def createPersonUsingForComprehension(name: String, age: Int): Either[String, Person] = {
      for {
        n <- validateName(name).right
        a <- validateAge(age).right
      } yield Person(n, a)
    }
    
    // lets create object
    val p1 = createPersonUsingForComprehension("dev", 18)
    println(p1) // --> Right(Person(DEV,18))
    
    val p2 = createPersonUsingForComprehension("ravi", 17)
    println(p2) // --> Left(Age is below 18, can not proceed)
    
  }

}