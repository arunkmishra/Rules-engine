import arun.tables.{ChainRules, InitSpark, TablesWithData}
import arun.engine.Engine._
import org.apache.spark.sql.DataFrame

object Runner extends InitSpark {

  def main(args: Array[String]): Unit = {

    import spark.implicits._

    val tablesData = new TablesWithData(spark)

    /*println(s"coresPerExecutor: ${coresPerExecutor(sc)}")
    println(s"cores: $coreCount")
    println(s"executorCount: $executorCount")*/

    tablesData.consolidateFact.createOrReplaceTempView("cdl_f_consolidated")

    val rule: DataFrame = tablesData.ruleTable

    val ruleChain = tablesData.chainRule

    //ChainRules.getAllRulesChaining(ruleChain).foreach(println)

    /*val q1: String = parseRow(rule.filter(rule("rule") === 1).head)
    val q2: String = parseRow(rule.filter(rule("rule") === 2).head)
    println("query1: " + q1)
    println("query2: " + q2)

    //spark.sql("SELECT amount from cdl_f_consolidated where product = 1 and country = 'Australia'").show
    spark.sql(q1).show
    spark.sql(q2).show

    rule1.show()*/
    spark.sql(ChainRules.getRuleById("rule_1", ruleChain)).show

    def parseTable(ruleId: String): String =
      parseRow(rule.filter(rule("rule") === ruleId).head)

    sparkClose

  }


}
