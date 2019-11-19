package com.thedevd.scalaexamples.basics

/*
 * Before this, read ScalaApplyMethod.scala to know about apply() method.
 *
 * What is extractor in scala
 * ############################
 * Just opposite of apply(), scala has unapply() method.
 * apply()-  method takes parameters and turns them into an object where as
 * unapply() - method takes and object(Most of the time String object) and extract values from it.
 *             These values are the same from which object was constructed in apply().
 *
 * Any object that implements unapply() method is called Extractor and they are widely used in pattern matching.
 * Extractor provides the additional ways of pattern matching against a specific object's data.
 * Note- A case class in Scala, by default implements these apply and unapply methods.
 *
 * unapply() method always returns Option[T] type, means it return
 *   either Some[T] - if it can successfully extract the parameters from the given object
 *   or None - if parameters can not be extracted from the given object.
 *   
 * unapply() method is mostly used when we want to do pattern matching on a string and that
 * string represents the object state. (We will look this in demo). 
 *    case class Student(name: String, age: Int)
 *    val s1 = Student("dev",28)
 *    s1 match {
 *    	case Student(name,age) => println(s"Topper in class: name=$name, age=$age")
 *    	case _ => println("Not a student")
 *    }
 *    
 *    So you can see here, s1 is matched against its case class by constructor pattern matching. so this way
 *    is straigtforward to extract values and print. But what if we want pattern matching for a string
 *    representing Student object, I mean val s1= "dev,28". To achieve this we need to use Extractor concept.
 */
object ScalaExtractors {

  def main(args: Array[String]): Unit = {

    example1_extractFromObject()
    example2_extractFromString()

  }

  def example1_extractFromObject() {

    val personDev = Person("dev", 28) // internally calls apply() method
    // val personDev = Person.apply("dev",28)

    val Person(p_name, p_age) = personDev // internally call unapply() method
    println(s"Person [name: $p_name, age: $p_age]") // --> Person [name: dev, age: 28]

    // Using the same in pattern matching
    Person("dev", 18) match {
      case Person(p_name, p_age) => println(s"Person [name: $p_name, age: $p_age]")
      case _ => println("Invalid person as age can not be zero")
    } // --> Person [name: dev, age: 28]
    
    Person("ravi", 0) match {
      case Person(p_name, p_age) => println(s"Person [name: $p_name, age: $p_age]")
      case _ => println("Invalid person as age can not be zero")
    } // --> Invalid person as age can not be zero

    /*
     * unapply() already implemented in case class
     */
    val studentDev = Student("dev", 28)
    val Student(s_name, s_age) = studentDev
    println(s"Student [name: $s_name, age: $s_age]") // --> Student [name: dev, age: 28]

  }

  def example2_extractFromString() {

    val e1 = "dev,28"
    println(Employee.unapply(e1)) // --> Some((dev,28))
    
    // unapply() making pattern matching possible on String object
    e1 match { 
      case Employee(e_name,e_age) => println(s"Employee [name:$e_name, age:$e_age]")
      case _ => println("Invalid Employee")
    } // --> Employee [name:dev, age:28]
    
    "ravi" match {
      case Employee(e_name,e_age) => println(s"Employee [name:$e_name, age:$e_age]")
      case _ => println("Invalid Employee")
    } // --> Invalid Employee
    
    // Even though partial matching can also be done
    "ankit,75" match {
      case Employee(_,75) => println(s"Employee is senion citizen")
       case _ => println("Invalid Employee")
    }
  }

  // Person object is an extractor here which takes Person object and extract values from it.
  class Person(var name: String, var age: Int)
  object Person {
    def apply(name: String, age: Int): Person = new Person(name, age)

    def unapply(person: Person): Option[(String, Int)] = {
      if (person.age == 0) None
      else Some((person.name, person.age))
    }
  }

  // case class by default implements apply and unapply methods.
  case class Student(name: String, age: Int)

  // Employee object is an Extractor here, which is extracting value from String represented object
  class Employee(name: String, age: Int)
  object Employee {
    def apply(name: String, age: Int): Employee = new Employee(name, age)
    
    def unapply(employee: String): Option[(String, Int)] = {
      val tokens = employee split "," // employee.split(",")
      if (tokens.length < 2 || tokens(1).toInt == 0) None
      else Some((tokens(0), tokens(1).toInt))
    }
  }
}