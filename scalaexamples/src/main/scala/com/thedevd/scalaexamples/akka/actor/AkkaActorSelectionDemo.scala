package main.scala.com.thedevd.scalaexamples.akka.actor

import akka.actor.{ Actor, ActorRef, ActorSelection, ActorSystem, Props }

/**
 * Let me first take your through the time, where I had a scenario where I used the "Actor Selection" concept.
 * So usecase was- There was an object which I was serializing and saving in db. On other hand at the time of
 * deserializing it, I wanted send some messages to one of the actor out of several actors which I had used before
 * serializing that obj. But I did not know exactly which actor to select from deserialized objects.
 *
 * So what I did, I included the corresponding actor path in that object, and serialized it.
 * Later after deserializing I had corresponding actor path available with me, So I simply used actorSelection() method
 * on ActorSystem, and that allowed me to send further messages to the actor after deserializing it.
 *
 * So HOW DO WE SELECT ACTOR
 * --------------------------------
 * We can do this by using actor's path. We just have to supply this path to actorSelection() method of ActorSystem.
 * (NOTE- This also works inside an actor, using 'context.actorSelection()' method)
 *
 * Look at the example below, where we have tried to select actors -
 * - actor-A,
 * - actor-B (Child of actor-A) and
 * - actor-C (Child of actor-B)
 *
 *    val actorASelection: ActorSelection = actorSystem.actorSelection("/user/actor-A")
 *    val actorBSelection: ActorSelection = actorSystem.actorSelection("/user/actor-A/actor-B")
 *    val actorCSelection: ActorSelection = actorSystem.actorSelection("/user/actor-A/actor-B/actor-C")
 *
 * See the path - we started with '/user Guardian actor' because we know actors we create go underneath '/user Guardian actor'.
 * After we have actorSelection, we can send message to them using similar ways, i.e tell(or !) methods.
 *
 * // PAY ATTENTION that when we try to select an actor using path which does not exist in system then
 * // internally Akka selects built-in deadLetters actor, it means sending message using that non existed selection goes to
 * // deadLetters actor. This can be seen with actor selection - actorSystem.actorSelection("/user/actor-A/actor-D")
 *
 */
object AkkaActorSelectionDemo extends App {

  case object ActorSelectionTest

  class ActorA extends Actor {
    override def receive: Receive = {
      case fwdMsg: String =>
        val actorBAsChild: ActorRef = context.actorOf(Props[ActorB], "actor-B")
        actorBAsChild ! fwdMsg
      case ActorSelectionTest => println(s"Hi my path is: ${self.path}")
    }
  }

  class ActorB extends Actor {
    override def receive: Receive = {
      case message: String =>
        val actorCAsChild: ActorRef = context.actorOf(Props[ActorC], "actor-C")
        actorCAsChild forward message
      case ActorSelectionTest => println(s"Hi my path is: ${self.path}")
    }
  }

  class ActorC extends Actor {
    override def receive: Receive = {
      case message: String =>
        println(s"[${self.path.name}] has got msg: $message from sender: ${sender().path.name}")
      case ActorSelectionTest => println(s"Hi my path is: ${self.path}")
    }
  }

  val actorSystem: ActorSystem = ActorSystem("ActorSelectionDemo")
  val parentActorA: ActorRef = actorSystem.actorOf(Props[ActorA], "actor-A")

  parentActorA ! "I am the boss"

  // actor can be selected using actorSystem.actorSelection(path: String)
  // use context.actorSelection() method if wants to select an actor insider another actor.
  val actorASelection: ActorSelection = actorSystem.actorSelection("/user/actor-A")
  val actorBSelection: ActorSelection = actorSystem.actorSelection("/user/actor-A/actor-B")
  val actorCSelection: ActorSelection = actorSystem.actorSelection("/user/actor-A/actor-B/actor-C")

  actorASelection ! ActorSelectionTest
  actorBSelection ! ActorSelectionTest
  actorCSelection ! ActorSelectionTest

  // PAY ATTENTION that when we try to select an actor using path which does not exist in system then
  // internally Akka selects built-in deadLetters actor, it means sending message using that non existed selection goes to
  // deadLetters actor.
  val nonExistedActorSelection: ActorSelection = actorSystem.actorSelection("/user/actor-A/actor-D")
  nonExistedActorSelection ! ActorSelectionTest // See the console output below, the last INFO message is because of this.

  /**
   * Console output
   * ----------------
   * [actor-C] has got msg: I am the boss from sender: actor-A
   * Hi my path is: akka://ActorSelectionDemo/user/actor-A
   * Hi my path is: akka://ActorSelectionDemo/user/actor-A/actor-B/actor-C
   * Hi my path is: akka://ActorSelectionDemo/user/actor-A/actor-B
   *
   * [INFO] [akkaDeadLetter][09/22/2022 16:08:55.653] [ActorSelectionDemo-akka.actor.default-dispatcher-7] [akka://ActorSelectionDemo/user/actor-A/actor-D] Message [main.scala.com.thedevd.scalaexamples.akka.actor.AkkaActorSelectionDemo$ActorSelectionTest$] to Actor[akka://ActorSelectionDemo/user/actor-A/actor-D] was not delivered. [1] dead letters encountered. If this is not an expected behavior then Actor[akka://ActorSelectionDemo/user/actor-A/actor-D] may have terminated unexpectedly. This logging can be turned off or adjusted with configuration settings 'akka.log-dead-letters' and 'akka.log-dead-letters-during-shutdown'.
   */

  actorSystem.terminate()
}
