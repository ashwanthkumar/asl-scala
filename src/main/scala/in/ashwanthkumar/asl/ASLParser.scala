package in.ashwanthkumar.asl

import spray.json._
import spray.json.lenses.JsonLenses._

sealed trait Predicate extends Product
sealed trait BooleanPredicate extends Predicate {
  def value: Boolean
}
case class BooleanEquals(value: Boolean) extends BooleanPredicate

sealed trait IntPredicate extends Predicate {
  def value: Int
}
case class NumericEquals(value: Int)            extends IntPredicate
case class NumericGreaterThan(value: Int)       extends IntPredicate
case class NumericGreaterThanEquals(value: Int) extends IntPredicate
case class NumericLessThan(value: Int)          extends IntPredicate
case class NumericLessThanEquals(value: Int)    extends IntPredicate

sealed trait StringPredicate extends Predicate {
  def value: String
}
case class StringEquals(value: String)               extends StringPredicate
case class StringGreaterThan(value: String)          extends StringPredicate
case class StringGreaterThanEquals(value: String)    extends StringPredicate
case class StringLessThan(value: String)             extends StringPredicate
case class StringLessThanEquals(value: String)       extends StringPredicate
case class TimestampEquals(value: String)            extends StringPredicate
case class TimestampGreaterThan(value: String)       extends StringPredicate
case class TimestampGreaterThanEquals(value: String) extends StringPredicate
case class TimestampLessThan(value: String)          extends StringPredicate
case class TimestampLessThanEquals(value: String)    extends StringPredicate

object FunctionJsonFormat extends DefaultJsonProtocol {
  implicit val functionFormat = new RootJsonFormat[Predicate] {
    // format: OFF
    override def read(json: JsValue): Predicate = {
      implicit val fields: Map[String, JsValue] = json.asJsObject.fields
      ifThisThen(BooleanEquals.toString(), value => BooleanEquals(value.convertTo[Boolean]))
        .orElse(ifThisThen(NumericEquals.toString(), value => NumericEquals(value.convertTo[Int])))
        .orElse(ifThisThen(NumericGreaterThan.toString(), value => NumericGreaterThan(value.convertTo[Int])))
        .orElse(ifThisThen(NumericGreaterThanEquals.toString(), value => NumericGreaterThanEquals(value.convertTo[Int])))
        .orElse(ifThisThen(NumericLessThan.toString(), value => NumericLessThan(value.convertTo[Int])))
        .orElse(ifThisThen(NumericLessThanEquals.toString(), value => NumericLessThanEquals(value.convertTo[Int])))
        .orElse(ifThisThen(StringEquals.toString(), value => StringEquals(value.convertTo[String])))
        .orElse(ifThisThen(StringGreaterThan.toString(), value => StringGreaterThan(value.convertTo[String])))
        .orElse(ifThisThen(StringGreaterThanEquals.toString(), value => StringGreaterThanEquals(value.convertTo[String])))
        .orElse(ifThisThen(StringLessThan.toString(), value => StringLessThan(value.convertTo[String])))
        .orElse(ifThisThen(StringLessThanEquals.toString(), value => StringLessThanEquals(value.convertTo[String])))
        .orElse(ifThisThen(TimestampEquals.toString(), value => TimestampEquals(value.convertTo[String])))
        .orElse(ifThisThen(TimestampGreaterThan.toString(), value => TimestampGreaterThan(value.convertTo[String])))
        .orElse(ifThisThen(TimestampGreaterThanEquals.toString(), value => TimestampGreaterThanEquals(value.convertTo[String])))
        .orElse(ifThisThen(TimestampLessThan.toString(), value => TimestampLessThan(value.convertTo[String])))
        .orElse(ifThisThen(TimestampLessThanEquals.toString(), value => TimestampLessThanEquals(value.convertTo[String])))
        .getOrElse(throw new RuntimeException(s"No valid Function found in the json: ${json.prettyPrint}"))
    }
    // format: ON

    override def write(obj: Predicate): JsValue = {
      JsObject(obj.productPrefix -> convertToJsValue(obj))
    }

    private def ifThisThen(key: String, converter: JsValue => Predicate)(
        implicit fields: Map[String, JsValue]
    ): Option[Predicate] = {
      fields.get(key).map(converter)
    }

    private def convertToJsValue(obj: Predicate) = obj match {
      case b: BooleanPredicate => JsBoolean(b.value)
      case i: IntPredicate     => JsNumber(i.value)
      case s: StringPredicate  => JsString(s.value)
    }
  }
}

case class Rule(function: Predicate, Variable: String)
object RuleJsonFormat extends DefaultJsonProtocol {
  import FunctionJsonFormat._
  implicit val ruleJsonFormat = new RootJsonFormat[Rule] {
    override def read(json: JsValue): Rule = {
      val variable                                 = json.extract[String]('Variable)
      def removeStr(input: String): Option[String] = None
      val jsonWithoutVariable                      = json.update('Variable ! modifyOrDeleteField(removeStr))
      val function                                 = jsonWithoutVariable.convertTo[Predicate]
      Rule(function, variable)
    }

    override def write(obj: Rule): JsValue = {
      val func = obj.function.toJson.asJsObject
      JsObject(func.fields + ("Variable" -> JsString(obj.Variable)))
    }
  }
}

sealed trait EachChoice
case class And(And: List[Rule], Next: String)   extends EachChoice
case class Or(Or: List[Rule], Next: String)     extends EachChoice
case class Not(Not: Rule, Next: String)         extends EachChoice
case class InlineRule(rule: Rule, Next: String) extends EachChoice

object EachChoiceJsonFormat extends DefaultJsonProtocol {
  import RuleJsonFormat._
  implicit val andFormat = jsonFormat2(And)
  implicit val orFormat  = jsonFormat2(Or)
  implicit val notFormat = jsonFormat2(Not)
  implicit val inlineRuleFormat = new RootJsonFormat[InlineRule] {
    override def read(json: JsValue): InlineRule = {
      val next                                     = json.extract[String]('Next)
      def removeStr(input: String): Option[String] = None
      val withoutNext                              = json.update('Next ! modifyOrDeleteField(removeStr))
      val rule                                     = withoutNext.convertTo[Rule]
      InlineRule(rule, next)
    }

    override def write(obj: InlineRule): JsValue = {
      val next           = "Next" -> JsString(obj.Next)
      val ruleAsJson     = obj.rule.toJson
      val fieldsAsTuples = ruleAsJson.asJsObject.fields.toList ++ List(next)
      JsObject(fieldsAsTuples: _*)
    }
  }
  implicit val eachChoiceFormat = new RootJsonFormat[EachChoice] {
    // format: OFF
    override def read(json: JsValue): EachChoice = {
      implicit val fields: Map[String, JsValue] = json.asJsObject.fields
      ifThisThen(And.toString(), value => value.convertTo[And])
        .orElse(ifThisThen(Or.toString(), value => value.convertTo[Or]))
        .orElse(ifThisThen(Not.toString(), value => value.convertTo[Not]))
        .orElse(ifThisThen("Variable", value => value.convertTo[InlineRule]))
        .getOrElse(throw new RuntimeException(s"No valid Choice in the json: ${json.prettyPrint}"))
    }
    // format: ON

    override def write(obj: EachChoice): JsValue = obj match {
      case a: And        => a.toJson
      case o: Or         => o.toJson
      case n: Not        => n.toJson
      case r: InlineRule => r.toJson
    }

    private def ifThisThen(key: String, converter: JsValue => EachChoice)(
        implicit fields: Map[String, JsValue]
    ): Option[EachChoice] = {
      fields.get(key).map(converter)
    }
  }
}

case class Choice(
    Comment: Option[String],
    InputPath: Option[String],
    OutputPath: Option[String],
    Choices: List[EachChoice],
    Default: String
)

object JsonFormats extends DefaultJsonProtocol {
  import EachChoiceJsonFormat._
  implicit val passFormat    = jsonFormat8(Pass)
  implicit val retrierFormat = jsonFormat4(Retrier)
  implicit val catcherFormat = jsonFormat2(Catcher)
  implicit val taskFormat    = jsonFormat13(Task)
  implicit val choiceFormat  = jsonFormat5(Choice)
}

object ASLParser {
  import JsonFormats._

  def parse(json: String): StateMachine = {
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
