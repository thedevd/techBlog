package scalaexamples.basics.traits

/*
 * Abstract class and scala Trait both are used to achieve reusing/sharing common behaviour across different classes
 * through inheritance (extends). But there are some significant difference b/w them -
 *
 * 1. Classes are allowed to inherit from a single abstract class but can inherit from multiple traits. So
 *    multiple inheritance is possible using Scala Traits. (See ScalaTraitsInheritanceAmbiguity.scala to know how
 *    ambiguity is resolved using Traits when using multiple inheritance)
 *
 * 2. Abstract classes allow specifying constructor parameters, but it is not possible using Trait atleast till Scala 2,
 *    In scala 3.0, traits can have constructor parameter.
 *
 * Now the main question comes when to opt abstract class over trait and vice versa -
 * 1. Opt trait over abstract class - when there is need of multiple inheritance, for example -
 *    class Duck extends Animal with ATrait // Duck class now can have common behaviours from two traits which was not
 *                                          // with abstract class
 *
 *    Generally this is the main reason to go for trait, because in Complex class hierarchy mixing a trait in b/w is
 *    totally possible.
 *
 * 2. Opt abstract class over trait (In my experience this is when you are using scala 2.0) - Some times I felt the need
 *    of constructor parameter to my base class especially when using Compile time Dependency injection,
 *    That parameter could be a common resource which you want to use in Base class such as
 *    play.api.Configuration object implementation, or a WSClient object implementation.
 *
 *    abstract class BaseRepository(configuration: Configuration) {
 *       getAll(limit: Int = configuration.pagingLimit, offset: Int = configuration.pagingOffset)
 *    }
 *    class MyTableRepositoryImpl(configuration: Configuration, wsClient: WSClient) extends BaseRepository(configuration) {
 *    }
 *
 *
 */
object ScalaTraitVsAbstractClass {

  // Abstract class example
  abstract class AAbstractClass
  abstract class Person(name: String) { // Construct arguments are allowed wih abstract class
    def getDetails(): String
    def greetPerson(): String = s"Hi $name"
  }
  class Student(name: String, rollNo: Int) extends Person(name) {
    override def getDetails(): String = s"Student with name as: $name and rollNo as: $rollNo"
  }
  class Employee(name: String, empNo: Int, salary: Double) extends Person(name) {
    override def getDetails(): String = s"Employee with name as: $name, empNo as: $empNo and salary as: $salary"
  }

  /*
   * Extending multiple abstract classes not allowed
   * due to ambiguity of multiple inheritance but traits make this possible.
   * Compiler reports error saying -> Class 'AAbstractClass' needs to be trait to be mixed in
   *
   * class ZPerson(name: String) extends Person(name) with AAbstractClass
   */

  // Trait example
  trait ATrait
  trait Animal {
    def heartBeat(): Int = 42
    def sound(): String
  }
  class Cat extends Animal {
    override def sound(): String = "Meow"
  }
  class Lion extends Animal {
    override def sound(): String = "Roar"
  }
  class Duck extends Animal with ATrait { // multiple inheritance is possible using Trait
    override def sound(): String = "Quack"
  }
  /*
   * Parameterize trait is not allowed till scala 2, but in scala 3 it is allowed.
   * So scala 2 compiler will report error in this line
   * trait AParameterizedTrait(name: String) ------> Compile time error saying "Trait parameters require Scala 3.0"
   *
   * A class can extend any no of traits so multiple inheritance is possible using Trait
   * (See ScalaTraitsInheritanceAmbiguity to know more about this)
   *
   * class Duck extends Animal with ATrait {
   *  override def sound(): String = "Quack"
   * }
   */

  def main(args: Array[String]): Unit = {
    println("Scala Traits vs Abstract classes")
  }
}