package com.thedevd.scalaexamples.collections

import scala.collection.mutable.ListBuffer

/*
 * List- is immutable collection. So you can not modify elements in list.
 * ListBuffere - is mutable form of List. So it is just like ArrayBuffer except it used LinkedList internally.
 */
object ListBufferVsList {
  
  def main(args: Array[String]): Unit = {
    
    val list = List[String]("Chair", "Sofa") // Immutable version
    println(list(0)) // --> Chair
    // list(0) = "Bed" --> Can not change immutable list
    // list += "Bed" --> Can not add to immutable list
    
    val listBuffer = ListBuffer[String]("Chair", "Sofa")
    listBuffer(0) = "Dining table" // modification is allowed
    listBuffer += "Bed" // adding element is allowed too as it is mutable list
    println(listBuffer) // --> ListBuffer(Dining table, Sofa, Bed)
    
    listBuffer ++= List[String]("dressing","Chair")
    println(listBuffer) // --> ListBuffer(Dining table, Sofa, Bed, dressing, Chair)

    
  }
  
}