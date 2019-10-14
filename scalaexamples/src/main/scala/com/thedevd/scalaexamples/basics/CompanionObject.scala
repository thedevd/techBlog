package com.thedevd.scalaexamples.basics

object CompanionObject {

  def main(args: Array[String]): Unit = {

    val e1 = new Employee("Dev Vishwakarma", 23500)
    val e2 = new Employee("Ravi Shekhar", 35000)

    println(e1) // [empId:1, name:Dev Vishwakarma, salary:23500.0]
    println(e2) // [empId:2, name:Ravi Shekhar, salary:35000.0]
  }

  
  class Employee(name: String, salary: Double) {
    var empId = Employee.getNextId()
    // Employee.id ---> Companion class can access private member of Companion object

    override def toString = "[empId:" + empId + ", name:" + name + ", salary:" + salary + "]"
  }

  /*
   *  object means singleton object. 
   *  If the object name is same as class Name and both class and object is in the same source file
   *  then object is called Companion object and the class is called Companion Class.
   *  
   *  Usage of companion object -
   *  1. Way to implement singleton pattern
   *  2. It is more closely to java static means it can be used to store static kind of resources like constants.
   *  3. Used to store some common utility methods that all the objects of Class is going to be used,
   *     As in this example we have created a utility to assign the empId to the object of Employee class.
   *  4. It can also be used as Class factory means to instantiate objects of the class based on some input
   */
  object Employee {  // This is Companion Object
    private var id = 0
    def getNextId() = {  // a utility method
      id += 1
      id
    }
  }
}


