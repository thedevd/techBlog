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
 * Problem
 * ############
 * Read /sparksql/student_marks.txt from resource directory.
 * The task here is to show the student's minimum scoring subject name and maximum scoring subject name.
 *
 * the output should contain all the columns i.e.
 * for minimum scoring subject =>
 * +---+---------+---------+--------+
 * | id|     name|  subject|minscore|
 * +---+---------+---------+--------+
 *
 * for maximum scoring subject =>
 * +---+---------+---------+--------+
 * | id|     name|  subject|maxscore|
 * +---+---------+---------+--------+
 *
 */
object SparkSqlAggregation {

  def main(args: Array[String]): Unit = {
    Logger.getLogger("org").setLevel(Level.ERROR)

    val spark = SparkSession.builder()
      .appName("SparkSqlAggregation")
      .master("local[*]")
      .getOrCreate()

    val schema = new StructType()
      .add(StructField("id", IntegerType, false))
      .add(StructField("name", StringType, false))
      .add(StructField("subject", StringType, false))
      .add(StructField("score", DoubleType, false))

    val student_report_card_df = spark.read.schema(schema)
      .option("delimiter", ",")
      .csv(getClass.getResource("/sparksql/student_marks.txt").getPath)

    import spark.implicits._
    import org.apache.spark.sql.functions._

    // Lets find min and max score of a student grouping by id
    val max_and_min_score_df = student_report_card_df.groupBy("id")
      .agg(min("score").as("minscore"), max("score").as("maxscore"))

    /*
     * The problem with above aggregated dataframe is that it has only three columns
     * +---+---------+--------+
     * | id| minscore|maxscore|
     * +---+---------+--------+
     *
     * So we have to find a way to display other columns which are name and subject where he got max or min score.
     * The hack is here is to join this dataframe with original dataframe using
     *      (id, minscore) to find minimum scoring subject and then join separately with
     *      (id, maxscore) columns to find maximum scoring subject name.
     *
     */

    // prepare original df to join with minscore and maxscore columns of aggregated dataframe 'max_and_min_score_df'
    val tmp_student_report_card_df = student_report_card_df
      .withColumn("minscore", $"score")
      .withColumn("maxscore", $"score")

    // join the df to show the student details with their minimum scoring subject
    tmp_student_report_card_df.join(max_and_min_score_df, Seq("id", "minscore"), "inner")
      .select("id", "name", "subject", "minscore").show()

    /*+---+---------+---------+--------+
     * | id|     name|  subject|minscore|
     * +---+---------+---------+--------+
     * |  1|   Joseph|  Physics|    74.0|
     * |  2|    Jimmy|  Physics|    62.0|
     * |  3|     Tina|Chemistry|    68.0|
     * |  4|   Thomas|  Biology|    74.0|
     * |  6|     Cory|    Maths|    56.0|
     * |  7|Jackeline|  Physics|    62.0|
     * |  8|     Juan|  Biology|    60.0|
     * +---+---------+---------+--------+*/

    // join the df to show the student details with their maximum scoring subject
    tmp_student_report_card_df.join(max_and_min_score_df, Seq("id", "maxscore"), "inner")
      .select("id", "name", "subject", "maxscore").show()

    /* +---+---------+---------+--------+
    * | id|     name|  subject|maxscore|
    * +---+---------+---------+--------+
    * |  1|   Joseph|Chemistry|    91.0|
    * |  2|    Jimmy|Chemistry|    97.0|
    * |  3|     Tina|  Biology|    87.0|
    * |  4|   Thomas|  Physics|    93.0|
    * |  6|     Cory|Chemistry|    71.0|
    * |  7|Jackeline|    Maths|    86.0|
    * |  8|     Juan|  Physics|    69.0|
    * +---+---------+---------+--------+*/

    // #################### Lets try to solve this problem using sql style
    println("############### Using sql text style")
    student_report_card_df.createOrReplaceTempView("student_score_card")
    val e1 = spark.sql("select a.id,a.name,a.subject,b.min_score from student_score_card a,"
      + " (select id, name, min(score) min_score from student_score_card group by id, name) b"
      + " where a.id = b.id and a.name = b.name and a.score = b.min_score")
    e1.show()

    /* +---+---------+---------+---------+
     * | id|     name|  subject|min_score|
     * +---+---------+---------+---------+
     * |  1|   Joseph|  Physics|     74.0|
     * |  2|    Jimmy|  Physics|     62.0|
     * |  3|     Tina|Chemistry|     68.0|
     * |  4|   Thomas|  Biology|     74.0|
     * |  6|     Cory|    Maths|     56.0|
     * |  7|Jackeline|  Physics|     62.0|
     * |  8|     Juan|  Biology|     60.0|
     * +---+---------+---------+---------+*/

    val e2 = spark.sql("select a.id,a.name,a.subject,b.max_score from student_score_card a,"
      + " (select id, name, max(score) max_score from student_score_card group by id, name) b"
      + " where a.id = b.id and a.name = b.name and a.score = b.max_score")

    e2.show()

    /* +---+---------+---------+---------+
      * | id|     name|  subject|max_score|
      * +---+---------+---------+---------+
      * |  1|   Joseph|Chemistry|     91.0|
      * |  2|    Jimmy|Chemistry|     97.0|
      * |  3|     Tina|  Biology|     87.0|
      * |  4|   Thomas|  Physics|     93.0|
      * |  6|     Cory|Chemistry|     71.0|
      * |  7|Jackeline|    Maths|     86.0|
      * |  8|     Juan|  Physics|     69.0|
      * +---+---------+---------+---------+*/

    e1.join(e2, Seq("id", "name"), "inner").show()
    /* +---+---------+---------+---------+---------+---------+
     * | id|     name|  subject|min_score|  subject|max_score|
     * +---+---------+---------+---------+---------+---------+
     * |  1|   Joseph|  Physics|     74.0|Chemistry|     91.0|
     * |  2|    Jimmy|  Physics|     62.0|Chemistry|     97.0|
     * |  3|     Tina|Chemistry|     68.0|  Biology|     87.0|
     * |  4|   Thomas|  Biology|     74.0|  Physics|     93.0|
     * |  6|     Cory|    Maths|     56.0|Chemistry|     71.0|
     * |  7|Jackeline|  Physics|     62.0|    Maths|     86.0|
     * |  8|     Juan|  Biology|     60.0|  Physics|     69.0|
     * +---+---------+---------+---------+---------+---------+
     * */
  }
}