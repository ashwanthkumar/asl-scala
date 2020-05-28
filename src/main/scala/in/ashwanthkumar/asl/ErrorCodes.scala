package in.ashwanthkumar.asl

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
