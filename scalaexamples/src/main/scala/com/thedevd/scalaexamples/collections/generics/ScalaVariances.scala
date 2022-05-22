package main.scala.com.thedevd.scalaexamples.collections.generics

/*
 * WHAT IS VARIANCE
 * ----------------------
 * First question that arises is what exactly is Variance ?
 * Variance -
 *    defines inheritance relationships b/w Parameterized Types. Now you can ask what the heck is this Parameterized Types.
 *    Lets understand Parameterized Types with this example, If I have this "List[T]" then here -
 *      T - is known as Type Parameter
 *      List[T] - as whole known as Generic List
 *
 *     So for List[T], we can have List[Int], List[String], List[AnyVal] etc. and these are nothing but Parameterized Types.
 *     And Variance defines relationship b/w these parameterized type, i.e whether a one parameterized type can refer to another type.
 *
 * PURPOSE OF VARIANCE
 * --------------------------
 * SOLE PURPOSE OF VARIANCE IS TO MAKE SCALA COLLECTION MORE TYPE-SAFE
 *
 * 3-TYPES OF VARIANCE
 * --------------------------
 * 1. Covariant -
 *    - Type parameter of a Generic G[_] is called Covariant if C is subtype of P and G[C] is also subtype of G[P]
 *      Example: CovariantList[+T] => if Cat is subtype of Animal, then CovariantList[Cat] will also be subtype of CovariantList[Animal]
 *    - As you can see, + operator is used just before type parameter to represents a Covariant type. I.e. CovariantList[+T]
 *    - This is a straight-forward way in Scala to force type-safety (because Assigning an object to a variable of one of its supertypes
 *      is always safe.) and this covariant nature is present in already available Generics such as -
 *      List[+T], Option[+T], Try[+T]
 *
 * 2. Contravariant
 *    - It is just opposite of Covariant where we say for generic type G[_], if C is subtype of P then G[C] is also subtype of G[P], WHEREAS -
 *    - Type parameter of a generic G[_] is called Contravariant if C is subtype of P and G[C] can not be subtype of G[P]
 *    - Example : Trainer[-T] => if S is subtype of T, then Trainer[Animal] is also subtype of Trainer[Cat]
 *    - As you can see, - operator is used just before type parameter to denote it is a contravariant type I.e.
 *    - Its very rare case of subtype relationship, where we are saying a Parent type object can be assigned to one of its subtype
 * 3. Invariant
 *    - Type Parameter of a Generic G[_] is called Contravariant if C is subtype of P, but G[C] can not be subtype of G[P]
 *    - Example: InvariantList[T] => if Cat is subtype of Animal, then InvariantList[Cat] and InvariantList[Animal] are not related.
 *      It means assigning an subtype's object variable to its supertype is not allowed here.
 *    - Generic classes in Scala are invariant by default.
 *    - The classes like Array[T], ListBuffer[T], ArrayBuffer[T] etc. are mutable so, they have invariant type parameter,
 *      if we use invariant type parameters in inheritance relationship or sub-typing then we will get a compilation error.
 *
 * SUMMARY
 * ----------
 * class Foo[+A] // A covariant generic class
 * class Bar[-A] // A contravariant generic class
 * class Baz[A]  // An invariant generic class
 *
 */
object ScalaVariances extends App {

  abstract class Animal {
    def name: String
  }
  case class Cat(name: String) extends Animal
  case class Dog(name: String) extends Animal

  // #############################################################################################################
  // COVARIANT - CovariantList[+T] is called Covariant because
  // If Cat is subtype of Animal, then CovariantList[Cat] is also subtype of CovariantList[Animal]
  // So val animalList: CovariantList[Animal] = new CovariantList[Cat] is totally possible with Covariant type
  class CovariantList[+T]
  val animal: Animal = Cat("daisi")
  val animalList: CovariantList[Animal] = new CovariantList[Cat]

  // Now lets see below given example which proves that the Scala standard library has a generic immutable List ->
  // sealed abstract class List[+A], which is defined as Covariant type,
  // and which made possible to write below method which can take List[Animal] as an argument but passing actually List[Cat] or List[Dog]
  def printAnimalNames(animals: List[Animal]): Unit = {
    animals.foreach(animal => println(animal.name))
  }

  val cats: List[Cat] = List(Cat("daisi"), Cat("rina"))
  val dogs: List[Dog] = List(Dog("Rex"), Dog("bruno"))
  printAnimalNames(cats)
  printAnimalNames(dogs)
  // above call proves that, If List[A] was not covariant, the above two method calls would not compile


  // #############################################################################################################
  // INVARIANT - InvariantList is called invariant generic because
  // If Cat is subtype of Animal, then InvariantList[Cat] and InvariantList[Animal] are not related
  class InvariantList[T]
  // The below line will report compilation error saying - Type Mismatch Required InvariantList[Animal] But found InvariantList[Cat]
  // val invariantAnimalList: InvariantList[Animal] = new InvariantList[Cat]
  val invariantAnimalList: InvariantList[Animal] = new InvariantList[Animal]

  val arrayOfCat: Array[Cat] = Array(Cat("daisi"), Cat("rina"))
  // val arrayOfAnimal: Array[Animal] = arrayOfCat --> This gives compilation error about Type mismatch, because
  // Type parameter in Array[] is defined as invariant i.e. Array[T]

  // #############################################################################################################
  // CONTRAVARIANT - If Cat is subtype of Animal, then Trainer[Animal] is also subtype of Trainer[Cat]
  abstract class Trainer[-T] {
    def printName(value: T): Unit
  }
  class AnimalTrainer extends Trainer[Animal] {
    def printName(animal: Animal): Unit = println(s"The name of animal being trained: ${animal.name}")
  }
  class CatTrainer extends Trainer[Cat] {
    def printName(cat: Cat): Unit = println(s"The name of cat being trained: ${cat.name}")
  }

  val myCat: Cat = Cat("myCat")
  def printCatTrainer(catTrainer: Trainer[Cat]): Unit = {
    catTrainer.printName(myCat)
  }

  val catTrainer: Trainer[Cat] = new CatTrainer
  val animalTrainer: Trainer[Animal] = new AnimalTrainer
  printCatTrainer(catTrainer)
  printCatTrainer(animalTrainer) // See here, we are able to assign parent type directly to its subtype

  val trainer: Trainer[Cat] = new AnimalTrainer // Animal trainer is basically a Trainer[Animal] type
  // the above line says a trainer of cat is also called trainer of animal because he can train all animals.
  // Its very special case of subtype relationship, where we are saying a Parent type can also be subtype of its subtype in some way.

}
