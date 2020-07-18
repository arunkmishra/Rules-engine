import arun.rulesGrammar.compiler.RuleCompiler
import arun.rulesGrammar.compiler.RuleCompiler._
import arun.rulesGrammar.parser.RuleAST
import arun.tables.{InitSpark, TablesWithData}
import org.apache.spark.sql.DataFrame

object RuleRunner extends InitSpark {

  def main(args: Array[String]): Unit = {
    println("Started")

    val r = "country in India,USA "

    val out = RuleCompiler(r)

    print(out)

   val tablesData = new TablesWithData(spark)

    val cdlDF = tablesData.consolidateFact

    def transformFilter(ruleAST: RuleAST)(df: DataFrame): DataFrame = {
      df.filter(df.getColumnFromRules(ruleAST))
    }

    val res: DataFrame = out match {
      case Right(r) => cdlDF.transform(transformFilter(r))
      case Left(err) => {
        println("error: " + err)
        cdlDF
      }
    }

    res.show()


  }

}
