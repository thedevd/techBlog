package main.scala.com.thedevd.scalaexamples.basics
/* Anonymous class can allow to instantiate or I would say provide implementation for trait, abstract class or concrete
 * class on the spot. For example say we have an abstract class as -
 *
 * abstract class Animal {
 *  def makeSound: Unit
 * }
 *
 * so using anonymous class we can provide implementation of above class on the spot like -
 * val cat = new Animal {
 *  def makeSound: Unit = println("Meowww")
 * }
 *
 * Sometimes above syntax confuses the beginners because it looks like you are creating instance of abstract class which
 * is not possible. But it is like extending Animal class internally.
 *
 * The only thumb rule of using anonymous class for abstract class and trait is, implement all abstract fields/methods.
 *
 */
object AnonymousClass extends App {

  // ############### Anonymous class with abstract
  abstract class Animal {
    def makeSound: Unit
  }

  val cat = new Animal {
    override def makeSound: Unit = println("Meowww")
  }
  /*
   * Internally above line is interpreted as like you are extending Animal class
   * class AnonymousClass$$animal$1 extends Animal {
   *  def makeSound: Unit = println("Meowww")
   * }
   */
  cat.makeSound // Meoww

  // ############### Anonymous class with trait
  trait Vehicle {
    val isFourWheeler: Boolean
    val isTwoWheeler: Boolean
  }

  val hondaActiva = new Vehicle {
    override val isFourWheeler: Boolean = false
    override val isTwoWheeler: Boolean = true
  }
  val vitaraBrezza = new Vehicle {
    override val isFourWheeler: Boolean = true
    override val isTwoWheeler: Boolean = false
  }
  println(hondaActiva.isTwoWheeler) // true
  println(vitaraBrezza.isFourWheeler) // true

  // ############### Anonymous class with Concrete class
  class Person(name: String) {
    def sayHi: Unit = println(s"Hi I am $name")
  }

  val ravi = new Person("ravi") {
    override def sayHi: Unit = println(s"Hi goodmorning, I am a spy cant reveal my name")
  }
  ravi.sayHi // Hi goodmorning, I am a spy cant reveal my name

}
