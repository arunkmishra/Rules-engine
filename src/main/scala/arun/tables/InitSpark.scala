package arun.tables

import org.apache.log4j.{Level, Logger}
import org.apache.spark.sql.{DataFrameReader, SQLContext, SparkSession}
import org.apache.spark.SparkContext

trait InitSpark {

  val spark: SparkSession = SparkSession
    .builder()
    .appName("Arun_rules")
    .master("local[*]")
    .getOrCreate()

  val sc: SparkContext = spark.sparkContext

  val sqlContext: SQLContext = spark.sqlContext

  val reader: DataFrameReader = spark.read
    .option("header", true)
    .option("inferSchema", true)
    .option("mode", "DROPMALFORMED")

  def initLog = {
    sc.setLogLevel("ERROR")
    Logger.getLogger("org").setLevel(Level.INFO)
    Logger.getLogger("akka").setLevel(Level.INFO)
    Logger.getRootLogger.setLevel(Level.INFO)
  }

  initLog

  def sparkClose =
    spark.close()

  /*----------------------------SPARK CONTEXT----------------------------------------*/
  private var _coresPerExecutor = 0

  def coresPerExecutor(spc: SparkContext): Int =
    synchronized {
      if (_coresPerExecutor == 0)
        sc.range(0, 1)
          .map(_ => java.lang.Runtime.getRuntime.availableProcessors)
          .collect
          .head
      else
        _coresPerExecutor
    }

  def executorCount: Int =
    sc.getExecutorMemoryStatus.size - 1

  def coreCount: Int =
    executorCount * coresPerExecutor(sc)

}
