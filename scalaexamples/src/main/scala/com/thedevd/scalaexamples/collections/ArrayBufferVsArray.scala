package com.thedevd.scalaexamples.collections

import scala.collection.mutable.ArrayBuffer

/*
 * Both Array and ArrayBuffer are mutable, which means that you can modify elements at particular indexes: a(i) = e,
 * But
 * Array - is fixed in size
 * ArrayBuffer - is resizable, So you can append elements further using += operator or remove using -=.
 *               Whole List can also be appended or removed to ArrayBuffer using ++= or --= respectively
 * 
 * */
object ArrayBufferVsArray {
  
  def main(args: Array[String]): Unit = {
    
    val array = Array("Sofa", "Chair", "table", 100)
    //val array = new Array[String](3)
    println(array.length) // --> 4 which is fixed
    // array += "bed" --> new items can not be appended
    
    val arrayBuffer = ArrayBuffer("Sofa", "Chair", "table") // resizable collection
    // val arrayBuffer = ArrayBuffer[String]()
    arrayBuffer += "Bed" // new items can be appended
    arrayBuffer(0) = "Dining table" // this proves ArrayBuffer is mutable
    println(arrayBuffer) // --> ArrayBuffer(Dining table, Chair, table, Bed)
    
    arrayBuffer -= "Chair"
    println(arrayBuffer) // -->  ArrayBuffer(Dining table, table, Bed)
    
    arrayBuffer ++= List[String]("Chair","dressing")
    println(arrayBuffer) // --> ArrayBuffer(Dining table, table, Bed, Chair, dressing)
    
    arrayBuffer --= List[String]("Dining table", "table")
    println(arrayBuffer) // --> ArrayBuffer(Bed, Chair, dressing)
  }
}