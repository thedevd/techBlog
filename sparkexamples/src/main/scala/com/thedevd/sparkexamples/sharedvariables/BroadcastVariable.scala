package com.thedevd.sparkexamples.sharedvariables

import org.apache.log4j.Logger
import org.apache.log4j.Level
import org.apache.spark.sql.SparkSession
import scala.collection.Map

/*
 * 1. Broadcast variables are immutable shared variable which are cached on each worker nodes on a Spark cluster.
 * 2. When to use Broadcast variable?
 *    ---------------------------------
 *    Broadcast variables are used for reading some common information across worker nodes.
 *    So they are used for read operation such as lookup and join.
 *
 *    Ex: Think of a scenario where you want to lookup the country using ISD code.
 *    And as we know in spark, data is distributed in the form of RDD across workers node, so
 *    each worker will need such lookup information cached locally.
 *
 *    In this situation, broadcast variables will be helping us to lookup country name.
 *
 * 3. Broadcast variable is created using sparkContext.broadcast(variable_name).
 * 4. It can only be read using value() method.
 *
 */
object BroadcastVariable {

  def main(args: Array[String]): Unit = {
    Logger.getLogger("org").setLevel(Level.ERROR)

    val spark = SparkSession.builder()
      .appName("BroadcastVariable")
      .master("local[*]") // for testing using master as local
      .getOrCreate()

    val ISDCodeLookupMap = Map("+91" -> "INDIA", "+1" -> "USA", "+61" -> "AUSTRALIA")
    val broadcastVariable = spark.sparkContext.broadcast(ISDCodeLookupMap) // created broadcast variable

    def lookupCountry(phone: String) = broadcastVariable.value(phone.split("-")(0)) // using value to read broadcast

    val data = spark.sparkContext.parallelize(Seq(
      (1, "dev", "+91-9876543210"),
      (2, "ravi", "+1-6758352390"),
      (3, "atul", "+61-4569021850")), 3) // lets assume this data is distributed across 3 nodes.

    val data_with_country = data
      .map(tuple => (tuple._1, tuple._2, tuple._3, lookupCountry(tuple._3)))

    import spark.implicits._
    spark.createDataFrame(data_with_country).toDF("id", "name", "phone", "country").show()
    /*  +---+----+--------------+---------+
     *  | id|name|         phone|  country|
     *  +---+----+--------------+---------+
     *  |  1| dev|+91-9876543210|    INDIA|
     *  |  2|ravi| +1-6758352390|      USA|
     *  |  3|atul|+61-4569021850|AUSTRALIA|
     *  +---+----+--------------+---------+*/

  }

}