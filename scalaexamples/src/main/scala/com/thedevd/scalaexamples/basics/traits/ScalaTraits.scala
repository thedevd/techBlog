package com.thedevd.scalaexamples.basics.traits

/*
 * Traits in scala are like java Abstract and Interface as they can allow to
 * have abstract methods along with concrete methods. Traits can also have field members.
 * 
 * Traits can not be instantiated and so you can not have traits with constructor parameters like this
 * trait invalidTrate(var1:String, var2:Int) { } ------- this is invalid 
 * Note- But from scala 3 parameterized traits are possible.
 *
 */
object ScalaTraits {

  def main(args: Array[String]): Unit = {
    val greetAndWelcome = new GreetAndWelcome("Dev")
    greetAndWelcome.greet() // Greeting Mr. Dev
    greetAndWelcome.greetDefault() // Greeting sir!
    greetAndWelcome.welcome() // Welcome Mr. Dev
    
    // Extending trait anonymously using two curly braces
    val specialTrait = new SpecialTrait {}
    specialTrait.message() // I am special trait having only complete method
  }

  // Trait1
  trait Greeting {
    def greet() // abstract method
    def greetDefault() = { // non-abstract method
      println("Greeting sir!")
    }
  }

  // Trait2
  trait Welcome {
    def welcome() // abstract method
  }

  /*
   * Class extending traits has to provide implementation of abstract methods otherwise should be defined as abstract
   * 
   * Traits support multiple inheritance =>
   * When a class needs to inherits multiple traits then use extends keyword before the first trait and after that use with keyword 
   * before other traits. 
   *   i.e. class className extends trait1 with trait2 with trait3 {}
   */
  class GreetAndWelcome(name: String) extends Greeting with Welcome {
    def greet() = {
      println("Greeting Mr. " + name)
    }

    def welcome() = {
      println("Welcome Mr. " + name)
    }
  }
  
  
  /*
   * Trait having only complete methods can be extends internally 
   * by anonymous class using two curly braces i.e.
   *   val specialTrait = new SpecialTrait { } 
   *   specialTrait.message()
   *   
   * So trait having alteast one abstract can not be extended like this.
   */
  trait SpecialTrait {
    def message() = println("I am special trait having only complete method")
  }
}

