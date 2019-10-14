package com.thedevd.scalaexamples

/*
 * In scala you can compose two functions in a way that output of one function
 * is automatically passed to input of another function. 
 * 
 * To combine functions, scala has andThen utility function.
 * Make a special care that 'andThen' method expects function as it's argument, not methods, 
 * means you CAN NOT compose two methods defined using def keyword using andThen.
 * 
 * Val functions inherit the andThen function.
 */
object FunctionCompositionUsingAndThen {

  def main(args: Array[String]): Unit = {
    // ---> Example 1
    val totalCost = 100.0
    /*
     * The apply discount function was called first andThen the apply GST function was called.
     * During this, the output from the first apply discount function was also passed as input parameter 
     * to the second apply GST function
     */
    val finalPrice = (applyDiscountValFunction andThen applyGSTValFunction)(totalCost)
    println("finalPrice after function composition: " + finalPrice)
    
    // ---> Example 2
    val increamentAndSquare = increamentByOne andThen square // composing two functions
    println("increamentAndSquare(1): " + increamentAndSquare(1))   
  }

  // ############## Example 1
  val applyDiscountValFunction = (amount: Double) => { // make a note here that we have created val function not methods using def
    amount - (amount * 10 / 100) // applying 10% discount
  }
  val applyGSTValFunction = (amount: Double) => {
    amount + (amount * 5 / 100) // applying 5% Tax
  }
  
  // ############## Example 2
  val increamentByOne = (num:Int) => num + 1
  val square = (num:Int) => num * num
}

