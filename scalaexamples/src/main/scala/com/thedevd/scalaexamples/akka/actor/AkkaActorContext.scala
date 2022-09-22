package main.scala.com.thedevd.scalaexamples.akka.actor

/**
 * Akka Actor context
 * -----------------------
 * Each actor has information about their context, here context can be simply means the environment where actor is running
 * Several important things about actor's context -
 * 1. Each Actor has an implicit context variable that stores context information
 *      implicit val context: ActorContext = {}
 *
 * 2. With context variable, we can access
 *    - actorSystem (context.system),
 *    - own actor Reference (context.self),
 *    - sender actor reference (context.sender),
 *    - actor path (context.self.path.name, context.sender.path.name),
 *    - actor selection using path (context.actorSelection(path: String)),
 *    - create child actor (context.actorOf())
 *    - lot of information.
 *
 * 3. context is only valid within the Actor itself, so do not close over it and publish it to other threads!
 *
 * Self and Sender actor reference using context
 * -------------------------------------------------
 * With actor context, we can easily refer
 *  - self actor reference
 *    (Usage of self reference is allowing to send message to itself)
 *  - sender actor reference - the reference to sender Actor of the last received message
 *    (Usage of sender reference is allow an actor to reply back to sender)
 *
 *  self reference -
 *  - Calling 'context.self' inside an actor basically returns actorRef of self. So 'context.self === 'this' in OOPs'.
 *    BTW Instead of 'context.self' we can also use 'self', because internally actor defines it as implicit variable ->
 *      implicit final val self: ActorRef = context.self
 *  - With self reference, actor is able to send message to itself.
 *
 *  sender reference -
 *  - Please pay attention to the sentence when I say context.sender gives -
 *    the reference to sender Actor of the last received message. It means actor' sender reference is the one which sends
 *    message to the actor recently.
 *    See 'ContextSenderDemo' actor at very bottom of this example.
 *
 *  - Now the question is how do an Actor gets a reference of sender. This logic is in tell (!) method which is used to
 *    send message to any actor. If you look at implementation of ! method you will see any implicit sender reference is
 *    sent all the time when you send a message to an actor.
 *        def !(message: Any)(implicit sender: ActorRef = Actor.noSender): Unit
 *
 *    So a message is sent from within an actor then the actor reference is implicitly passed on as the implicit 'sender' argument.
 *    For eg. in our case when we send `Hi` message to ContextSenderDemo actor from within SimpleActor, then simple actor
 *    reference is sent automatically to ContextSenderDemo.
 *        class SimpleActor extends Actor {
 *          override def receive: Receive = {
 *            case SayHiTo(contextSenderDemoActor) =>
 *               contextSenderDemoActor ! Hi
 *               // similar to calling - 'contextSenderDemoActor.!(Hi)(context.self)'
 *          }
 *        }
 *
 *    And if you are sending message from outside not withing any actor,
 *    then Actor.noSender which is default to sender ActorRef is passed as sender.
 *    Actor.noSender means null, and null actorRef means akka deadLetters actor.
 *    For eg. in our case when we send PrintSender msg to ContextSenderDemo actor from main thread then Actor.noSender is passed
 *        contextSenderDemoActor ! PrintSender   (line #142)
 *
 */

import akka.actor.{ Actor, ActorRef, ActorSystem, Props }

object AkkaActorContext extends App {

  case class SayHiTo(friend: ActorRef)
  case object Hi
  case class ReplyWith(msg: String)

  class Messenger extends Actor {
    override def receive: Receive = {
      case SayHiTo(friend) =>
        val selfRef: ActorRef = context.self
        println(s"[${selfRef.path.name}] wants to say Hi to (${friend.path.name})")
        friend ! Hi
        // 'friend ! Hi' is similar to 'friend.!(Hi)(self)'
        // Here self is passed implicitly as sender reference because we are calling tell method from inside actor.
        // Why implicitly self is passed because internally context.self is defined as implicit val -
        //    implicit final val self: ActorRef = context.self
      case Hi =>
        // val senderRef: ActorRef = context.sender
        // Internally there is method 'sender()' defined i.e. final def sender(): ActorRef = context.sender() so we can also use -
        // val senderRef: ActorRef = sender
        println(s"[${self.path.name}] I got your Hi dear (${sender.path.name})")
        sender ! ReplyWith("Hi there, how are you!")
      case ReplyWith(msg) =>
        println(s"[${self.path.name}] I got your reply dear (${sender.path.name}): $msg")
    }
  }

  val actorSystem: ActorSystem = ActorSystem("ActorSystemName")
  val messenger1: ActorRef = actorSystem.actorOf(Props[Messenger], "messenger1")
  val messenger2: ActorRef = actorSystem.actorOf(Props[Messenger], "messenger2")

  messenger1 ! SayHiTo(messenger2)
  // similar to messenger1.!(SayHiTo(messenger2))(Actor.noSender). Actor.noSender means no Sender is available.
  // So here Actor.noSender is passed implicitly because we are not calling tell method from inside any Actor.

  /**
   * Output
   * ------------
   * [messenger1] wants to say Hi to (messenger2)
   * [messenger2] I got your Hi dear (messenger1)
   * [messenger1] I got your reply dear (messenger2): Hi there, how are you!
   */

  Thread.sleep(2000)
  /**
   * Demonstration that context.sender gives -
   * the reference to sender Actor of the last received message. It means for a actor, sender is that actor's reference
   * which sends message to the actor recently.
   *
   */

  case object PrintSender
  class ContextSenderDemo extends Actor {
    override def receive: Receive = {
      case Hi =>
        println(s"[${self.path.name}] received Hi from sender - ${sender.path.name}")
        sender ! ReplyWith("Hi bro, whats up") // Replying back to sender
      case PrintSender =>
        println(s"[${self.path.name}] Last message Sender is - ${sender.path.name}")
    }
  }

  class SimpleActor extends Actor {
    override def receive: Receive = {
      case SayHiTo(contextSenderDemoActor) =>
        contextSenderDemoActor ! Hi
        // similar to calling - 'contextSenderDemoActor.!(Hi)(context.self)'
      case ReplyWith(msg) =>
        println(s"[${self.path.name}] received reply: $msg")
    }
  }

  val contextSenderDemoActor: ActorRef = actorSystem.actorOf(Props[ContextSenderDemo], "contextSenderDemo")
  val actor1: ActorRef = actorSystem.actorOf(Props[SimpleActor], "actor-1")
  val actor2: ActorRef = actorSystem.actorOf(Props[SimpleActor], "actor-2")
  val actor3: ActorRef = actorSystem.actorOf(Props[SimpleActor], "actor-3")

  // SimpleActor ----> ContextSenderDemo Actor
  actor1 ! SayHiTo(contextSenderDemoActor)
  actor2 ! SayHiTo(contextSenderDemoActor)
  actor3 ! SayHiTo(contextSenderDemoActor) // At this point, for contextSenderDemoActor, actor3 is the sender

  contextSenderDemoActor ! PrintSender
  // What do you think what will print as sender after sending this last msg, if you are thinking 'actor-3', then you
  // are wrong, because we sent the last msg from outside the actor, so tell (!) method will send Actor.noSender as
  // the sender reference which is null i.e. akka's default deadLetters is the sender here.

  /**
   * Output of ContextSenderDemo
   * (Note some of the statements may intermingled in b/w,
   * so for understanding I have arranged them in some order)
   * ------------------------------------------------------
   * [contextSenderDemo] received Hi from sender - actor-1
   * [contextSenderDemo] received Hi from sender - actor-3
   * [contextSenderDemo] received Hi from sender - actor-2
   * [actor-1] received reply: Hi bro, whats up
   * [actor-3] received reply: Hi bro, whats up
   * [actor-2] received reply: Hi bro, whats up
   * [contextSenderDemo] Last message Sender is - deadLetters
   *
   */

  actorSystem.terminate()

}
