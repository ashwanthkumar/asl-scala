package in.ashwanthkumar.asl

import spray.json.JsObject

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
