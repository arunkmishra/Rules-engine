package arun.rulesGrammar.lexer

import arun.rulesGrammar.compiler.{Location, RuleLexerError}

import scala.util.parsing.combinator.RegexParsers

object RuleLexer extends RegexParsers{

  override def skipWhitespace: Boolean = true
  override val whiteSpace = "[ \t\r\f]+".r

  def apply(code: String): Either[RuleLexerError, List[RuleToken]] = {
    parse(tokens, code) match {
      case NoSuccess(msg, next) => Left(RuleLexerError(Location(next.pos.line, next.pos.column), msg))
      case Success(result, next) => Right(result)
    }
  }

  def tokens: Parser[List[RuleToken]] = {
    phrase(rep1(equals | or | and | in | comma | greaterThan | literal | identifier))
  }

  def identifier: Parser[IDENTIFIER] =
    "[a-zA-Z_][a-zA-Z0-9_]*".r ^^ { str => IDENTIFIER(str) }


  def literal: Parser[LITERAL] =
    """"[^"]*"""".r ^^ { str =>
      val content = str.substring(1, str.length - 1)
      LITERAL(content)
    }

  def equals: RuleLexer.Parser[EQUALS]          = "=="            ^^ (_ => EQUALS())
  def and: RuleLexer.Parser[AND]                = "and"            ^^ (_ => AND())
  def or: RuleLexer.Parser[OR]                  = "or"            ^^ (_ => OR())
  def in: RuleLexer.Parser[IN]                  = "in"            ^^ (_ => IN())
  def greaterThan: RuleLexer.Parser[GREATERTHAN] = "gt"            ^^ (_ => GREATERTHAN())
  def comma: RuleLexer.Parser[COMMA]            = ","             ^^ (_ => COMMA())

}
