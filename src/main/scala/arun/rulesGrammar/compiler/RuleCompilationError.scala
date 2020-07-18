package arun.rulesGrammar.compiler

trait RuleCompilationError

case class RuleLexerError(location: Location, msg: String) extends RuleCompilationError
case class RuleParserError(location: Location, msg: String) extends RuleCompilationError

case class Location(line: Int, column: Int) {
  override def toString: String = s"[$line:$column]"
}
