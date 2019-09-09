package com.thedevd.sparkexamples.sparksql

import org.apache.log4j.Logger
import org.apache.log4j.Level
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.types.StructType
import org.apache.spark.sql.types.StructField
import org.apache.spark.sql.types.IntegerType
import org.apache.spark.sql.types.StringType
import org.apache.spark.sql.types.DoubleType
import org.apache.spark.sql.Dataset
import org.apache.spark.sql.Dataset
import org.apache.spark.sql.Dataset

object SparkSqlJoins {

  case class Customer(customerId: Integer, customerName: String)
  case class Order(orderId: Integer, customerId: Integer, amount: Double)

  def main(args: Array[String]): Unit = {
    Logger.getLogger("org").setLevel(Level.ERROR)

    val spark = SparkSession.builder()
      .appName("SparkSqlJoins")
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
      * 8. Self join
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
    innerjoin_example(customer_ds, order_ds)

    println("############### left Join example")
    leftjoin_example(customer_ds, order_ds)

    println("############### right Join example")
    rightjoin_example(customer_ds, order_ds)

    println("############### full outer Join example")
    fullouterjoin_example(spark)

  }

  def innerjoin_example(customer_ds: Dataset[SparkSqlJoins.Customer], order_ds: Dataset[SparkSqlJoins.Order]) = {

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

  def leftjoin_example(customer_ds: Dataset[SparkSqlJoins.Customer], order_ds: Dataset[SparkSqlJoins.Order]) = {

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

  def rightjoin_example(customer_ds: Dataset[SparkSqlJoins.Customer], order_ds: Dataset[SparkSqlJoins.Order]) = {

    /*
     * Just like leftjoin, in right join all the rows from the Right table are returned irrespective of
     * whether there is a match in the left side table. If no matching found in left table, nulls are appended.
     */

    //val rightjoin_ds = customer_ds.join(order_ds, Seq("customerId"), "right")
    val rightjoin_ds = customer_ds.join(order_ds, Seq("customerId"), "rightouter") // right or rightouter is same
    rightjoin_ds.show()
    /*+----------+------------+-------+------+
     * |customerId|customerName|orderId|amount|
     * +----------+------------+-------+------+
     * |       101|        Ravi|      1| 450.0|
     * |       101|        Ravi|      2|1500.0|
     * |       102|         Dev|      3| 690.0|
     * +----------+------------+-------+------+*/

    /*
      * Notice here if we exchange the right and left dataset it would behave like left join
      *  val rightjoin_ds = order_ds.join(customer_ds, Seq("customerId"), "rightouter")
      *+----------+------------+-------+------+
    	* |customerId|customerName|orderId|amount|
    	* +----------+------------+-------+------+
    	* |       101|        Ravi|      2|1500.0|
    	* |       101|        Ravi|      1| 450.0|
    	* |       102|         Dev|      3| 690.0|
    	* |       103|        Atul|   null|  null|
    	* +----------+------------+-------+------+*/
  }

  def fullouterjoin_example(spark: SparkSession) = {
    /*
     * Full outer join is recommended when would require data from both the table.
     * For example, considering an IT organization -
     * 1. you want to find a department which doesn’t have an employee and
     * 2. also find an employee who doesn’t have a department means they are just hired and yet to be assigned to dept.
     */
    
    // Creating employee schema
    val empSchema = new StructType()
      .add(StructField("emp_id", IntegerType, false))
      .add(StructField("emp_name", StringType, false))
      .add(StructField("dept_id", IntegerType, true)) // dept_id can be nullable for some employee

    // Creating department schema
    val deptSchema = new StructType()
      .add(StructField("dept_id", IntegerType, false))
      .add(StructField("dept_name", StringType, false))

    // Creating dataframes using schema
    val emp_df = spark.read.schema(empSchema)
      .option("header", true)
      .csv(getClass.getResource("/sparksql/joins/employee.csv").getPath)
      
    val dept_df = spark.read.schema(deptSchema)
    .option("header", true)
    .csv(getClass.getResource("/sparksql/joins/department.csv").getPath)

    println("Employee table")
    emp_df.show()
    println("Department table")
    dept_df.show()
    
    println("Full outer join")
    val fulljoin_df = emp_df.join(dept_df, Seq("dept_id"), "full")
    // val fulljoin_df = emp_df.join(dept_df, Seq("dept_id"), "fullouter")
    fulljoin_df.show()
   /* +-------+------+--------+---------+
    * |dept_id|emp_id|emp_name|dept_name|
    * +-------+------+--------+---------+
    * |   null|   103|    atul|     null|
    * |      1|   101|     dev|       IT|
    * |      1|   102|    ravi|       IT|
    * |      3|  null|    null|  Finance|
    * |      2|   104|    isha| Admin HR|
    * +-------+------+--------+---------+*/
    
    println("find employees who doesn’t have a department")
    fulljoin_df.select("emp_id", "emp_name").where(fulljoin_df("dept_id").isNull).show()
   /* +------+--------+
    * |emp_id|emp_name|
    * +------+--------+
    * |   103|    atul|
    * +------+--------+*/
    
    println("find department which doesn’t have an employee")
    fulljoin_df.select("dept_id", "dept_name").where(fulljoin_df("emp_id").isNull).show()
    /*+-------+---------+
     * |dept_id|dept_name|
     * +-------+---------+
     * |      3|  Finance|
     * +-------+---------+*/
  }

}