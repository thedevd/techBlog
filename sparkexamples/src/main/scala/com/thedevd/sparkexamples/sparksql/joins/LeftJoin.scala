package com.thedevd.sparkexamples.sparksql.joins

import org.apache.log4j.Logger
import org.apache.log4j.Level
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.types.StructType
import org.apache.spark.sql.types.StructField
import org.apache.spark.sql.types.IntegerType
import org.apache.spark.sql.types.StringType
import org.apache.spark.sql.types.DoubleType

/*
 * Problem
 * ##########
 * Read /sparksql/joins/customers.csv and /sparksql/joins/orders.csv.
 * Find the customer details who has not placed any order.
 */
object LeftJoin {

  case class Customer(customerId: Integer, customerName: String)
  case class Order(orderId: Integer, customerId: Integer, amount: Double)

  def main(args: Array[String]): Unit = {
    Logger.getLogger("org").setLevel(Level.ERROR)

    val spark = SparkSession.builder()
      .appName("SparkSql-LeftJoin")
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

    println("############### left Join example")
    /*
     * If a matching id is found in the right table, it is returned or else a null is appended.
     * We use Left Join when nulls matter, for example here we want to know customer list who has not ordered yet.
     */
    val leftjoin_ds = customer_ds.join(order_ds, Seq("customerId"), "left")
    // val leftjoin_ds = customer_ds.join(order_ds, Seq("customerId"), "leftouter") // left or leftouter is same
    leftjoin_ds.show()

    /* +----------+------------+-------+------+
    * |customerId|customerName|orderId|amount|
    * +----------+------------+-------+------+
    * |       101|        Ravi|      2|1500.0|
    * |       101|        Ravi|      1| 450.0|
    * |       102|         Dev|      3| 690.0|
    * |       103|        Atul|   null|  null|
    * +----------+------------+-------+------+*/

    leftjoin_ds.where(leftjoin_ds("orderId").isNull).show()
    // leftjoin_ds.where("orderId is null").show()
    /*+----------+------------+-------+------+
     * |customerId|customerName|orderId|amount|
     * +----------+------------+-------+------+
     * |       103|        Atul|   null|  null|
     * +----------+------------+-------+------+*/
  }
}