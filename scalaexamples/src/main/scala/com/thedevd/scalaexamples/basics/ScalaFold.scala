package com.thedevd.scalaexamples.basics

/*
 * Scala has three variant of fold function -
 * 1. fold() -- parallel
 * 2. foldleft() -- sequential
 * 3. foldRight() -- sequential
 * 
 * Comparing fold() with foldRight and foldLeft, fold() does not guarantee 
 * about the order in which the elements of the collection will be processed.
 * So you will basically use fold() when you want to process the collection 
 * in parallel without worrying about processing order.
 * 
 * Where as foldLeft and foldRight guarantee about the order in which the elements
 * of the collection will be processed. 
 * In foldLeft, elements are processed from left to right.
 * In foldRight, elements are processed from right to left.
 */
object ScalaFold {

  def main(args: Array[String]): Unit = {
    example1()
    example2()
  }

  def example1() = {

    val numList = List(1, 2, 3, 4, 5)
    // val numList = 1 to 100 // used this collection for more better clarity
    
    // Note- using par on numList to create parallel collection.
    
    val r1 = numList.par.fold(0)((acc, value) => {
      println("adding accumulator=" + acc + ", value=" + value + " => " + (acc + value))
      acc + value
    })
    println("fold(): " + r1)
    println("#######################")
    /*
     * You can see from the output that,
     * fold process the elements of parallel collection in parallel
     * So it is parallel operation not linear. 
     * 
     * adding accumulator=0, value=4 => 4
     * adding accumulator=0, value=3 => 3
     * adding accumulator=0, value=1 => 1
     * adding accumulator=0, value=5 => 5
     * adding accumulator=4, value=5 => 9
     * adding accumulator=0, value=2 => 2
     * adding accumulator=3, value=9 => 12
     * adding accumulator=1, value=2 => 3
     * adding accumulator=3, value=12 => 15
     * fold(): 15
     */
    
    val r2 = numList.par.foldLeft(0)((acc, value) => {
      println("adding accumulator=" + acc + ", value=" + value + " => " + (acc + value))
      acc + value
    })
    println("foldLeft(): " + r2)
    println("#######################")
    /*
     * You can see that foldLeft
     * picks elements from left to right.
     * It means foldLeft does sequence operation
     * 
     * adding accumulator=0, value=1 => 1
     * adding accumulator=1, value=2 => 3
     * adding accumulator=3, value=3 => 6
     * adding accumulator=6, value=4 => 10
     * adding accumulator=10, value=5 => 15
     * foldLeft(): 15
     * #######################
     */
    
    // --> Note in foldRight second arguments is accumulated one.
    val r3 = numList.par.foldRight(0)((value, acc) => {
     println("adding value=" + value + ", acc=" + acc + " => " + (value + acc))
      acc + value
    })
    println("foldRight(): " + r3)
    println("#######################")
    
    /*
     * You can see that foldRight
     * picks elements from right to left.
     * It means foldRight does sequence operation.
     * 
     * adding value=5, acc=0 => 5
     * adding value=4, acc=5 => 9
     * adding value=3, acc=9 => 12
     * adding value=2, acc=12 => 14
     * adding value=1, acc=14 => 15
     * foldRight(): 15
     * #######################
     */

  }

  def example2() = {
    val empList = Employee("dev", 30, "male") :: Employee("mayuri", 34, "female") :: Employee("ravi", 36, "male") :: Nil

    val empDetailsWithTitle = empList.foldLeft(List[String]())({ (result, emp) =>
      val title = emp.gender match {
        case "male" => "Mr."
        case "female" => "Ms."
      }
      result :+ s"[$title ${emp.name} ${emp.age}]" // :+ used to append elements to List
    })

    val empDetailsWithTitle1 = empList.foldRight(List[String]())({ (emp, result) =>
      val title = emp.gender match {
        case "male" => "Mr."
        case "female" => "Ms."
      }
      result :+ s"[$title ${emp.name} ${emp.age}]"
    })

    println("############################# Example 2")
    println("foldLeft: " + empDetailsWithTitle)
    println("foldRight: " + empDetailsWithTitle1)

    /*
     * Output-
     * ##########
     * foldLeft: List([Mr. dev 30], [Ms. mayuri 34], [Mr. ravi 36])
     * foldRight: List([Mr. ravi 36], [Ms. mayuri 34], [Mr. dev 30])
     * 
     * look at the order here, foldLeft picks elements from left to right
     * and foldRight picks elements from right to left
     */
  }

  case class Employee(name: String, age: Integer, gender: String)

}