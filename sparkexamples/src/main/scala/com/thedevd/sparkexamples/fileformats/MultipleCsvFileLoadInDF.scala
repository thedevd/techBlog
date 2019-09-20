package com.thedevd.sparkexamples.fileformats

import org.apache.log4j.Logger
import org.apache.log4j.Level
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.types.StructType
import org.apache.spark.sql.types.StructField
import org.apache.spark.sql.types.IntegerType
import org.apache.spark.sql.types.StringType
import org.apache.spark.sql.types.DoubleType
import org.apache.spark.sql.SaveMode

/*
 * Multiple different CSV files with same schema can be read into a single Dataframe by
 * passing multiple file's path to
 * 1. load() method if reading using format() way
 * 2. csv() method if reading using csv() way
 *
 * Note- best way to use wild cards if all files to be read from same location.
 *
 * Lets try to read two csv files from sparksql/multuCsvLoadInDF/ in resource directory
 * 1. failed_subject.csv - having student details where they scored less than 70 marks in a subject.
 * 2. passed_subject.csv - having student details where they scored more than or equal to 70 marks in a subject.
 */
object MultipleCsvFileLoadInDF {

  def main(args: Array[String]): Unit = {
    Logger.getLogger("org").setLevel(Level.ERROR)

    val spark = SparkSession.builder()
      .appName("MutipleCsvFileLoadIntoDataFrame")
      .master("local[*]")
      .getOrCreate()

    val schema = new StructType()
      .add(StructField("id", IntegerType, true))
      .add(StructField("name", StringType, true))
      .add(StructField("subject", StringType, true))
      .add(StructField("marks", DoubleType, true))
      .add(StructField("result", StringType, true))

    val student_result = spark.read.schema(schema)
      .option("delimiter", ",")
      .csv(getClass.getResource("/sparksql/multiCsvLoadInDF/").getPath + "*.csv") // --> using wild card
    //.csv(getClass.getResource("/sparksql/multiCsvLoadInDF/failed_subject.csv").getPath, getClass.getResource("/sparksql/multiCsvLoadInDF/passed_subject.csv").getPath)

    println(student_result.count()) // 28 records total

  }
}