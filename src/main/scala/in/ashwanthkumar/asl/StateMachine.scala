package in.ashwanthkumar.asl

import spray.json._
import spray.json.lenses.JsonLenses._

trait State
case class StateMachine(comment: Option[String], startAt: String, states: Map[String, State])
