package com.thedevd.scalaexamples.basics

/*
 * Rather than using for/yield comprehension, we can call the map function on the collection
 * to transform to new collection. Map function takes a function as input and that function
 * should take atleast one parameter.
 *
 * But once you add a guard (if statments), a for/yield loop is no longer directly equivalent to
 * just a map method call.
 *
 * In this demo we will see this.
 * 
 * Note- When you add guards to a for-comprehension and want to write it as a multiline expression, 
 * the recommended coding style is to use curly braces rather than parentheses
 * This makes the code more readable, especially when the list of guards becomes long.
 */
object ScalaForComprehensionVsMap {

  def main(args: Array[String]): Unit = {

    example1_toUpperCase()
    example2_useOfGuard()

  }

  def example1_toUpperCase() = {
    val namelist = List("dev", "ravi", "atul", "ankit")

    val upperNameList_for = for (name <- namelist) yield name.toUpperCase()
    println(upperNameList_for) // --> List(DEV, RAVI, ATUL, ANKIT)

    val upperNameList_map = namelist.map(name => name.toUpperCase())
    println(upperNameList_map) // --> List(DEV, RAVI, ATUL, ANKIT)

    /*
     * Passing function as parameter to map
     */
    val toUpper = (name: String) => name.toUpperCase()
    println(namelist.map(toUpper)) // --> List(DEV, RAVI, ATUL, ANKIT)

  }

  def example2_useOfGuard() = {
    /*
     * As we saw in example1, that for simple usecase for/yield is same to map.
     * But once you add a guard, a for/yield loop is no longer directly equivalent to just a map method call
     */

    val namelist = List("dev", "ravi", "atul", "ankit")
    val upperNameList_for = for {
      name <- namelist if (name.length() > 3) // adding guard as if statement
    } yield name.toUpperCase()
    println(upperNameList_for) // --> List(RAVI, ATUL, ANKIT)

    // let see the outcome of applying similar logic here
    val toUpper = (name: String) => if (name.length() > 3) name.toUpperCase()
    val upperNameList_map = namelist.map(toUpper)
    println(upperNameList_map) // --> List((), RAVI, ATUL, ANKIT)
    /*
     * So you can see, if guard is there in form of if statement, then map produces different result
     * (one of the output is empty List((), RAVI, ATUL, ANKIT)
     *
     * So in such case you can consider guard as filter(), and to achieve the correct result using map
     * you need to filter the elements first before applying map. Lets see how
     */

    val correctNameList_map = namelist.filter(_.length() > 3).map(toUpper)
    println(correctNameList_map) // --> List(RAVI, ATUL, ANKIT)

  }
}