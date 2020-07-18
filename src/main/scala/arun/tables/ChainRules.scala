package arun.tables

import org.apache.spark.sql.DataFrame
import arun.engine.Engine._

case class ChainRules(ruleNumber: String, chaining: String, Type: String)

object ChainRules extends InitSpark {

  val data = new TablesWithData(spark)

  def getRuleById(ruleId: String, df: DataFrame): String = {
    val currentRule = df.filter(df("rule") === ruleId).head
    val currentType: String = currentRule.getAs[String]("type")

    currentType match {
      case s: String if s == "rule" || s == "mixed" =>
        evaluateRules(currentRule.getAs[String]("chaining"), df, ruleId)
      case "table" =>
        evaluateRulesTables(ruleId)
      case _ => s"Failed for type : $currentType"
    }

  }

  def evaluateRules(chain: String, df: DataFrame, ruleId: String): String = {
    val parts: List[String] = chain.split(" ").toList

    def dep(ls: List[String], acc: String): String = {
      ls match {
        case h :: tail if h.startsWith("rule") =>
          dep(tail, acc concat s"( ${getRuleById(h, df)} )")
        case h :: tail if h.startsWith("table") =>
          dep(tail, acc concat s"( ${evaluateTables(h)} )")
        case h :: tail => dep(tail, acc + h.toString)
        case Nil       => acc
      }
    }

    dep(parts, "")
  }

  def evaluateRulesTables(ruleId: String): String =
    parseRow(data.ruleTable.filter(data.ruleTable("rule") === ruleId).head)

  def evaluateTables(str: String): String =
  s"SELECT * FROM $str ${str}_tmp"

  def getAllRulesChaining(df: DataFrame): Seq[String] = {
    val rules = df.collect.map(_.getAs[String]("rule"))
    rules.map { ruleId => s"$ruleId : " + getRuleById(ruleId, df)
    }
  }
}
