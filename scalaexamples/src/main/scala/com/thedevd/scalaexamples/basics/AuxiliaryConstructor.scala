package com.thedevd.scalaexamples.basics

/*
 * A Scala class can contain two kinds of constructors:
 * 1. Primary Constructor
 * 2. Auxiliary Constructor
 * 
 * What is an Auxiliary Constructor -
 * In Scala, Auxiliary Constructors are used to provide Constructors Overloading.
 * However need of auxiliary constructor can be avoided using default value in class parameters.
 * 
 * How to define auxiliary constructor-
 * using "def" and "this" keywords. "this" is the constructor name. We can have more than one auxiliary constructor.
 *   class A(a:Int, b:Int) {
 *     def this( this(0,0)) {}
 *     def this(a:Int) { this(a,0)}
 *   }
 * 
 * Note- An Auxiliary Constructor must call either Primary Constructor or another previously define Auxiliary constructors 
 * by using "this" name.
 * 
 * Using scala's default class parameters, we can create auxiliary constructors internally without need to define them explicitly
 * See the Employee and Employee2 in this example
 */
object AuxiliaryConstructor {

  def main(args: Array[String]): Unit = {

    /*
     * ######################################################
     * Constructor Overloading using auxiliary constructors
     */
    val emp1 = new Employee()
    println(emp1)
    /*
     * Call from primary constructor
     * Call from zero-argument auxiliary constructor
     * Employee [id=0, name=n/a, age=21]
     * 
     */
    
    val emp2 = new Employee("dev")
    println(emp2)
    /*
     * Call from primary constructor
     * Call from one-argument auxiliary constructor
     * Employee [id=0, name=dev, age=21]
     */
    
    
    val emp3 = new Employee(1,"dev")
    println(emp3)
    /*
     * Call from primary constructor
     * Call from two-argument auxiliary constructor
     * Employee [id=1, name=dev, age=21]
     */
    
    val emp4 = new Employee(1,"dev", 30)
    println(emp4)
    /*
     * Call from primary constructor
     * Employee [id=1, name=dev, age=30]
     */
    
    
    /*
     * #########################################################################
     *  Constructor Overloading using only PrimaryConstructor with default value
     */
    val emp5 = new Employee2()
    /*
     * Call from Primary Constructor
     * Employee2 [id=0, name=n/a, age=21]
     */
    
    val emp6 = new Employee2(1)
    /*
     * Call from Primary Constructor
     * Employee2 [id=1, name=n/a, age=21]
     */
    
    val emp7 = new Employee2(1,"dev")
    /*
     * Call from Primary Constructor
     * Employee2 [id=1, name=dev, age=21]
     */
    
    val emp8 = new Employee2(1,"dev", 30)
    /*
     * Call from Primary Constructor
     * Employee2 [id=1, name=dev, age=30]
     */
  }

  class Employee(id: Int, name: String, age: Int) {
    println("Call from primary constructor")

    def this() {
      this(0, "n/a", 21) // auxiliary constructor must first call primary or other auxiliary constructor
      println("Call from zero-argument auxiliary constructor")
    }

    def this(name: String) {
      this(0,name,21)
      println("Call from one-argument auxiliary constructor")
    }

    def this(id: Int, name: String) {
      this(id, name, 21)
      println("Call from two-argument auxiliary constructor")
    }
    
    override def toString() = {
      "Employee [id=" + id + ", name=" + name + ", age=" + age + "]"
    }

  }
  
  // constructor overloading using primary constructor only along with default value
  // default value means they are optional to provide
  class Employee2(id: Int = 0, name: String = "n/a", age:Int = 21) {
    println("Call from Primary Constructor")
    println(this)
    
    override def toString() = {
      "Employee2 [id=" + id + ", name=" + name + ", age=" + age + "]"
    }
    
  }
}

