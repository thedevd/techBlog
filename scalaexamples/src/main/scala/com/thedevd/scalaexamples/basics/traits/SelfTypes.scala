package scalaexamples.basics.traits

/*
 * Self type in scala is used to solve a relationship problem b/w two entirely different hierarchy.
 * This same problem can also be solved using inheritance but that completely does not make sense if you do so.
 * (We will see this using an example why it does not make sense at all to establish IS A relationship using inheritance)
 *
 * Lets say we have these three different hierarchy - Person, Diet and Vehicle
 *    trait Thing
 *
 *    // Hierarchy #1
 *    trait Person {
 *      def hasAllergiesTo(thing: Thing): Boolean
 *    }
 *    trait Child extends Person
 *    trait Adult extends Person
 *
 *    // Hierarchy #2
 *    trait Diet {
 *      def eatable(thing: Thing): Boolean
 *    }
 *    trait Vegetarian extends Diet
 *    trait NonVegetarian extends Diet
 *
 *    // Hierarchy #3
 *    trait Vehicle
 *    trait Car extends Vehicle
 *    trait Bike extends Vehicle
 *
 * AND WE WANT TO ENSURE TWO THINGS HERE (I CAN SAY WE HAVE TWO PROBLEMS HERE) -
 * 1. Enforce that Diet is only applicable to Person, meaning if any class extending Diet should also extend Person.
 *    In other word, indirectly we want to enforce same thing at Compile time i.e.
 *      trait Vegetarian extends Diet with Person { }
 *
 *    So If any class only extends Diet without Person, then compiler should report it.
 *      trait AnotherDiet extends Diet with Car ------> Compiler should report that Person is not there
 *
 * 2. We want to access hasAllergiesTo() logic inside Diet before saying whether thing is eatable or not. I mean
 *    def eatable(thing: Thing): Boolean = {
 *       // check if person has allergies to the thing if so return false, to do so we need access to Person's hasAllergiesTo() method
 *    }
 *
 *    To solve this problem most of you must be thinking to do something like this (Use inheritance)-
 *      trait Diet extends Person {
 *        def eatable(thing: Thing): Boolean = {
 *          if(this.hasAllergiesTo(thing)) false   // So Diet has now access to Person's hasAllergiesTo() method
 *          else true
 *        }
 *      }
 *
 *    You will be thinking that you solved this problem, I mean accessing logic of Person in Diet using inheritance,
 *    BUT PAY CLOSE ATTENTION TO THIS LINE when you say - trait Diet extends Person,
 *    DO YOU REALLY THINK that "Diet IS A Person", no way this is not a ideal IS A relationship, this is breaking our
 *    real world statement.
 *    IN SIMPLE WORD WE CAN SAY - relationship b/w Diet and Person is not actually a IS A, it is like REQUIRES relationship
 *
 * See the code below which shows how these two thing are ensured using self type
 *  trait Diet { self: Person => // ATTENTION - this looks like lambda but it is different as this is used in trait or class
 *    def eatable(thing: Thing): Boolean = {
 *      if(self.hasAllergiesTo(thing)) false   // See using self we have access to Person's hasAllergiesTo() method
 *      else true
 *    }
 *  }
 *
 *  So the line "trait Diet { self: Person ....... }" says Diet REQUIRES Person.
 *
 * NOTE- self is nothing but this keyword so `this / self` can also be used interchangably
 *
 */
object SelfTypes {

  trait Thing

  // Hierarchy #1
  trait Person {
    def hasAllergiesTo(thing: Thing): Boolean
  }
  trait Child extends Person
  trait Adult extends Person

  // Hierarchy #2
  trait Diet { self: Person => // ATTENTION - this looks like lambda but it is different as this is used in trait or class
    def eatable(thing: Thing): Boolean = {
      if (self.hasAllergiesTo(thing)) false // See using self we have access to Person's hasAllergiesTo() method
      else true
    }
  }

  // trait Vegetarian extends Diet ----> Compile time error now saying Illegal inheritance, self-type Vegetarian does not conform to Person
  // trait NonVegetarian extends Diet ----> Compile time error now saying Illegal inheritance, self-type Vegetarian does not conform to Person
  trait Vegetarian extends Diet with Person
  trait NonVegetarian extends Diet with Person

  // Hierarchy #3
  trait Vehicle
  trait Car extends Vehicle
  trait Bike extends Vehicle

  def main(args: Array[String]): Unit = {
    println("self type explanation")
  }
}
