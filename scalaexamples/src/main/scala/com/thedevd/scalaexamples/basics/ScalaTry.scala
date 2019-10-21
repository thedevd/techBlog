package com.thedevd.scalaexamples.basics

import scala.util.Try
import scala.util.Failure
import scala.util.Success
import scala.io.Source
import java.io.File

/*
 * Before reading this, go through these classes first ScalaOption.scala and ScalaEither.scala.
 *
 * 1. In Scala, Try/Success/Failure is another alternative of error handling and error reporting.
 *    (We also have java like try/catch, Option/Some/None and Either/Left/Right).
 *
 *    As scala has lot of different way of exception handling and reporting, which makes difficult
 *    for developers to decide which one to choose. In this example we will take a look on these.
 *
 * 2. Try is like Either/Option, i.e it has two cases- Failure and Success
 *         Try[A]
 *             -- Failure[A]
 *             -- Success[A]
 *    but difference is that Try has only one parameter Type that can be either
 *    Failure or Success. And the Failure can only be of type of Throwable. It has no Left and Right concept.
 *         Try[A] is isomorphic to Either[Throwable, A]
 *
 * 2. First lets discuss why use Try vs try/catch
 * ################################################
 * Try is functional way to handle errors in Scala.
 * Try allows users to chain together some statements that are only executed if flow is in Success state
 * or allows to follow different path when something has failed. This chaining of statements in basically
 * known as function composition. Where as OOP way of try/catch does not offer any function composition
 * abilities.
 *
 * Another advantage Try provides to client is that, with Try client can take responsibility how they want
 * to handle errors rather than just handling them in traditional try/catch mechanism.
 *           try {
 *               val num = 1/0
 *               }
 *            catch {
 *               case Exception => println("Can not divide by zero")
 *            }
 *
 *        Here you see we have handled the exception, now what if client wants to handle that by thier own way.
 *        Try allows that.
 *
 *
 * Option[T] vs Either[L,R] vs Try[T]
 * #####################################
 * 1. Option[T], use it when a value can be absent or some validation can fail and you don’t care about the exact cause.
 *    Typically used in data retrieval (such as in Map) and validation logic.
 *
 * 2. Either[L,R], similar use case as Option but when you you want to provide some information about the error.
 *
 * 3. Try[T], use it when dealing with code that can throw exceptions — and you always want the exception details (stack trace + message), but
 *    this information should be of Throwable Type only.
 *
 * */

object ScalaTry {

  def main(args: Array[String]): Unit = {

    example1_divisionByZero()
    example2_parseInt()
    example3_readFromFile()
  }

  def example1_divisionByZero() = {

    @throws(classOf[Exception]) def divisionWithErrorHandling(x: Int, y: Int): Double = {
      x / y
    }

    /*
     *  In scala there is not checked exception all are unchecked, so if you call
     *  above function compiler is not going to tell you at compile time that you forgot to handle exception,
     *  So even if you throws checked exception of java such as  IOException, compiler wont help.
     *
     *  val result = divisionWithErrorHandling(1, 0) // this will blow up with Arithmatic exception
     *
     *  So let use try and catch
     */

    def divisionWithErrorHandling_tryCatch(x: Int, y: Int): Double = {
      try { x / y } catch { case e: Exception => 0 }
    }
    val result = divisionWithErrorHandling_tryCatch(1, 0)
    println(result) // --> 0.0

    /*
     * Lets see how can we transform this to return Try[T]
     */
    def divisionWithErrorHandling_Try(x: Int, y: Int): Try[Int] = {
      Try(x / y)
    }

    val resultTry = divisionWithErrorHandling_Try(1, 0)
    println(resultTry) // --> Failure(java.lang.ArithmeticException: / by zero)

    // Using in pattern matching
    resultTry match {
      case Failure(error) => println("Something went wrong, reason: " + error)
      case Success(value) => println(value)
    } // --> Something went wrong, reason: java.lang.ArithmeticException: / by zero

    /*
     * As mentioned, we can also chain the operation with Try
     */
    val finalResult = divisionWithErrorHandling_Try(10, 5).map(x => x * x).filter(x => (x % 2 == 0))
    println(finalResult) // --> Success(4)
    val finalResult_1 = divisionWithErrorHandling_Try(10, 0).map(x => x * x).filter(x => (x % 2 == 0))
    println(finalResult_1) // -->  Failure(java.lang.ArithmeticException: / by zero)

  }

  def example2_parseInt() = {

    /*
     * Demo using Option
     */
    def parseInt_Option(s: String): Option[Int] = {
      try {
        Some(Integer.parseInt(s))
      } catch {
        case e: Exception => None // So you can see None does not tell anything about error (what happened)
      }
    }
    println(parseInt_Option("10")) // --> Some(10)
    println(parseInt_Option("10a")) // --> None

    /*
     * Demo using Try
     */
    def parseInt_Try(s: String): Try[Int] = {
      try {
        Success(Integer.parseInt(s))
      } catch {
        case e: Exception => Failure(e)
        // case e:Exception => Failure(e.getMessage) ---> error here
        // here you can not return anything other than Throwable,
        // So what is I want to return simply a string message, then I have to use Either[A,B]
      }
    }
    // def parseInt_Try(s:String): Try[Int] = Try(s.toInt)
    println(parseInt_Try("10")) // --> Success(10)
    println(parseInt_Try("10a")) // --> Failure(java.lang.NumberFormatException: For input string: "10a")

    
    /*
     * Demo using Either
     */
    def parseInt_Either(s: String): Either[String, Int] = {
      try {
        Right(Integer.parseInt(s))
      } catch {
        case e: Exception => Left("NumberFormatException " + e.getMessage) // So you can see we can return non Throwable object also
      }
    }
    println(parseInt_Either("10")) // --> Right(10)
    println(parseInt_Either("10a")) // --> Left(NumberFormatException For input string: "10a")
    
    // chaining the statements
    println(parseInt_Try("10").map(_*10)) // --> Success(100)
  }
  
  
  def example3_readFromFile() = {
    
    def readFromFile(filePath: String): Try[List[String]] = {
      Try(Source.fromFile(new File(filePath)).getLines().toList)
    }
    
    readFromFile("/notexist/file.txt") match {
      case Failure(error) => println("Error occurred: " + error)
      case Success(lines) => lines.foreach(println)
    } // --> Error occurred: java.io.FileNotFoundException: \notexist\file.txt (The system cannot find the path specified)
  }

}