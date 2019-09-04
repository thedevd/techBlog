package com.thedevd.sparkexamples.sparksql

import org.apache.log4j.Logger
import org.apache.log4j.Level
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.types.StructType
import org.apache.spark.sql.types.StructField
import org.apache.spark.sql.types.IntegerType
import org.apache.spark.sql.types.StringType
import org.apache.spark.sql.types.DoubleType

/*
 * Read /sparksql/student_marks.txt/ in DataFrame
 * And add an additional column 'result' with value -
 * 	1. Pass if score is greater or equal to 70
 * 	2. Fail if score of subject is lower than 70
 *
 * Sample output
 * +---+---------+---------+-----+------+
 * | id|     name|  subject|score|result|
 * +---+---------+---------+-----+------+
 * |  1|   Joseph|    Maths| 83.0|pass  |
 * |  1|   Joseph|  Physics| 74.0|pass  |
 *   ..    .....    ......   ...   ...
 *   
 * Solution- use withColumn on DataFrame to add new column
 */
object AddingColumnToDataFrame {

  def main(args: Array[String]): Unit = {

    Logger.getLogger("org").setLevel(Level.ERROR)

    val spark = SparkSession.builder()
      .appName("AddingColumnToDataFrame")
      .master("local[*]")
      .getOrCreate()

    val schema = new StructType()
      .add(StructField("id", IntegerType, true))
      .add(StructField("name", StringType, true))
      .add(StructField("subject", StringType, true))
      .add(StructField("score", DoubleType, true))

    val student_report_card_df = spark.read
      .format("csv")
      .option("delimiter", ",")
      .schema(schema)
      .load(getClass.getResource("/sparksql/student_marks.txt").getPath)

      import org.apache.spark.sql.functions._ // for when and otherwise
      val student_result = student_report_card_df
        .withColumn("result", when(col("score") >= 70, "pass").otherwise("fail")) // withColumn is used to add column
      
       student_result.show(10)
       /*	+---+------+---------+-----+------+
        * | id|  name|  subject|score|result|
        * +---+------+---------+-----+------+
        * |  1|Joseph|    Maths| 83.0|  pass|
        * |  1|Joseph|  Physics| 74.0|  pass|
        * |  1|Joseph|Chemistry| 91.0|  pass|
        * |  1|Joseph|  Biology| 82.0|  pass|
        * |  2| Jimmy|    Maths| 69.0|  fail|
        
        */
       
       // withColumnRenamed(existingName: String, newName: String) is used to rename column 
       val student_result1 = student_result.withColumnRenamed("result", "pass/fail")
       student_result1.show(1)
       /*	+---+------+-------+-----+---------+
        * | id|  name|subject|score|pass/fail|
        * +---+------+-------+-----+---------+
        * |  1|Joseph|  Maths| 83.0|     pass|
        * +---+------+-------+-----+---------+
        * */
  }

}