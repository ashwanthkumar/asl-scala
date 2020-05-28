package in.ashwanthkumar.asl

import spray.json._
import spray.json.DefaultJsonProtocol._
import spray.json.lenses.JsonLenses._

trait State
case class StateMachine(comment: Option[String], startAt: String, states: Map[String, State])

object ASLParser {
  def parse(json: String) = {
    val allStatesInStateMachine = json.extract[JsObject]('States)
    allStatesInStateMachine.fields.map {
      case (stateName, stateSpec) =>
        val typeOfState = stateSpec.extract[String]('Type)
        typeOfState match {
          case "Pass" =>
            println("PAss State")
            ""
          case "Task" =>
            println("Task State")
            ""
          case "Choice" =>
            println("Choice State")
            ""
          case "Wait" =>
            println("Wait State")
            ""
          case "Succeed" =>
            println("Succeed State")
            ""
          case "Fail" =>
            println("Fail State")
            ""
          case "Parallel" =>
            println("Parallel State")
            ""
          case "Map" =>
            println("Map State")
            ""
        }
    }
  }
}
