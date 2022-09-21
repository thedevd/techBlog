package main.scala.com.thedevd.scalaexamples.akka.actor

import akka.actor.{ Actor, ActorRef, ActorSystem, Props }

/**
 * Maintaining a kind of counter in a multi-threaded environment is a classic example of Race condition,
 * If we forget about Java AtomicInteger, then implementing a counter in multi-threaded env is a pain in the neck
 * because we have to think about locking and synchronizing updates to counter variable.
 *
 * But doing this is so so simple using the Akka Actor model.
 * We have defined an actor 'Counter' which has a mutable variable 'counter' to store the current count value.
 * This actor reacts to messages - Increment, Decrement and Print (You can clearly see we do have message handlers
 * for each of those message types)
 *
 * Now if you would have gone through 'AkkaActorChangingHandler.scala' first, then we can ask the same question to
 * ourselves that can we do better than this, maybe get rid of maintaining a mutable state inside actor by changing
 * actor's message handler (context.become()). The answer is again yes we can do better by changing the actor's message
 * handler each time if receives a different type of msg which current msg handler is not supposed to handle.
 *
 * So look at the improved version of the 'Counter' Actor, I am calling it 'StatelessCounter' actor
 * -------------------------------------------------------------------------------------------------
 * Pay close attention to this custom msg handler 'counterReceive(currentCount: Int)', I created in 'StatelessCounter' actor
 *      override def receive: Receive = counterReceive(0)
 *
 *      def counterReceive(currentCount: Int): Receive = {
 *        case Increment => context.become(counterReceive(currentCount + 1))
 *        case Decrement => context.become(counterReceive(currentCount - 1))
 *        case Print => println(s"[StatelessCounter] current value of counter is: $currentCount")
 *        case unknownMsg => println(s"Sorry counter actor does not understand msg: $unknownMsg")
 *      }
 *
 * By default, this message handler has 0 as the initial count value. And when an actor receives -
 * - 'Increment' message, it overrides/replaces the same message handler but with a new incremented value
 * - 'Decrement' message, it overrides/replaces the same message handler but with a new decremented value.
 * - 'Print' message, it simply prints currentCount value available in 'counterReceive' message handler.
 */

object CounterActorExercise extends App {

  case object Increment
  case object Decrement
  case object Print

  class Counter extends Actor {
    var counter: Int = 0
    override def receive: Receive = {
      case Increment => counter += 1
      case Decrement => counter -= 1
      case Print => println(s"[Counter] current value of counter is: $counter")
      case unknownMsg => println(s"Sorry counter actor does not understand msg: $unknownMsg")
    }
  }

  val actorSystem: ActorSystem = ActorSystem("CounterActorDemo")
  val counter: ActorRef = actorSystem.actorOf(Props[Counter])

  (1 to 100).foreach(_ => counter ! Increment)
  (1 to 50).foreach(_ => counter ! Decrement)
  counter ! Print // [Counter] current value of counter is: 50

  // ##################################################################################################################
  // Stateless Counter
  class StatelessCounter extends Actor {
    /*
    override def receive: Receive = incrementReceive(0)

    def incrementReceive(currentCount: Int): Receive = {
      case Increment => context.become(incrementReceive(currentCount + 1))
      case Decrement => context.become(decrementReceive(currentCount - 1))
      case Print => println(s"current value of counter is: $currentCount")
    }

    def decrementReceive(currentCount: Int): Receive = {
      case Increment => context.become(incrementReceive(currentCount + 1))
      case Decrement => context.become(decrementReceive(currentCount - 1))
      case Print => println(s"current value of counter is: $currentCount")
    }
    */
    // Improve one
    override def receive: Receive = counterReceive(0)

    def counterReceive(currentCount: Int): Receive = {
      case Increment => context.become(counterReceive(currentCount + 1))
      case Decrement => context.become(counterReceive(currentCount - 1))
      case Print => println(s"[StatelessCounter] current value of counter is: $currentCount")
      case unknownMsg => println(s"Sorry counter actor does not understand msg: $unknownMsg")
    }
  }

  val statelessCounter: ActorRef = actorSystem.actorOf(Props[StatelessCounter])
  (1 to 100).foreach(_ => statelessCounter ! Increment)
  (1 to 49).foreach(_ => statelessCounter ! Decrement)
  statelessCounter ! Print // [StatelessCounter] current value of counter is: 51

  actorSystem.terminate()

}
