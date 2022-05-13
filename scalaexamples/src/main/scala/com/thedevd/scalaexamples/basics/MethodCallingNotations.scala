package main.scala.com.thedevd.scalaexamples.basics

/*
 * 1. Infix notation =>
 *      - Seq(1,2,3) equals Seq(1,2,3) ===> equivalent to  ===> Seq(1,2,3).equals(Seq(1,2,3))
 *      - 1 + 2 ===> equivalent to ===> 1.+(2)
 *        (TO BE NOTED all operators in Scala are actually methods because in scala every thing is object including 1 or 2 here, so 1 is basically Int)
 * 2. Prefix notation =>
 *      - val x = -1 ===> equivalent to ===> 1.unary_!
 *        (TO BE NOTED that unary_ prefix only works with operators - + ~ !
 * 3. Postfix notation =>
 *      - Seq(1,2,3) isEmpty
 *        (TO BE NOTED that this notation is only available with the scala.language.postfixOps import - which is discouraged to use
 *
 * There is a Beautiful thing in scala w.r.t. operator that OPERATORS LIKE +,*,!,? can be used as METHOD NAME.
 * For example - in below given class Person, we have defined method +(person: Person)
 *
 * Similarly we have lot of places where Scala API itself uses this a lot, for example in AKKA we have method names as
 * ! (tell method) and
 * ? (ask method)
 *
 * apply() method
 * ----------------
 * There is interesting method that each class can have by which is apply() method,
 * so if we put parenthesis () just after object, then internally its a indication to call apply() method
 *  val person1: Person = new Person("ravi", "Pizza", 45)
 *  person() --> this calls apply() method
 *
 * NOTE - generally apply() method is widely used by scala case class by default when we create object.
 * See - ScalaApplyMethod.scala
 */
object MethodCallingNotations {

  def main(args: Array[String]): Unit = {

    val ravi: Person = new Person("Ravi", "Pizza", 45)
    val ankit: Person = new Person("Ankit", "Chicken", 35)

    // infix notation
    println(ravi likesFood "Pizza")
    println(ravi.likesFood("Pizza")) // These both are equivalent

    println(ravi + ankit)
    println(ravi.+(ankit)) // Here + is nothing but a method in Person class so possible to use with . operator as well

    println(1 + 2)
    println(1.+(2))
    println(1.==(1)) // ALL OPERATORS are actually methods, because everything in scala is object, so 1 is also a Int class object

    // prefix notation
    val x = -1
    val y = 1.unary_- // So internally -1 can be represented as prefix notation as 1.unary_-

    println(!ravi)
    println(ravi.unary_!) // !ravi which is prefix notation is equivalent of calling unary_! method like ravi.unary_!

    // postfix notation
    // only available with the scala.language.postfixOps import - but it is discouraged
    import scala.language.postfixOps
    println(ravi.isAdult)
    println(ravi isAdult) // this postfix method calling notation is equivalent to ravi.isAdult

    // apply() method
    println(ravi.apply())
    println(ravi()) // this is equivalent to ravi.apply()
    println(ravi(5) isAdult) // this is equivalent of calling to apply method which takes one Int argument i.e. ravi.apply(5)

  }

  class Person(val name: String, favouriteFood: String, age: Int = 18) {
    def likesFood(food: String): Boolean = favouriteFood == food
    def +(person: Person): String = s"$name has a friendship with ${person.name}" // See operator can be used as method name in scala
    def eats(food: String): String = s"$name is eating $food"
    def eatsPizza(): String = this eats "Pizza" // see how we used infix notation

    def unary_! : String = s"$name says what the heck is this unary_!, I says its way to use prefix notation"
    def unary_+ : Person = new Person(name, favouriteFood, age + 1)
    def unary_- : Person = new Person(name, favouriteFood, age -1)

    def isAdult: Boolean = age >= 18

    def apply(): String = s"Hi I am $name, and I likes $favouriteFood"
    def apply(n: Int): Person = new Person(name, favouriteFood, n)
  }

}
