package main.scala.com.thedevd.scalaexamples.collections.generics

object MyListWithGeneric extends App {

  abstract class MyList[+A] {
    def head: A
    def tail: MyList[A]
    def isEmpty: Boolean
    def add[B >: A](element: B): MyList[B]
    def printElements: String
    def print: String = "[" + printElements + "]"
  }

  class MyConcreteList[+A](h: A, t: MyList[A]) extends MyList[A] {
    def head: A = h
    def tail: MyList[A] = tail
    def isEmpty: Boolean = false
    def add[B >: A](element: B): MyList[B] = new MyConcreteList[B](element, this)
    def printElements: String = {
      if (t.isEmpty) h.toString
      else h.toString + " " + t.printElements
    }
  }

  class MyEmptyList extends MyList[Nothing] {
    def head: Nothing = throw new NoSuchElementException
    def tail: MyList[Nothing] = throw new NoSuchElementException
    def isEmpty: Boolean = true
    def add[B >: Nothing](element: B): MyList[B] = new MyConcreteList[B](element, new MyEmptyList)
    def printElements: String = ""
  }

  // See MyEmptyList works well with different types
  val emptyIntList: MyList[Int] = new MyEmptyList
  val emptyStringList: MyList[String] = new MyEmptyList

  // See Generic MyConcreteList works well with different type
  val listOfInt: MyList[Int] = new MyConcreteList[Int](5, new MyConcreteList[Int](4, new MyConcreteList[Int](3, new MyEmptyList)))
  val listOfString: MyList[String] = new MyConcreteList[String]("scala", new MyConcreteList[String]("generic", new MyEmptyList))

  println(listOfInt.print) // [5 4 3]
  println(listOfString.print) // [scala generic]

}
