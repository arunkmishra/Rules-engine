package arun.rulesGrammar.compiler
import arun.rulesGrammar.lexer.RuleLexer
import arun.rulesGrammar.parser._
import arun.rulesGrammar.utils.Filters
import org.apache.spark.sql.{Column, DataFrame}
import org.apache.spark.sql.functions.lit

import scala.annotation.tailrec

object RuleCompiler {
  def apply(code: String): Either[RuleCompilationError, RuleAST] = {
    for {
      tokens <- RuleLexer(code).right
      ast <- RuleParser(tokens).right
    } yield ast
  }

  implicit class RichDf(dataFrame: DataFrame) {
    val filters = new Filters(dataFrame)
    def getColumnFromRules(ruleAST: RuleAST): Column = {
      @tailrec
      def tailFunc(col: Column, rule: RuleAST): Column =
        rule match {
          case And(condition, r) => tailFunc(col && convertConditionToColumn(condition), r)
          case Or(condition, r) => tailFunc(col || convertConditionToColumn(condition), r)
          case OnlyOneCondition(condition) => col && convertConditionToColumn(condition)
        }
      tailFunc(lit(true), ruleAST)

    }


    private def convertConditionToColumn(conditionAST: ConditionAST) = conditionAST match {
      case Equals(a, b) => filters.equalFilter(a, b)
      case In(a, b) => filters.isinFilter(a, b)
      case GreaterThan(a, b) => filters.greaterThan(a, b)
      case EmptyCondition => filters.identity
    }
  }

}