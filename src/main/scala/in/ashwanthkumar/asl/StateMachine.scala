package in.ashwanthkumar.asl

trait State
case class StateMachine(Comment: Option[String], StartAt: String, States: Map[String, State])
