package com.thedevd.sparkexamples.sparksql.joins

import org.apache.log4j.Logger
import org.apache.log4j.Level
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.types.StructType
import org.apache.spark.sql.types.StructField
import org.apache.spark.sql.types.IntegerType
import org.apache.spark.sql.types.StringType

/*
 * Problem
 * #########
 * Read /sparksql/joins/emp_manager.csv which has details of employee and their manager.
 * Find all the employee who are also manager.
 *
 * This type of problem where we need to join a table with itself is called self join.
 * 
 * In self join, we join the dataframe with itself. But for this,
 * We have to make sure we are giving aliases to the dataframe so that we can access 
 * the individual columns without name collisions.
 */

object SelfJoin {

  def main(args: Array[String]): Unit = {
    Logger.getLogger("org").setLevel(Level.ERROR)

    val spark = SparkSession.builder()
      .appName("SparkSql-SelfJoin")
      .master("local[*]")
      .getOrCreate()

    val schema = new StructType()
      .add(StructField("emp_id", IntegerType, false))
      .add(StructField("emp_name", StringType, false))
      .add(StructField("manager_id", IntegerType, true)) // some employee might not have any manager

    val emp_manager_df = spark.read.schema(schema)
      .option("header", true) // Let the spark tell, file has header
      .option("delimiter", "|") // values are separated by pipe |
      .csv(getClass.getResource("/sparksql/joins/emp_manager.csv").getPath)

    emp_manager_df.show()
    /*  +------+--------+----------+
    *  |emp_id|emp_name|manager_id|
    *  +------+--------+----------+
    *  |     1|     Dev|       100|
    *  |     2|   Ankit|       100|
    *  |   100|    Ravi|       420|
    *  |   420|    Siri|      null|
    *  +------+--------+----------+
    */

    import spark.implicits._ // for using $
    // The following two are equivalent:
    val selfjoin = emp_manager_df.as("emp").join(
      emp_manager_df.as("manager"),
      $"emp.manager_id" === $"manager.emp_id") // === Equality test b/w two columns

    val selfjoin1 = emp_manager_df.as("emp").join(emp_manager_df.as("manager")) // self join using alias
      .where($"emp.manager_id" === $"manager.emp_id") // joining condition
      
    println("Showing the employee with their manager name")
    selfjoin1.select($"e.emp_id", $"e.emp_name", $"m.emp_name".as("manager_name")).show() // select using alias
   /* +------+--------+------------+
    * |emp_id|emp_name|manager_name|
    * +------+--------+------------+
    * |     1|     Dev|        Ravi|
    * |     2|   Ankit|        Ravi|
    * |   100|    Ravi|        Siri|
    * +------+--------+------------+*/

    println("Showing the employee who are also manager")
    selfjoin1.select($"m.emp_id", $"m.emp_name", $"m.manager_id").distinct().show()
    /*+------+--------+----------+
     * |emp_id|emp_name|manager_id|
     * +------+--------+----------+
     * |   100|    Ravi|       420|
     * |   420|    Siri|      null|
     * +------+--------+----------+*/

  }

}