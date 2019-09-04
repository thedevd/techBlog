package com.thedevd.sparkexamples.sparksql

import org.apache.log4j.Logger
import org.apache.log4j.Level
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.types.StructType
import org.apache.spark.sql.types.StructField
import org.apache.spark.sql.types.IntegerType
import org.apache.spark.sql.types.StringType
import org.apache.spark.sql.types.DoubleType
import org.apache.spark.sql.Row
import org.apache.spark.sql.types.LongType

/*
 * Problem of assigning row no to records in dataFrame
 * #########################################################################
 * In some cases in database we want to assign a unique sequence number to the records in a table.
 * It becomes a challenge in a distributed environment like spark as we have to make sure we dont come across
 * duplicate sequence numbers for data stored in multiple nodes/partitions.
 *
 * To do this we have the row_number() window function in hive to assign unique numbers to the dataset across the nodes.
 * But Spark 1.3 does not support window functions yet.
 *
 * As a workaround we can use the zipWithIndex RDD function which does the same as row_number() in hive.
 */
object SequenceNumberInDataFrameUsingZipWithIndex {

  def main(args: Array[String]): Unit = {
    Logger.getLogger("org").setLevel(Level.ERROR)

    val spark = SparkSession.builder()
      .appName("SequenceNumberInDataFrameUsingZipWithIndex")
      .master("local[*]")
      .getOrCreate()

    // read the file first in RDD form so that we can apply zipWithIndex
    val studentReportCard_row_rdd = spark.sparkContext
      .textFile(getClass.getResource("/sparksql/student_marks.txt").getPath, 4)
      .zipWithIndex() // RDD[(String, Long)]
      .map(t => (t._1.split(","), t._2)) // RDD[(Array[String], Long)]
      .map(t => Row(t._1(0).toInt, t._1(1), t._1(2), t._1(3).toDouble, t._2)) // RDD[Row] -- Rdd must be of Row type to create DataFrame from Rdd.

    // create schema for /sparksql/student_marks.txt
    val schema = new StructType()
      .add(StructField("id", IntegerType, false))
      .add(StructField("name", StringType, false))
      .add(StructField("subject", StringType, true))
      .add(StructField("score", DoubleType, true))
      .add(StructField("row_num", LongType, false))

    // lets create DataFrame using row type rdd with schema
    val studentReportCard_df = spark.createDataFrame(studentReportCard_row_rdd, schema)
    studentReportCard_df.show(10)
    /*
		+---+------+---------+-----+-------+
		| id|  name|  subject|score|row_num|
		+---+------+---------+-----+-------+
		|  1|Joseph|    Maths| 83.0|      0|
		|  1|Joseph|  Physics| 74.0|      1|

		*/

    /*
      * Addition-
      * #############
      * if we want to assign the unique numbers starting from 1000,
      * then we simply add it to the zipWithIndex value
      *
      * see line# 40 where we are converting RDD to RDD of Row type
      * .map(t => Row(t._1(0).toInt, t._1(1),t._1(2), t._1(3).toDouble, t._2 + 1000L))
      */

  }
}