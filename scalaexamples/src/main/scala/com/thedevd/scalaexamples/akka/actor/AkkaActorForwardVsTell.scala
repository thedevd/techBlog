package main.scala.com.thedevd.scalaexamples.akka.actor

import akka.actor.{ Actor, ActorRef, ActorSystem, Props }

/**
 * So far we have been seeing that there are two ways using which two actors can communicate with each other -
 * 1. Using ! method
 *      actor2 ! "Hi"  --> Assuming you are calling this from inside actor1
 *      actor2.!("Hi")
 * 2. Using tell method (Which gives option to explicitly pass a sender reference)
 *      actor2.tell("Hi", context.self)
 *        --> Assuming you are calling this from inside actor1
 *        --> passing the self reference as sender
 *
 * However there is another third way to pass a message from one actor to another actor which is
 *    Using `forward`
 *
 * Now the biggest question is what is difference b/w ! method and forward
 * -------------------------------------------------------------------------
 * Forward is used to forwards the message by keeping the original sender actor as the sender reference.
 * While in !, always actor's self reference is passed each time while sending msg to other actors.
 *
 * Consider a scenario where Actor-1 wants to pass a message to Actor-3 via Actor-2. So
 *  - Actor-1 sends a msg to Actor-2
 *  - Actor-2 in turns sends the same message to Actor-3
 *
 * If we use standard '!' method in Actor-2 to forward message (received from Actor-1) to Actor-3,
 * then Actor-3 will always assume that Actor-2 is the sender of the msg, it means in Actor-3 we lost the information
 * that who is the original sender of that msg. This is the place where 'forward' method can rescue us which will allow
 * keeping the original sender information when Actor-2 forwards Actor-1's msg to Actor-3.
 *
 * Demonstration of ! vs forward
 * --------------------------------
 * We have two section below to demonstrate the behaviour using '!' method and 'forward' method.
 *
 * 1. The first section, where Actor-1, Actor-2 and Actor-3 involved, is the demonstration about the fact that
 *    what happens if Actor-2 forwards the exact same msg received from Actor-1 to Actor-3. And so in Actor-3
 *    who is going to be actual sender.
 *
 *    Look at the output of first section which is -
 *        [actor-3] received msg: (This message originated by actor-1) from sender: [actor-2]
 *
 *    Pay a close attention to the output here, Actor-3 assumes that sender of the msg - (This message originated by actor-1)
 *    is Actor-2, (But we did not want that, as actual sender of this msg is Actor-1).
 *
 *    This happened because Actor-2 used '!' method to forward the msg to Actor-3, and we know that when you use '!' method
 *    automatically actor self reference is implicitly passed to destination actor. So we can say the below line inside
 *    Actor-2 is similar to calling -
 *        actor2 ! stringMsg
 *        actor2.!(stringMsg)(context.self)  --> actor2.!(stringMsg)([actor2 reference])
 *
 *    So we can say using ! method will cause loosing the actual sender information in destination actor.
 *
 * 2. The second section, where Actor-A, Actor-B and Actor-C involved, is the demonstration about the fact that
 *    what happens if Actor-B forwards the exact same msg received from Actor-A to Actor-C. And so in Actor-C
 *    who is going to be actual sender.
 *
 *    Look at the output of second section which is -
 *        [actor-C] received msg: (This message originated by actor-A) from sender: [actor-A]
 *
 *    Pay a close attention to the output here, Actor-C has to right original sender of the msg - (This message originated by actor-1)
 *    as Actor-A, (This is what we wanted i.e. preserving the actual original sender of the msg all the way in the chain)
 *
 *    This happened because Actor-B used 'forward' method not ! while forwarding the msg to Actor-C. And internally forward is
 *    written as -
 *        def forward(message: Any)(implicit context: ActorContext) = tell(message, context.sender())
 *
 *    So according the above definition, when Actor-B was forwarding msg to Actor-C, Actor-B had the Actor-A as context.sender,
 *    and that is the reason when Actor-C received the msg from Actor-B, it had Actor-A as sender actor reference. And therefor
 *    Actor-C said 'received msg: (This message originated by actor-A) from sender: [actor-A]' (Look at value after from sender:)
 *
 * So In nutshell
 * -------------------
 * tell (aka !)
 *    A tells message ["M"] to B
 *    B tells that message ["M"] to C
 *    C thinks sender of message ["M"] is B
 *
 * forward
 *    A tells message ["M"] to B
 *    B forwards that message ["M"] to C
 *    C thinks sender of message ["M"] is A
 *
 *    We can infer here, to forward msg for keeping original sender, use 'forward' method from second Actor in the communication chain.
 *
 * WORTH TO READ THIS -
 * ########################
 * We can achieve the forward behaviour using 'tell()' method also, because even internally forward uses tell() method.
 * So I meant, instead of using 'forward', we could have written like this in Actor-B while forwarding the msg to Actor-C
 *      class ActorB extends Actor {
 *        override def receive: Receive = {
 *          case stringMsg: String =>
 *            val actorC: ActorRef = context.system.actorOf(Props[ActorC], "actor-C")
 *            val actorA: ActorRef = context.sender() // original sender
 *            actorC.tell(stringMsg, actorA) // similar to actorC forwards stringMsg
 *        }
 *      }
 *
 */

object AkkaActorForwardVsTell extends App {

  // Lets first see the problem of using ! method when we want to forward msg from Actor 1 --> 2 --> 3
  class Actor1 extends Actor {
    override def receive: Receive = {
      case stringMsg: String =>
        val actor2: ActorRef = context.system.actorOf(Props[Actor2], "actor-2")
        actor2 ! stringMsg
    }
  }

  class Actor2 extends Actor {
    override def receive: Receive = {
      case stringMsg: String =>
        val actor3: ActorRef = context.system.actorOf(Props[Actor3], "actor-3")
        actor3 ! stringMsg
    }
  }

  class Actor3 extends Actor {
    override def receive: Receive = {
      case stringMsg: String =>
        println(s"[${self.path.name}] received msg: ($stringMsg) from sender: [${sender.path.name}]")
    }
  }

  val actorSystem: ActorSystem = ActorSystem("ActorSystemName")

  val actor1: ActorRef = actorSystem.actorOf(Props[Actor1], "actor-1")
  actor1 ! "This message originated by actor-1"

  /**
   * Output of the above first section
   * --------------------------------------
   * [actor-3] received msg: (This message originated by actor-1) from sender: [actor-2]
   */


  //####################################################################################################################
  // Lets use forward to keep original sender reference all the way while forwarding msg from Actor A --> B --> C
  class ActorA extends Actor {
    override def receive: Receive = {
      case stringMsg: String =>
        val actorB: ActorRef = context.system.actorOf(Props[ActorB], "actor-B")
        actorB ! stringMsg
    }
  }

  class ActorB extends Actor {
    override def receive: Receive = {
      case stringMsg: String =>
        val actorC: ActorRef = context.system.actorOf(Props[ActorC], "actor-C")
        actorC forward stringMsg // ActorB responsibility is forward the msg to Actor C, not tell
        // similar to calling - actorC.tell(stringMsg, context.sender())
        // Here context.sender() is nothing but ActorA reference
    }
  }

  class ActorC extends Actor {
    override def receive: Receive = {
      case stringMsg: String =>
        println(s"[${self.path.name}] received msg: ($stringMsg) from sender: [${sender.path.name}]")
    }
  }

  val actorA: ActorRef = actorSystem.actorOf(Props[ActorA], "actor-A")
  actorA ! "This message originated by actor-A"

  /**
   * Output of the above second section
   * -------------------------------------
   * [actor-C] received msg: (This message originated by actor-A) from sender: [actor-A]
   */

  actorSystem.terminate()

}
