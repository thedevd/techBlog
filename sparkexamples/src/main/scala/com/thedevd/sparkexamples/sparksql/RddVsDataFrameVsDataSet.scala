package com.thedevd.sparkexamples.sparksql

import org.apache.log4j.Logger
import org.apache.log4j.Level
import org.apache.spark.sql.SparkSession
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.types.StructType
import org.apache.spark.sql.types.StructField
import org.apache.spark.sql.types.StringType

/*
 * We have the data set /sparksql/user_fav_color.
 * we want to know the most favorite color between the blue and the green one.
 *
 * Accomplish this by using RDD, DataFrame and Dataset
 *
 */
object RddVsDataFrameVsDataSet {

  def main(args: Array[String]): Unit = {
    Logger.getLogger("org").setLevel(Level.ERROR)

    val spark = SparkSession.builder()
      .appName("RddVsDataFrameVsDataSet_MostFavColor")
      .master("local[*]")
      .getOrCreate()

    usingRdd(spark)
    usingDataFrame(spark)
    usingDataSet(spark)
    
    /*
     * Let see the major difference among RDD/DataFrame/DataSet
     * ############################################################
     * 1. Schema - 
     * This is the great difference between RDD and DataFrame/Dataset.
     * RDD has no schema,  so It fits well with unstructured data like media or text stream
     * 
     * DataFrame/Dataset are suited for structured and semistructured data. The schema
     * gives good flexibility to manage to domain/business data using domain specific operation
     * like groupBy, avg, orderBy
     * 
     * 2. Performance -
     * A DataFrame/Dataset tends to be more efficient than an RDD. The reason is Catalyst Query
     * Optimization engine.
     * 
     * What happens inside Spark core is that a DataFrame/Dataset is converted into an optimized RDD. 
     * Then Spark analyses the code and chooses the best way to execute it.
     * 
     * For instance, if you are grouping data before filtering it, it is not efficient.
     * If you do this with an RDD, Spark will execute this way.
     * But, if you do the same with a DataFrame/Dataset, Spark will optimize the execution and 
     * will filter your data before grouping them. This is what we call the “Catalyst optimization”.
     * 
     * Why there is performance limitation with RDD is - 
     * Being in-memory jvm objects, RDDs involve overhead of Garbage Collection and Java Serialization 
     * which are expensive when data grows.
     * 
     * Conclusion
     * ##############
     * RDD are typed and offer a way to get analysis errors at compile time. 
     * It is a low level API with no performance optimization, but you have full control on what to do with data. 
     * It is less expressive too and is more useful for unstructured data where you dont want to impose any schema.
     * 
     * DataFrame is more suitable for structure data and more efficient (Catalyst Optimizer). 
     * However, it is untyped and can lead to runtime errors.
     * 
     * Dataset looks like DataFrame but it is typed API. With them, you have compile time type safety. 
     * For serializing data, the Dataset API has the concept of ENCODERS, which is responsible for converting 
     * type-specific JVM objects to tabular representation of dataSet. The tabular representation of dataset is stored 
     * using Spark internal Tungsten binary format which improve memory utilization.
     * Spark 1.6 comes with wide variety of built in encoders types, including primitive types (e.g. String, Integer, Long), Scala case. 
     * 
     * 
     */
  }

  def usingRdd(spark: SparkSession) = {

    // RDD is also typed just like DataSet
    // I have given type RDD[(String, Int)] just to show the RDD is typed.
    // Although giving type not must as scala is type inference language
    val colorCountPairRdd: RDD[(String, Int)] = spark.sparkContext
      .textFile(getClass.getResource("/sparksql/user_fav_color.csv").getPath)
      .map(line => (line.split(",")(1), 1))
      .reduceByKey(_ + _)
      .sortBy(_._2, ascending = false)

    colorCountPairRdd.take(1).toList.foreach(println)
    // (blue,4)
  }

  def usingDataFrame(spark: SparkSession) = {

    val customSchema = new StructType()
      .add(StructField("user_id", StringType, true))
      .add(StructField("color", StringType, true))

    val color_df = spark.read.format("csv")
      .schema(customSchema)
      .load(getClass.getResource("/sparksql/user_fav_color.csv").getPath)

    import org.apache.spark.sql.functions._
    val most_fav_color_df = color_df
      .groupBy("color")
      .count()
      .orderBy(desc("count")) // desc function is imported by  import org.apache.spark.sql.functions._
      .limit(1) // only take the highest count

    most_fav_color_df.show()

  }

  def usingDataSet(spark: SparkSession) = {

    val customSchema = new StructType()
      .add(StructField("userId", StringType, true)) // make sure name matches with case class param name
      .add(StructField("color", StringType, true))

    val color_df = spark.read.format("csv")
      .schema(customSchema)
      .load(getClass.getResource("/sparksql/user_fav_color.csv").getPath)

    import spark.implicits._
    val color_ds = color_df.as[UserFavColor] // using encoders to introduce type-safety

    import org.apache.spark.sql.functions._
    color_ds.groupBy("color")
      .count()
      .orderBy(desc("count"))
      .limit(1)
      .show()
    
  }

  case class UserFavColor(userId: String, color: String)
}