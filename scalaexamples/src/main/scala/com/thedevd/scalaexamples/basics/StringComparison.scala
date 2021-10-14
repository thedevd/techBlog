package com.thedevd.scalaexamples.basics

/*
 * Initially when I started in Scala, I had very confusing question that
 * For string comparison in scala Why is it recommended to use == rather than .equals ?
 *
 * If you are from java background then you notice that they recommend the use of .equals() for String comparison in Java
 * but why is it advised to do the exact reverse in Scala?
 *
 * Lets clear this doubt here -
 * In Scala, == is equivalent to equals(), but it also does an extra thing which is kind a benefit to us -
 * it also handles null so no NullPointerException is thrown. (see the examples below for much more clarity)
 *
 */
object StringComparison {

  def main(args: Array[String]): Unit = {

    val s1: String = "Scala"
    val s2: String = "Scala"
    val s3: String = s1 + " weird but awesome"

    // lets test string equality using ==
    println("is s1 == s2: " + (s1 == s2)) // true
    println("is s1 == s3: " + (s1 == s3)) // false

    // Now see the benefit of == method is that it does not throw a NullPointerException
    // on a basic test if a String is null
    val s4: String = null
    println("is s4 == s1: " + (s4 == s1)) // false

    // However, be aware that calling a method on a null string can throw a NullPointerException i.e.
    // s4.toUpperCase == s1
  }
}
