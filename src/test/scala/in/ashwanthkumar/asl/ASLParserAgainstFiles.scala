package in.ashwanthkumar.asl

import org.scalatest.FlatSpec
import org.scalatest.Matchers.{convertToAnyShouldWrapper, a, be, theSameInstanceAs}

import scala.io.Source

class ASLParserAgainstFiles extends FlatSpec {
  private def readJson(resource: String) = {
    val source = Source.fromInputStream(getClass.getResourceAsStream(resource))
    source.getLines().mkString("\n")
  }

  // Ref - https://docs.aws.amazon.com/step-functions/latest/dg/concepts-amazon-states-language.html
  "ASLParserAgainstFiles" should "parse example-state-language.json" in {
    val input        = readJson("/example-state-language.json")
    val stateMachine = ASLParser.parse(input)
    stateMachine.States("FirstState") shouldBe a[Task]
    stateMachine.States("ChoiceState") shouldBe a[Choice]
    stateMachine.States("FirstMatchState") shouldBe a[Task]
    stateMachine.States("DefaultState") shouldBe a[Fail]
  }
}
