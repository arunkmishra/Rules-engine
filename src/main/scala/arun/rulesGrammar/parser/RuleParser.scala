package arun.rulesGrammar.parser

import arun.rulesGrammar.compiler.{Location, RuleParserError}
import arun.rulesGrammar.lexer._

import scala.util.parsing.combinator.Parsers
import scala.util.parsing.input.{NoPosition, Position, Reader}

object RuleParser extends Parsers {

  override type Elem = RuleToken

  class RuleTokenReader(tokens: Seq[RuleToken]) extends Reader[RuleToken] {
    override def first: RuleToken = tokens.head
    override def atEnd: Boolean = tokens.isEmpty
    override def pos: Position = NoPosition
    override def rest: Reader[RuleToken] = new RuleTokenReader(tokens.tail)
  }

  def apply(tokens: Seq[RuleToken]): Either[RuleParserError, RuleAST] = {
    val reader = new RuleTokenReader(tokens)
    program(reader) match {
      case NoSuccess(msg, next) => Left(RuleParserError(Location(next.pos.line, next.pos.column), msg))
      case Success(result, next) => Right(result)
    }
  }

  def program: Parser[RuleAST] = positioned {
    phrase(block)
  }

  def block: Parser[RuleAST] = positioned {
    andParser | orParser
  }

  def orParser: Parser[RuleAST] = {
    rep(conditionalAst ~ OR()) ~ conditionalAst ^^ {
      case conditionAnd ~ c2 => Or(c2, explodeTypeConditions(OnlyOneCondition(EmptyCondition), conditionAnd))
    }
  }

  def explodeTypeConditions(acc: RuleAST, list: List[ConditionAST ~ RuleParser.Elem]): RuleAST = {
      list match {
        case h :: Nil => explodeTypeConditions(Or(h._1, acc), Nil)
        case h :: t => explodeTypeConditions(Or(h._1, acc), t)
        case Nil => acc
      }
    }


  def andParser: Parser[RuleAST] = {
    rep(conditionalAst ~ AND()) ~ conditionalAst ^^ {
      case conditionAnd ~ c2 => And(c2, explodeConditions(OnlyOneCondition(EmptyCondition), conditionAnd))
    }
  }

  def explodeConditions(acc: RuleAST, list: List[ConditionAST ~ RuleParser.Elem]): RuleAST = {
    list match {
      case h :: Nil => explodeConditions(And(h._1, acc), Nil)
      case h :: t => explodeConditions(And(h._1, acc), t)
      case Nil => acc
    }
  }


  def conditionalAst: Parser[ConditionAST] = {
    inParser | equalParser | greaterThanParser
  }

  def equalParser: Parser[ConditionAST] = {
    val eq = (identifier ~ EQUALS() ~ literal) ^^ { case IDENTIFIER(id) ~ equ ~ LITERAL(lit) => Equals(id, lit) }
    eq
  }

  def inParser: Parser[ConditionAST] = {
    val isin = (identifier ~ IN() ~ rep(identifier ~ COMMA()) ~ identifier) ^^ {
      case IDENTIFIER(str) ~ in ~ inputs ~ IDENTIFIER(lastInput) => In(str, inputs.map(_._1.str) ++ List(lastInput))
    }
    isin
  }

  def greaterThanParser: Parser[ConditionAST] = {
    val gt = (identifier ~ GREATERTHAN() ~ literal) ^^ {
      case IDENTIFIER(factName) ~ gt ~ LITERAL(factValue) => GreaterThan(factName, factValue)
    }
    gt
  }



  def condition: Parser[ConditionAST] = ???

  def andOr: Parser[RuleAST] = ???


  private def identifier: Parser[IDENTIFIER] =
    accept("identifier", { case id @ IDENTIFIER(name) => id })


  private def literal: Parser[LITERAL] =
    accept("string literal", { case lit @ LITERAL(name) => lit })


}
