package com.thedevd.scalaexamples.basics

import java.sql.Time

/*
 * Doing long concatenation of string literal along with variables,
 * sometimes you may end up with forgetting closing string literals using 
 * double quotes.
 * 
 * To avoid such mistakes, scala has String interpolation concept.
 * 
 * Scala has 3  built-in interpolators-
 * ############################################
 * 1.Simple string interpolator-
 *   ex-  s"Hello $user"
 *   
 * 2.Formatted interpolator-
 *   ex-
 *   val balance = 5000.50 
 *   f"balance is $balance%5.0f"  
 *   ---> this will produce 'balance is 5001'
 * 
 * 3. Raw interpolator-
 *    ex- raw"escaped literals \t and \n"    
 *    ---> here \t and \n will be treated as normal literal not escape sequence.
 */
object ScalaStringInterpolation {
  
  def main(args: Array[String]): Unit = {
    
    val user = "Dev"
    val balance = 5000.50
    val now = new Time(System.currentTimeMillis())
    
    val message = "Hello " + user + "the account balance is " + balance + "at time: " + now
    // we can do same concatenation using string interpolation. see next
    
    val interpolationMessage = s"Hello $user the account balance is $balance, at time $now" // small case s is used
    println("interpolationMessage: " + interpolationMessage)
    
  }
  
}