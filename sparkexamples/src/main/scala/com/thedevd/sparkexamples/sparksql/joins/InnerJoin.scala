package com.thedevd.sparkexamples.sparksql.joins

import org.apache.log4j.Level
import org.apache.log4j.Logger
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.types.DoubleType
import org.apache.spark.sql.types.IntegerType
import org.apache.spark.sql.types.StringType
import org.apache.spark.sql.types.StructField
import org.apache.spark.sql.types.StructType


/*
 * Problem
 * ##########
 * Read /sparksql/joins/customers.csv and /sparksql/joins/orders.csv
 * Find details of customer's order along with amount
 */
object InnerJoin {

  case class Customer(customerId: Integer, customerName: String)
  case class Order(orderId: Integer, customerId: Integer, amount: Double)

  def main(args: Array[String]): Unit = {
    Logger.getLogger("org").setLevel(Level.ERROR)

    val spark = SparkSession.builder()
      .appName("SparkSql-InnerJoin")
      .master("local[*]")
      .getOrCreate()

    /* Currently, Spark offers
      * 1. Inner-Join,
      * 2. Left-Join,
      * 3. Right-Join,
      * 4. Outer-Join
      * 5. Cross-Join,
      * 6. Left-Semi-Join,
      * 7. Left-Anti-Semi-Join
     */

    // Customer ------------
    val custSchema = new StructType()
      .add(StructField("customerId", IntegerType, true))
      .add(StructField("customerName", StringType, true))

    val customer_df = spark.read.format("csv")
      .option("header", true)
      .option("delimiter", ",")
      .schema(custSchema)
      .load(getClass.getResource("/sparksql/joins/customers.csv").getPath)

    // Order ----------------
    val orderSchema = new StructType()
      .add(StructField("orderId", IntegerType, true))
      .add(StructField("customerId", IntegerType, true))
      .add(StructField("amount", DoubleType, true))

    val order_df = spark.read.format("csv")
      .option("header", true)
      .option("sep", ",") // sep can also be used instead of delimiter
      .schema(orderSchema)
      .load(getClass.getResource("/sparksql/joins/orders.csv").getPath)

    // dataframe to dataset conversion using encoders ----------
    import spark.implicits._
    val customer_ds = customer_df.as[Customer]
    val order_ds = order_df.as[Order]

    println("############### Inner Join example")
    /*
     * This is the default join in Spark if no join type is given.
     * This join keeps only the common things of two tables based on the matching column.
     * This is like intersection operation.
     */
    val innerjoin_ds = customer_ds.join(order_ds, "customerId") // customerId has to be on both table
    // val innerjoin_ds = customer_ds.join(order_ds, Seq("customerId"), "inner") // using joinType
    innerjoin_ds.show()

    /* +----------+------------+-------+------+
    * |customerId|customerName|orderId|amount|
    * +----------+------------+-------+------+
    * |       101|        Ravi|      1| 450.0|
    * |       101|        Ravi|      2|1500.0|
    * |       102|         Dev|      3| 690.0|
    * +----------+------------+-------+------+*/

    /*
     * Some Notes
     * #############
     * 1. As you noticed above that we have not specified joinType, so by default spark considered it as inner join.
     * So what if you want to specify joinType, then use these overloaded functions -
     *
     * 		 customer_ds.join(order_ds, Seq("customerId"), "inner") // or
     * 		 customer_ds.join(order_ds, $"customerId", "inner") // for this import spark.implicits._
     *
     */

  }
}