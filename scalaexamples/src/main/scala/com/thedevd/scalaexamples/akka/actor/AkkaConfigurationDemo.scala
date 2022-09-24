package main.scala.com.thedevd.scalaexamples.akka.actor

import akka.actor.{ Actor, ActorLogging, ActorRef, ActorSystem, Props }
import com.typesafe.config.{ Config, ConfigFactory }

object AkkaConfigurationDemo extends App {

  class MyActor extends Actor with ActorLogging {
    override def receive: Receive = {
      case message =>
        log.debug(s"debug logging: $message")
        log.info(s"info logging: $message")
        log.warning(s"warn logging: $message")
        log.error(s"error logging: $message")
    }
  }

  // ActorSystem with default akka provided configuration
  val actorSystem: ActorSystem = ActorSystem("actorSystemWithDefaultAkkaConfig")
  // Similar to calling -->  ActorSystem("actorSystemWithDefaultAkkaConfig", ConfigFactor.load())
  val myActor1: ActorRef = actorSystem.actorOf(Props[MyActor], "myActor-default-akka-config")
  myActor1 ! "akka default config loading"

  /**
   * Output
   * --------
   * [INFO] [09/24/2022 16:44:58.048] [actorSystemWithDefaultAkkaConfig-akka.actor.default-dispatcher-5] [akka://actorSystemWithDefaultAkkaConfig/user/myActor-default-akka-config] info logging: akka default config loading
   * [WARN] [09/24/2022 16:44:58.049] [actorSystemWithDefaultAkkaConfig-akka.actor.default-dispatcher-5] [akka://actorSystemWithDefaultAkkaConfig/user/myActor-default-akka-config] warn logging: akka default config loading
   * [ERROR] [09/24/2022 16:44:58.051] [actorSystemWithDefaultAkkaConfig-akka.actor.default-dispatcher-5] [akka://actorSystemWithDefaultAkkaConfig/user/myActor-default-akka-config] error logging: akka default config loading
   */

  actorSystem.terminate()
  Thread.sleep(2000)

  println("#############################################################################################################")
  /**
   * 1. Using akka inline configuration
   */
  val inlineConfig: String = "akka { loglevel = DEBUG }"
  val inlineAkkaConfig: Config = ConfigFactory.parseString(inlineConfig)
  val actorSystemWithInlineConfig: ActorSystem = ActorSystem("actorSystemWithInlineAkkaConfig", inlineAkkaConfig) // passing config explicitly to actorSystem
  val myActor2: ActorRef = actorSystemWithInlineConfig.actorOf(Props[MyActor], "myActor-inline-akka-config")
  myActor2 ! "Akka inline config loading"

  /**
   * Output
   * -------
   * DEBUG] [09/24/2022 16:45:00.081] [main] [EventStream(akka://actorSystemWithInlineAkkaConfig)] logger log1-Logging$DefaultLogger started
   * [DEBUG] [09/24/2022 16:45:00.081] [main] [EventStream(akka://actorSystemWithInlineAkkaConfig)] Default Loggers started
   * [DEBUG] [09/24/2022 16:45:00.091] [main] [akka.serialization.Serialization(akka://actorSystemWithInlineAkkaConfig)] Replacing JavaSerializer with DisabledJavaSerializer, due to `akka.actor.allow-java-serialization = off`.
   * [DEBUG] [09/24/2022 16:45:00.093] [actorSystemWithInlineAkkaConfig-akka.actor.default-dispatcher-7] [akka://actorSystemWithInlineAkkaConfig/user/myActor-inline-akka-config] debug logging: Akka inline config loading
   * [INFO] [09/24/2022 16:45:00.093] [actorSystemWithInlineAkkaConfig-akka.actor.default-dispatcher-7] [akka://actorSystemWithInlineAkkaConfig/user/myActor-inline-akka-config] info logging: Akka inline config loading
   * [WARN] [09/24/2022 16:45:00.094] [actorSystemWithInlineAkkaConfig-akka.actor.default-dispatcher-7] [akka://actorSystemWithInlineAkkaConfig/user/myActor-inline-akka-config] warn logging: Akka inline config loading
   * [ERROR] [09/24/2022 16:45:00.094] [actorSystemWithInlineAkkaConfig-akka.actor.default-dispatcher-7] [akka://actorSystemWithInlineAkkaConfig/user/myActor-inline-akka-config] error logging: Akka inline config loading
   * [DEBUG] [09/24/2022 16:45:00.095] [main] [CoordinatedShutdown(akka://actorSystemWithInlineAkkaConfig)] Running CoordinatedShutdown with reason [ActorSystemTerminateReason]
   * [DEBUG] [09/24/2022 16:45:00.095] [main] [CoordinatedShutdown(akka://actorSystemWithInlineAkkaConfig)] Performing phase [before-service-unbind] with [0] tasks
   * [DEBUG] [09/24/2022 16:45:00.095] [actorSystemWithInlineAkkaConfig-akka.actor.internal-dispatcher-4] [CoordinatedShutdown(akka://actorSystemWithInlineAkkaConfig)] Performing phase [service-unbind] with [0] tasks
   * [DEBUG] [09/24/2022 16:45:00.095] [actorSystemWithInlineAkkaConfig-akka.actor.internal-dispatcher-4] [CoordinatedShutdown(akka://actorSystemWithInlineAkkaConfig)] Performing phase [service-requests-done] with [0] tasks
   * [DEBUG] [09/24/2022 16:45:00.095] [actorSystemWithInlineAkkaConfig-akka.actor.internal-dispatcher-4] [CoordinatedShutdown(akka://actorSystemWithInlineAkkaConfig)] Performing phase [service-stop] with [0] tasks
   * [DEBUG] [09/24/2022 16:45:00.095] [actorSystemWithInlineAkkaConfig-akka.actor.internal-dispatcher-4] [CoordinatedShutdown(akka://actorSystemWithInlineAkkaConfig)] Performing phase [before-cluster-shutdown] with [0] tasks
   * [DEBUG] [09/24/2022 16:45:00.095] [actorSystemWithInlineAkkaConfig-akka.actor.internal-dispatcher-4] [CoordinatedShutdown(akka://actorSystemWithInlineAkkaConfig)] Performing phase [cluster-sharding-shutdown-region] with [0] tasks
   * [DEBUG] [09/24/2022 16:45:00.096] [actorSystemWithInlineAkkaConfig-akka.actor.internal-dispatcher-4] [CoordinatedShutdown(akka://actorSystemWithInlineAkkaConfig)] Performing phase [cluster-leave] with [0] tasks
   * [DEBUG] [09/24/2022 16:45:00.096] [actorSystemWithInlineAkkaConfig-akka.actor.internal-dispatcher-4] [CoordinatedShutdown(akka://actorSystemWithInlineAkkaConfig)] Performing phase [cluster-exiting] with [0] tasks
   * [DEBUG] [09/24/2022 16:45:00.096] [actorSystemWithInlineAkkaConfig-akka.actor.internal-dispatcher-4] [CoordinatedShutdown(akka://actorSystemWithInlineAkkaConfig)] Performing phase [cluster-exiting-done] with [0] tasks
   * [DEBUG] [09/24/2022 16:45:00.096] [actorSystemWithInlineAkkaConfig-akka.actor.internal-dispatcher-4] [CoordinatedShutdown(akka://actorSystemWithInlineAkkaConfig)] Performing phase [cluster-shutdown] with [0] tasks
   */

  actorSystemWithInlineConfig.terminate()
  Thread.sleep(2000)
  println("#############################################################################################################")

  /**
   * 2. Using config file (application.conf) - MOST RECOMMENDED WAY
   *    To use any file for configuration, akka always looks for application.conf file in app's resource directory. If its
   *    available then it uses it as default.
   *    So lets have an application.conf file in resource directory.
   */
  val applicationConfig: Config = ConfigFactory.load("application.conf")
  // Similar to calling ---> ConfigFactory.load(), bcz internally if no param is passed in load(), akka choose conf file name as application.conf
  val actorSystemUsingConfigFile: ActorSystem = ActorSystem("actorSystemUsingConfigFile", applicationConfig)
  val myActor3: ActorRef = actorSystemUsingConfigFile.actorOf(Props[MyActor], "myActor-akka-application-conf")
  myActor3 ! "Akka resources/application.conf loading"

  // We can also access any property from configuration using Config object reference i.e
  println("aStringConfig", applicationConfig.getString("aStringConfig"))
  println("aBooleanConfig", applicationConfig.getBoolean("aBooleanConfig"))
  println("otherConfig.aStringConfig", applicationConfig.getString("otherConfig.aStringConfig"))
  println("otherConfig.aIntConfig", applicationConfig.getInt("otherConfig.aIntConfig"))
  println("otherConfig.aNestedConfig.aNestedStringConfig", applicationConfig.getString("otherConfig.aNestedConfig.aNestedStringConfig")) // Nested

  /**
   * Output
   * -------
   * (aStringConfig,I am from application.conf)
   * (aBooleanConfig,true)
   * (otherConfig.aStringConfig,Scala is awesome)
   * (otherConfig.aIntConfig,100)
   * (otherConfig.aNestedConfig.aNestedStringConfig,I am a nested config value)
   * [INFO] [09/24/2022 16:45:02.123] [actorSystemUsingConfigFile-akka.actor.default-dispatcher-7] [akka://actorSystemUsingConfigFile/user/myActor-akka-application-conf] info logging: Akka resources/application.conf loading
   * [WARN] [09/24/2022 16:45:02.124] [actorSystemUsingConfigFile-akka.actor.default-dispatcher-7] [akka://actorSystemUsingConfigFile/user/myActor-akka-application-conf] warn logging: Akka resources/application.conf loading
   * [ERROR] [09/24/2022 16:45:02.124] [actorSystemUsingConfigFile-akka.actor.default-dispatcher-7] [akka://actorSystemUsingConfigFile/user/myActor-akka-application-conf] error logging: Akka resources/application.conf loading
   */

  actorSystemUsingConfigFile.terminate()
  Thread.sleep(2000)
  println("#############################################################################################################")

  /**
   * 3. Using separate conf file location other than resources/application.conf
   *    As we know that creating Actor system normally by just passing systemName, internally uses ConfigFactor.load()
   *    which is same as loading application.conf from resources if the file is there. However it is also possible
   *    to use a conf file which might not there directly in resources folder, it could be somewhere else in resources,
   *    then we can pass the path explicitly in ConfigFactory.load() to use that configuration file for akka.
   */

  val configFromOtherFile: Config = ConfigFactory.load("other/application.conf") // note- it looks path in resources directory
  val actorSystemUsingOtherConfFile: ActorSystem = ActorSystem("actorSystemUsingOtherConfFile", configFromOtherFile)
  val myActor4: ActorRef = actorSystemUsingOtherConfFile.actorOf(Props[MyActor], "myActor-other-conf-file")
  myActor4 ! "Akka resources/other/application.conf loading"

  println("aStringConfig", configFromOtherFile.getString("aStringConfig"))

  /**
   * Output
   * --------
   * (aStringConfig,I am from other/application.conf)
   * [ERROR] [09/24/2022 16:45:04.149] [actorSystemUsingOtherConfFile-akka.actor.default-dispatcher-5] [akka://actorSystemUsingOtherConfFile/user/myActor-other-conf-file] error logging: Akka resources/other/application.conf loading
   */

  actorSystemUsingOtherConfFile.terminate()
  Thread.sleep(2000)
  println("#############################################################################################################")

  /**
   * 4. Using different formats -
   *    .json, .properties
   */
  val jsonConfig: Config = ConfigFactory.load("other/application.json")
  val actorSystemUsingJsonConfig: ActorSystem = ActorSystem("actorSystemUsingJsonConfig", jsonConfig)
  val myActor5: ActorRef = actorSystemUsingJsonConfig.actorOf(Props[MyActor], "myActor-config-from-json-file")
  myActor5 ! "Akka json config resources/other/application.json loading"

  println("aStringConfig", jsonConfig.getString("aStringConfig"))

  /**
   * Output
   * --------
   * (aStringConfig,I am from other/application.json)
   * [ERROR] [09/24/2022 16:45:06.172] [actorSystemUsingJsonConfig-akka.actor.default-dispatcher-6] [akka://actorSystemUsingJsonConfig/user/myActor-config-from-json-file] error logging: Akka json config resources/other/application.json loading
   */

  actorSystemUsingJsonConfig.terminate()
  Thread.sleep(2000)
  println("#############################################################################################################")

  val propertiesConfig: Config = ConfigFactory.load("other/application.properties")
  val actorSystemUsingPropertiesConfig: ActorSystem = ActorSystem("actorSystemUsingPropertiesConfig", propertiesConfig)
  val myActor6: ActorRef = actorSystemUsingPropertiesConfig.actorOf(Props[MyActor], "myActor-config-from-properties-file")
  myActor6 ! "Akka resources/other/application.properties loading"

  println("aStringConfig", propertiesConfig.getString("aStringConfig"))

  /**
   * Output
   * -------
   * (aStringConfig,I am from other/application.properties)
   * [WARN] [09/24/2022 16:45:08.191] [actorSystemUsingPropertiesConfig-akka.actor.default-dispatcher-7] [akka://actorSystemUsingPropertiesConfig/user/myActor-config-from-properties-file] warn logging: Akka resources/other/application.properties loading
   * [ERROR] [09/24/2022 16:45:08.191] [actorSystemUsingPropertiesConfig-akka.actor.default-dispatcher-7] [akka://actorSystemUsingPropertiesConfig/user/myActor-config-from-properties-file] error logging: Akka resources/other/application.properties loading
   */

  actorSystemUsingPropertiesConfig.terminate()

}
