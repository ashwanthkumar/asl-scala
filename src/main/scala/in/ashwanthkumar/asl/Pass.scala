package in.ashwanthkumar.asl

import spray.json.JsObject

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
