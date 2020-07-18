package arun.rulesGrammar.parser

import scala.util.parsing.input.Positional

sealed trait RuleAST extends Positional
case class And(condition1: ConditionAST, condition2: RuleAST) extends RuleAST
case class Or(condition1: ConditionAST, condition2: RuleAST) extends RuleAST
case class OnlyOneCondition(conditionAST: ConditionAST) extends RuleAST

sealed trait ConditionAST
case class Equals(a: String, b: Any) extends ConditionAST
case class In(a: String, b: List[Any]) extends ConditionAST
case class GreaterThan(a: String, b: Any) extends ConditionAST

case object EmptyCondition extends ConditionAST

sealed trait Ref
case class Field(name: String) extends Ref
case class Value(x: Any) extends Ref