package main.scala.com.thedevd.scalaexamples.akka.actor

import akka.actor.{ Actor, ActorRef, ActorSystem, Props }

/**
 * This is very interesting akka actor exercise where I have tried to model a simple Voting system. Lets see it in details.
 *
 * So there are 2 main Actors could be in any Voting system -
 * 1. Voter - A person who gives his/her vote to a Candidate (aka Neta in our Indian Language).
 * 2. VotingBooth - A voting booth where actual voting happens against Candidate.
 *
 * Voter actor
 * --------------
 * Voter actor maintains a mutable variable 'votedCandidate' which hold to whom voter has voted.
 * This mutable variable can also be used to prevent a voter voting more than once.
 * If by chance, voter tries to vote again, then Voter actor sends 'DuplicateVoteError' to self and prints the
 * message that voter has already voted.
 *
 * Once Voter actor done voting from its end (i.e modifies votedCandidate variable to name of the voted candidate), then
 * it sends a VotingDone status to 'VotingBooth' actor to let it know that I have voted to this candidate so
 * please update your voting count.
 *
 * See Voter actor is so straightforward.
 *
 *
 * VotingBooth actor
 * -------------------
 * VotingBooth actor can keep track of
 *    - Counting of votes (i.e. which candidate has got how many votes)
 *    - Voters list belonging to that booth
 *    - Voters list who has done voting
 *
 *    And there are several more behaviours of VotingBooth actor -
 *    - Can print counting of votes. (Upon receiving 'PrintVoteCounting' msg)
 *    - Can print who is leading winner (or Final winner). (Upon receiving 'PrintWinningCandidate' msg)
 *    - Can print voter list belonging to that booth. (Upon receiving 'PrintVoterList' msg)
 *    - Can print voter's name who has not done voting yet. (Upon receiving 'PrintVoterListNotVoted' msg)
 *
 * So the basic flow in VotingBooth actor is -
 * When it receives a 'VotingDone' message from a particular 'Voter' actor, then it does two things-
 * - Increment the voting count for the Candidate.
 * - Updates its 'votedVoterList' to keep track of voters who have done voting (Then in turns this list is used to know
 *   voters who have not voted yet)
 */
object VotingSystemExercise extends App {

  case class VotedTo(candidate: String, votingBooth: ActorRef)
  case object DuplicateVoteError

  object Voter {
    def props(id: String): Props = Props(new Voter(id))
  }
  class Voter(id: String) extends Actor {
    // Each voter has votedCandidate variable to store info whom they have voted or note, it will be None at the beginning
    var votedCandidate: Option[String] = None

    override def receive: Receive = {
      case VotedTo(candidate, votingBooth) =>
        if(votedCandidate.isDefined) {
          self ! DuplicateVoteError
        } else {
          votedCandidate = Some(candidate)
          votingBooth ! VotingDone(self, candidate)
        }
      case DuplicateVoteError => println(s"[Voter] $id has already voted")
    }
  }

  case class VotingDone(voter: ActorRef, candidate: String) // Voter sends this msg to votingBooth to inform about their elected candidate
  case object PrintVoteCounting // prints overall vote counting, i.e. candidateName -> VoteCount
  case object PrintVoterList // all voters of a voting booth
  case object PrintVotedVoterList // whose voting done
  case object PrintVoterListNotVoted // prints voters who have not done voting
  case object PrintWinningCandidate // prints winning candidate who has maximum vote count

  class VotingBooth(voterList: Set[ActorRef]) extends Actor {
    // Map[candidateName -> totalVotes]
    var currentVoteCounting: Map[String, Int] = Map.empty
    // voters list who voted
    var votedVoterList: Set[ActorRef] = Set.empty

    override def receive: Receive = {
      case VotingDone(voter, candidate) =>
        votedVoterList += voter
        currentVoteCounting += (candidate -> (currentVoteCounting.getOrElse(candidate, 0) + 1))
        println(s"[VotingBooth] Voting done by voter: ${voter.path.name}")
      case PrintVoteCounting =>
        println(s"[VotingBooth] Voting status: $currentVoteCounting")
      case PrintVoterList =>
        println(s"[VotingBooth] Voters list is: ${voterList.map(_.path.name)}")
      case PrintVotedVoterList =>
        println(s"[VotingBooth] Voters who voted are: ${votedVoterList.map(_.path.name)}")
      case PrintVoterListNotVoted =>
        val voterNotVoted = voterList.map(_.path.name) diff votedVoterList.map(_.path.name)
        println(s"[VotingBooth] Voters who not voted are: $voterNotVoted")
      case PrintWinningCandidate =>
        val winningCandidate = currentVoteCounting.maxBy(_._2)
        println(s"[VotingBooth] Winning candidate for booth is: $winningCandidate")
    }
  }

  val actorSystem: ActorSystem = ActorSystem("VotingSystem")

  val voter1: ActorRef = actorSystem.actorOf(Voter.props("voter1"), "voter1")
  val voter2: ActorRef = actorSystem.actorOf(Voter.props("voter2"), "voter2")
  val voter3: ActorRef = actorSystem.actorOf(Voter.props("voter3"), "voter3")
  val voter4: ActorRef = actorSystem.actorOf(Voter.props("voter4"), "voter4")
  val voter5: ActorRef = actorSystem.actorOf(Voter.props("voter5"), "voter5")

  val votingBooth1: ActorRef = actorSystem.actorOf(Props(new VotingBooth(Set(voter1, voter2, voter3, voter4, voter5))))

  voter1 ! VotedTo("Narendra Modi", votingBooth1)
  voter2 ! VotedTo("Arvind Kejriwal", votingBooth1)
  voter3 ! VotedTo("Narendra Modi", votingBooth1)
  voter4 ! VotedTo("Narendra Modi", votingBooth1)

  // Voter1 tries to vote again
  voter1 ! VotedTo("Manmohan Singh", votingBooth1) // [Voter] voter1 has already voted

  Thread.sleep(2000) // Waiting for all voters to complete their voting, it is like waiting for End of day before we do counting

  votingBooth1 ! PrintVoteCounting // Map(Narendra Modi -> 3, Arvind Kejriwal -> 1)
  votingBooth1 ! PrintVotedVoterList // [VotingBooth] Voters who voted are: Set(voter1, voter2, voter4, voter3)
  votingBooth1 ! PrintVoterList // [VotingBooth] Voters who voted are: Set(voter1, voter2, voter4, voter3, voter5)
  votingBooth1 ! PrintVoterListNotVoted // [VotingBooth] Voters who not voted are: Set(voter5)
  votingBooth1 ! PrintWinningCandidate // [VotingBooth] Winning candidate for booth is: (Narendra Modi,3)

  /**
   * Overall output
   * -------------------
   * [Voter] voter1 has already voted
   * [VotingBooth] Voting done by voter: voter2
   * [VotingBooth] Voting done by voter: voter4
   * [VotingBooth] Voting done by voter: voter1
   * [VotingBooth] Voting done by voter: voter3
   *
   * [VotingBooth] Voting status: Map(Arvind Kejriwal -> 1, Narendra Modi -> 3)
   * [VotingBooth] Voters who voted are: Set(voter2, voter4, voter1, voter3)
   * [VotingBooth] Voters list is: Set(voter3, voter2, voter1, voter5, voter4)
   * [VotingBooth] Voters who not voted are: Set(voter5)
   * [VotingBooth] Winning candidate for booth is: (Narendra Modi,3)
   */

  actorSystem.terminate()
}
