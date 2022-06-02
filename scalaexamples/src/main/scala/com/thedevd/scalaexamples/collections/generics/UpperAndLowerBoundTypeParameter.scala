package main.scala.com.thedevd.scalaexamples.collections.generics

/*
 * There is another concept related to Type Parameter in Generic class, and it is
 * 1. upper bound type parameter
 *    - Can be written as [T <: S] where T is type parameter and S is a type
 *    - [T <: S] means the type parameter T must be either same as S or subtype of S. So T is at upper bound here.
 * 2. lower bound type parameter
 *    - Can be written as [T >: S] where T is type parameter and S is a type
 *    - [T >: S] means the type parameter T must be either same as S OR supertype of S. So T is at lower bound here.
 */
object UpperAndLowerBoundTypeParameter extends App {

  abstract class Animal {
    def name: String
  }

  case class Dog(name: String) extends Animal
  class Puppy(name: String) extends Dog(name)

  case class Cat(name: String) extends Animal

  class AnimalCarer {
    def displayL[T >: Dog](t: T) {
      println(t)
    }

    def displayU[T <: Dog](t: T): Unit = {
      println(t)
    }
  }

  val dog: Dog = Dog("dog1")
  val puppy: Puppy = new Puppy("puppy1")
  val cat: Cat = Cat("cat1")

  val animalCarer = new AnimalCarer

  println("Checking lower bound..... T >: Dog")
  animalCarer.displayL(puppy)
  animalCarer.displayL(dog)
  animalCarer.displayL(cat) // Cat type is allowed because Cat is also an Animal and Animal is supertype of Dog

  println("Checking upper bound..... T <: Dog")
  animalCarer.displayU(puppy)
  animalCarer.displayU(dog)
  /* animalCarer.displayU(cat) // type mismatch error. Cat type is now allowed here because Cat is not same as Dog or subtype of Dog.
   *
   * To further describe upper bound if we change upper bound type to puppy i.e. def displayU [T <: Puppy](t: T), then
   * even animalCarer.displayU(dog) will not be allowed, because Dog is supertype of puppy, and according to upper bound type
   * T should be either same as Puppy or subtype of Puppy considering def displayU [T <: Puppy](t: T).
   */

}
