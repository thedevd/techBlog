package main.scala.com.thedevd.scalaexamples.akka.actor

import akka.actor.ActorSystem

object AkkaActorDemo extends App {
  val actorSystem = ActorSystem("HelloActorSystem")
  println(actorSystem.name)
}
