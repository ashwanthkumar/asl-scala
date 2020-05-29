package in.ashwanthkumar.asl

case class Fail(
    Comment: Option[String],
    Cause: Option[String],
    Error: Option[String]
) extends State
