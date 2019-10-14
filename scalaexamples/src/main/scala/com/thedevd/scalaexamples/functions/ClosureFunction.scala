package com.thedevd.scalaexamples.functions

/*
 * 
 */
object ClosureFunction {
  
  def main(args: Array[String]): Unit = {
    val finalPrice = calculateFinalPrice(100) // --> calling outer function
    val netbill = finalPrice // --> call the closure function to get final result
    
    println("netBill: " + netbill) // netBill: 110.0
  }
  
  
  def calculateFinalPrice(amount: Double) = {
    var serviceCharge = 10 // 10%
    
    /*
     * finalPrice() function is called closure function because its returned value
     * dependent on serviceCharge which is defined in outer function.
     * 
     * So after the outer function 'calculateFinalPrice' is called, scala compiler will act as smart person means 
     * it will keep the serviceCharge variable in visibility scope of the inner function 'finalPrice'.
     * and this way inner function 'finalPrice' will be able to produce the final result. 
     * (Normally scope of the variable used within function gets destroyed after calling function, but
     * with closure, scala acts as smart compiler.)
     */
    def finalPrice() = { 
      var gst = 5 // tax is 5%
      amount + (amount * serviceCharge/100) + (amount * gst/100)
    }
    
    finalPrice // returning inner function
  }
}