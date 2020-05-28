package in.ashwanthkumar.asl

import spray.json._
import spray.json.lenses.JsonLenses._

trait State
case class StateMachine(comment: Option[String], startAt: String, states: Map[String, State])

case class Pass(
    Comment: Option[String],
    InputPath: Option[String],
    OutputPath: Option[String],
    Parameters: Option[JsObject],
    ResultPath: Option[String],
    Result: Option[JsObject],
    Next: Option[String],
    End: Option[Boolean]
) extends State

object JsonFormats extends DefaultJsonProtocol {
  implicit val passFormat = jsonFormat8(Pass)
}

object ASLParser {
  import JsonFormats._

  def parse(json: String) = {
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
        }

        stateName -> state
    }

    stateMachine.copy(states = states)
  }
}
