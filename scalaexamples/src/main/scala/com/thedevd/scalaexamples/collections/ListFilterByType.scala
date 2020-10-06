package main.scala.com.thedevd.scalaexamples.collections

/* Sometimes when the list or seq has different type of elements stored
 * And you want to separate out a specific type of elements from collection,
 * then collect method can help you with this.
 *
 * In this example we will see how to use collect method to collect specific type of
 * element.
 */
object ListFilterByType {

  def main(args: Array[String]): Unit = {
    println("######## Example1 #########")
    example1()

    println()
    println("######## Example2 #########")
    example2()

    /*
     * Output
     * ######## Example1 #########
     * only Employees: List(Employee(1), Employee(2), Employee(3))
     * Only Accounts: List(Account(1000,1), Account(1001,2), Account(1002,3))
     *
     * ######## Example2 #########
     * using collect with diff: List(C(C), D(D))
     * Using flatmap: List(C(C), D(D))
     */

  }

  def example1(): Unit = {
    // lets create collection having different elements type
    val collection: Seq[Any] = Seq(Employee(1), Account(1000L, 1), Employee(2), Account(1001, 2), Employee(3), Account(1002, 3))
    val onlyEmployee: Seq[Employee] = collection.collect { case e: Employee => e}
    val onlyAccount: Seq[Account] = collection.collect { case a: Account => a}

    println("only Employees: " + onlyEmployee)
    print("Only Accounts: " + onlyAccount)
  }

  def example2(): Unit = {
    // I have a collection of the various instances,
    // and I want to filter out some of the subclasses from the List such as B and E
    // means I want only other type elements i.e. C and D type element
    val subClassElements = Seq(B("B"), C("C"), D("D"), E("E"), B("BB"), E("EE"))

    // get all elements except B and E
    val output1 = subClassElements diff subClassElements.collect { case x@(_:B | _:E) => x}
    println("using collect with diff: " + output1)

    val output2 = subClassElements.flatMap {
      case _:B | _:E => None
      case other => Some(other)
    }
    println("Using flatmap: " + output2)

  }

}

case class Employee(id: Long)
case class Account(accountNo: Long, empId: Long)

abstract class A
case class B(name: String) extends A
case class C(name: String) extends A
case class D(name: String) extends A
case class E(name: String) extends A