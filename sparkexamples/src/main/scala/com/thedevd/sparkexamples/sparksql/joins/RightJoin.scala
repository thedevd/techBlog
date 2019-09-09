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
 * #########
 * Read /sparksql/joins/employee.csv and /sparksql/department.csv.
 * Find all the department where no employee is working using right join
 */
object RightJoin {

  case class Employee(employeeId: Integer, employeeName: String, deptId: Integer)
  case class Department(deptId: Integer, deptName: String)

  def main(args: Array[String]): Unit = {
    Logger.getLogger("org").setLevel(Level.ERROR)

    val spark = SparkSession.builder()
      .appName("SparkSql-RightJoin")
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

    val empSchema = new StructType()
      .add(StructField("employeeId", IntegerType, false))
      .add(StructField("employeeName", StringType, false))
      .add(StructField("deptId", IntegerType, true)) // dept_id can be nullable for some employee

    // Creating department schema
    val deptSchema = new StructType()
      .add(StructField("deptId", IntegerType, false))
      .add(StructField("deptName", StringType, false))

    // Creating dataframes using schema
    val emp_df = spark.read.schema(empSchema)
      .option("header", true)
      .csv(getClass.getResource("/sparksql/joins/employee.csv").getPath)

    val dept_df = spark.read.schema(deptSchema)
      .option("header", true)
      .csv(getClass.getResource("/sparksql/joins/department.csv").getPath)

    // dataframe to dataset conversion using encoders ----------
    import spark.implicits._
    val emp_ds = emp_df.as[Employee]
    val dept_ds = dept_df.as[Department]

    println("Employee table")
    emp_ds.show()
    println("Department table")
    dept_ds.show()

    println("############### right Join example")
    /*
     * Just like leftjoin, in right join all the rows from the Right table are returned irrespective of
     * whether there is a match in the left side table. If no matching found in left table, nulls are appended.
     */

    //val rightjoin_ds = emp_ds.join(dept_ds, Seq("deptId"), "right")
    val rightjoin_ds = emp_ds.join(dept_ds, Seq("deptId"), "rightouter") // right or rightouter is same
    rightjoin_ds.show()
  /* +------+----------+------------+--------+
   * |deptId|employeeId|employeeName|deptName|
   * +------+----------+------------+--------+
   * |     1|       102|        ravi|      IT|
   * |     1|       101|         dev|      IT|
   * |     2|       104|        isha|Admin HR|
   * |     3|      null|        null| Finance|
   * +------+----------+------------+--------+*/
    
    println("Dept where no employee is working")
    rightjoin_ds.select("deptId", "deptName").where(rightjoin_ds("employeeId").isNull).show()
   /* +------+--------+
    * |deptId|deptName|
    * +------+--------+
    * |     3| Finance|
    * +------+--------+*/


  }
}