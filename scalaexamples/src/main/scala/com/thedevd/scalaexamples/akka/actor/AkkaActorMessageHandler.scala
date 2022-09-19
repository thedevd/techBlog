package main.scala.com.thedevd.scalaexamples.akka.actor

import akka.actor.{ Actor, ActorRef, ActorSystem, Props }

/**
 * We have already seen in AkkaActorDemo.scala, that how an actor is created.
 * Lets summarize it -
 * 1. Actor trait is extended.
 *    - class SimpleActor extends Actor {}
 * 2. receive() method has to be implemented from Actor trait.
 *      override def receive: PartialFunction[Any, Unit] = {}
 *
 *    TO BE NOTED that the return type of this method is PartialFunction[Any, Unit] that means
 *    - It can accept a message of Any type, and returns Unit.
 *    - The message should SERIALIZABLE.
 *      (Means JVM should be able to send message in byte form over the network to another JVM either on same machine
 *      or anywhere in world).
 *      So in practice for custom type case classes/objects is used because they are indirectly implements Serializable
 *      interface.
 *
 *    NOTE - For return type We can also use Receive 'type alias' which is same as PartialFunction[Any, Unit]
 *    i.e. internally akka define it as - 'type Receive = PartialFunction[Any, Unit]'
 *      override def receive: Receive = {}
 * 3. Define message handlers in receive methods to handle particular type of message.
 *    Case Pattern matching is used to define message handlers.
 *
 *    TO BE NOTED - if message sent to an actor does not match any handler including default one, then msgs go to akkaDeadLetter.
 *    'akkaDeadLetter' is the internal actor provided by ActorSystem, you can consider this as garbage of unhandled messages of actors.
 * 4. ActorSystem's actorOf is used to instantiate an actor which gives us back ActorRef.
 * 5. Using ActorRef, we send messages to actor using tell method (!)
 *
 *
 */
object AkkaActorMessageHandler extends App {

  case class Person(name: String)
  case object Earth
  class Employee(name: String, gender: String) extends Person(name) {
    override def toString: String = s"Employee($name, $gender)"
  }

  class SimpleActor extends Actor {
    //override def receive: PartialFunction[Any, Unit] = {
    override def receive: Receive = {
      case anyString: String => println(s"Hi I am string: $anyString")
      case anyInteger: Int => println(s"Hi I am integer: $anyInteger")
      //case employee: Employee => println(s"Hi I am Employee case class: $employee") // If you uncomment this, then sending msg of Employee type will be handled by this.
      case person: Person => println(s"Hi I am Person case class: $person")
      case employee: Employee => println(s"Hi I am Employee case class: $employee") // This will never be used as we have defined handler of its Parent class before this
      case Earth => println(s"Hi I am Earth case object")
      case unknownMsg => println(s"Sorry I do not have msg handler defined for this msg: $unknownMsg")
    }
  }

  val actorSystem: ActorSystem = ActorSystem("ActorSystemName")
  val actor1: ActorRef = actorSystem.actorOf(Props[SimpleActor], "simpleActor1")

  actor1 ! "Its string"
  actor1 ! 100
  actor1 ! Person("dev")
  actor1 ! Earth
  actor1 ! new Employee("ravi", "male") // This will always pick up handler for Parent class if it is defined before its handler
  actor1 ! 99.99 // unknownMsg type - gets handled by 'case unknownMsg' handler.
  // If suppose we do not have 'case unknownMsg' msg handler, then sending any unknown msg to this actor would have gone to akkaDeadLetter,
  // just try commenting that default handler then you would see this INFO msg by AKKA when sending 99.99
  // [INFO] [akkaDeadLetter][09/19/2022 20:31:24.822] [ActorSystemName-akka.actor.default-dispatcher-4] [akka://ActorSystemName/user/simpleActor1] Message [java.lang.Double] to Actor[akka://ActorSystemName/user/simpleActor1#199483293] was unhandled. [1] dead letters encountered. This logging can be turned off or adjusted with configuration settings 'akka.log-dead-letters' and 'akka.log-dead-letters-during-shutdown'.

  actorSystem.terminate()

  /**
   * Output
   * -----------
   * Hi I am string: Its string
   * Hi I am integer: 100
   * Hi I am Person case class: Person(dev)
   * Hi I am Earth case object
   * Hi I am Person case class: Employee(ravi, male)
   * Sorry I do not have msg handler defined for this msg: 99.99
   */

}
