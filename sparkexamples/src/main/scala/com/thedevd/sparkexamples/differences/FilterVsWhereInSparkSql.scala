package com.thedevd.sparkexamples.differences

import org.apache.log4j.Logger
import org.apache.log4j.Level
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.types.StructType
import org.apache.spark.sql.types.StructField
import org.apache.spark.sql.types.IntegerType
import org.apache.spark.sql.types.StringType
import org.apache.spark.sql.types.DoubleType

/*
 * Read /sparksql/student_marks.txt in DataFrame and
 * select records where score is greater than 70 using filter and where
 *
 * Both 'filter' and 'where' in Spark SQL gives same result. There is no difference between the two.
 * filter is simply the standard function name in scala, and where is for people who prefer SQL.
 */
object FilterVsWhereInSparkSql {

  def main(args: Array[String]): Unit = {
    Logger.getLogger("org").setLevel(Level.ERROR)

    val spark = SparkSession.builder()
      .appName("FilterVsWhereInSparkSql")
      .master("local[*]")
      .getOrCreate()

    val schema = new StructType()
      .add(StructField("id", IntegerType, true))
      .add(StructField("name", StringType, true))
      .add(StructField("subject", StringType, true))
      .add(StructField("score", DoubleType, true))

    // create dataframe
    val student_df = spark.read
      .schema(schema)
      .option("delimiter", ",") // this option is necessary if delimiter is other than comma for csv file
      .csv(getClass.getResource("/sparksql/student_marks.txt").getPath)

    /* Or can be read using format approach
    val student_df = spark.read.format("csv")
      .option("header", "false") // we do not have header
      .option("delimiter", ",") // this is option if delimiter is comma
      .schema(schema)
      .load(getClass.getResource("/sparksql/student_marks.txt").getPath)
    */

    //following are equivalent, there is no difference
    import spark.implicits._
    student_df.filter($"score" > 70).show()
    student_df.where($"score" > 70).show()
    /*
     	student_df.filter("score > 70").show()
    	student_df.where("score > 70").show()
    */

  }

}