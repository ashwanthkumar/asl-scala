package in.ashwanthkumar.asl

import org.scalatest.FlatSpec
import org.scalatest.Matchers.{be, convertToAnyShouldWrapper}
import spray.json.{JsNumber, JsObject}

class ASLParserTest extends FlatSpec {
  def stateInStateMachine(stateSpec: String): String = {
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

  "ASLParser" should "parse Pass State" in {
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
    val stateMachine = ASLParser.parse(stateInStateMachine(state))
    stateMachine.comment should be(Option("A simple minimal example of the States language"))
    stateMachine.startAt should be("Hello World")
    val pass = stateMachine.states("No-op").asInstanceOf[Pass]
    pass.Result should be(Some(JsObject("x-datum" -> JsNumber(0.381018), "y-datum" -> JsNumber(622.2269926397355))))
    pass.ResultPath should be(Option("$.coords"))
    pass.Next should be(Option("End"))
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
    val stateMachine = ASLParser.parse(stateInStateMachine(state))
    val taskState    = stateMachine.states("TaskState").asInstanceOf[Task]
    taskState.Comment should be(Option("Task State example"))
    taskState.Resource should be("arn:aws:states:us-east-1:123456789012:task:HelloWorld")
    taskState.Next should be(Some("NextState"))
    taskState.TimeoutSeconds should be(Option(300L))
    taskState.HeartbeatSeconds should be(Option(60L))
  }
}
