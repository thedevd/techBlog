package com.thedevd.scalaexamples.basics

import scala.collection.Map

/*
 * 1. Like Option[A], Either is container Type, but difference is that
 * Either has two parameter types i.e.
 *    Either[A,B]
 *
 * Either[A,B] means it can contain either instance of A or instance of B.
 *
 * 2. Like Option[A], Either has two subclasses - Left and Right
 *    Either[A, B]
 *       --> Left
 *       --> Right
 *
 *    As I said Either is just like option so, by convention
 *    - Left is just like None but it can contain String content to describe to problem
 *    - Right is just like Some
 *
 * 3. Why Either against Option (Either vs Option)
 * ################################################
 * Error handling is a very common use of Either over Option and it is used to know the reason why something has failed.
 * In scala Either is widely used in companion with Option and Try to know what is the reason or
 * why something has failed rather just returning ErrorType.
 *
 * Using Option alone you can not achieve this, because Option does not tell you why something has failed
 * that is why you got None instead of Some. So using Either you can return the information about the
 * problem to the client if something goes wrong or return the actual value on success.
 *
 * In this demo, we will look how Either is used to return the information about the problem
 * if anything goes wrong or return the actual value on success ->
 *     Example 1 - divideByZero
 *         Either[String, Int]
 *
 *     Example 2- getEmployeeFromWebService
 *         Either[String, Employee]
 *
 *
 */
object ScalaEither {

  def main(args: Array[String]): Unit = {

    example1_divideByZero()
    example2_getEmployeeFromDb()

    /*
     * In the above example, you can notice to print the error and success message,
     * we have used pattern matching i.e.
     *
     *   getEmployeeFromDb(3) match {
     *     case Left(error) => println("Error => " + error) // Error => Employee Not found for id:3
     *     case Right(value) => println("Success => " + value)
     *   }
     *   
     * we can also use isLeft or isRight boolean methods.
     * See eitherPrinterUtil() method
     *
     */

  }

  def example1_divideByZero() = {

    def divideXByY(x: Int, y: Int): Either[String, Int] = {
      if (y == 0)
        Left("Bro, you can not divide by 0.") // returning error information as message in Left child (failure)
      else
        Right(x / y) // returning result in Right child (success)
    }

    // how to use Either further
    val error = divideXByY(1, 0) // --> Error: Bro, you can not divide by 0.
    val result = divideXByY(6, 2) // --> 3
    eitherPrinterUtil(error)
    eitherPrinterUtil(result)

    println(divideXByY(2, 2)) // --> Right(1)
    println(divideXByY(2, 0)) // --> Left(Bro, you can not divide by 0.)

  }

  case class Employee(id: Int, name: String, salary: Double, age: Int)

  def example2_getEmployeeFromDb() = {

    // fake map to simulate db here
    val empLookup = Map(1 -> Employee(1, "dev", 5000, 34), 2 -> Employee(2, "ravi", 6000, 36))

    def getEmployeeFromDb(id: Int): Either[String, Employee] = {
      empLookup.get(id) match {
        case None => Left("Employee Not found for id:" + id)
        case Some(value) => Right(value)
      }
    }

    // call function
    /*getEmployeeFromDb(3) match {
      case Left(error) => println("Error => " + error) // Error => Employee Not found for id:3
      case Right(emp) => println("Success => " + emp)
    }*/
    eitherPrinterUtil(getEmployeeFromDb(3)) // --> Error => Employee Not found for id:3
    eitherPrinterUtil(getEmployeeFromDb(1)) // --> Employee(1,dev,5000.0,34)

    // using isLeft or isRight boolean
    val result = getEmployeeFromDb(2)
    if (result.isLeft) println("Something went wrong: " + result.left.get)
    else println("Hurray got the result: " + result.right.get) // --> Hurray got the result: Employee(2,ravi,6000.0,36)

  }

  /*
   * Just a utility to print Either's Left or Right
   */
  def eitherPrinterUtil(either: Either[String, Any]) = {
    either match {
      case Left(error) => println("Error => " + error)
      case Right(value) => println(value)
    }

    // Instead of using pattern matching we can make use of isLeft or isRight
    /*
     * if(either.isLeft) println("Error => " + either.left.get)
     * else println(either.right.get)
     */
  }

}