package com.thedevd.sparkexamples.sparksql

import org.apache.log4j.Logger
import org.apache.log4j.Level
import org.apache.spark.sql.SparkSession

/*
 * Spark User's Defined Functions (UDFs)
 * #########################################
 * 1. Some times built-in sql function does not meet our buiness requirement
 * when dealing with sql data. In such case we need out own custom function to be created.
 *
 * 2. Spark allows you to define such custom SQL function which is called user-defined-functions (UDF).
 *
 * 3. UDFs are great when built-in SQL functions aren’t sufficient, but they pay the price of
 * performance as they are treated as black box where spark can not apply optimization.
 * So UDFs have to be designed only when needed.
 *
 */
object SparkUDF {

  def main(args: Array[String]): Unit = {
    Logger.getLogger("org").setLevel(Level.ERROR)

    val spark = SparkSession.builder()
      .appName("SparkUDF")
      .master("local[*]")
      .getOrCreate()

    val emp_rdd = spark.sparkContext.parallelize(Seq((1, "dev vishwakarma"), (2, "ravi chaudhary")))

    import spark.implicits._
    val emp_df = spark.createDataFrame(emp_rdd).toDF("emp_id", "emp_name")

    // 1. create a regular scala function
    val upperReplaceSpaceWithUnderscore = (input: String) => input.toUpperCase().replaceAll("\\s", "_")
    // 2. pass the function to udf to create custom UDF function
    import org.apache.spark.sql.functions.udf
    val upperReplaceSpaceWithUnderscoreUDF = udf(upperReplaceSpaceWithUnderscore)

    // upperCaseUdf is ready to be used in dataFrame
    emp_df.withColumn("cleaned_name", upperReplaceSpaceWithUnderscoreUDF($"emp_name")).show()
    /* +------+---------------+---------------+
     * |emp_id|       emp_name|   cleaned_name|
     * +------+---------------+---------------+
     * |     1|dev vishwakarma|DEV_VISHWAKARMA|
     * |     2| ravi chaudhary| RAVI_CHAUDHARY|
     * +------+---------------+---------------+*/

    // 3. To use custom spark udf in sql style expression, it need to be registered with spark.udf
    spark.udf.register("cleanupName", upperReplaceSpaceWithUnderscoreUDF)
    emp_df.createOrReplaceTempView("emp")
    spark.sql("select emp_id, emp_name, cleanupName(emp_name) as cleaned_name from emp").show()
    /* +------+---------------+---------------+
     * |emp_id|       emp_name|   cleaned_name|
     * +------+---------------+---------------+
     * |     1|dev vishwakarma|DEV_VISHWAKARMA|
     * |     2| ravi chaudhary| RAVI_CHAUDHARY|
     * +------+---------------+---------------+*/

    /*
     * Gotchas when having Null
     * #########################
     * 1. When writing a UDF, make sure to handle the null case as this is a common cause of errors.
     * 2. Above UDF will throw an error, if the DataFrame column contains a null value =>
     *
     * 	val emp_rdd2 = spark.sparkContext.parallelize(Seq((1, "dev vishwakarma"), (2, null)))
     * 	val emp_df_with_null = spark.createDataFrame(emp_rdd2).toDF("emp_id", "emp_name")
     * 	emp_df_with_null.withColumn("cleaned_name", upperReplaceSpaceWithUnderscoreUDF($"emp_name")).show()
     *
     * 	The last line here with throw =>
     * 	org.apache.spark.SparkException: Failed to execute user defined function(anonfun$1: (string) => string)
     * 			.....
     * 			.....
     * 	Caused by: java.lang.NullPointerException
     * 			at com.thedevd.sparkexamples.sparksql.SparkUDF$$anonfun$1.apply(SparkUDF.scala:36)
     * 			at com.thedevd.sparkexamples.sparksql.SparkUDF$$anonfun$1.apply(SparkUDF.scala:36)
     *
     * 	3. So we need to deal with null column values in UDFs. lets do this
     */

    val optimizedUpperReplaceSpaceWithUnderscore = (input: String) => {
      if (input == null)
        None
      else
        Some(input.toUpperCase().replaceAll("\\s", "_"))
        /* without using Some, the return type of would be java.io.Serializable
         * And this gives error=> 
         * 				java.lang.UnsupportedOperationException: Schema for type java.io.Serializable is not supported
         * 
         * That is why returning Some type along with None. this would make return type as Option[String]
         * and this will work
    		*/
    }
    

    val optimizedUDF = udf(optimizedUpperReplaceSpaceWithUnderscore)

    val emp_rdd2 = spark.sparkContext.parallelize(Seq((1, "dev vishwakarma"), (2, null)))
    val emp_df_with_null = spark.createDataFrame(emp_rdd2).toDF("emp_id", "emp_name")
    emp_df_with_null.withColumn("cleaned_name", optimizedUDF($"emp_name")).show()
    /*+------+---------------+---------------+
     * |emp_id|       emp_name|   cleaned_name|
     * +------+---------------+---------------+
     * |     1|dev vishwakarma|DEV_VISHWAKARMA|
     * |     2|           null|           null|
     * +------+---------------+---------------+*/

  }

}