package com.thedevd.scalaexamples.basics

/*
 * In java, when creating class you need to declare
 * member variables and their getter+ setter and constructor to initialize the object.
 * And also you need to override equals, toString or clone method when required.
 *
 * In scala, case class is shorthand to achieve all above in single line.
 * You just need to put case keyword to create case class -
 *
 *    case class Employee(name:String, salary:Double, age:Int)
 *
 * As I mentioned, with case class you get these things free -
 * 1. Built-n Companion object with factory like apply method. It is like constructor.
 * 2. Class parameters becomes member variables
 * 3. A nice implemented toString
 * 4. hashCode and equals for object equality
 * 5. Can be used in pattern matching
 * 6. Copy method	to clone instances
 *
 * Drawback of case class with Inheritance
 * #########################################
 * Case-to-case inheritance in not allowed, So Case Class can NOT extend another Case class.,
 * but Case Class can extend another Class/Abstract Class/trait .. (Even though case class can be declared abstract)
 * 
 * case class A (a:Int)
 * case class B(a:Int, b: Int) extends A(a) -------> case-to-case inheritance not supported
 * 
 * class C(c: Int) --> normal class
 * case class B(c:Int, b: Int) extends C(c) ---> Supported
 * 
 * abstract case class AbstractCaseClass(z: Int) ---> case class can be abstract
 * 
 */
object CaseClasses {

  def main(args: Array[String]): Unit = {

    // creating object and printing it with built-in nice toString()
    val emp1 = Employee("Dev", 5000, 34, Address("noida", "up", 123456))
    println(emp1) // --> Employee(Dev,5000.0,34,Address(noida,up,123456))

    // checking equality
    val checkEmp = emp1.copy(emp1.name, emp1.salary, emp1.age, emp1.address)
    //val checkEmp = Employee("Dev", 5000, 34, Address("noida","up",123456))
    println(emp1 == checkEmp) // --> true

    // emp1.name = "ravi" ---> error because by default parameters in case class are val

    // Using in Pattern matching
    val pMatching = (e: Employee) => {
      e match {
        case Employee("Dev", 5000, 34, Address("noida", "up", 123456)) => "Admin"
        case _ => "User"
      }
    }
    val role = pMatching(emp1)
    println(role) // --> Admin
  }

  case class Employee(name: String, salary: Double, age: Int, address: Address)
  case class Address(city: String, state: String, pincode: Int)
  
}