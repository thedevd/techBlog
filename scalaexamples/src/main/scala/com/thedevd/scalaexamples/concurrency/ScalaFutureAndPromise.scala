package com.thedevd.scalaexamples.concurrency

import scala.concurrent.Promise
import scala.concurrent.Future
import scala.util.Success
import scala.util.Failure
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.control.NonFatal

/*
 * Future is just a read-only placeholder to store a value that may be available after some point later.
 * So Future is just a way to work with the value that it will compute and handle to failure in case of any exception.
 *
 * There is some other part which works internally that write the value in Future, and this is done by Promise. So
 *  we can say -
 *     Future --> represents a value which may or may not currently available.
 *     Promise --> represents the computation that will produce value (or completes future) in some time
 *                 and return it in the form of Future object.
 *
 *                 Promise internally completes the future in either of two ways-
 *                 1. completing the future with a value by calling success method (promise.success). This is called
 *                 completing the promise.
 *                 2. or complete the future with an exception by calling failure method (promise.failure). This is called
 *                 failing the promise.
 *
 *
 * In very very general language-
 * ## A Future is just an abstraction layer over Promise. and it is readonly object that represents a value which may become available at some point.
 * A Future object either holds a result of a computation or an exception in the case that the computation failed.
 * Future is immutable, you can only read from it.
 *
 * Promise is responsible to produce the value at some time. It is like promise is promising the end user to give
 * something which it does not have right now but it will be soon.
 *
 */
object ScalaFutureAndPromise {

  def main(args: Array[String]): Unit = {

    example1_returningPromise()
    Thread.sleep(6000) // some wait time so that we can see the output of async call, this is only for demo

    example2_convertingPromiseToFuture()
    Thread.sleep(6000) // some wait time so that we can see the output of async call, this is only for demo

    example3_sendMailToEmployeeWithTaxDeductionDetails()
    
    println("I am main thread, not waiting for async call result for example3_sendMailToEmployeeWithTaxDeductionDetails()")
    Thread.sleep(10000)
    
    /*
     * Overall output - 
     * I am done: 2
     * error: java.lang.ArithmeticException: / by zero
     * I am main thread, not waiting for async call result for example3_sendMailToEmployeeWithTaxDeductionDetails()
     * (Employee(1,dev,6000.0,dev@gmail.com),5400.0)
     */
  }

  /*
   * ############## Example1
   */
  def example1_returningPromise() = {

    // giving promise directly to client allow them to complete it whenever they want,  which is bad! 
    val promise = divideNumber(10, 5)

    val future = promise.future

    future.onComplete { //Define the callback
      case Success(r) => println(r)
      case Failure(f) => println(f)
    }

    /*
     * If we give promise directly to end user, then it can allow them
     * to complete the future by thier own by calling success or failure method.
     * And this we do not want them to do by their own, because in case if future is already
     * complete and they try to complete it, it will throw exception -
     *    Exception in thread "main" java.lang.IllegalStateException: Promise already completed.
     *
     * see how user can complete the promise using
     */
    // promise success "I am completing it forcibly after some time"
    // this will throw this - Exception in thread "main" java.lang.IllegalStateException: Promise already completed.

    /*
     * So to prevent end user to complete the future by their, we have to return the Future object, because
     * in Future there is no success or failure method, you can only rely on callbacks.
     *
     * See the example2_convertingPromiseToFuture() method, which solves this problem
     */
  }

  def divideNumber(x: Int, y: Int): Promise[String] = {
    //ExecutionContext is required for Future/Promise so Scala knows where to get the threads from
    // This allow a built in ExecutionContext will be added to all Promises/Futures
    // import scala.concurrent.ExecutionContext.Implicits.global
    val promise = Promise[String] //The promise will hold a string
    Future { //A future tells the System to spawn a new thread and run the code block inside it
      Thread.sleep(5000)
      try {
        promise.success("I am done: " + x / y)
      } catch {
        case NonFatal(e) => promise.failure(e)
      }
    }
    promise
  }

  /*
   * ############## Example2
   */
  def example2_convertingPromiseToFuture() = {

    val future_goodone = divideNumber_goodone(10, 0)
    // End user won't be able to complete it as future doesn't have a .success() function
    future_goodone.onComplete {
      case Success(r) => println(r)
      case Failure(f) => println("error: " + f)
    }
  }

  /*
   * converting Promise into a future using promise.future
   */
  def divideNumber_goodone(x: Int, y: Int): Future[String] = {
    val promise = Promise[String]
    Future {
      Thread.sleep(5000)
      try {
        promise.success("I am done! nobody can complete me except me: " + x / y)
      } catch {
        case NonFatal(e) => promise.failure(e)
      }
    }
    promise.future // returning Future not promise directly
  }

  /*
   * ############## Example3
   */
  def example3_sendMailToEmployeeWithTaxDeductionDetails() = {

    /*
     * Imaging you want to send details of employee tax deduction to the employee.
     * You have separate web service call to -
     *   1. get employee using Id - getEmployeeById(int:Id)
     *   2. fetching tax rate based on salary - fetchTaxRate(emp: Employee)
     *   3. send mail - sendEmail(emp: Employee, tax:TaxRate)
     *
     *   From the example we can say it is scenario of chaining the futures
     */

    val empLookup = Map(1 -> Employee(1, "dev", 6000, "dev@gmail.com"))

    // Webservice simulation for getEmployeeById from db
    def getEmployeeById(id: Int): Future[Employee] = {
      val p = Promise[Employee]
      Future {
        try {
          val emp = empLookup.get(id).get
          p.success(emp)
        } catch {
          case NonFatal(e) => p.failure(e)
        }
      }

      Thread.sleep(3000)
      p.future
    }

    // Webservice simulation for getTaxRate of an employee
    def fetchTaxRate(emp: Employee): Future[TaxRate] = {
      val p = Promise[TaxRate]
      Future {
        if (emp.salary > 10000) p.success(new TaxRate(20))
        else if (emp.salary > 5000) p.success(new TaxRate(10))
        else p.success(new TaxRate(5))
      }

      Thread.sleep(2000)
      p.future
    }

    // Webservice call to calculate tax
    def calculateSalaryAfterTax(emp: Employee, taxRate: TaxRate): Future[Double] = Future {
      emp.salary - (emp.salary * taxRate.rate / 100)
    }

    // Webservice simulation to send Email
    def sendEmail(emp: Employee, tax: TaxRate, newSalary:Double): Future[String] = {
      val p = Promise[String]
      p.success(s"sent email to: $emp.email \t salary: $emp.salary after taxrate: $tax.rate")
      
      Thread.sleep(2000)
      p.future
    }

    val result = for {
      emp <- getEmployeeById(1)
      taxrate <- fetchTaxRate(emp)
      newSalary <- calculateSalaryAfterTax(emp, taxrate)
      email <- sendEmail(emp, taxrate, newSalary)
    } yield (emp, newSalary)

    result.onComplete {
      case Success(r) => println(r)
      case Failure(f) => println("error:" + f)
    }

  }

  case class Employee(id: Int, name: String, salary: Double, email: String)
  case class TaxRate(rate: Double)
}