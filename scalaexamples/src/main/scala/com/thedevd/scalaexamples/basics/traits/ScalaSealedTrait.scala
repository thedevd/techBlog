package com.thedevd.scalaexamples.basics.traits

/*
 * What is sealed class/trait
 * #######################################
 * If you want a class or trait to be extended by the classes
 * but in the same scala source file then you will call that class or trait is known
 * as sealed. This way you are disallowing another class in the other source 
 * file to extend that sealed class or trait.
 * 
 * In scala Option[T] is declared as sealed abstract class that is extended by
 * Some and None class in the same source file, i.e
 *     sealed abstract class Option[A]
 *     final case class Some[A](x:A) extends Option[A]
 *     case class None extends Option[Nothing]
 *     
 * Similar to Option[T], sealed is used in lot of core scala APIs.
 * 
 * So when to use sealed trait or class
 * ########################################
 * When you want to force that a class can have only few numbers of subclasses,
 * then you will declare the class or trait as sealed.
 * 
 * Thumb rule of sealed classe is - class extending sealed class has to be in the same source file.
 * 
 * Advantages of sealed trail or class
 * ######################################
 * Since all subclasses of the sealed class/trait are in the same source file,
 * so compiler exactly knows which subclasses extend the superclass, and because of this
 * compiler can help us in some situation where we may forget to handle the cases 
 * of all the subclasses (such as in case of doing pattern matching on the basis of class type). 
 * 
 * If we forget to handle the case of some of the subclass types, then compiler can
 * give us a warning about them, and which developer can easily go the that place 
 * and fix the issue that may cause failure in run time.
 * 
 * Lets take an example of Word trait which can have exactly three subclasses- 
 *    Noun, Verb and Adjective
 */
object ScalaSealedTrait {
  
  def main(args: Array[String]): Unit = {
    
    val nWord = Noun("man")
    val vWord = Verb("walk")
    val aWord = Adjective("happy")
    
    println(whatis(nWord)) // Noun
    println(whatis(vWord)) // Verb
    println(whatis(aWord)) // Adjective
    
  }
  
  /*
   * Creating a sealed trait Word,
   * So all class that need to extend this trait must be defined in the same source file.
   * 
   * No other class in separate source file can extend Word as it is sealed within source file only
   */
  sealed trait Word
  case class Noun(word: String) extends Word
  case class Verb(word: String) extends Word
  case class Adjective(word: String) extends Word
  
  /*
   * If we do not provide case for Adjective class in below pattern matching,
   * then compile will give us this warning- 
   * 
   * " match may not be exhaustive. It would fail on the following input: Adjective(_)"
   */
  def whatis(word: Word) = {
    word match { // pattern matching here on Word's subclasses
      case Noun(w) => "Noun" 
      case Verb(w) => "Verb"
      case Adjective(w) => "Adjective"
    }
  }
}