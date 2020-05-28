package in.ashwanthkumar.asl

import in.ashwanthkumar.asl.ErrorCodes.ErrorNames
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

case class Retrier(
    ErrorEquals: List[ErrorNames],
    IntervalSeconds: Option[Int] = Some(1),
    MaxAttempts: Option[Int] = Some(3),
    BackoffRate: Option[Double] = Some(2.0)
)

case class Catcher(
    ErrorEquals: List[ErrorNames],
    Next: String
)

case class Task(
    Resource: String,
    HeartbeatSeconds: Option[Long],
    Comment: Option[String],
    InputPath: Option[String],
    OutputPath: Option[String],
    Parameters: Option[JsObject],
    ResultPath: Option[String],
    Result: Option[JsObject],
    Next: Option[String],
    End: Option[Boolean],
    TimeoutSeconds: Option[Long] = Some(60),
    retriers: Option[List[Retrier]] = None,
    catchers: Option[List[Catcher]] = None
) extends State {

  if (TimeoutSeconds.isDefined && HeartbeatSeconds.isDefined) {
    require(
      HeartbeatSeconds.get < TimeoutSeconds.get,
      "The HeartbeatSeconds interval MUST be smaller than the TimeoutSeconds value."
    )
  }

  // Ref - https://github.com/spray/spray-json/issues/257
  // FIXME: We can remove these helpers once the above Issue is resolved
  def retriersAsList: List[Retrier] = retriers.toList.flatten
  def catchersAsList: List[Catcher] = catchers.toList.flatten
}

object JsonFormats extends DefaultJsonProtocol {
  implicit val passFormat    = jsonFormat8(Pass)
  implicit val retrierFormat = jsonFormat4(Retrier)
  implicit val catcherFormat = jsonFormat2(Catcher)
  implicit val taskFormat    = jsonFormat13(Task)
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
          case "Task" =>
            stateSpec.convertTo[Task]
        }

        stateName -> state
    }

    stateMachine.copy(states = states)
  }
}
