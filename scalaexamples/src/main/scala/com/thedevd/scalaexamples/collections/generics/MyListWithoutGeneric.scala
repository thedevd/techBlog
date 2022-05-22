package main.scala.com.thedevd.scalaexamples.collections.generics

/*
 * Here we have tried to use basic inheritance to create our own List (where elements are added to head).
 * But we have made this custom list only works for Int type for a reason which is to showcase the use
 * of generics.
 *
 * So ideally if we want the same List to be worked for String as well, then we will end up duplicating the entire
 * piece of code of String type as well.
 * This is where GENERICS are used. We will try to make this MyList work with any type i.e. it will work for
 * Int, Long, String, Double, Custom class etc...
 */
object MyListWithoutGeneric {

  /*
    head = first element of  the  list
    tail = remainder of the list
    isEmpty = is this list empty
    add(int) => new list with this element added to head position
    toString => a string representation of the list
  */

  abstract class MyList {
    def head: Int

    def tail: MyList

    def isEmpty: Boolean

    def add(n: Int): MyList

    def printElements: String

    override def toString: String = "[" + printElements + "]"
  }

  class MyEmptyList extends MyList {
    def head: Int = throw new NoSuchElementException

    def tail: MyList = throw new NoSuchElementException

    def isEmpty: Boolean = true

    def add(n: Int): MyList = new MyNonEmptyList(n, new MyEmptyList)

    def printElements: String = ""
  }

  class MyNonEmptyList(h: Int, t: MyList) extends MyList {
    def head: Int = h

    def tail: MyList = t

    def isEmpty: Boolean = false

    def add(n: Int): MyList = new MyNonEmptyList(n, this)

    def printElements: String = {
      if (t.isEmpty) h.toString
      else h + " " + t.printElements
    }
  }

  def main(args: Array[String]): Unit = {
    val myNonEmptyList: MyNonEmptyList = new MyNonEmptyList(1, new MyEmptyList)
    println(myNonEmptyList.toString) // [1]
    println(myNonEmptyList.add(0)) //[0 1]
    println(myNonEmptyList.add(2).head) // 2
  }
}
