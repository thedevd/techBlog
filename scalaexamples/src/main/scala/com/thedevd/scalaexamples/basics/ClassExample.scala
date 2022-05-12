package main.scala.com.thedevd.scalaexamples.basics

/*
 * Class in Scala is just like other languages which acts as blueprint and then we can create multiple objects
 * using that blueprint. Lets see some of the important key points about a class in scala -
 *
 * 1. Like other languages class keyword is used to create class and then new keyword is used to create objects -
 *    class Person
 *    val person1 = new Person
 * 2. Scala class can have class parameters like -
 *    class Person(name: String, age: Int)
 *    val person1 = new Person("Ravi", 40)
 *
 *    Note- Class parameters are not class fields, I mean we can not access class parameters using dot operator so
 *          person1.name ---> is not possible
 *
 *          So if you put "val" or "var" in-front of class parameter they become class fields and then you can access them
 *          using dot(.) operator. I meant
 *          class Person(val name: String, age: Int)
 *          val person1 = new Person("Ravi", 45)
 *          person1.name ----> now it is possible
 *
 * 3. Class definition with parameters/fields itself is considered a constructor, so you do not need to define any
 *    constructor with Class body, so
 *    class Person(name: String, age: Int) --> this line itself is constructor that is why you are able to create objects
 *    using new keyword
 *
 * 4. Scala class can have parameters with default value
 *    class Person(name: String, age: Int = 18)
 *    val person1 = new Person("Adult")
 *    val person2 = new Person("Teenager", 15)
 *
 */

object ClassExample {

  def main(args: Array[String]): Unit = {
    val person1: PersonWithoutParameters = new PersonWithoutParameters

    val person2: PersonWithParameters = new PersonWithParameters("Ravi", 45)
    // person2.name ----> Not possible because name is parameter not field

    val person3: PersonWithFields = new PersonWithFields("Ankit", 35)
    println(s"person3.name ${person3.name}") // Ankit
    // person3.name ----> possible because name is field variable

    val person4: PersonWithDefaultParamValue = new PersonWithDefaultParamValue("Adult")
    val person5: PersonWithDefaultParamValue = new PersonWithDefaultParamValue("Teenage", 16)
    println(s"person4.age ${person4.age}") // 18
    println(s"person5.age ${person5.age}") // 16

    val person6: PersonWithClassBody = new PersonWithClassBody("Atul", 70, "female") // called auxilary constructor
    println(s"person6.gender ${person6.gender}")
    person6.greet() // Atul says hello
    person6.greet("Ravi") // Atul says hi to Ravi

  }

  // class without parameter
  class PersonWithoutParameters

  // class with parameters (to be NOTED, class parameter and class fields variable is different, parameter CANNOT be accessed using dot operator)
  class PersonWithParameters(name: String, age: Int) // Itself a constructor

  // class with fields (just put val or var) so parameter becomes field, so class field can be accessed using dot operator outside
  class PersonWithFields(val name: String, age: Int)

  // class with default parameter
  class PersonWithDefaultParamValue(name: String, val age: Int = 18)

  // class with class body
  class PersonWithClassBody(name: String, age: Int) {
    // field variable
    var gender: String = "" // ---> This is called Class fields, this is another way of having class field variables

    // method
    def getGender(): String = gender // --> We can have class methods inside body

    // method overloading possible - compiler checks only the method arguments, NOT Return type
    def greet(name: String): Unit = println(s"${this.name} says hi to $name")
    def greet(): Unit = println(s"${this.name} says hello")
    // def greet(): String = s"${this.name} says hello" ---> This is now allowed because greet() is already defined above, so no matter return type is different here.

    // ---> Auxilary constructor, TO BE NOTED, first statement in any auxilary constructor must be a main constructor
    // or any other existing auxilary constructor, and this is one of the downside of using auxilary constructor which
    // can be avoided by using default parameter in class such as -> class Person(name: String, age: Int, gender: String = "")
    def this(name: String, age: Int, gender: String) = {
      this(name, age)
      this.gender = gender // --> Note I have to use "this" to distinguish that left hand side gender is for class variable
    }
  }

}
