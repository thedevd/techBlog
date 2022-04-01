package main.scala.com.thedevd.scalaexamples.collections

/* Problem statement -
 * ---------------------
 * Some times we struggle to compare two sequences especially in the case if those sequences containing
 * the same elements but in different order. i.e.
 * Seq("1", "2") vs Seq("2", "1")           => true
 * Seq("3", "1", "2") vs Seq("2", "1", "3") => true
 * Seq("1", "1", "2") vs Seq("2", "1")      => true
 *
 *
 * So the question we ask ourselves is -
 * " Is there a way in Scala to compare two sequences in a way that it returns true if it contains the same elements,
 * regardless of order and repetitions? "
 *
 * Solution -
 * ------------
 * Convert to sets and compare those.
 *
 */
object SequenceComparison {

  def main(args: Array[String]): Unit = {
    // Sample input data
    println(Seq("1", "2") == Seq("2", "1")) // false
    println(Seq("1", "2").toSet == Seq("2", "1").toSet) // true

    println(Seq(3, 1, 2) == Seq(2, 1, 3)) // false
    println(Seq(3, 1, 2).toSet == Seq(2, 1, 3).toSet) // true

    println(Seq("1", "1", "2") == Seq("2", "1")) // false
    println(Seq("1", "1", "2").toSet == Seq("2", "1").toSet) // true

    // Comparison on User defined class
    println(Seq(Person("A", 10), Person("Z", 20)) == Seq(Person("Z", 20), Person("A", 10))) // false
    println(Seq(Person("A", 10), Person("Z", 20)).toSet == Seq(Person("Z", 20), Person("A", 10)).toSet) // true
  }

  case class Person(name: String, age: Int)
}
