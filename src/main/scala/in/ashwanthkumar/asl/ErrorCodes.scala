package in.ashwanthkumar.asl

import in.ashwanthkumar.asl.ErrorCodes.ErrorNames

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

object ErrorCodes {
  // https://states-language.net/spec.html#appendix-a
  type ErrorNames = String

  object States {
    val ALL                    = "States.ALL"
    val Timeout                = "States.Timeout"
    val TaskFailed             = "States.TaskFailed"
    val Permissions            = "States.Permissions"
    val ResultPathMatchFailure = "States.ResultPathMatchFailure"
    val ParameterPathFailure   = "States.ParameterPathFailure"
    val BranchFailed           = "States.BranchFailed"
    val NoChoiceMatched        = "States.NoChoiceMatched"
  }

}
