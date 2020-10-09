package main.scala.com.thedevd.scalaexamples.basics

/*
 * A case class can take arguments,
 * so each instance of that case class can be different based on the values of it's arguments
 *
 * A case object on the other hand does not take args in the constructor,
 * so there can only be one instance of it (a singleton, like a regular scala object is).
 *
 * case object is widely used with an AKKA actor.
 * If your message to your actor does not need any value differentiation, use a case object.
 * class CounterDemo extends Actor {
 *   var count = 0
 *   override def receive: Receive = {
 *     case IncrementBy(num) => count += num
 *     case DecrementBy(num) => count -= num
 *     case Print => println(count)    // case object
 *   }
 * }
 *
 * object CounterDemo {
 *   case class IncrementBy(num: Int)
 *   case class DecrementBy(num: Int)
 *   case object Print     // case object
 * }
 * }
 */
object CaseClassVsCaseObject {

  def main(args: Array[String]): Unit = {
    greet(SayHelloTo("Dev"))
    greet(SayHelloTo("Ravi"))
    greet(SayHello)
  }

  def greet(opType: Any): Unit = {
    opType match {
      case SayHelloTo(name) => println(s"Hello $name")
      case SayHello => println("Hello")
    }
  }

  case class SayHelloTo(name: String)
  case object SayHello
}

/*
 * Output
 * -----------------
 * Hello Dev
 * Hello Ravi
 * Hello
 */
