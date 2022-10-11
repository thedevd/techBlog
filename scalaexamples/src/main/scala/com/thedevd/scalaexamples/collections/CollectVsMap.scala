package main.scala.com.thedevd.scalaexamples.collections
import scala.util.Try

/**
 * We can replace filter + map/flatMap operation by collect method, because collect supports pattern matching with if condition.
 *
 * In the example #1 below where we have a list of elements where some can be numbers and some can not be.
 * So one simpler way which comes in our mind is, first filter the list with elements that can be casted to Int,
 * and then apply map to transform them into Int using .toString.toInt.
 *      .filter(x => Try(x.toString.toInt).isSuccess).map(_.toString.toInt)
 *
 * As I mentioned above that - Everytime you have chaining filter with map, you can replace with collect.
 * So that is what we did in 'onlyNumbersUsingCollect'
 *      .collect { case possibleNumber if Try(possibleNumber.toString.toInt).isSuccess => possibleNumber.toString.toInt }
 *
 * Note- If you have read my com.thedevd.scalaexamples.functions.PartialFunctionsInScala, then you will notice that
 * in collect we kind have same case with if condition. It means collect method takes a PartialFunction as an argument
 * and maps only range of values using partial function and, skipping others according to if condition.
 *
 * I have provided total 3 examples to demonstrate usage of collect -
 * Example #1 - Filter only numbers
 * Example #2 - Doubling only even numbers
 * Example #3 - Getting word representation from number
 *
 * Example #3 is very interesting usecase of Collect
 * -----------------------------------------------------
 * In this we have a simple map - Map(1 -> "One", 5 -> "Five"). So we know that -
 * 1. If we want to retrieve an item from this map, then we can use get and but this gives us an Option -
 *    aMap.get(1) // Some(1)
 *    aMap.get(20) // None
 *
 *    And furthermore, to get actual value we can use get again to see the actual value, But we may see a magical error i.e.
 *    aMap.get(1).get // 1
 *    aMap.get(20).get // java.util.NoSuchElementException
 *
 * 2. So if we want to get list of items from this map then we can apply here filter first to check if map has that element,
 *    and then can get the actual value i.e.
 *        Seq(1,2,3,4,5).filter(n => aMap.contains(n)).flatMap(n => aMap.get(n))
 *
 *    And same thing is written using collect methid like this -
 *        Seq(1,2,3,4,5).collect{ case number if aMap.contains(number) => aMap.get(number)}.flatten
 *
 *    Interestingly, a nice alternative to write above line using collect is 0
 *        Seq(1,2,3,4,5).collect(aMap)
 *
 */
object CollectVsMap extends App {

  // ---- Example #1
  val possibleNumbers: Seq[Any] = Seq(1, "2", "a", "b", "100")
  val onlyNumbers: Seq[Int] = possibleNumbers.filter(x => Try(x.toString.toInt).isSuccess).map(_.toString.toInt)
  // List(1, 2, 100)

  val onlyNumbersUsingCollect: Seq[Int] = possibleNumbers.collect {
    case possibleNumber if Try(possibleNumber.toString.toInt).isSuccess => possibleNumber.toString.toInt
  }// List (1, 2, 100)

  // ################################################################################################################
  // ---- Example #2
  val numbers: Seq[Int] = Seq(1,2,3,4,5)
  val doubleOnlyEvenNumbers: Seq[Int] = numbers.filter(_ % 2 == 0).map(_ * 2) // List(4, 8)
  val doubleOnlyEvenNumbersUsingCollect: Seq[Int] = numbers.collect{ case number if number % 2 == 0 => number * 2 } //List(4, 8)

  // ################################################################################################################
  // ---- Example #3
  val aMap: Map[Int, String] = Map(1 -> "One", 5 -> "Five")
  val numberToWord: Seq[String] = numbers.filter(n => aMap.contains(n)).flatMap(n => aMap.get(n))
  val numberToWorkUsingCollect: Seq[String] = numbers.collect{ case number if aMap.contains(number) => aMap.get(number)}.flatten
  val mostNiceWay: Seq[String] = numbers.collect(aMap)

  println(s"numberToWord: $numberToWord") // List(One, Five)
  println(s"numberToWorkUsingCollect: $numberToWorkUsingCollect") // List(One, Five)
  println(s"mostNiceWay: $mostNiceWay") // List(One, Five)

}
