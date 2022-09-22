package main.scala.com.thedevd.scalaexamples.akka.actor

import akka.actor.{ Actor, ActorRef, ActorSystem, Props }

/**
 * Till now we have seen that we created so many actors outside an actor using ActorSystem's actorOf() method.
 *
 * However Akka also allows an Actor to create child actor from inside, and it is possible using an implicit variable
 * which stores the information about the environment where actor is running.
 *
 * Can you think of that implicit variable, let me hint you - do you remember anything from
 * AkkaActorContext.scala where we talked about an implicit variable.
 * Yeah you got it right, using 'context' variable an Actor can create child actor by using context.actorOf() method.
 *    - context.actorOf()
 *
 * In the example below, I have used context.actorOf method to create Child actor of Parent actor.
 *      class Parent extends Actor {
 *        override def receive: Receive = {
 *          case CreateChild(name) =>
 *            println(s"[Parent Actor] ${self.path} is going to create child actor with name: $name")
 *            val child: ActorRef = context.actorOf(Props[Child], name)
 *            child ! "Hello my child"
 *        }
 *      }
 *
 * Take away from this example's output
 * ----------------------------------------
 * Just try to run this demo once, and look at the output closely, the output would look like -
 *    [Parent Actor] akka://ParentChildDemo/user/parent is going to create child actor with name: dearChild
 *    [Child Actor] akka://ParentChildDemo/user/parent/dearChild, my parent said to me: Hello my child
 *
 * So pay attention to the akka provided actor path which I have printed in Parent and Child Actor.
 * - The path for Parent starts something like-
 *      akka://<ActorSystemName>/user/<actorName>
 *        i.e. akka://ParentChildDemo/user/parent
 * - The path for Child actor starts something like -
 *      akka://<ActorSystemName>/user/<ParentActorName>/<actorName>
 *        i.e. akka://ParentChildDemo/user/parent/dearChild
 *
 *  This proves that Parent Actor 'parent' has a child 'dearChild' underneath its hierarchy.
 *
 *  WORTH TO READ -
 *  ----------------
 *  In above output, you will see one of the thing in COMMON, that both actors 'parent' and 'dearChild' has /user
 *  in common. So here /user is 'user guardian actor'. Just remember that all the actors that we create explicitly
 *  comes under '/user guardian actor'.
 *
 */
object AkkaChildActorDemo extends App {

  case class CreateChild(name: String)
  class Parent extends Actor {
    override def receive: Receive = {
      case CreateChild(name) =>
        println(s"[Parent Actor] ${self.path} is going to create child actor with name: $name")
        val child: ActorRef = context.actorOf(Props[Child], name) // context.actorOf is used to create child actor
        child ! "Hello my child"
    }
  }

  class Child extends Actor {
    override def receive: Receive = {
      case message => println(s"[Child Actor] ${self.path}, my parent said to me: $message")
    }
  }

  val actorSystem: ActorSystem = ActorSystem("ParentChildDemo")
  val parent: ActorRef = actorSystem.actorOf(Props[Parent], "parent")
  parent ! CreateChild("dearChild")

  /**
   * Program output
   * -------------------
   * [Parent Actor] akka://ParentChildDemo/user/parent is going to create child actor with name: dearChild
   * [Child Actor] akka://ParentChildDemo/user/dearChild, my parent said to me: Hello my child
   */

  actorSystem.terminate()

}
