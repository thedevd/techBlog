package com.thedevd.scalaexamples.basics

/*
 * Option
 * ##################
 * Option[T] is the simplest monad (or container) which contains
 * either one elements (some element) or zero element (none).
 *
 * Option (although all monads) requires a type. For example
 * an Option[Int] can only hold integers, and an Option[String] can only hold strings
 *
 * Option[T] Class hierarchy -
 * ###################
 * Option has two subtypes: Some and None, which instantiate the abstract Option type.
 * If an Option contains a value, it is Some(value). Otherwise, it’s None.
 *
 * Option[T]
 *       --> Some[T]
 *       --> None
 *
 * Some widely used Methods of Option -
 * - get() => returns either value or None
 * - getOrElse() => just like get(), excepts it can allow to provide default value when no value exists
 * - isEmpty() => to check if Option[T] has None value or not. And returns boolean
 *
 */
object ScalaOption {

  def main(args: Array[String]): Unit = {

    /*
     * One place where we get an Option value is calling get() method on a Map
     */
    val map = Map(1 -> "Dev", 2 -> "Ravi")

    getDemo(map)
    getOrElseDemo(map)
    isEmptyDemo(map)
    useInPatternMatching(map)

  }

  def getDemo(map: Map[Int, String]) = {
    
    val value1: Option[String] = map.get(1)
    val value3: Option[String] = map.get(3)

    println(value1) // Some(Dev) --> Some means it has a value
    println(value3) // None --> None means no value
    
    println("map.get(1).get: " + value1.get)
    // println(value3.get) --> can not call get on None, this throws exception
    // Therefore to prevent exception on calling get on None, getOrElse can be used
  }

  def getOrElseDemo(map: Map[Int, String]) = {

    val value1: Option[String] = map.get(1)
    val value3: Option[String] = map.get(3)

    val value1_1 = value1.getOrElse(1, "Key not found")
    val value3_3 = value3.getOrElse(3, "Key not found")
    println("map.get(1).getOrElse: " + value1_1) // Dev
    println("map.get(3).getOrElse: " + value3_3) // (3, Key not found)
    
    // or we call directly call getOrElse on map
    val v1 = map.getOrElse(1, "Key not found")
    val v3 = map.getOrElse(3, "Key not found")
    println("map.getOrElse(1, Key not found): " + v1) // Dev
    println("map.getOrElse(3, Key not found): " + v3) // Key not found
    
    
  }

  def isEmptyDemo(map: Map[Int, String]) = {

    val value1: Option[String] = map.get(1)
    val value3: Option[String] = map.get(3)

    println("map.get(1).isEmpty: " + value1.isEmpty) // true
    println("map.get(3).isEmpty: " + value3.isEmpty) // false
  }
  
  def useInPatternMatching(map: Map[Int, String]) = {
    
    println("Using PatternMatching map.get(1): " + patternMatch(map.get(1))) // Dev
    println("Using PatternMatching map.get(3): " + patternMatch(map.get(3))) // Key Not found
    
    def patternMatch(option: Option[String]) = {
      option match { // matching Option with its subClass
        case Some(x) => x
        case None => "Key Not found"
      }
    }
  }

}