package main.scala.com.thedevd.scalaexamples.akka.actor

import akka.actor.{ Actor, ActorRef, ActorSystem, Props }

/**
 * While modeling solution using Actor (Mostly Parent-Child actor relationship), ALWAYS REMEMBER -
 *    NEVER EVER IN YOUR LIFE, DO NOT EVEN THINK TO PASS 'this' REFERENCE OUTSIDE.
 *    IT CAN DESTROY YOUR CAREER AND SO THE LIFE :)
 *
 *
 * I have tried to demonstrate this NEVER FORGIVEN ACT using Banking example, where we have two actors -
 * - BankAccount
 * - CreditCard
 *
 * BankAccount Actor -
 *  This actor has basic behaviours of any Bank account ie.
 *  - currentBalance member variable to store current account balance
 *  - Deposit(amt) message handler to perform deposit (I have put some checks in msg handler itself to explain the issue)
 *  - Withdraw(amt) message handler to perform withdraw (I have put some checks in msg handler itself to explain the issue)
 *  - PrintBalance message handler to show current account balance
 *  - IssueCreditCard message handler to associate a CreditCard to BankAccount, and this is IMPORTANT place for this example.
 *  - Finally def withdraw(amt), def deposit(amt) helper methods which just add or substract request amount from balance.
 *
 *  As I mentioned IssueCreditCard message handler is the IMPORTANT place for this example. In this message handler
 *  we are creating CreditCard child actor, important thing to note here that we have passed a 'this' reference to the child
 *  actor which is BIGGEST SIN in actor model. And why we are passing 'this' reference because it is the requirement
 *  of CreditCard Actor where we have knowingly (Some beginners may do this unknowingly) defined BankAccount constructor
 *  parameter. (So this is again a BIGGEST DESIGN MISTAKE of the actor).
 *  And after we create CreditCard Child actor we request it to show initial Available limit.
 *
 *  Lets jump into the definition of CreditCard Actor.
 *
 * CreditCard Actor -
 *  For simplicity this actor has only single behaviour which is
 *  - ShowAvailableLimit message handler to print available limit. Further more we are doing something very DANGEROUS
 *    with the its associated bank account which is calling withdraw() method directly and accessing current balance
 *    on the BankActor side. WHICH SHOULD NEVER NEVER BE ALLOWED, but CreditCard actor is able to do because it has
 *    got the main object reference using which it can access any public methods and variables of BankAccount Actor.
 *
 *  So see, just exposing 'this' reference mistakenly can BREAK actor's full encapsulation principle and thus
 *  can lead to very very DANGEROUS situations in REAL WORLD problem.
 *
 * SO LETS TAKE AN OATH WITH ME
 * -----------------------------
 * I WILL NOT EVEN DREAMING ABOUT PASSING 'this' REFERENCE OF AN ACTOR OUTSIDE, NEVER EVER IN LIFE.
 *
 * BONUS (So what should we have done here to prevent breaking actor's encapsulation
 * --------------------------------------------------------------------------------------
 * 1. In BankAccount actor, while IssueCreditCard, pass actorReference not 'this', i.e
 *    val creditCard: ActorRef = context.actorOf(Props(self), "creditCard")
 * 2. And so change CreditCard actor definition to have actor reference as constructor parameter i.e.
 *    class CreditCard(bankAccount: ActorRef) extends Actor { }
 */
object BreakingActorEncapsulation extends App {

  case class Deposit(amt: Int)
  case class Withdraw(amt: Int)
  case object PrintBalance

  case object IssueCreditCard

  class BankAccount extends Actor {
    var currentBalance: Int = 0

    override def receive: Receive = {
      case Deposit(amt) =>
        if(amt < 0) {
          println("Error, Amount can not be negative")
        } else {
          deposit(amt)
        }
      case Withdraw(amt) =>
        if(amt < 0) {
          println("Error, Amount to be withdraw can not be negative")
        } else if (amt > currentBalance) {
          println("Error, insufficient balance in account")
        } else {
          withdraw(amt)
        }
      case PrintBalance => println(s"current balance is: $currentBalance")
      case IssueCreditCard =>
        // ########## DANGER HERE !!!! - accidentally passed this reference to CreditCard Actor
        val creditCard: ActorRef = context.actorOf(Props(new CreditCard(this)), "creditCard")
        creditCard ! ShowAvailableLimit
    }

    def deposit(amt: Int): Unit = currentBalance += amt // I have intentionally put checks outside this method
    def withdraw(amt: Int): Unit = currentBalance -= amt // I have intentionally put checks outside this method
  }

  case object ShowAvailableLimit
  class CreditCard(bankAccount: BankAccount) extends Actor {
    override def receive: Receive = {
      case ShowAvailableLimit =>
        println("[CreditCard] Available limit is: 100000")
        // ####################################################################################################################
        // This is end of the life, as we allowed CreditCard Actor to directly access to BankAccount Actor's state and behaviour
        // And this broke Actor full encapsulation principle which we have been discussing since beginning that, no no other way
        // to interact with an actor other than sending message.
        // So here using BankAccount direct object reference, CreditCard Actor can do a lot serious things with BankAccount's state
        // which we already demonstrated here (Withdrawn 1000000 amount, even though amount was 0)
        // ######################################################################################################################
        bankAccount.withdraw(10000000) // Big Big concern, should not allow accessing any method or data directly on actor
        println(s"[CreditCard] currentBalance accessed in CreditCard actor: ${bankAccount.currentBalance}")
    }
  }

  val actorSystem: ActorSystem = ActorSystem("BreakingActorEncapsulation")
  val bankAccount: ActorRef = actorSystem.actorOf(Props[BankAccount], "bankAccount-XYZ")

  bankAccount ! PrintBalance
  bankAccount ! IssueCreditCard

  /**
   * Output
   * ----------
   * current balance is: 0
   * [CreditCard] Available limit is: 100000
   * [CreditCard] currentBalance accessed in CreditCard actor: -10000000
   */

  actorSystem.terminate()

}
