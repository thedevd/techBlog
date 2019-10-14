package com.thedevd.scalaexamples

/*
 * Currying in programming world means transforming a function
 * which takes more than one arguments into chain of functions where each of function takes exactly one argument.
 * i.e def f(a:Int,b:Int,c:Int) ---------> def f(a:Int)(b:Int)(c:Int)
 *
 * for example -
 *     def add2Numbers(a:Int, b:Int) = a + b   // so this function taking two arguments,
 *
 *     converting the above function to currying function
 *     def add2Numbers(a:Int) (b:Int) = a + b // each separate function is taking one argument
 *
 *     how to call this currying function
 *     val sum = add2Numbers(10)(20)
 *
 *     or another way of calling it partially one by one is -
 *     val partialSum = add2Numbers(10)_  // note _ is added to tell this is Partially Applied functions and more functions to come.
 *     val finalSum = partialSum(20) // 30 is the output
 *
 */
object CurryingFunction {

  def main(args: Array[String]): Unit = {
    
    // ---> Example 1
    val add10 = curriedAdd(10)_ // this is a Partially applied function
    // val add10 = curriedAdd(10)(_) // we can also write this
    val add20To10 = add10(20)
    println("add20To10: " + add20To10)
    
    
    // ---> Example 2
    val cafe = new Cafe()
    val actualPrice = cafe.curriedFinalPrice(100)_ // 100 INR is actual Price
    val serviceChargeAdded = actualPrice(10)  // adding 10% serviceCharge
    val finalPriceAfterGSTAdded = serviceChargeAdded(5) // addiong 5% GST
    println("finalPriceAfterGSTAdded: " + finalPriceAfterGSTAdded)
    
    /*
     * Now lets say the second branch of cafe is at the beach side and there we have 12% serviceCharge,
     * so lets calculate final price for the same product with actualPrice as 100
     */
    val beachBranchServiceCharge = actualPrice(12) // adding 12% serviceCharge for beach side cafe
    val finalPriceAfterGSTAddedForBeachCafe = beachBranchServiceCharge(5)
    println("finalPriceAfterGSTAddedForBeachCafe: " + finalPriceAfterGSTAddedForBeachCafe)
    

  }

  // ################# Example 1
  def add(a: Int, b: Int) = a + b // non-currying function
  def curriedAdd(a: Int)(b: Int) = a + b // currying function

  // ################# Example 2
  class Cafe() {

    /*
     * To call this non-currying function it is must to provide
     * all three parameters altogether.
     */
    def finalPrice(price: Double, serviceCharge: Double, GST: Double) = {
      price + (price * serviceCharge / 100) + (price * GST / 100)
    }

    /*
     * Rather than giving all the parameters to function, we can supply them
     * one by one when needed.
     */
    def curriedFinalPrice(price: Double)(serviceCharge: Double)(GST: Double) = {
      price + (price * serviceCharge / 100) + (price * GST / 100)
    }
  }

}


