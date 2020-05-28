package in.ashwanthkumar.asl

import org.scalatest.FlatSpec
import org.scalatest.Matchers.{convertToAnyShouldWrapper, be}

class ASLParserTest extends FlatSpec {
  def stateInStateMachine(stateSpec: String) = {
    s"""
      |{
      |    "Comment": "A simple minimal example of the States language",
      |    "StartAt": "Hello World",
      |    "States": {
      |     $stateSpec
      |    }
      |}
      |
      |""".stripMargin
  }
  it should "parse Pass State" in {
    val state =
      """
        |"No-op": {
        |  "Type": "Pass",
        |  "Result": {
        |    "x-datum": 0.381018,
        |    "y-datum": 622.2269926397355
        |  },
        |  "ResultPath": "$.coords",
        |  "Next": "End"
        |}
        |""".stripMargin
    ASLParser.parse(stateInStateMachine(state))
  }
  it should "parse Task State" in {
    val state =
      """
        |"TaskState": {
        |  "Comment": "Task State example",
        |  "Type": "Task",
        |  "Resource": "arn:aws:states:us-east-1:123456789012:task:HelloWorld",
        |  "Next": "NextState",
        |  "TimeoutSeconds": 300,
        |  "HeartbeatSeconds": 60
        |}
        |""".stripMargin
    ASLParser.parse(stateInStateMachine(state))
  }
}
