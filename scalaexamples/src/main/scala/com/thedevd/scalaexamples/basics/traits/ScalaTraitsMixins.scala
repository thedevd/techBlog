package com.thedevd.scalaexamples.basics.traits

/*
 * What is Scala Trait Mixins?
 * ###########################
 * In scala, a class can extends any number of scala traits with a concrete class or abstract class.
 * But when mixing traits with abstract class we should maintain order of mixing, otherwise
 * compiler will throw an error saying - 
 *    class <className> needs to be a trait to be mixed in.
 *
 * The order must be -
 * first extend any concrete class or abstract class  and then extend any number of traits.
 */
object ScalaTraitsMixins {
  
  def main(args: Array[String]): Unit = {
    val greetAndWelcome = new GreetAndWelcome("Dev")
    greetAndWelcome.greet() // Greeting Mr. Dev
    greetAndWelcome.welcome() // Welcome Mr. Dev
  }

  // a trait
  trait Greeting {
    def greet()
    def greetDefault() = {
      println("Greeting sir!")
    }
  }
  
  // An abstract class
  abstract class Welcome {
    def welcome()
  }
  
  
  // class extending abstract class and then trait
  // class GreetAndWelcome(name: String) extends Greeting with Welcome ---> this is invalid as the order of mixing is not correct
  class GreetAndWelcome(name: String) extends Welcome with Greeting {
    
    def greet() = {
      println("Greeting Mr. " + name)
    }
    
    def welcome() = {
      println("Welcome Mr. " + name)
    }
  }
}

