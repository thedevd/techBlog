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
object BroadcastVariableWithUDF {

  def main(args: Array[String]): Unit = {
    Logger.getLogger("org").setLevel(Level.ERROR)

    val spark = SparkSession.builder()
      .appName("BroadcastVariableWithUDF")
      .master("local[*]") // for testing using master as local
      .getOrCreate()

    val ISDCodeLookupMap = Map("+91" -> "INDIA", "+1" -> "USA", "+61" -> "AUSTRALIA")
    val broadcastVariable = spark.sparkContext.broadcast(ISDCodeLookupMap)

    val data = spark.sparkContext.parallelize(Seq(
      (1, "dev", "+91-9876543210"),
      (2, "ravi", "+1-6758352390"),
      (3, "atul", "+61-4569021850")), 3) // lets assume this data is distributed across 3 nodes.
      
   import spark.implicits._
   val df = spark.createDataFrame(data).toDF("id","name","phone") // this is again distributed
   
   val countryLookupUsingISD = (phone: String) => broadcastVariable.value(phone.split("-")(0))
   import org.apache.spark.sql.functions.udf
   val countryLookupUsingUDF = udf(countryLookupUsingISD)
   
    val df_with_location = df.withColumn("country", countryLookupUsingUDF($"phone"))
    df_with_location.show()
   /* +---+----+--------------+---------+
    * | id|name|         phone|  country|
    * +---+----+--------------+---------+
    * |  1| dev|+91-9876543210|    INDIA|
    * |  2|ravi| +1-6758352390|      USA|
    * |  3|atul|+61-4569021850|AUSTRALIA|
    * +---+----+--------------+---------+*/
    

  }
  
}