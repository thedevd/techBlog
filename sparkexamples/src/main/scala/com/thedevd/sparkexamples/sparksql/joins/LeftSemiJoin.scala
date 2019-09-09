package com.thedevd.sparkexamples.sparksql.joins

import org.apache.log4j.Logger
import org.apache.log4j.Level
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.types.StructType
import org.apache.spark.sql.types.StructField
import org.apache.spark.sql.types.IntegerType
import org.apache.spark.sql.types.StringType

/*
 *
 * Left semi join returns only the data from the left table that has a match on the right table
 * based on the condition provided in the join statement.
 *
 * So this is really useful when you are want to extract only the data in left table that has a match on the right.
 * Semi-Join can look similar to Inner Join, but there is difference.
 */
object LeftSemiJoin {

  def main(args: Array[String]): Unit = {
    Logger.getLogger("org").setLevel(Level.ERROR)

    val spark = SparkSession.builder()
      .appName("SparkSql-LeftSemiJoin")
      .master("local[*]")
      .getOrCreate()

    val empSchema = new StructType()
      .add(StructField("emp_id", IntegerType, false))
      .add(StructField("emp_name", StringType, false))
      .add(StructField("dept_id", IntegerType, true))

    val deptSchema = new StructType()
      .add(StructField("dept_id", IntegerType, false))
      .add(StructField("dept_name", StringType, false))

    val emp_df = spark.read.schema(empSchema)
      .option("header", true)
      .option("delimiter", ",")
      .csv(getClass.getResource("/sparksql/joins/employee.csv").getPath)

    val dept_df = spark.read.schema(deptSchema)
      .option("header", true)
      .option("delimiter", ",")
      .csv(getClass.getResource("/sparksql/joins/department.csv").getPath)

    println("###### Left Semi Join")
    emp_df.join(dept_df, Seq("dept_id"), "leftsemi").show()
    /* +-------+------+--------+
     * |dept_id|emp_id|emp_name|
     * +-------+------+--------+
     * |      1|   101|     dev|
     * |      1|   102|    ravi|
     * |      2|   104|    isha|
     * +-------+------+--------+
     * 
     * */

    println("###### Left Join vs Left semi join")
    emp_df.join(dept_df, Seq("dept_id"), "left").show()
    /* +-------+------+--------+---------+
    * |dept_id|emp_id|emp_name|dept_name|
    * +-------+------+--------+---------+
    * |      1|   101|     dev|       IT|
    * |      1|   102|    ravi|       IT|
    * |   null|   103|    atul|     null|
    * |      2|   104|    isha| Admin HR|
    * +-------+------+--------+---------+
    *
    * See here emp_name 'Atul' had no dept_id, but this was part of left table so
    * left join also included this. But in left semi join exactly only matching of left table
    * is included, rest are ignored
    * */

    println("##### Inner join vs Left semi join")
    emp_df.join(dept_df, Seq("dept_id"), "inner").show()
   /* +-------+------+--------+---------+
    * |dept_id|emp_id|emp_name|dept_name|
    * +-------+------+--------+---------+
    * |      1|   101|     dev|       IT|
    * |      1|   102|    ravi|       IT|
    * |      2|   104|    isha| Admin HR|
    * +-------+------+--------+---------+
    * 
    * Semi-Join can look similar to Inner Join but the difference between them is that 
    * Left Semi Join only returns the columns from the left table including matching column, 
    * whereas the Inner Join returns the columns from both tables.
    * 
    * So you can see here in inner join that 'dept_name' column is also included which is 
    * from right table, where as in left-semi it is not included.
    * 
    * */
  }
}