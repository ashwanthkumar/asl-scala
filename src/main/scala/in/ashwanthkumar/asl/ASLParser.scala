package in.ashwanthkumar.asl

import spray.json._
import spray.json.lenses.JsonLenses._

object JsonFormats extends DefaultJsonProtocol {
  import EachChoiceJsonFormat._
  implicit val passFormat    = jsonFormat8(Pass)
  implicit val retrierFormat = jsonFormat4(Retrier)
  implicit val catcherFormat = jsonFormat2(Catcher)
  implicit val taskFormat    = jsonFormat13(Task)
  implicit val choiceFormat  = jsonFormat5(Choice)
}

object ASLParser {
  import JsonFormats._

  def parse(json: String): StateMachine = {
    val comment      = json.extract[String]('Comment.?)
    val startAt      = json.extract[String]('StartAt)
    val stateMachine = StateMachine(comment, startAt, Map())

    val allStatesInStateMachine = json.extract[JsObject]('States)
    val states: Map[String, State] = allStatesInStateMachine.fields.map {
      case (stateName, stateSpec) =>
        val typeOfState = stateSpec.extract[String]('Type)
        val state = typeOfState match {
          case "Pass" =>
            stateSpec.convertTo[Pass]
          case "Task" =>
            stateSpec.convertTo[Task]
          case "Choice" =>
            stateSpec.convertTo[Choice]
        }

        stateName -> state
    }

    stateMachine.copy(states = states)
  }
}
