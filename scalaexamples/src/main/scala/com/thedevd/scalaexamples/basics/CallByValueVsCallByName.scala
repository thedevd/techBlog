package scalaexamples.basics

import scala.util.{Random, Try}

object CallByValueVsCallByName {

  def callMeByValue(x: Int): Unit = {
    println(s"call by Value: $x")
    println(s"call by Value: $x")
  }

  def callMeByName(x: => Int): Unit = {
    println(s"call by Name: $x")
    println(s"call by Name: $x")
  }

  def main(args: Array[String]): Unit = {
    callMeByValue(Random.nextInt())
    callMeByName(Random.nextInt())

    Try(throw new NullPointerException) // valid because Try's apply method takes call by name as parameter

    /*
     * Output
     * -----------
     * call by Value: -671961561
     * call by Value: -671961561
     * call by Name: -195967228
     * call by Name: -683091752
     *
     * Now lets pay attention to the output, when we call method callMeByValue then same value gets printed twice
     * BUT when we call callMeByName then two different values get printed. Why is SO BECAUSE-
     * 1. At runtime the value to callMeByValue() method is evaluated before calling the method, so same value is printed
     * 2. But in case of callMeByName() method, the expression (See I am not using the term value) is evaluated each time it is used within the method
     *    It means Random.nextInt() is evaluated two times because we are using this two times in println().
     *    Also note that when you want to call a method by NAME, a special => sign is used which SAYS this method needs to be called by NAME.
     *
     *    Interestingly this is used in many places in scala library for example in Try(), and that is the reason we can write the Try() statement
     *    as Try(throw new NullPointerException()). And assume if Try's apply() method was defined as call be value then we would never been able to write
     *    Try(throw new NullPointerException()) because as we know that in call by value, the value to the method is evaluated before so in that case
     *    we would have got NullPointerException(). So internally Try object is defined like this -
     *
     *    object Try {
     *    // Constructs a `Try` using the by-name parameter.  This method will ensure any non-fatal exception is caught and a `Failure` object is returned
     *       def apply[T](r: => T): Try[T] =
     *         try Success(r) catch {
     *           case NonFatal(e) => Failure(e)
     *         }
     *    }
     *
     */

  }
}
