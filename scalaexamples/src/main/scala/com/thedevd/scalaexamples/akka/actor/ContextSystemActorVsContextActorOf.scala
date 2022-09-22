package main.scala.com.thedevd.scalaexamples.akka.actor

import akka.actor.{ Actor, ActorRef, ActorSystem, Props }

/**
 * RECOMMENDED TO GO THROUGH 'AkkaChildActorDemo.scala and AkkaActorHierarchy.scala' FIRST.
 *
 * I would not lie to you guys, At beginning I started using context.system.actorOf() to create child actors inside an actor.
 * But I did not know that creating Child actors inside an actor using context.system.actorOf() is different than creating
 * child using context.actorOf() method. (I came to know about this fact while working with 'AkkaChildActorDemo.scala')
 *
 * Lets see what is that difference.
 * context.actorOf Vs context.system.actorOf in Akka tool kit
 * --------------------------------------------------------------
 * First of all both ways can allow us to create actors inside another Actor, but difference lies in where that created
 * actors are placed in the Akka Actor hierarchy.
 *
 * context.actorOf -
 *  So actor created using 'context.actorOf()' will be child of the actor where in is being created. So it will be
 *  underneath of actual Parent actor hierarchy.
 *
 *  Look at the example below where we have created actor-B inside actor-A and then actor-C inside actor-B, so
 *  the actor-B and actor-C hierarchy path looks like -
 *     [TryContextActorOf] Actor actor-B path is: akka://ContextSystemActorVsContextActorOf/user/actor-A/actor-B
 *     [TryContextActorOf] Actor actor-C path is: akka://ContextSystemActorVsContextActorOf/user/actor-A/actor-B/actor-C
 *
 * where as in case of context.system.actorOf -
 *  Actors created using 'context.system.actorOf' will be direct children of '/user Guardian actor', does not matter
 *  if you created it inside another actor itself.
 *
 *  Look at the example below where we have created actor_B_1 inside actor-A and then actor_C_1 insider actor-B_1, so
 *  the actor_B_1 and actor_C_1 shows directly under /user Guardian actor -
 *    [TryContextSystemActorOf] Actor actor-B_1 path is: akka://ContextSystemActorVsContextActorOf/user/actor-B_1
 *    [TryContextSystemActorOf] Actor actor-C_1 path is: akka://ContextSystemActorVsContextActorOf/user/actor-C_1
 *
 * CONCLUSION IS - TO CREATE CHILD ACTORS, ALWAYS USE 'context.actorOf()' method.
 *
 */
object ContextSystemActorVsContextActorOf extends App {

  case class TryContextSystemActorOf(fwdMsg: String)
  case class TryContextActorOf(fwdMsg: String)

  class ActorA extends Actor {
    override def receive: Receive = {
      case TryContextActorOf(msg) =>
        println(s"[TryContextActorOf] Actor ${self.path.name} path is: ${self.path}")
        val actorBAsChild: ActorRef = context.actorOf(Props[ActorB], "actor-B")
        actorBAsChild ! TryContextActorOf(msg)

      case TryContextSystemActorOf(msg) =>
        println(s"[TryContextSystemActorOf] Actor ${self.path.name} path is: ${self.path}")
        val actorBAsChild: ActorRef = context.system.actorOf(Props[ActorB], "actor-B_1")
        actorBAsChild ! TryContextSystemActorOf(msg)
    }
  }

  class ActorB extends Actor {
    override def receive: Receive = {
      case TryContextActorOf(msg) =>
        println(s"[TryContextActorOf] Actor ${self.path.name} path is: ${self.path}")
        val actorCAsChild: ActorRef = context.actorOf(Props[ActorC], "actor-C")
        actorCAsChild forward TryContextActorOf(msg)

      case TryContextSystemActorOf(msg) =>
        println(s"[TryContextSystemActorOf] Actor ${self.path.name} path is: ${self.path}")
        val actorCAsChild: ActorRef = context.system.actorOf(Props[ActorC], "actor-C_1")
        actorCAsChild forward TryContextSystemActorOf(msg)
    }
  }

  class ActorC extends Actor {
    override def receive: Receive = {
      case TryContextActorOf(_) =>
        println(s"[TryContextActorOf] Actor ${self.path.name} path is: ${self.path}")

      case TryContextSystemActorOf(_) =>
        println(s"[TryContextSystemActorOf] Actor ${self.path.name} path is: ${self.path}")
    }
  }

  val actorSystem: ActorSystem = ActorSystem("ContextSystemActorVsContextActorOf")
  val parentActorA: ActorRef = actorSystem.actorOf(Props[ActorA], "actor-A")

  parentActorA ! TryContextActorOf("Creating Child Actor using context.actorOf()")
  Thread.sleep(1000)
  parentActorA ! TryContextSystemActorOf("Creating Child Actor using context.system.actorOf()")

  /**
   * Console Output
   * -----------------
   * [TryContextActorOf] Actor actor-A path is: akka://ContextSystemActorVsContextActorOf/user/actor-A
   * [TryContextActorOf] Actor actor-B path is: akka://ContextSystemActorVsContextActorOf/user/actor-A/actor-B
   * [TryContextActorOf] Actor actor-C path is: akka://ContextSystemActorVsContextActorOf/user/actor-A/actor-B/actor-C
   *
   * [TryContextSystemActorOf] Actor actor-A path is: akka://ContextSystemActorVsContextActorOf/user/actor-A
   * [TryContextSystemActorOf] Actor actor-B_1 path is: akka://ContextSystemActorVsContextActorOf/user/actor-B_1
   * [TryContextSystemActorOf] Actor actor-C_1 path is: akka://ContextSystemActorVsContextActorOf/user/actor-C_1
   *
   */

  actorSystem.terminate()
}
