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
 * Read /sparksql/joins/employee.csv and /sparksql/joins/department.csv.
 * Find following -
 * 1. find a department which doesn’t have an employee
 * 2. also find an employee who doesn’t have a department means they are just hired and yet to be assigned to dept.
 *
 * Note- You can see here we require data from both table so fullouter join is used.
 *
 */
object FullOuterJoin {

  def main(args: Array[String]): Unit = {
    Logger.getLogger("org").setLevel(Level.ERROR)

    val spark = SparkSession.builder()
      .appName("SparkSql-FullOuterJoin")
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

    // Achieving this using DataFrame only. so no case classes here as done in RightJoin.scala

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