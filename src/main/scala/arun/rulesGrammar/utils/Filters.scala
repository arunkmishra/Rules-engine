package arun.rulesGrammar.utils
import org.apache.spark.sql.{Column, DataFrame}

class Filters(df: DataFrame) {

  def equalFilter(a: String, b: Any): Column =
    df(a) === b

  def notEqualFilter(a: String, b: Any): Column =
    !equalFilter(a, b)

  def isinFilter(a: String, b: List[Any]): Column =
    df(a).isin(b:_*)

  def greaterThan(a: String, b: Any): Column =
    df(a) > b

  def identity: Column =
    df("fact_name") === "arun"

}
