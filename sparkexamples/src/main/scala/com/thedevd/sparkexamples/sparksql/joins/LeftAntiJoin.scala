package com.thedevd.sparkexamples.sparksql.joins

import org.apache.log4j.Logger
import org.apache.log4j.Level
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.types.StructType
import org.apache.spark.sql.types.StructField
import org.apache.spark.sql.types.IntegerType
import org.apache.spark.sql.types.StringType

/*
 * Left anti join does exactly the opposite of Left semi-join. It means
 * the output would just return the data that doesn’t have a match on the right table. And
 * Only the columns on the left table would be included in the result just like left semi join.
 */
object LeftAntiJoin {

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

    println("###### Left anti Join")
    emp_df.join(dept_df, Seq("dept_id"), "leftanti").show()
    /*+-------+------+--------+
     * |dept_id|emp_id|emp_name|
     * +-------+------+--------+
     * |   null|   103|    atul|
     * +-------+------+--------+
     * 
     * Only the emp_id 103 had no match in right table, so only
     * this is included by left-anti join.
     * */

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
     * Only the matching id of left table are included. 
     * */
  }

}