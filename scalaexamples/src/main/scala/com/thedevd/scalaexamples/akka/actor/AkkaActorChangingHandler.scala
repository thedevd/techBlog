package main.scala.com.thedevd.scalaexamples.akka.actor

import akka.actor.{ Actor, ActorRef, ActorSystem, Props }

/**
 * Lets first understand the example which is given here.
 * -------------------------------------------------------------
 * I have tried to mimic a funny conversation b/w a wife and husband. Husband first sends several commands to wife
 * such as - CookFood, LetsParty, Abuse. And the msg wife gets changes her mood such as - Happy, Sad, Angry. So basically
 * - Wife's mood changes to HAPPY, if husbands tell her LetsParty
 * - Wife's mood changes to SAD, if husband tell her to CookFood
 * - Wife's mood changes to ANGRY, if husband abuse her (please I meant casual abuse)
 *
 * And when Husband Ask further anything to Wife, then based on her mood, wife can reply either Accept, Reject or slap i.e.
 * - If wife's mood HAPPY, she can reply with Accept (WifeAccept)
 * - If wife's mood SAD, she can reply with a Deny (WifeReject)
 * - If wife's mood ANGRY, then she can reply with a slap (WifeSlap)
 *
 *
 * At first attempt, I have tried to model this conversation using common style of using a mutable variable.
 * (See Wife actor definition)
 * - Wife actor has a mutable variable called 'mood' which holds the mood of her (HAPPY, SAD or ANGRY).
 * - Wife changes its mood based on message it gets from Husband.
 * - Wife replies (Accept, Reject or Slap) to Husband based on her mood.
 *    If else if pattern is used to decide which type of Reply need to send to Husband.
 *
 * Although there is nothing wrong in this approach, but its not kind a scala style because -
 * 1. Using a mutable variable in scala (FP) looks ugly and most of the time discouraged
 * 2. If else If pattern could go complex if we keep on adding more moods.
 *
 * So can we do it in more better way? The answer is yes, we can do better to model the solution by changing
 * Wife actor context again and again. With this approach we can get rid of maintaining a mutable variable + no
 * if else if patter needed to reply to Husband based on mood.
 * (See StatelessWife actor definition)
 *
 * In second attempt, I have used actor context.become() method to change actor's message handler based on the
 * message it processes. So you can see we have total 3 different message handler defined -
 * 1. def happyReceive: Receive = {}
 * 2. def sadReceive: Receive = {}
 * 3. def angryReceive: Receive = {}
 *
 * And default message handler I have kept is happyReceive i.e.
 *    override def receive: Receive = happyReceive
 *
 * In happyReceive message handler we are saying that, if you receive msg other than LetsParty, then change the
 * message handler which is responsible to handle that particular message.
 * For eg. If 'CookFood' msg is received then we change actor's context to become 'sadReceive' message handler
 *
 *    def happyReceive: Receive = {
 *      case CookFood => context.become(sadReceive) // change message handler to sadReceive
 *      case LetsParty => // lets continue with current message handler
 *      case Abuse => context.become(abuseReceive) // change message handler to abuseReceive
 *      case Ask(_) => sender ! WifeAccept
 *    }
 *
 * Lets understand the flow using the example below (The same is used in code block from line #185)
 * -------------------------------------------------------------------------------------------------
 * 1. Initially 'happyReceive' is the default handler in StatelessWife actor
 * 2. husband ! AskWifeToGoParty(statelessWife)
 *      Still the StatelessWife's msg handler is same i.e. 'happyReceive'
 * 3. husband ! AskSomethingElse(statelessWife, "what happened")
 *      StatelessWife replied with 'WifeAccept' using current msg handler which is 'happyReceive'
 *
 * 4. husband ! AskWifeToCookFood(statelessWife)
 *      As per StatelessWife's 'happyReceive' msg handler, upon receiving CookFood, it changes its msg handler to 'sadReceive'
 *      so now current handler in StatelessWife has become 'sadReceive', mean from now own msg will be handler using this handler.
 * 5. husband ! AbuseWife(statelessWife)
 *      As per StatelessWife's 'sadReceive' msg handler, upon receiving Abuse, it changes its msg handler to 'abuseReceive'
 *      so now current handler in StatelessWife has become 'abuseReceive'.
 * 6. husband ! AskSomethingElse(wife, "what happened dear")
 *      StatelessWife replied with 'WifeSlap' using current msg handler which is 'abuseReceive'
 *
 * If you notice, I am changing the actor's msg handler back and forth using context.become() method.
 *
 */
object AkkaActorChangingHandler extends App {

  object Wife {
    // mood
    val HAPPY_MOOD = "HAPPY"
    val SAD_MOOD = "SAD"
    val ANGRY_MOOD = "ANGRY"

    // Command
    case object CookFood
    case object LetsParty
    case object Abuse

    case class Ask(msg: String)

    // Reply
    case object WifeAccept
    case object WifeReject
    case object WifeSlap
  }

  // Using common style of using mutable variable and simple if else if patter to reply based on mood
  class Wife extends Actor {
    import Wife._

    var mood: String = HAPPY_MOOD
    override def receive: Receive = {
      case CookFood => mood = SAD_MOOD
      case LetsParty => mood = HAPPY_MOOD
      case Abuse => mood = ANGRY_MOOD
      case Ask(_) =>
        if(mood == SAD_MOOD) {
          // reply with Reject
          sender ! WifeReject
        } else if(mood == HAPPY_MOOD) {
          // reply with Accept
          sender ! WifeAccept
        } else {
          // for mood == ANGRY, reply with slap
          sender ! WifeSlap
        }
    }
  }

  object Husband {
    case class AskWifeToCookFood(wife: ActorRef)
    case class AskWifeToGoParty(wife: ActorRef)
    case class AbuseWife(wife: ActorRef)
    case class AskSomethingElse(wife: ActorRef, msg: String)
  }

  class Husband extends Actor {
    import Husband._
    import Wife._
    override def receive: Receive = {
      case AskWifeToCookFood(wife) => wife ! CookFood
      case AskWifeToGoParty(wife) => wife ! LetsParty
      case AbuseWife(wife) => wife ! Abuse
      case AskSomethingElse(wife, msg) => wife ! Ask(msg)
      case WifeAccept => println("My wife is HAPPY so I am and my whole family!")
      case WifeReject => println("My wife is SAD, when every time I told her to cook something")
      case WifeSlap => println("Even god can not save me as my wife got ANGRY")
    }
  }

  val actorSystem: ActorSystem = ActorSystem("ChangingHandlerDemo")
  val wife: ActorRef = actorSystem.actorOf(Props[Wife], "wife")
  val husband: ActorRef = actorSystem.actorOf(Props[Husband], "husband")

  import Husband._
  husband ! AskWifeToCookFood(wife)
  husband ! AskSomethingElse(wife, "what happened dear") // My wife is SAD, when every time I told her to cook something

  husband ! AbuseWife(wife)
  husband ! AskSomethingElse(wife, "what happened dear") // Even god can not save me as my wife got ANGRY

  Thread.sleep(2000)

  // ###################################################################################################################
  // Stateless Wife actor with no mutable mood variable and no if else if pattern used to reply husband based on mood.
  class StatelessWife extends Actor {
    import Wife._

    // var mood: String = HAPPY_MOOD --> No need to maintain a state
    override def receive: Receive = happyReceive // by default handle message using happyReceive message handler

    def happyReceive: Receive = {
      case CookFood => context.become(sadReceive) // change message handler to sadReceive
      case LetsParty => // lets continue with current message handler
      case Abuse => context.become(abuseReceive) // change message handler to abuseReceive
      case Ask(_) => sender ! WifeAccept
    }

    def sadReceive: Receive = {
      case CookFood => // lets continue with current message handler
      case LetsParty => context.become(happyReceive) // change message handler to happyReceive
      case Abuse => context.become(abuseReceive) // change message handler to abuseReceive
      case Ask(_) => sender ! WifeReject
    }

    def abuseReceive: Receive = {
      case CookFood => context.become(sadReceive) // change message handler to sadReceive
      case LetsParty => context.become(happyReceive) // change message handler to happyReceive
      case Abuse => // lets continue with current message handler
      case Ask(_) => sender ! WifeSlap
    }
  }

  val statelessWife: ActorRef = actorSystem.actorOf(Props[StatelessWife], "statelessWifeActor")
  husband ! AskWifeToGoParty(statelessWife)
  husband ! AskSomethingElse(statelessWife, "what happened") // My wife is HAPPY so I am and my whole family!

  husband ! AskWifeToCookFood(statelessWife)
  husband ! AbuseWife(statelessWife)
  husband ! AskSomethingElse(statelessWife, "Wanna go party dear") // Even god can not save me as my wife got ANGRY

  actorSystem.terminate()

}
