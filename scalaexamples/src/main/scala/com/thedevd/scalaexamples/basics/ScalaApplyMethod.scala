package com.thedevd.scalaexamples.basics

/*
 * When we create case class in scala, by-default we get a - > companion object with apply() method.
 * Internally this apply() method works as class factory to create instance. And due to this method,
 * we are allowed to create class object without using new keyword.
 * 
 * To allow creating object without new keyword, class must have its companion object with apply() implementation.
 * If it does not has apply(), then object has to be create using new keyword only
 * 
 * 
 */
object ScalaApplyMethod {

  def main(args: Array[String]): Unit = {

    /*
     * Person class is having companion object with apply() defined. 
     * Then one can create an object without calling the new keyword.
     * Internally, it is calling the apply method of Person companion object to create new Person object.
     */
    val person1 = Person("dev", 33)
    
    /*
     * we can not create object without using new, because for Employee, no apply() is available.
     * To create object, use new keyword explicitly
     */
    // val emp1 = Employee("dev",33) --> not allowed without new keyword
    val emp1 = new Employee("dev",33)
    
    /*
     * case class comes with apply() method, so object can be created without using new keyword
     */
    val student1 = Student("vikas",40)
    
    /*
     * See the magic of apply here, Although PersonRaw is expected to take two arguments separately for name and age,
     * but we are creating object using a raw string with name and age seperated by comma. So to create object
     * using this raw string we have defined companion object with apply() method, which is responsible to 
     * construct the name and age from raw string and return us expected PersonRaw object.
     */
    val specialPerson = PersonRaw("ravi,30") // --> equivalent to PersonRaw.apply("ravi,30")
    println(specialPerson) // name: ravi, age: 30
  }

  // Person class with companion Object having apply() method
  class Person(name: String, age: Int)
  object Person {
    def apply(name: String, age: Int): Person = {
      println("inside Person' apply")
      new Person(name, age)
    }
  }

  // Employee class with no companion Object, so no apply() available
  class Employee(name: String, age: Int)
  
  // case class comes with built-in apply() with companion object
  case class Student(name: String, age: Int)
  
  
  // Special class with Companion object
  class PersonRaw(name:String, age: Int) { override def toString() = "name: " + name + ", age: " + age}
  object PersonRaw {
    def apply(raw:String): PersonRaw = {
      val split = raw.split(",")
      new PersonRaw(split(0),split(1).toInt)
    }
  }
  
}