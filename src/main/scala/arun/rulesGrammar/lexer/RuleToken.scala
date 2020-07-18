package arun.rulesGrammar.lexer

trait RuleToken

case class IDENTIFIER(str: String) extends RuleToken
case class LITERAL(str: String) extends RuleToken
case class EQUALS() extends RuleToken
case class AND() extends RuleToken
case class OR() extends RuleToken
case class IN() extends RuleToken
case class GREATERTHAN() extends RuleToken
case class COMMA() extends RuleToken