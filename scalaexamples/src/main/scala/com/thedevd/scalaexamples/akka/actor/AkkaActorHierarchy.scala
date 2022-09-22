package main.scala.com.thedevd.scalaexamples.akka.actor

import akka.actor.{ Actor, ActorRef, ActorSystem, Props }

/**
 * The Akka actor hierarchy
 * ----------------------------
 * ALWAYS REMEMBER that - In Akka, an actor always belongs to a Parent. Lets prove it using example shown below.
 *
 * In below example, we have 3 actors -
 * - ActorA
 * - ActorB
 * - ActorC
 *
 * Look at definition of ActorA and ActorB -
 * - In ActorA, we are creating child actor 'actor-B'
 * - In ActorB, we are creating child actor 'actor-C'
 *
 * That means when we create 'actor-A' then actor hierarchy looks like
 *    actor-A ---> actor-B ---> actor-C
 *
 * And that is what I printed the actor hierarchy in each actor, which you can see after running this example -
 *    [actor-A] path is: akka://AkkActorHierarchyDemo/user/actor-A
 *    [actor-B] path is: akka://AkkActorHierarchyDemo/user/actor-A/actor-B
 *    [actor-C] path is: akka://AkkActorHierarchyDemo/user/actor-A/actor-B/actor-C
 *
 * Lets understand anatomy of actor's hierarchy path, I am taking actor-C' path
 *    [actor-C] path is: akka://AkkActorHierarchyDemo/user/actor-A/actor-B/actor-C
 *
 *    - akka://AkkActorHierarchyDemo
 *      Each actor's starts with path akka://<ActorSystemName>, where akka:// is the value of protocol.
 *      Then akka protocol is followed with ActorSystemName (In our example we gave systemName as - AkkActorHierarchyDemo).
 *      In Akka clustering/remoting where multiple systems are involved, this part of the Path also included 'HostName' so
 *      that other actors can find each other on the cluster network.
 *    - /user/actor-A/actor-B/actor-C
 *      Rest part of the Path describes the entire actor's hierarchy. Since as I mentioned all actors we create for our
 *      application are part of 'user Guardian actors' so /user is for that.
 *      And the last piece in /actor-A/actor-B/actor-C shows the actor-name preceded with parent actor's name.
 *
 * How to access Actor's hierarchy
 * ------------------------------------
 * Any Actor's Hierarchy can be accessed using Actor reference itself. Just call .path, it will print the entire
 * hierarchy.
 *    'actorRef.path'
 *    'self.path' ---> inside actor, actorRef can be accessed using self implicit variable
 *
 * In general, we can say -
 * ActorA path -> /user/actor-A
 * ActorB Path -> /user/actor-A/actor-B
 * ActorC Path -> /user/actor-A/actor-B/actor-C
 *
 * PROOF that an Actor always belongs to a parent in Akka
 * --------------------------------------------------------
 * - actor-C's parent is actor-B
 * - actor-B's parent is actor-A
 * - Even actor-A who is top level actor in our example, also belonging to akka's '/user guardian actor', So
 *   actor-A's parent is '/user guardian actor'.
 *
 *   Apart from '/user' guardian actors, Akka have two more guardian actors, lets see them.
 *
 * Akka Built-in Actors (called Guardian Actors)
 * -------------------------------------------------
 * In fact, when we created our first actor 'actor-A', Akka already created 3 built-in actors in the system.
 * Akka call these built-in actors as Guardian actor. They are -
 * 1. /
 *    Called Root Guardian. This is parent of all actors in system and so this is the last one to stop when actorSystem
 *    itself is terminated.
 * 2. /system
 *    Called System Guardian. Generally Akka and other libraries built on top of Akka may create their Actors under System
 *    Guardian hierarchy.
 * 3. /user
 *    Called User Guardian. All the actors which we create for our application go under /user guardian actor hierarchy.
 *
 * Now you can ask a question to yourself that why Akka maintains Actor hierarchy, I mean why it keeps track of which is
 * the parent actor of a actor and so on.
 *
 * Why Akka keeps track of Actor Hierarchy
 * -------------------------------------------
 * Akka does it to manage lifecycle of actors. If you remember in 'StoppingActorDemo' where we said a statement that
 * when an actor is stopped, then before main actor stops itself it first wait for child actors to stop. And you know
 * this is very important step that "Whenever an actor is stopped, all of its children are recursively stopped too."
 *
 * In fact this step allows Akka to simplify resource cleanup and so helps avoid resource leaks which might cause by
 * open sockets and files.
 *
 * So conclusion is, How akka knows that whenever an actor is stopped, what are all its children to be stopped first,
 * Akka uses actor hierarchy to fetch this information.
 *
 *
 */
object AkkaActorHierarchy extends App {

  class ActorA extends Actor {
    override def receive: Receive = {
      case fwdMsg =>
        // first print actor's hierarchy path to understand akka actor hierarchy
        println(s"[${self.path.name}] path is: ${self.path}")
        val actorB: ActorRef = context.actorOf(Props[ActorB], "actor-B") // create child actor
        actorB ! fwdMsg
    }
  }

  class ActorB extends Actor {
    override def receive: Receive = {
      case message =>
        println(s"[${self.path.name}] path is: ${self.path}")
        val actorC: ActorRef = context.actorOf(Props[ActorC], "actor-C") // create child actor
        actorC forward message
    }
  }

  class ActorC extends Actor {
    override def receive: Receive = {
      case message =>
        println(s"[${self.path.name}] path is: ${self.path}")
        println(s"[${self.path.name}] got message: $message from sender: (${sender().path})")
    }
  }

  val actorSystem: ActorSystem = ActorSystem("AkkActorHierarchyDemo")
  val parentActor: ActorRef = actorSystem.actorOf(Props[ActorA], "actor-A")

  parentActor ! "I am the boss"

  /**
   * Output
   * ------------
   * [actor-A] path is: akka://AkkActorHierarchyDemo/user/actor-A
   * [actor-B] path is: akka://AkkActorHierarchyDemo/user/actor-A/actor-B
   * [actor-C] path is: akka://AkkActorHierarchyDemo/user/actor-A/actor-B/actor-C
   * [actor-C] got message: I am the boss from sender: (akka://AkkActorHierarchyDemo/user/actor-A)
   */

  actorSystem.terminate()

}
