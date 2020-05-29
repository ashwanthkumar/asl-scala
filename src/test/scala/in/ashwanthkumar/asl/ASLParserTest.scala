package in.ashwanthkumar.asl

import org.scalatest.FlatSpec
import org.scalatest.Matchers.{be, convertToAnyShouldWrapper, have, size}
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
    stateMachine.Comment should be(Option("A simple minimal example of the States language"))
    stateMachine.StartAt should be("Hello World")
    val pass = stateMachine.States("No-op").asInstanceOf[Pass]
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
    val taskState    = stateMachine.States("TaskState").asInstanceOf[Task]
    taskState.Comment should be(Option("Task State example"))
    taskState.Resource should be("arn:aws:states:us-east-1:123456789012:task:HelloWorld")
    taskState.Next should be(Some("NextState"))
    taskState.TimeoutSeconds should be(Option(300L))
    taskState.HeartbeatSeconds should be(Option(60L))
  }

  it should "parse Choice State with InlineRule" in {
    val state =
      """
        |"ChoiceStateX": {
        |  "Comment": "Choice State example",
        |  "Type": "Choice",
        |  "Choices": [
        |    {
        |      "Variable": "$.value",
        |      "NumericEquals": 0,
        |      "Next": "ValueIsZero"
        |    }
        |  ],
        |  "Default": "DefaultState"
        |}
        |""".stripMargin
    val stateMachine = ASLParser.parse(stateInStateMachine(state))
    val choiceState  = stateMachine.States("ChoiceStateX").asInstanceOf[Choice]
    choiceState.Comment should be(Option("Choice State example"))
    choiceState.Default should be("DefaultState")
    choiceState.Choices should have size 1

    val onlyChoiceRule = choiceState.Choices.head
    val inlineRule     = onlyChoiceRule.asInstanceOf[InlineRule]
    inlineRule.Next should be("ValueIsZero")
    inlineRule.rule.Variable should be("$.value")
    inlineRule.rule.function should be(NumericEquals(0))
  }

  it should "parse Choice State with And" in {
    val state =
      """
        |"ChoiceStateX": {
        |  "Comment": "Choice State example",
        |  "Type": "Choice",
        |  "Choices": [
        |  {
        |      "And": [
        |        {
        |          "Variable": "$.value",
        |          "NumericGreaterThanEquals": 20
        |        },
        |        {
        |          "Variable": "$.value",
        |          "NumericLessThan": 30
        |        }
        |      ],
        |      "Next": "ValueInTwenties"
        |   }
        |  ],
        |  "Default": "DefaultState"
        |}
        |""".stripMargin
    val stateMachine = ASLParser.parse(stateInStateMachine(state))
    val choiceState  = stateMachine.States("ChoiceStateX").asInstanceOf[Choice]
    choiceState.Comment should be(Option("Choice State example"))
    choiceState.Default should be("DefaultState")
    choiceState.Choices should have size 1

    val onlyChoiceRule = choiceState.Choices.head
    val and            = onlyChoiceRule.asInstanceOf[And]
    and.Next should be("ValueInTwenties")
    and.And should have size 2
    val first = and.And.head
    first.Variable should be("$.value")
    first.function should be(NumericGreaterThanEquals(20))
    val second = and.And.apply(1)
    second.Variable should be("$.value")
    second.function should be(NumericLessThan(30))
  }

  it should "parse Choice State with Or" in {
    val state =
      """
        |"ChoiceStateX": {
        |  "Comment": "Choice State example",
        |  "Type": "Choice",
        |  "Choices": [
        |  {
        |      "Or": [
        |        {
        |          "Variable": "$.value",
        |          "NumericGreaterThanEquals": 20
        |        },
        |        {
        |          "Variable": "$.value",
        |          "NumericLessThan": 30
        |        }
        |      ],
        |      "Next": "ValueInTwenties"
        |   }
        |  ],
        |  "Default": "DefaultState"
        |}
        |""".stripMargin
    val stateMachine = ASLParser.parse(stateInStateMachine(state))
    val choiceState  = stateMachine.States("ChoiceStateX").asInstanceOf[Choice]
    choiceState.Comment should be(Option("Choice State example"))
    choiceState.Default should be("DefaultState")
    choiceState.Choices should have size 1

    val onlyChoiceRule = choiceState.Choices.head
    val or             = onlyChoiceRule.asInstanceOf[Or]
    or.Next should be("ValueInTwenties")
    or.Or should have size 2
    val first = or.Or.head
    first.Variable should be("$.value")
    first.function should be(NumericGreaterThanEquals(20))
    val second = or.Or.apply(1)
    second.Variable should be("$.value")
    second.function should be(NumericLessThan(30))
  }

  it should "parse Choice State with Not" in {
    val state =
      """
        |"ChoiceStateX": {
        |  "Comment": "Choice State example",
        |  "Type": "Choice",
        |  "Choices": [
        |    {
        |        "Not": {
        |          "Variable": "$.type",
        |          "StringEquals": "Private"
        |        },
        |        "Next": "Public"
        |    }
        |  ],
        |  "Default": "DefaultState"
        |}
        |""".stripMargin
    val stateMachine = ASLParser.parse(stateInStateMachine(state))
    val choiceState  = stateMachine.States("ChoiceStateX").asInstanceOf[Choice]
    choiceState.Comment should be(Option("Choice State example"))
    choiceState.Default should be("DefaultState")
    choiceState.Choices should have size 1

    val onlyChoiceRule = choiceState.Choices.head
    val not            = onlyChoiceRule.asInstanceOf[Not]
    not.Next should be("Public")
    val rule = not.Not
    rule.Variable should be("$.type")
    rule.function should be(StringEquals("Private"))
  }

  it should "parse Fail State" in {
    val state =
      """
        |"FailState": {
        |  "Type": "Fail",
        |  "Cause": "Invalid response.",
        |  "Error": "ErrorA"
        |}
        |""".stripMargin
    val stateMachine = ASLParser.parse(stateInStateMachine(state))
    stateMachine.Comment should be(Option("A simple minimal example of the States language"))
    stateMachine.StartAt should be("Hello World")
    val fail = stateMachine.States("FailState").asInstanceOf[Fail]
    fail.Cause should be(Some("Invalid response."))
    fail.Error should be(Option("ErrorA"))
  }

}
