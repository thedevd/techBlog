package com.thedevd.scalaexamples.concurrency

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.Success
import scala.util.Failure
import scala.util.Random
import scala.concurrent.Await

/*
 * scala.concurrent.Future is used to write asynchronous non-blocking operation in scala.
 * A very normal usecase would be calling a webservice or executing a long running task and
 * you want to run it separately from main thread.
 *
 * Creating Future is so simple, you just need to put the block of code in Future Constructor
 * that you want to run off the main thread
 *     Future { //code blocks here
 *              // return some value }
 *
 * Few things to know about Future
 * ##################################
 * 1. Future is used to run tasks off of the main thread.
 * 2. A Futures run immediately after you create it.
 *    (So it is not like java thread where you have to call start() method to run the task)
 * 3. The value in a Future is always an instance of one of the Try types: Success or Failure.
 *       3.a When a Future is completed with a value, we say that the future was successfully completed with that value.
         3.b When a Future is completed with an exception thrown by the computation, we say that 
             the Future was failed with that exception.
 * 4. onComplete() or other callback methods (onSuccess, onFailure) are used to process future's result
 *    when it is available. Means what to do with future'result is decided in callback.
 * 5. Use for-comprehension, if you want to combine results of more than one future to produce single result.
 *
 * so overall -
 * Common callback methods
 *   -- onComplete
 *   -- onSuccess
 *   -- onFailure
 *
 * Note - Import 'import scala.concurrent.ExecutionContext.Implicits.global' is needed to create Future task,
 * because internally implemented apply method of Future takes ExecutionContenxt type parameter.
 *             Future { ... }(executionContext)   
 *                   OR in more general way-
 *             import scala.concurrent.ExecutionContext.Implicits.global 
 *             Future {...}
 *             
 * What is ExecutionContext
 * ###########################
 * Future and Promises revolve around ExecutionContexts, which is responsible for executing computations.
 * An ExecutionContext is similar to an Executor: it executes computations in a new thread/pooled thread.
 * The scala.concurrent package comes out of the box with an ExecutionContext implementation, a global static thread pool.
 * import scala.concurrent.ExecutionContext.Implicits.global is used to import global static thread pool.
 *             
 * A Future has an important property that it may only be assigned once. Once a Future object is given a value or an exception, 
 * it becomes immutable– it can never be overwritten.
 *
 */
object ScalaFuture {

  def main(args: Array[String]): Unit = {

    example1_getEmployeeNameFromWebService()
    example2_getStockPriceFromWebService()
  }

  def example1_getEmployeeNameFromWebService() = {

    // simulate getEmployee webservice
    val empDb = Map(1 -> Employee(1, "dev"), 2 -> Employee(2, "ravi"))
    def getEmployeeNameFromWebService(id: Int): String = {
      Thread.sleep(2000) // say- web service takes min 2 second to respond
      empDb.get(id) match {
        case None => s"Employee not found with id: $id"
        case Some(value) => value.name
      }
    }

    // lets call the webservice using Future and use Callback onComplete to process the result
    val empWithId1 = Future { getEmployeeNameFromWebService(1) }
    empWithId1.onComplete {
      case Success(value) => println(s"Got the result from callback, value = $value")
      case Failure(e) => e.printStackTrace
    }

    val empWithId3 = Future { getEmployeeNameFromWebService(3) }
    empWithId3.onComplete {
      case Success(value) => println(s"Got the result from callback, value = $value")
      case Failure(e) => e.printStackTrace
    }

    // some other work by main thread .....
    println("I am main method and I am doing my rest of work....")

    /*
     * Chaining Futures using for-comprehension
     */
    val empNameList = for {
      emp1Name <- Future { getEmployeeNameFromWebService(1) }
      emp2Name <- Future { getEmployeeNameFromWebService(2) }
    } yield (emp1Name, emp2Name)

    empNameList.onComplete {
      case Success(value) => println(s"Got the result from callback, empList = $value")
      case Failure(e) => e.printStackTrace
    }

    println("More works done by Main thread... ")
    Thread.sleep(10000)

    /*
     * output of this example is going to be-
     *
     * I am main method and I am doing my rest of work....
     * More works done by Main thread...
     * Got the result from callback, value = dev
     * Got the result from callback, value = Employee not found with id: 3
     * Got the result from callback, empList = (dev,ravi)
     *
     * So you can see here main thread does not get block after running future, it goes on doing its remaining task
     * and if Future gets completed before main thread dies,we get Future's output because
     * we have used a callback function onComplete on the Future.
     */

  }
  case class Employee(id: Int, name: String)

  def example2_getStockPriceFromWebService() = {

    /*
     * Imagine, a method in your application calling a web service to get the current price of a stock.
     * Because it is a web service it can be slow to response, and even fail.
     * As a result, you create a method calling webservice to run as a Future .
     * It takes a stock symbol as an input parameter and returns the stock price as a Double inside a Future.
     *    def getStockPrice(stockSymbol: String): Future[Double] = {..... }
     *
     * So in this method we are calling a webservice. In this demo we have simulated task of webservice within the method
     * for understanding purpose.
     */

    // Simulated web service, which is called by a method in our application
    def getStockPrice(symbol: String): Future[Double] = Future {
      val random = scala.util.Random
      val stockPrice = random.nextDouble() * 100

      val randomSleepTime = random.nextInt(3000) // rest service takes time to respond, so adding a sleep for simulation
      Thread.sleep(randomSleepTime)
      println(s"$randomSleepTime ms time taken to respond stockPrice: $stockPrice for stockSymbol: $symbol")

      stockPrice
    }

    /*
     * Lets say, you are told to get stock price of three stocks in parallel and
     * return their result altogether in single tuple.
     *
     * So to achieve this lets create three Futures and use for-comprehension to return their result collectively.
     * And then use onComplete callback to process the result.
     */
    val result = for {
      tcs <- getStockPrice("tcs")
      infy <- getStockPrice("infosys")
      hdfc <- getStockPrice("hdfc")
    } yield (tcs, infy, hdfc)

    result.onComplete { // the value of Future is instance of one of Try type - Success or Failure
      case Success(value) => println(s"stock prices: $value")
      case Failure(ex) => ex.printStackTrace()
    }

    // Adding sleep for main thread to keep it alive for some time, this is done for demo
    // so that we can see the asynchronous result from the future.
    Thread.sleep(10000)
    
    /*
     * Output on completion of all those three Future is -
     * 
     * 2924 ms time taken to respond stockPrice: 79.3931672446358 for stockSymbol: tcs
     * 2513 ms time taken to respond stockPrice: 69.96650362250996 for stockSymbol: infosys
     * 372 ms time taken to respond stockPrice: 78.69795827466594 for stockSymbol: hdfc
     * stock prices: (79.3931672446358,69.96650362250996,78.69795827466594)
     */
  }
}