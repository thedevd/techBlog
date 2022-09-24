package main.scala.com.thedevd.scalaexamples.akka.actor

import akka.actor.{ Actor, ActorLogging, ActorRef, ActorSystem, Props }
import com.typesafe.config.ConfigFactory

/**
 *
 * ActorLogging trait
 * ----------------------
 * We can easily obtain a reference to a logger by mixing in ActorLogging trait into your Actor
 * Once you mixed in ActorLogging, that logger reference can be accessed "log" variable. See this
 *    class MyActor extends Actor with ActorLogging {
 *        override def receive: Receive = {
 *          case message =>
 *            log.debug(s"debug logging: $message")
 *            log.info(s"info logging: $message")
 *            log.warning(s"warn logging: $message")
 *            log.error(s"error logging: $message")
 *        }
 *    }
 *
 *
 * Default logging level in Akka
 * --------------------------------
 * Akka Logging level -
 * 1. DEBUG
 * 2. INFO
 * 3. WARN (warning)
 * 4. ERROR
 *
 * Default loglevel in Akka is - INFO, that is the reason in example above when we created MyActor using default actorSystem
 * i.e. ActorSystem("AkkaActorLoggingDemo"), debug level message not printed in console.
 * (NOTE- changing the loglevel can be done by playing with Akka configuration)
 *
 * Playing with Akka configuration is explained in detail in another section (AkkaConfigurationDemo.scala).
 * Just to give an idea here, I have demonstrated use of an inline akka configuration in the ActorSystem,
 * where we changed the akka default 'loglevel' to DEBUG
 *    val akkaInlineConfiguration: String =
 *    """
 *      |akka {
 *      | loglevel = DEBUG
 *      |}
 *      |""".stripMargin
 *
 *    val actorSystemConfig = ConfigFactory.parseString(akkaInlineConfiguration)
 *    val actorSystemUsingInlineConfig: ActorSystem = ActorSystem("actorSystemUsingInlineConfig", actorSystemConfig)
 *
 * So actors created using above actorSystem 'actorSystemUsingInlineConfig' will use the supplied Akka configuration.
 * Same thing we can see that 'myActor2' is now printing the DEBUG messages as well, as we changed akka loglevel to DEBUG.
 *
 */
object AkkaActorLoggingDemo extends App {

  class MyActor extends Actor with ActorLogging {
    override def receive: Receive = {
      case message =>
        log.debug(s"debug logging: $message")
        log.info(s"info logging: $message")
        log.warning(s"warn logging: $message")
        log.error(s"error logging: $message")
    }
  }

  val actorSystem: ActorSystem = ActorSystem("AkkaActorLoggingDemo")
  val myActor: ActorRef = actorSystem.actorOf(Props[MyActor], "myActor")

  myActor ! "Akka logging is nice!"
  /**
   * Output from myActor
   * ----------------------
   * INFO] [09/24/2022 09:55:48.571] [AkkaActorLoggingDemo-akka.actor.default-dispatcher-5] [akka://AkkaActorLoggingDemo/user/myActor] info logging: Akka logging is nice!
   * [WARN] [09/24/2022 09:55:48.572] [AkkaActorLoggingDemo-akka.actor.default-dispatcher-5] [akka://AkkaActorLoggingDemo/user/myActor] warn logging: Akka logging is nice!
   * [ERROR] [09/24/2022 09:55:48.574] [AkkaActorLoggingDemo-akka.actor.default-dispatcher-5] [akka://AkkaActorLoggingDemo/user/myActor] error logging: Akka logging is nice!
   */

  val akkaInlineConfiguration: String =
    """
      |akka {
      | loglevel = DEBUG
      |}
      |""".stripMargin

  val actorSystemConfig = ConfigFactory.parseString(akkaInlineConfiguration)
  val actorSystemUsingInlineConfig: ActorSystem = ActorSystem("actorSystemUsingInlineConfig", actorSystemConfig)
  val myActor2: ActorRef = actorSystemUsingInlineConfig.actorOf(Props[MyActor], "myActor2")
  myActor2 ! "Akka logging level changed to DEBUG"

  /**
   * Output from myActor2
   * ---------------------
   * [DEBUG] [09/24/2022 10:10:56.333] [main] [EventStream(akka://actorSystemUsingInlineConfig)] logger log1-Logging$DefaultLogger started
   * [DEBUG] [09/24/2022 10:10:56.333] [main] [EventStream(akka://actorSystemUsingInlineConfig)] Default Loggers started
   * [DEBUG] [09/24/2022 10:10:56.345] [main] [akka.serialization.Serialization(akka://actorSystemUsingInlineConfig)] Replacing JavaSerializer with DisabledJavaSerializer, due to `akka.actor.allow-java-serialization = off`.
   * [DEBUG] [09/24/2022 10:10:56.347] [actorSystemUsingInlineConfig-akka.actor.default-dispatcher-5] [akka://actorSystemUsingInlineConfig/user/myActor2] debug logging: Akka logging level changed to DEBUG
   * [INFO] [09/24/2022 10:10:56.347] [actorSystemUsingInlineConfig-akka.actor.default-dispatcher-5] [akka://actorSystemUsingInlineConfig/user/myActor2] info logging: Akka logging level changed to DEBUG
   * [WARN] [09/24/2022 10:10:56.347] [actorSystemUsingInlineConfig-akka.actor.default-dispatcher-5] [akka://actorSystemUsingInlineConfig/user/myActor2] warn logging: Akka logging level changed to DEBUG
   * [ERROR] [09/24/2022 10:10:56.347] [actorSystemUsingInlineConfig-akka.actor.default-dispatcher-5] [akka://actorSystemUsingInlineConfig/user/myActor2] error logging: Akka logging level changed to DEBUG
   *
   * [DEBUG] [09/24/2022 10:56:07.582] [main] [CoordinatedShutdown(akka://actorSystemUsingInlineConfig)] Running CoordinatedShutdown with reason [ActorSystemTerminateReason]
   * [DEBUG] [09/24/2022 10:56:07.582] [main] [CoordinatedShutdown(akka://actorSystemUsingInlineConfig)] Performing phase [before-service-unbind] with [0] tasks
   * [DEBUG] [09/24/2022 10:56:07.582] [actorSystemUsingInlineConfig-akka.actor.internal-dispatcher-2] [CoordinatedShutdown(akka://actorSystemUsingInlineConfig)] Performing phase [service-unbind] with [0] tasks
   * [DEBUG] [09/24/2022 10:56:07.582] [actorSystemUsingInlineConfig-akka.actor.internal-dispatcher-2] [CoordinatedShutdown(akka://actorSystemUsingInlineConfig)] Performing phase [service-requests-done] with [0] tasks
   * [DEBUG] [09/24/2022 10:56:07.582] [actorSystemUsingInlineConfig-akka.actor.internal-dispatcher-2] [CoordinatedShutdown(akka://actorSystemUsingInlineConfig)] Performing phase [service-stop] with [0] tasks
   * [DEBUG] [09/24/2022 10:56:07.582] [actorSystemUsingInlineConfig-akka.actor.internal-dispatcher-2] [CoordinatedShutdown(akka://actorSystemUsingInlineConfig)] Performing phase [before-cluster-shutdown] with [0] tasks
   * [DEBUG] [09/24/2022 10:56:07.582] [actorSystemUsingInlineConfig-akka.actor.internal-dispatcher-2] [CoordinatedShutdown(akka://actorSystemUsingInlineConfig)] Performing phase [cluster-sharding-shutdown-region] with [0] tasks
   * [DEBUG] [09/24/2022 10:56:07.582] [actorSystemUsingInlineConfig-akka.actor.internal-dispatcher-2] [CoordinatedShutdown(akka://actorSystemUsingInlineConfig)] Performing phase [cluster-leave] with [0] tasks
   * [DEBUG] [09/24/2022 10:56:07.582] [actorSystemUsingInlineConfig-akka.actor.internal-dispatcher-2] [CoordinatedShutdown(akka://actorSystemUsingInlineConfig)] Performing phase [cluster-exiting] with [0] tasks
   * [DEBUG] [09/24/2022 10:56:07.582] [actorSystemUsingInlineConfig-akka.actor.internal-dispatcher-2] [CoordinatedShutdown(akka://actorSystemUsingInlineConfig)] Performing phase [cluster-exiting-done] with [0] tasks
   * [DEBUG] [09/24/2022 10:56:07.582] [actorSystemUsingInlineConfig-akka.actor.internal-dispatcher-2] [CoordinatedShutdown(akka://actorSystemUsingInlineConfig)] Performing phase [cluster-shutdown] with [0] tasks
   * [DEBUG] [09/24/2022 10:56:07.583] [actorSystemUsingInlineConfig-akka.actor.internal-dispatcher-2] [CoordinatedShutdown(akka://actorSystemUsingInlineConfig)] Performing phase [before-actor-system-terminate] with [0] tasks
   * [DEBUG] [09/24/2022 10:56:07.583] [actorSystemUsingInlineConfig-akka.actor.internal-dispatcher-2] [CoordinatedShutdown(akka://actorSystemUsingInlineConfig)] Performing phase [actor-system-terminate] with [1] tasks.
   * [DEBUG] [09/24/2022 10:56:07.583] [actorSystemUsingInlineConfig-akka.actor.internal-dispatcher-2] [CoordinatedShutdown(akka://actorSystemUsingInlineConfig)] Performing task [terminate-system] in CoordinatedShutdown phase [actor-system-terminate]
   * [DEBUG] [09/24/2022 10:56:07.592] [actorSystemUsingInlineConfig-akka.actor.internal-dispatcher-8] [EventStream] shutting down: StandardOutLogger
   */

  actorSystem.terminate()
  actorSystemUsingInlineConfig.terminate()

}
