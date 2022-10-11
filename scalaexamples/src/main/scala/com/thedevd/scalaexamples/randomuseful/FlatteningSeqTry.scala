package main.scala.com.thedevd.scalaexamples.randomuseful

import scala.util.{ Failure, Success, Try }

object FlatteningSeqTry extends App {

  def stringToInt(string: String): Try[Int] = {
    Try(string.toInt)
  }

  val trySeq: Seq[Try[Int]] = Seq("100", "11a", "350", "500a0", "1000").map(stringToInt)
  // List(Success(100), Failure(NumberFormatException), Success(350), Failure(NumberFormatException), Success(1000))

  // ---- separating out success and failure cases
  val toAllSuccesses: Seq[Try[Int]] = trySeq.filter(_.isSuccess)
  val toAllFailures: Seq[Try[Int]] = trySeq.filter(_.isFailure)
  val(successes, failures) = trySeq.partition(_.isSuccess) // Above two flavors of filtering can be combined by applying .partition

  // ---- Transforming to get only success i.e. only numbers
  val toIntFromSuccess: Seq[Int] = toAllSuccesses.map(_.get) // .get can only be applied on success to get the actual value
  val toIntFromSuccessUsingCollect: Seq[Int] = toAllSuccesses.collect{ case Success(value) => value }
  val toIntByMapSuccess: Seq[Int] = toAllSuccesses.map{ case Success(value) => value }
  val toOptionSeq: Seq[Option[Int]] = trySeq.map(_.toOption) // .toOption on Try transform failures to None
  val toIntSeq: Seq[Int] = trySeq.flatMap(_.toOption) // MOSTLY USED ELEGANT WAY

  // ---- Collecting the failure exceptions only - This is very much useful when we want to aggregate all failure exception and log them
  val toThrowableFromFailures: Seq[Throwable] = failures.map{ case Failure(exception) => exception }
  val toThrowableFromFailureUsingCollect: Seq[Throwable] = failures.collect{ case Failure(exception) => exception }

  println("----- Input Seq[Try] -----")
  println(trySeq)

  println("----- Separating out success and failure cases -----")
  println(s"toAllSuccesses: $toAllSuccesses")
  println(s"toAllFailures: $toAllFailures")
  println(s"val(successes, failures): $successes, $failures")

  println("----- Get only success values -----")
  println(s"toAllSuccesses.map(_.get): $toIntFromSuccess")
  println(s"toAllSuccesses.collect{ case Success(value) => value }: $toIntFromSuccessUsingCollect")
  println(s"toAllSuccesses.map{ case Success(value) => value }: $toIntByMapSuccess")
  println(s"trySeq.map(_.toOption): $toOptionSeq")
  println(s"trySeq.flatMap(_.toOption): $toIntSeq")

  println("----- Collecting the failure exceptions only ----")
  println(s"failures.map{ case Failure(exception) => exception }: $toThrowableFromFailures")
  println(s"failures.collect{ case Failure(exception) => exception }: $toThrowableFromFailureUsingCollect")

  /**
   * Program output
   * ---------------
   *
   * ----- Input Seq[Try] -----
   * List(Success(100), Failure(java.lang.NumberFormatException: For input string: "11a"), Success(350), Failure(java.lang.NumberFormatException: For input string: "500a0"), Success(1000))
   *
   * ----- Separating out success and failure cases -----
   * toAllSuccesses: List(Success(100), Success(350), Success(1000))
   * toAllFailures: List(Failure(java.lang.NumberFormatException: For input string: "11a"), Failure(java.lang.NumberFormatException: For input string: "500a0"))
   * val(successes, failures): List(Success(100), Success(350), Success(1000)), List(Failure(java.lang.NumberFormatException: For input string: "11a"), Failure(java.lang.NumberFormatException: For input string: "500a0"))
   *
   * ----- Get only success values -----
   * toAllSuccesses.map(_.get): List(100, 350, 1000)
   * toAllSuccesses.collect{ case Success(value) => value }: List(100, 350, 1000)
   * toAllSuccesses.map{ case Success(value) => value }: List(100, 350, 1000)
   * trySeq.map(_.toOption): List(Some(100), None, Some(350), None, Some(1000))
   * trySeq.flatMap(_.toOption): List(100, 350, 1000)
   *
   * ----- Collecting the failure exceptions only ----
   * failures.map{ case Failure(exception) => exception }: List(java.lang.NumberFormatException: For input string: "11a", java.lang.NumberFormatException: For input string: "500a0")
   * failures.collect{ case Failure(exception) => exception }: List(java.lang.NumberFormatException: For input string: "11a", java.lang.NumberFormatException: For input string: "500a0")
   *
   */

}
