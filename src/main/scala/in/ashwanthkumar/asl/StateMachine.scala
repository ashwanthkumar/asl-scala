package in.ashwanthkumar.asl

trait State
case class StateMachine(comment: Option[String], startAt: String, states: Map[String, State])
