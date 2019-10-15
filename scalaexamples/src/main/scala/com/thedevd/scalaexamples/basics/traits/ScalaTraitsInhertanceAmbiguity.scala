package com.thedevd.scalaexamples.basics.traits

/*
 * If two traits have same methods and a class is extending them
 * then there might be ambiguity for the compiler, to resolve this
 * you need to override that method in the class, but you will be 
 * only able to call the method of last traits mixin in the chain.
 */
object ScalaTraitsInhertanceAmbiguity {
  
  def main(args: Array[String]): Unit = {
    val greetAndWelcome = new GreetAndWelcome("Dev")
    greetAndWelcome.sayHello() // Hello from Welcome
    
  }
  
  trait Greeting {
    def greet()
    def sayHello() = {
      println("Hello from Greeting")
    }
  }
  
  trait Welcome {
    def welcome()
    def sayHello() = {
      println("Hello from Welcome")
    }
  }
  
  class GreetAndWelcome(name: String) extends Greeting with Welcome {
    
    def greet() = {
      println("Greeting Mr. " + name)
    }
    
    def welcome() = {
      println("Welcome Mr. " + name)
    }
    
    /*
     * If we dont override sayHello() methods which is in both Greeting and Welcome traits,
     * then this creates an ambiguity error from the compilers point of view.
     * 
     * To overcome this, you need to explicitly override the method in the implementing class/trait,
     * and call the method using super. Note, super here is referring to the last trait in the mixin chain which
     * Welcome trait. So calling sayHello from GreetAndHellow will call method from Welcome trait.
     */
    override def sayHello() = {
      super.sayHello() // --> calling sayHello from the last trait (Welcome) in the mixin chain.
    }
    
  }
  
}