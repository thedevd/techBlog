package main.scala.com.thedevd.scalaexamples.akka.actor

/**
 * Akka Actor's core terminologies -
 * --------------------------------
 * 1. Actor
 * 2. State and behaviours
 * 3. Mailbox, Message, Message Handler, Asynchronous communication, Full Encapsulation
 * 4. Actor System
 * 5. Tell method (!) - Communicating with actor
 * 6. ActorRef
 * 7. Thread safety and At most-once guarantee
 *
 * And we will see how exactly AKKA actor works
 *
 * Actor / State+Behaviors / Mailbox / Message / Message Handler-
 * ---------------------------------------------------------------
 * In akka, you can think Actors persons/humans communicating with each other over text msging. Where
 * one person sends a text msg to other and does not wait further. Other person gets the msg, process it independently and then
 * he can responds back to sender again by sending a txt message. So the communication here is Asynchronous in nature.
 *
 * In general an actor is a data structure (having message handler + message queue) where
 * messages are dropped in its mailbox and they are picked up one by one for processing.
 * The msgs sent to an actor and its processing it completely asynchronous i.e. fire and forget semantic.
 *
 * Actor properties -
 * 1. Actors are uniquely identified, so you can not have two actors with same in the ActorSystem.
 * 2. Each actor has a
 *     - message handler (in the form of receive method)
 *     - message queue (aka mailbox). This queue is thread safe, means only one thread can work on this queue at a time.
 * 3. Actors are fully encapsulated, means you can not call any method inside an actor.
 *    There is only way to interact with actor is by sending the messages.
 * 4. Actor communication is asynchronous, means messages are sent to an actor and processed in actor asynchronously,
 *    where sender has no need to wait for result after sending a message to an actor.
 *
 * Actor System
 * --------------
 * The ActorSystem is like entry point to Akka world.
 * ActorSystem is a heavy weight data structures that maintains number of threads under the hood and
 * then allocates them to the actors created in the actor system. Actor system can be create using ActorSystem
 *    val actorSystem: ActorSystem = ActorSystem("MyActorSystem") // actorSystem name can not contain spaces
 *
 * Communicating with Actor
 * -----------------------------
 * As mentioned there is no way to interact with an Actor directly rather than sending messages.
 * Actor trait provides tell method (! method) which can be used to send messages to an Actor mailbox.
 *    val wordCounterActor1: ActorRef = actorSystem.actorOf(Props[WordCounter], "wordCounterActor1")
 *    wordCounterActor1 ! "Hi"
 *    wordCounterActor1.!("Hi")
 *    wordCounterActor1.tell("Hi", wordCounterActor1)
 *
 *    With tell, we can also include the sender reference if possible, by default it You can pass [[akka.actor.ActorRef]] `noSender` or `null`
 *    as sender if there is nobody to reply to.
 *
 * Again I am saying you can not invoke anything in Actor directly whether it is data / any helper methods, only way to
 * interact with actor is sending messages to it. This is one of the proof of an Actor providing full encapsulation.
 *
 * ActorRef
 * --------------
 * When you creat an actor using actorSystem's actorOf(), in return you get only reference to that Actor which has type
 * ActorRef. Interesting thing about this reference is you can only use this reference to send messages to the actor means,
 * with this reference you can only call ! or tell() methods to send message to actor.
 *
 * This is another proof why actor provides full encapsulation because with ActorRef you can only send messages, and
 * can not access anything inside actor.
 *
 * Guarantees akka system provides ->
 * ------------------------------------
 * 1. Thread safe guarantees i.e
 *    - Only one thread allowed to operates on an actor at any time (This solves race condition).
 *    This means actors are single threaded, where we do not need to worry about LOCKS and inconsistencies in actor state.
 *
 * 2. Message delivery guarantees
 *     - At most once delivery (So no duplicates)
 *     - for any sender-receiver pair, the msg order is always maintained, for ex
 *
 * If Devendra send Ravi message - A followed by B then we can assure that
 *    - Ravi will never receive duplicates of A or B.
 *    - Ravi will always receive A before B (Possibly with some others msgs in between)
 *
 * 3. Message processing atomicity guarantee
 *    -  Processing of a message is atomic, means it is not possible that thread operating on actor
 *       leaves during message processing.
 *
 *
 * How Akka actor works
 * -----------------------
 * Akka actor system has a thread pool that it shares with actors.
 * Each actor has a
 *     - message handler (in the form of receive method)
 *     - message queue (aka mailbox). This queue is thread safe, means only one thread can work on this queue at a time.
 *
 * How communication happens -
 * Sending a message
 * - Messages sent to an actor are enqueued in the actor's mailbox, which is thread safe.
 *
 * Processing a message
 * - a thread from actorSystem's thread pool is scheduled to run this actor.
 * - the thread picks up the message from the mailbox one by one, in order.
 * - the thread invokes the msg handler on each message and based on msg type it process that message.
 * - the thread can be unscheduled from the actor at any point of time (But never during msg processing),
 *   so that it can serve another actor (no starvation problem)
 *
 */

import akka.actor.{ Actor, ActorRef, ActorSystem, Props }

object AkkaActorDemo extends App {

  // Step 1 - Define Actor (State + behaviours + Message handlers to behave correctly)
  class WordCounter extends Actor {
    var totalWordsCounted = 0 // state
    override def receive: PartialFunction[Any, Unit] = { // behaviours
      case sentence: String => // case representing message handler for specific type of messages it will receive
        println(s"[${self.path.name}] I have received sentence: $sentence")
        totalWordsCounted += splitSentence(sentence).length
        println(s"[${self.path.name}] I have counted total words: $totalWordsCounted")
      case otherMsgType =>
        println(s"[${self.path.name}] I can not understand msg: $otherMsgType")
    }

    // Even this method can not be invoked by any means outside Actor
    def splitSentence(sentence: String): Array[String] = {
      sentence.split(" ")
    }
  }

  // Step 2 - Create Actor System
  val actorSystem = ActorSystem("HelloActorSystem")
  println(s"Actor system name: [${actorSystem.name}]")

  // Step 3 - Create Actor reference
  val wordCounterActor1: ActorRef = actorSystem.actorOf(Props[WordCounter], "wordCounterActor1")

  // Step 4 - Communicate with actor by sending messages to it
  wordCounterActor1 ! "This is 5words simple sentence" // Infix notation
  wordCounterActor1.!("This is 4words sentence") // prefix dot notation
  wordCounterActor1.tell("2words sentence", wordCounterActor1) // tell also internally call ! method

  wordCounterActor1 ! 100

  /**
   * Above Program Output
   * ------------------
   * Actor system name: [HelloActorSystem]
   *
   * [wordCounterActor1] I have received sentence: This is 5words simple sentence
   * [wordCounterActor1] I have counted total words: 5
   *
   * [wordCounterActor1] I have received sentence: This is 4words sentence
   * [wordCounterActor1] I have counted total words: 9
   *
   * [wordCounterActor1] I have received sentence: 2words sentence
   * [wordCounterActor1] I have counted total words: 11
   *
   * [wordCounterActor1] I can not understand msg: 100
   */

  // Can not use new keyword outside to create actor ref
  // new WordCounter() ---> This is not allowed if do so this will throw below given exception
  // Exception in thread "main" akka.actor.ActorInitializationException:
  // You cannot create an instance of [main.scala.com.thedevd.scalaexamples.akka.actor.AkkaActorDemo$WordCounter]
  // explicitly using the constructor (new). You have to use one of the 'actorOf' factory methods to create a new actor.

  /**
   * Since we know that trying to create instance of Actor directly using new keyword outside is not allowed,
   * So interesting fact is now, how do we create instance of an Actor that takes parameter.
   *
   * Solution to such case-
   * -----------------------
   * The above statement about "not allowed to instantiate an actor using new keyword" is not 100% true, yes we can
   * instantiate actor using new keyword BUT it is allowed only inside akka.actor.Props, so you can have
   *      val wordCounterActorUsingNew: ActorRef = actorSystem.actorOf(Props(new WordCounter()), "wordCounterActorUsingNew")
   * (This property shows another proof that Actor provides full encapsulation)
   *
   * Lets understand this fact using below example where we have an intelligent kind of WordCounter actor which takes
   * wordSplitter as parameter argument.
   *
   */
  class IntelligentWordCounter(wordSplitter: String) extends Actor {
    var totalWordsCounted = 0
    override def receive: Receive = { // Receive is type alias to PartialFunction[Any, Unit] i.e. type Receive = PartialFunction[Any, Unit]
      case sentence: String =>
        println(s"[${self.path.name}] I have received sentence: $sentence")
        totalWordsCounted += splitSentence(sentence).length
        println(s"[${self.path.name}] I have counted total words: $totalWordsCounted")
      case otherMsgType =>
        println(s"[${self.path.name}] I can not understand msg: $otherMsgType. I understand only string type msg")
    }

    def splitSentence(sentence: String): Array[String] = {
      sentence.split(wordSplitter)
    }
  }
  object IntelligentWordCounter {
    // so you can see instantiating IntelligentWordCounter actor using new is valid inside Props, but not in other places
    def props(wordSplitter: String): Props = Props(new IntelligentWordCounter(wordSplitter))
  }

  // so you can see instantiating IntelligentWordCounter actor using new is valid inside Props, but not in other places
  val intelligentWordCounter1: ActorRef = actorSystem.actorOf(Props(new IntelligentWordCounter(" ")), "wordCounterBySpace")
  // Best practise of initialising the constructor based actor is - using companion object
  val intelligentWordCounter2: ActorRef = actorSystem.actorOf(IntelligentWordCounter.props(":::"), "wordCounterByTripleColon")

  intelligentWordCounter1 ! "This is 4words sentence"
  intelligentWordCounter1 ! "1word"

  intelligentWordCounter2 ! "This:::is:::triplecolon:::separated:::5words"

  /**
   * Output of above program
   * -------------------------
   * [wordCounterBySpace] I have received sentence: This is 4words sentence
   * [wordCounterBySpace] I have counted total words: 4
   * [wordCounterBySpace] I have received sentence: 1word
   * [wordCounterBySpace] I have counted total words: 5
   *
   * [wordCounterByTripleColon] I have received sentence: This:::is:::triplecolon:::separated:::5words
   * [wordCounterByTripleColon] I have counted total words: 5
   */

  actorSystem.terminate()
}

