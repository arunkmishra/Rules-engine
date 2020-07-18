package arun.tables

import org.apache.spark.sql.{DataFrame, SparkSession}

class TablesWithData(spark: SparkSession) {

  import spark.implicits._

  val consolidateFact: DataFrame = Seq(
    (1, "Australia", "b", 20, 104.1, "arun"),
    (3, "India", "ab", 30, 103.1, "arun"),
    (2, "UK", "cd", 40, 104.1, "arun"),
    (1, "USA", "cd", 30, 120.1, "arun"),
    (4, "Russia", "b", 140, 110.1, "arun"),
    (3, "China", "ab", 120, 110.1, "arun"),
    (4, "Germany", "a", 110, 104.1, "arun")
  ).toDF("product", "country", "cpty", "amount", "notional_amt", "fact_name")

  val rule1Tuple = (
    "rule_1",
    "cdl_f_consolidated",
    "stress_f_result",
    "product EQUALS 1 && country ISIN ('Australia', 'India')",
    "amount"
  )

  val rule2Tuple = (
    "rule_2",
    "cdl_f_consolidated",
    "stress_f_result",
    "cpty ISIN ('ab') && country EQUALS 'China' || product EQUALS 3",
    "notional_amt"
  )
  val rule3Tuple = (
    "rule_3",
    "cdl_f_consolidated",
    "stress_f_result",
    "cpty ISIN ('ab') || product EQUALS 3",
    "notional_amt"
  )

  val ruleTable = Seq(rule1Tuple, rule2Tuple, rule3Tuple).toDF(
    "rule",
    "source",
    "destination",
    "given",
    "action"
  )

  val chainRule: DataFrame = Seq(
    ("rule_1", "table(cdl_f_consolidated)", "table"),
    ("rule_2", "table(cdl_f_consolidated)", "table"),
    ("rule_3", "table(user)", "table"),
    ("rule_4", "rule_2 || rule_3", "rule"),
    ("rule_5", "rule_4 && table(emp)", "mixed")
  ).toDF("rule", "chaining", "type") //.as[ChainRules]

}
