package main.scala.com.thedevd.scalaexamples.akka.actor

import akka.actor.{ Actor, ActorRef, ActorSystem, Props }

/**
 * DISTRIBUTED WORD COUNTING
 * -------------------------------
 * This is very interesting Akka exercise, which kind a demonstrate -
 * - How parent-child actor modeling can come in handy to distribute jobs like MASTER-WORKER architecture
 * - Building thought process to model the solution from Stateful to Stateless manner.
 *
 * Ok. The exercise is all about doing a WORLD COMPLEX TASK called counting words in a text (HAHAHA).
 * We will try to do this in MASTER-WORKER style that means, where we would have
 * - A MASTER actor - Responsibility of master would be manage several worker and then assign WordCountTask to one of the
 *   workers it is managing (In example I have done this round-robin order to assign task one by one to worker).
 * - MANY WORKER actors - Will receive their work from the master, and so they will also reply to master.
 *
 * We have another Actor called REQUESTER, you can consider it as end user who wants to count words in a text, so
 * ---> REQUESTER submits WordCountTask to MASTER.
 *      --->  MASTER delegates the task to one of the WORKERS it manages.
 *          --->  WORKER receives task from MASTER, it does the job and reply the output to MASTER.
 *                ---> MASTER receives the response from WORKER and then reply the task result to REQUESTER.
 *                     ---> REQUESTER has the final result received from MASTER.
 *
 * So we can say,
 * - WORKERS are like children of MASTER.
 * - WORKERS only interacts with MASTER
 * - MASTER interacts with WORKERS and REQUESTER BOTH.
 * - REQUESTER only interacts with MASTER.
 *
 * There are 2 variants of the distributed WordCount in this example - one is in stateful style (object StatefulStyle{})
 * and second is in stateless style (object StatelessStyle{}).
 * You can see in stateful style, we have use couple of mutable variables for the supporting functionality such as
 * - 'var workers: Seq[ActorRef]', 'var currentWorkerId: Int' in WordCountMaster actor
 * - 'var jobId: Int' in Requester actor
 *
 * However in stateless style (object StatelessStyle{}), we have managed to get rid of above mutable state with
 * context.become() style. (Please also see CounterActorExercise.scala and AkkaActorChangingHandler.scala to know more
 * about stateless style). Here I am going to explain the stateless style implementation.
 *
 * Stateless style of doing distributed WordCount problem
 * ---------------------------------------------------------
 * For simplicity, one worker will do whole word counting of a task. The term distributed is in the sense that the
 * master will assign WordCountTask to workers one by one in roundRobin order turn by turn.
 * For eg. if we have 5 workers, and if we submit 7 WordCountTasks to Master then master will assign
 *  - task-1 to Worker-1
 *  - task-2 to Worker-2
 *  - task-3 to Worker-3
 *  - task-4 to Worker-4
 *  - task-5 to Worker-5
 *  - task-6 to Worker-1 -----> Round robin counter reset here
 *  - task-7 to Worker-2
 *  - and so on.......
 *
 *
 * So exactly similar we did in Stateless WordCountMaster Actor.
 * --> Initialize Master with 5 Workers
 *      ---> Requester submits WordCountTask to Master
 *           ---> Master chooses a worker to do the job (Choice is made in Round-Robin fashion)
 *                ---> Master sends the WordCountTask to chosen Worker
 *                     ---> Chosen worker completes its task and reply the word counts to Master
 *                          ---> Master get result from Worker
 *                               ---> Master sends result back to Requester
 *      ---> Requester submits another Task to Master
 *           ---> Master chooses next worker to do the job
 *                ----> And so on..........
 *
 * To be noted that in Stateless WordCountMaster and WordCountWorker, we are always keeping track of 'jobId' and
 * 'requester' reference. This is needed because after Worker finishes it job, it can let the Master know that for
 * that particular Task(identified by jobId) here is the count. And then Master should know to whom need to send the result.
 * So at the end Master has both the details - The count of the particular task, and requester actor who has actually
 * submitted the Task to master.
 *
 * For better understanding, look at the console output of the exercise (Starting from line# 110)
 *
 */
object MasterWorkersWordCountExercise extends App {

  val actorSystem: ActorSystem = ActorSystem("MasterWorkersWordCountExercise")
  println("################### Testing Stateful solution first")
  StatefulStyle.testSolution()

  Thread.sleep(2000)
  println("################### Testing Stateless solution")
  StatelessStyle.testSolution()

  /**
   * Console Output
   * -------------------
   * ################### Testing Stateful solution first
   * [Stateful Requester] sending wordCount task: [JobId: 1, text: count me2]
   * [Stateful Requester] sending wordCount task: [JobId: 2, text: count me 3]
   * [Stateful Requester] sending wordCount task: [JobId: 3, text: count me 3 4]
   * [Stateful Requester] sending wordCount task: [JobId: 4, text: count me 3 4 5]
   * [Stateful Requester] sending wordCount task: [JobId: 5, text: count me 3 4 5 6]
   * [Stateful Requester] sending wordCount task: [JobId: 6, text: count me 3 4 5 6 7]
   * [Stateful WordCountMaster] chosen worker: Worker-1 for wordCount task: [JobId: 1, text: count me2]
   * [Stateful WordCountMaster] chosen worker: Worker-2 for wordCount task: [JobId: 2, text: count me 3]
   * [Stateful WordCountMaster] chosen worker: Worker-3 for wordCount task: [JobId: 3, text: count me 3 4]
   * [Stateful WordCountMaster] chosen worker: Worker-4 for wordCount task: [JobId: 4, text: count me 3 4 5]
   * [Stateful WordCountMaster] chosen worker: Worker-5 for wordCount task: [JobId: 5, text: count me 3 4 5 6]
   * [Stateful WordCountMaster] chosen worker: Worker-1 for wordCount task: [JobId: 6, text: count me 3 4 5 6 7]
   * [Stateful Requester] got result: [JobId: 5, count: 6]
   * [Stateful Requester] got result: [JobId: 3, count: 4]
   * [Stateful Requester] got result: [JobId: 4, count: 5]
   * [Stateful Requester] got result: [JobId: 1, count: 2]
   * [Stateful Requester] got result: [JobId: 2, count: 3]
   * [Stateful Requester] got result: [JobId: 6, count: 7]
   *
   * ################### Testing Stateless solution
   * [Stateless WordCountRequester] sending wordCount task: [JobId: 1, text: count me2, requester: stateless-requester] to master
   * [Stateless WordCountRequester] sending wordCount task: [JobId: 2, text: count me 3, requester: stateless-requester] to master
   * [Stateless WordCountRequester] sending wordCount task: [JobId: 3, text: count me 3 4, requester: stateless-requester] to master
   * [Stateless WordCountRequester] sending wordCount task: [JobId: 4, text: count me 3 4 5, requester: stateless-requester] to master
   * [Stateless WordCountRequester] sending wordCount task: [JobId: 5, text: count me 3 4 5 6, requester: stateless-requester] to master
   * [Stateless WordCountRequester] sending wordCount task: [JobId: 6, text: count me 3 4 5 6 7, requester: stateless-requester] to master
   * [Stateless Master] chosen worker: Worker-1 for wordCount task: [JobId: 1, text: count me2, requester: stateless-requester]
   * [Stateless Master] chosen worker: Worker-2 for wordCount task: [JobId: 2, text: count me 3, requester: stateless-requester]
   * [Stateless Master] chosen worker: Worker-3 for wordCount task: [JobId: 3, text: count me 3 4, requester: stateless-requester]
   * [Stateless Master] chosen worker: Worker-4 for wordCount task: [JobId: 4, text: count me 3 4 5, requester: stateless-requester]
   * [Stateless Master] chosen worker: Worker-5 for wordCount task: [JobId: 5, text: count me 3 4 5 6, requester: stateless-requester]
   * [Stateless Master] chosen worker: Worker-1 for wordCount task: [JobId: 6, text: count me 3 4 5 6 7, requester: stateless-requester]
   * [Stateless WordCountRequester] got result: [JobId: 1, count: 2, requester: stateless-requester]
   * [Stateless WordCountRequester] got result: [JobId: 2, count: 3, requester: stateless-requester]
   * [Stateless WordCountRequester] got result: [JobId: 3, count: 4, requester: stateless-requester]
   * [Stateless WordCountRequester] got result: [JobId: 4, count: 5, requester: stateless-requester]
   * [Stateless WordCountRequester] got result: [JobId: 5, count: 6, requester: stateless-requester]
   * [Stateless WordCountRequester] got result: [JobId: 6, count: 7, requester: stateless-requester]
   *
   */

  object StatefulStyle {

    case class SubmitTaskToMaster(master: ActorRef, text: String)
    case class WordCountTask(requester: ActorRef, text: String, jobId: Int)
    class Requester extends Actor {
      var jobId: Int = 1
      override def receive: Receive = {
        case SubmitTaskToMaster(master, text) =>
          println(s"[Stateful Requester] sending wordCount task: [JobId: $jobId, text: $text]")
          master ! WordCountTask(self, text, jobId)
          jobId += 1
        case MasterReplyToRequester(count, jobId) =>
          println(s"[Stateful Requester] got result: [JobId: $jobId, count: $count]")
      }
    }

    case class InitializeMaster(nWorkers: Int)
    case class WorkerReplyToMaster(requester: ActorRef, count: Int, jobId: Int)
    case class MasterReplyToRequester(count: Int, jobId: Int)

    class WordCountMaster extends Actor {
      var workers: Seq[ActorRef] = Seq.empty
      var currentWorkerId: Int = 0

      override def receive: Receive = {
        case InitializeMaster(nWorkers) =>
          workers = (1 to nWorkers).map(workerId => context.actorOf(Props[WordCountWorker], s"Worker-$workerId"))
        case WordCountTask(requester, text, jobId) =>
          if(currentWorkerId == workers.length) {
            currentWorkerId = 0
          }
          val chosenWorker: ActorRef = workers(currentWorkerId)
          println(s"[Stateful WordCountMaster] chosen worker: ${chosenWorker.path.name} for wordCount task: [JobId: $jobId, text: $text]")
          chosenWorker ! WordCountTask(requester, text, jobId)
          currentWorkerId += 1
        case WorkerReplyToMaster(requester, count, jobId) =>
          requester ! MasterReplyToRequester(count, jobId)
      }
    }

    class WordCountWorker extends Actor {
      override def receive: Receive = {
        case WordCountTask(requester, text, jobId) =>
          val wordCounts: Int = text.split(" ").map(_.trim).length
          sender() ! WorkerReplyToMaster(requester, wordCounts, jobId)
      }
    }

    def testSolution(): Unit = {
      val requester: ActorRef = actorSystem.actorOf(Props[Requester], "stateful-requester")
      val wordCountMaster: ActorRef = actorSystem.actorOf(Props[WordCountMaster], "stateful-master")

      wordCountMaster ! InitializeMaster(5)

      requester ! SubmitTaskToMaster(wordCountMaster, "count me2")
      requester ! SubmitTaskToMaster(wordCountMaster, "count me 3")
      requester ! SubmitTaskToMaster(wordCountMaster, "count me 3 4")
      requester ! SubmitTaskToMaster(wordCountMaster, "count me 3 4 5")
      requester ! SubmitTaskToMaster(wordCountMaster, "count me 3 4 5 6")
      requester ! SubmitTaskToMaster(wordCountMaster, "count me 3 4 5 6 7")
    }
  }

  object StatelessStyle {

    case class SubmitTaskToMaster(master: ActorRef, text: String)
    case class WordCountTask(text: String, jobId: Int, requester: ActorRef) {
      override def toString: String = s"[JobId: $jobId, text: $text, requester: ${requester.path.name}]"
    }
    class WordCountRequester extends Actor {
      override def receive: Receive = taskSubmitReceive(1)

      def taskSubmitReceive(jobId: Int): Receive = {
        case SubmitTaskToMaster(master, text) =>
          val taskForMaster: WordCountTask = WordCountTask(text, jobId, self)
          println(s"[Stateless WordCountRequester] sending wordCount task: $taskForMaster to master")
          master ! taskForMaster
          context.become(taskSubmitReceive(jobId + 1))
        case wordCountResult: WordCountResult =>
          println(s"[Stateless WordCountRequester] got result: $wordCountResult")
      }
    }

    case class InitializeMaster(nWorkers: Int)
    class WordCountMaster extends Actor {
      override def receive: Receive = {
        case InitializeMaster(nWorkers) =>
          val workers: Map[Int, ActorRef] = (1 to nWorkers).map(workerId => (workerId, context.actorOf(Props[WordCountWorker], s"Worker-$workerId"))).toMap
          context.become(masterTaskReceive(workers,1))
      }

      def masterTaskReceive(workers: Map[Int, ActorRef], currentWorkerId: Int): Receive = {
        case wordCountTask: WordCountTask =>
          val chosenWorker: ActorRef = workers.getOrElse(currentWorkerId, context.actorOf(Props[WordCountWorker], s"Worker-default"))
          println(s"[Stateless Master] chosen worker: ${chosenWorker.path.name} for wordCount task: $wordCountTask")
          chosenWorker ! wordCountTask

          val nextWorkerId: Int = if(currentWorkerId >= workers.keys.size) 1 else currentWorkerId + 1
          context.become(masterTaskReceive(workers, nextWorkerId))
        case wordCountResult: WordCountResult =>
          wordCountResult.requester ! wordCountResult
      }
    }

    case class WordCountResult(count: Int, jobId: Int, requester: ActorRef) {
      override def toString: String = s"[JobId: $jobId, count: $count, requester: ${requester.path.name}]"
    }
    class WordCountWorker extends Actor {
      override def receive: Receive = {
        case WordCountTask(text, jobId, requester) =>
          val wordCounts: Int = text.split(" ").map(_.trim).length
          sender() ! WordCountResult(wordCounts, jobId, requester)
      }
    }

    def testSolution(): Unit = {
      val requester: ActorRef = actorSystem.actorOf(Props[WordCountRequester], "stateless-requester")
      val wordCountMaster: ActorRef = actorSystem.actorOf(Props[WordCountMaster], "stateless-master")

      wordCountMaster ! InitializeMaster(5)

      requester ! SubmitTaskToMaster(wordCountMaster, "count me2")
      requester ! SubmitTaskToMaster(wordCountMaster, "count me 3")
      requester ! SubmitTaskToMaster(wordCountMaster, "count me 3 4")
      requester ! SubmitTaskToMaster(wordCountMaster, "count me 3 4 5")
      requester ! SubmitTaskToMaster(wordCountMaster, "count me 3 4 5 6")
      requester ! SubmitTaskToMaster(wordCountMaster, "count me 3 4 5 6 7")
    }
  }

  actorSystem.terminate()

}
