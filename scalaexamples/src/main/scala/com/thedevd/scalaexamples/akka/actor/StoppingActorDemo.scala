package main.scala.com.thedevd.scalaexamples.akka.actor

import akka.actor.{ Actor, ActorRef, ActorSystem, Kill, PoisonPill, Props }

/**
 * There are situations where we might need to stop an actor (May be need to stop further processing of the message by the actor 
 * or may be we want to reinitialize the actor by stopping and starting it again).
 *
 * In Akka there are 3 ways to stop an actor, which kind a confuse to beginners that why the hell we have these 3 ways.
 * Here I am going to explain them with proper example i.e. which way does what and which to choose when.
 *
 * Way #1 - Stopping Actor FORCEFULLY
 * --------------------------------------
 * WHEN TO USE -
 * Generally this should be avoided as it forces the actor to stop,
 * but can be used if there is need to immediately stop an actor after the current message being processed is finished
 *
 * WHAT HAPPENS -
 * As I mentioned calling actorSystem.stop(actorRef),
 * stops the actor immediately after it finishes processing the current message. (You can very well see this behaviour
 * in the program output which is shown at the bottom, where parentActor seems stopped immediately after it finished
 * current message ("stop demo", "MyChild-1"), after that other two messages we sent went to akkaDeadLetter queue)
 *
 * From the output these points can be summarized -
 * 1. Actor stops immediately after completes current msg processing and suspends it mailbox. Any further msgs after current msg
 *    are not processed (All go to dead letter queue)
 * 2. Actor waits for any child actor to stop.
 * 3. Actor calls postStop() lifecycle hook method for any resource-cleanup tasks.
 * 4. Actor shutdown completely.
 *
 * TO BE NOTED - to stop an actor forcefully, we called .stop() method on actorSystem mentioning the ActorRef of that actor i.e.
 *  actorSystem.stop(parentActor)
 *
 *
 * Way #2/#3 - Stopping Actor GRACEFULLY
 * --------------------------------------
 * WHEN TO USE -
 * This way is mostly adopted one, where actor does not stop itself immediately, rather it completes processing of all available msgs
 * which were there in queue before sending stop request message to actor.
 * For example- if actor's mailbox has 100 messages, and it is processing say 50 the messages, and we sent stop messages
 * to the actor (which will be stored as 101 message in queue), it means remaining 50 messages will be processed as normal, and
 * once actor finds 101th message as stop message and it will starts its stop process. After this no messages will be processed.
 *
 * You can very well see in the program output shown at bottom, that after sending PoisonPill/Kill message,
 * ParentActor-2/ParentActor-3 process the all first 3 messages as normal, then wait for their child actor to stop,
 * then finally it shutdown it (call postStop before shutdown) and other 5th messages sent after it is stopped, that
 * msg goes to dead letter queue.
 *
 * There are two ways to stop actor gracefully  -
 * 1. Sending PoisonPill message
 * 2. Sending Kill message.
 *
 * This is to be noted, unlike first way where we called a special method on actor syste (stop()),
 * these PoisonKill and Kill are not special type of Scala case object(class) not methods.
 * It means actor treats PoisonPill and Kill as ordinary messages which are sent just like normal messages, I mean
 * they go to back (tail) for the mailbox (queue)
 *
 * WHAT HAPPENS -
 * 1. We send PoisonPill/Kill message to actor, actor appends it to the end of mailbox.
 * 2. Actor does not stop immediately rather it process all messages which are before PoisonPill/Kill messages.
 * 3. Actor encounters PoisonPill/Kill message, it immediately suspends mailbox so no messages after that will be processed.
 * 4. Actor waits for Child-actor to stop first (Child actors call postStop() hook of their own)
 * 5. Actor calls postStop() lifecycle hook to perform its cleanup tasks.
 * 6. Actor stops finally.
 *
 *
 * PoisonPill vs. Kill
 * ------------------------
 * Actor behaves exactly same when it encounters PoisonPill or Kill message, only there is slight difference in
 * behaviour with Kill message, in Kill case actor also logs the ERROR message about actor being stopped by Kill command
 * You can very well see this in ########### Kill demo
 *  [ERROR] [09/12/2022 15:13:54.143] [StoppingActorDemo-akka.actor.internal-dispatcher-3] [akka://StoppingActorDemo/user/ParentActor-2] Kill (akka.actor.ActorKilledException: Kill)
 *
 */

object StoppingActorDemo extends App {

  class ParentActor extends Actor {
    override def postStop(): Unit = {
      super.postStop()
      println("Calling postStop() on parent-actor")
    }

    override def receive: Receive = {
      case (message: String, childActorName: String) =>
        println(s"In parent-actor ${self.path.name}, message received: ($message, $childActorName)")
        val childActor = context.actorOf(Props[ChildActor], s"Child-$childActorName")
        childActor ! message
      case _ => println("ParentActor dont understand this language")
    }
  }

  class ChildActor extends Actor {

    override def postStop(): Unit = {
      super.postStop()
      println(s"Calling postStop() on child-actor: ${self.path.name}")
    }

    override def receive: Receive = {
      case messageFromParent: String =>
        Thread.sleep(500)
        println(s"In child-actor ${self.path.name}, Message received from parent-actor: $messageFromParent")
    }
  }

  // Creating actorSystem first
  val actorSystem: ActorSystem = ActorSystem("StoppingActorDemo")

  println("############### Stop demo")
  val parentActor: ActorRef = actorSystem.actorOf(Props[ParentActor], "ParentActor")
  parentActor ! ("stop demo", "MyChild-1")
  parentActor ! ("stop demo", "MyChild-2")
  parentActor ! ("stop demo", "MyChild-3")

  // using stop() method on actorSystem to stop actor forcefully
  actorSystem.stop(parentActor)

  Thread.sleep(3000)

  println("############### Kill demo")
  val parentActor2: ActorRef = actorSystem.actorOf(Props[ParentActor], "ParentActor-2")
  parentActor2 ! ("Kill demo", "MyChild-1")
  parentActor2 ! ("Kill demo", "MyChild-2")
  parentActor2 ! ("Kill demo", "MyChild-3")

  // sending Kill message to actor to stop it gracefully
  parentActor2 ! Kill
  parentActor2 ! ("Kill demo", "MyChild-4") // This msg will go to dead letter queue as actor stopped already

  Thread.sleep(3000)

  println("############### PoisonKill demo")
  val parentActor3: ActorRef = actorSystem.actorOf(Props[ParentActor], "ParentActor-3")
  parentActor3 ! ("PoisonPill demo", "MyChild-1")
  parentActor3 ! ("PoisonPill demo", "MyChild-2")
  parentActor3 ! ("PoisonPill demo", "MyChild-3")

  // sending PoisonPill message to actor to stop it gracefully
  parentActor3 ! PoisonPill
  parentActor3 ! ("PoisonPill demo", "MyChild-4") // This msg will go to dead letter queue as actor stopped already

  Thread.sleep(3000)
  actorSystem.terminate()

  /**
   * Output of the program -
   * --------------------------
   * ############### Stop demo
   * In parent-actor ParentActor, message received: (stop demo, MyChild-1)
   * In child-actor Child-MyChild-1, Message received from parent-actor: stop demo
   * Calling postStop() on child-actor: Child-MyChild-1
   * Calling postStop() on parent-actor
   * [INFO] [akkaDeadLetter][09/12/2022 15:13:51.645] [StoppingActorDemo-akka.actor.default-dispatcher-4] [akka://StoppingActorDemo/user/ParentActor] Message [scala.Tuple2] to Actor[akka://StoppingActorDemo/user/ParentActor#-135704561] was not delivered. [1] dead letters encountered. If this is not an expected behavior then Actor[akka://StoppingActorDemo/user/ParentActor#-135704561] may have terminated unexpectedly. This logging can be turned off or adjusted with configuration settings 'akka.log-dead-letters' and 'akka.log-dead-letters-during-shutdown'.
   * [INFO] [akkaDeadLetter][09/12/2022 15:13:51.647] [StoppingActorDemo-akka.actor.default-dispatcher-4] [akka://StoppingActorDemo/user/ParentActor] Message [scala.Tuple2] to Actor[akka://StoppingActorDemo/user/ParentActor#-135704561] was not delivered. [2] dead letters encountered. If this is not an expected behavior then Actor[akka://StoppingActorDemo/user/ParentActor#-135704561] may have terminated unexpectedly. This logging can be turned off or adjusted with configuration settings 'akka.log-dead-letters' and 'akka.log-dead-letters-during-shutdown'.
   *
   * ############### Kill demo
   * In parent-actor ParentActor-2, message received: (Kill demo, MyChild-1)
   * In parent-actor ParentActor-2, message received: (Kill demo, MyChild-2)
   * In parent-actor ParentActor-2, message received: (Kill demo, MyChild-3)
   * [ERROR] [09/12/2022 15:13:54.143] [StoppingActorDemo-akka.actor.internal-dispatcher-3] [akka://StoppingActorDemo/user/ParentActor-2] Kill (akka.actor.ActorKilledException: Kill)
   * In child-actor Child-MyChild-1, Message received from parent-actor: Kill demo
   * In child-actor Child-MyChild-2, Message received from parent-actor: Kill demo
   * In child-actor Child-MyChild-3, Message received from parent-actor: Kill demo
   * Calling postStop() on child-actor: Child-MyChild-1
   * Calling postStop() on child-actor: Child-MyChild-2
   * Calling postStop() on child-actor: Child-MyChild-3
   * Calling postStop() on parent-actor
   * [INFO] [akkaDeadLetter][09/12/2022 15:13:54.645] [StoppingActorDemo-akka.actor.default-dispatcher-7] [akka://StoppingActorDemo/user/ParentActor-2] Message [scala.Tuple2] to Actor[akka://StoppingActorDemo/user/ParentActor-2#915139138] was not delivered. [3] dead letters encountered. If this is not an expected behavior then Actor[akka://StoppingActorDemo/user/ParentActor-2#915139138] may have terminated unexpectedly. This logging can be turned off or adjusted with configuration settings 'akka.log-dead-letters' and 'akka.log-dead-letters-during-shutdown'.
   *
   * ############### PoisonKill demo
   * In parent-actor ParentActor-3, message received: (PoisonPill demo, MyChild-1)
   * In parent-actor ParentActor-3, message received: (PoisonPill demo, MyChild-2)
   * In parent-actor ParentActor-3, message received: (PoisonPill demo, MyChild-3)
   * In child-actor Child-MyChild-1, Message received from parent-actor: PoisonPill demo
   * In child-actor Child-MyChild-2, Message received from parent-actor: PoisonPill demo
   * In child-actor Child-MyChild-3, Message received from parent-actor: PoisonPill demo
   * Calling postStop() on child-actor: Child-MyChild-1
   * Calling postStop() on child-actor: Child-MyChild-2
   * Calling postStop() on child-actor: Child-MyChild-3
   * Calling postStop() on parent-actor
   * [INFO] [akkaDeadLetter][09/12/2022 15:13:57.645] [StoppingActorDemo-akka.actor.default-dispatcher-4] [akka://StoppingActorDemo/user/ParentActor-3] Message [scala.Tuple2] to Actor[akka://StoppingActorDemo/user/ParentActor-3#277496909] was not delivered. [4] dead letters encountered. If this is not an expected behavior then Actor[akka://StoppingActorDemo/user/ParentActor-3#277496909] may have terminated unexpectedly. This logging can be turned off or adjusted with configuration settings 'akka.log-dead-letters' and 'akka.log-dead-letters-during-shutdown'.
   *
   */
}
