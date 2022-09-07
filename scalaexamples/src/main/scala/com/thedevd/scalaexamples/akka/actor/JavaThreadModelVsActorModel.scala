package main.scala.com.thedevd.scalaexamples.akka.actor

import akka.actor.{ Actor, ActorRef, ActorSystem, Props }

object JavaThreadModelVsActorModel extends App {

 /** Problem #1 using Java Thread Model
  * - Always have the problem with consistency of shared data in multi-threaded env (Race Condition)
  *
  * Illustration -
  * See the below example - deposit() and withdraw() method of BankAccount() class where we have line
  *   this.balance += amount and this.balance -= amount respectively.
  *
  *    Although it is just one line, But there are actually three operations:
  *       reading balance, incrementing/decrementing it and putting the updated value back.
  *    So If two threads run at exactly the same time, it is possible that one thread read the balance, but another thread
  *    already read and modified it, so first thread is basically operating on wrong data which got modified by other thread.
  *    so we can say the update done by second thread might be overridden by first thread. So this is the Race condition problem.
  *
  *    And this is exactly what is happening with BankAccount() class
  *
  *    // inconsistent balance with deposit
  *    val bankAccount = new BankAccount(1000)
  *    for(_ <- 1 to 1000) {
  *     new Thread(() => bankAccount.deposit(1)).start()
  *    }
  *
  *    Thread.sleep(5000)
  *    println(s"Inconsistent balance after deposit ${bankAccount.getBalance}") // Ideally Expecting 2000 Rs, but it never was
  *
  *    // inconsistent balance with withdraw
  *    val bankAccount2 = new BankAccount(1000)
  *    for(_ <- 1 to 1000) {
  *     new Thread(() => bankAccount2.withdraw(100)).start()
  *    }
  *
  *    Thread.sleep(5000)
  *    println(s"Inconsistent balance after withdrawing ${bankAccount2.getBalance}") // Ideally if the balance is 0 no withdraw should be allowed, but you may find balance in negative
  *
  * Race Condition -
  * -----------------
  * When we have different threads simultaneously accessing and modifying a variable, we have a race condition. (balance is that variable in example)
  * So we can say we need some kind of Coordination/Synchronization mechanism among threads simultaneously accessing and modifying a variable, in
  * such as way that if one thread is using that shared variable, no other thread can be allowed to operate on the same.
  *
  * Now you must be thinking of two things -
  * 1. I will use Java provided thread-safe Atomic Variables (such as AtomicInteger) for balance variable as it is only involved in
  * increment/decrement operation. Thats fine, we can use AtomicInteger for this purpose/example where we have counter kind of variable,
  * But Atomic Integer only works on single variable. How do we make several operations atomic ?
  *
  * 2. So to make several operations atomic easily, we can use synchronized block
  *
  * */
  class BankAccount(@volatile private var balance: Int) {
    def deposit(amount: Int): Unit = {
      Thread.sleep(1) // Sleep added to force threads interleave/prempt in b/w
      this.balance += amount
    }

    def withdraw(amount: Int): Unit = {
      if(balance > 0) {
        Thread.sleep(1) // Sleep added to force threads interleave/prempt in b/w
        this.balance -= amount
      }
    }

    def getBalance: Int = balance
  }

  val bankAccount = new BankAccount(1000)
  for(_ <- 1 to 1000) {
    new Thread(() => bankAccount.deposit(1)).start()
  }

  // inconsistent balance with deposit
  // Ideally Expecting 2000 Rs, but it never was
  Thread.sleep(5000)
  println(s"Inconsistent balance after deposit ${bankAccount.getBalance}")

  val bankAccount2 = new BankAccount(1000)
  for(_ <- 1 to 1000) {
    new Thread(() => bankAccount2.withdraw(100)).start()
  }

  // inconsistent balance with withdraw
  // Ideally if the balance is 0 no withdraw should be allowed, but you may find balance in negative
  // It is very dangerous in real world. As Two threads running at the same time may cause the bank to issue two withdrawals
  // even if the balance is no longer enough.
  Thread.sleep(5000)
  println(s"Inconsistent balance after withdrawing ${bankAccount2.getBalance}")

  /**
   * Solution -
   *  - So solution to prevent inconsistency issue in Race condition is to use Synchronized block
   *
   * Illustration-
   * In the below example of SomeBetterBankAccount() class,
   * we used synchronize block to wrap the statements which we want to be executed by single thread at a time.
   *
   * To be noted, synchronized is used on object level i.e. this.synchronized{} (In java it is synchronized(this){})
   *
   * The idea of synchronized block is so simple.
   *  - One thread enters synchronized block and locks it, while other threads wait outside.
   *    The lock is basically an object, in our case (this).
   *  - After that thread is done, the lock is released and passed to another thread waiting for the lock to be released.
   *  - That another thread then does the same thing.
   * Also, note the keyword volatile before balance variable which is needed to prevent the thread
   * to cache variable x in local CPU cache, means there is only single copy will be maintained.
   *
   * Problems of using Synchronized solution
   * -----------------------------------------
   * Although the synchronized usage in this example seems so fair and straightforward, but needs to be used very very
   * carefully. Most of the time with large application it becomes so so complex to deal with more blocks and more locks.
   * And when it comes to dealing with more locks, it started becoming very much risky which may result in deadlock condition.
   *
   * Plus having more locks results in poor performance because of the fact that if a thread is working with synchronized block/lock,
   * all other threads wait outside and they don't do anything rather than waiting waiting and waiting ......
   *
   * So if you do not want to worry about locking/synchronizing while working with shared state in multi-threaded env,
   * then AKKA framework has ACTOR MODEL for this purpose.
   *
   */
  class SomeBetterBankAccount(@volatile private var balance: Int) {
    def deposit(amount: Int): Unit = {
      Thread.sleep(1) // Sleep added to force threads interleave/prempt in b/w
      this.synchronized {
        this.balance += amount
      }
    }

    def withdraw(amount: Int): Unit = {
      this.synchronized {
        if(this.balance > 0) {
          Thread.sleep(1) // Sleep added to force threads interleave/prempt in b/w
          this.balance -= amount
        }
      }
    }

    def getBalance: Int = balance
  }

  val betterBankAccount = new SomeBetterBankAccount(1000)
  for(_ <- 1 to 1000) {
    new Thread(() => betterBankAccount.deposit(1)).start()
  }

  Thread.sleep(5000)
  println(s"Consistent balance after synchronized deposit: ${betterBankAccount.getBalance}") // This will always guarantee 2000 as balance

  val betterBankAccount2 = new SomeBetterBankAccount(1000)
  for(_ <- 1 to 1000) {
    new Thread(() => betterBankAccount2.withdraw(100)).start()
  }

  Thread.sleep(5000)
  println(s"Consistent balance after synchronized withdraw: ${betterBankAccount2.getBalance}") // This will always guarantee no withdrawn allowed if balance is 0

  /**
   * Actor Model (Akka Actor)
   * ---------------------------
   * Akka Actor Model is based on basic principle of QUEUE.
   * So in above BankAccount Example, what if we drop all the deposit or withdraw operation (JOB) in a QUEUE,
   * and process them one by one based on FIFO order, this will guarantee that next operation is processed only
   * after previous operation is done.
   *
   * The another benefit of this is there is no more waiting since threads will just drop the message to be performed and
   * proceed with their other things. (BTW Results can be delivered to them later with ask or tell)
   *
   * So each actor has its own queue called mailbox, messages are dropped in its mailbox and so
   * processed in first-in-first-out order. There is another important characteristic of Actors are
   * they provide full encapsulation, means actors do not expose their state and behavior at all, the only way to communicate
   * with an actor is just by sending messages, (However in OOPs, we have to expose several behaviours and even sometimes state
   * are exposed (public scope). So if want to change their internal state then also this can only be done through passing known messages to Actor
   *
   * Lets rewrite above example using Actor model
   * ------------------------------------------------
   * We will create two Actors
   * 1. Actor for managing BankAccount activities (balance, withdraw logic, deposit logic, Show balance)
   * 2. Actor for Account Holder which will basically send messages to BankAccount actor to perform banking activities.
   *
   * It is to be noted, all communication b/w above two actors are done purely by sending messages each other.
   * - Account Holder sends Deposit, Withdraw, ShowBalance messages to ActorBased BankAccount.
   * - After ActorBased BankAccount actor done with operation, it sends back the response to Account Holder actor via
   *   TransactionDone or TransactionFailed messages.
   *
   * Basic Thumb-rule of Actor is for each known messages, it has dedicated message handler, for example in
   * ActorBased BankAccount we have dedicated message handler for Deposit/Withdraw/ShowBalance message type.
   */

  case class Deposit(amount: Int)
  case class Withdraw(amount: Int)
  case object ShowBalance
  case class TransactionDone(accountNumber: Int, message: String)
  case class TransactionFailed(accountNumber: Int, failureReason: String)

  class ActorBasedBankAccount(accountNumber: Int) extends Actor {
    var balance: Int = 0

    override def receive: Receive = {
      case Deposit(amount) =>
        Thread.sleep(1)
        balance += amount
        sender ! TransactionDone(accountNumber, s"$amount deposited, updated balance: $balance")
      case Withdraw(amount) =>
        if (balance > 0 && amount <= balance) {
          Thread.sleep(1)
          balance -= amount
          sender ! TransactionDone(accountNumber, s"$amount withdraws, updated balance: $balance")
        } else {
          sender ! TransactionFailed(accountNumber, s"Insufficient balance $balance")
        }
      case ShowBalance =>
        sender ! TransactionDone(accountNumber, s"Current balance $balance")
      case _ =>
        sender ! TransactionFailed(accountNumber, s"Unknown message sent")
    }
  }

  class ActorBasedAccountHolder(name: String, accountNumber: Int, bankAccount: ActorRef) extends Actor {
    override def receive: Receive = {
      case deposit: Deposit => bankAccount ! deposit
      case withdraw: Withdraw => bankAccount ! withdraw
      case ShowBalance => bankAccount ! ShowBalance
      case done: TransactionDone => println(done)
      case failed: TransactionFailed => println(failed)
      case _ => println("Sorry Service is not yet available")
    }
  }

  val actorSystem: ActorSystem = ActorSystem("BankSystem")

  //First open bankAccount and then create holder
  val bankAccount1: ActorRef = actorSystem.actorOf(Props(new ActorBasedBankAccount(1)), s"customer1BankAccount")
  val bankAccount1Holder: ActorRef = actorSystem.actorOf(Props(new ActorBasedAccountHolder("customer1", 1, bankAccount1)))

  for(_ <- 1 to 1000) {
    bankAccount1Holder ! Deposit(2)
  }
  bankAccount1Holder ! ShowBalance // TransactionDone(1,Current balance 2000)

  // lets do some fun by mixing deposit and withdraw operation
  for(_ <- 1 to 1000) {
    bankAccount1Holder ! Deposit(2)
    bankAccount1Holder ! Withdraw(1)
  }

  // expected balance here is 2000 (old) + 1000 * 2(Deposit) - 1000 * 1(Withdraw) = 3000
  bankAccount1Holder ! ShowBalance // TransactionDone(1,Current balance 3000)
}
