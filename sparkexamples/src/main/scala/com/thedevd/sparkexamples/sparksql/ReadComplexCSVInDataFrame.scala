package com.thedevd.sparkexamples.sparksql

import org.apache.log4j.Logger
import org.apache.log4j.Level
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.types.TimestampType

/*
 * Problem
 * ############
 * CSV is probably the most popular data-exchange format around.
 * Due to its popularity, this format has many variations on its core structure ->
 * 		1. separators aren’t always commas,
 * 		2. Some records may span over multiple lines,
 * 		3. There are various ways of escaping the separator to prevent parsing failure
 * 		and many more to be considered.
 *
 * To handle these complexities with CSV Apache Spark offers a variety of options for ingesting those CSV files.
 * In spark ingesting CSV is much easy and the option of schema inference is a powerful feature.
 *
 * Let’s have a look at more advanced examples with more options that illustrate the complexity of CSV file.
 * file location - /sparksql/books.csv with two records and a header row. Few observations for this csv file are -
 *   1. The file isn’t comma-separated but semicolon-separated.
 *   2. If you look at the record with id 4, there’s a semicolon in the title, which breaks the parsing,
 *   		therefore this field is surrounded by stars *. (this is called escaping the separator).
 *   3. If you look at the record with id 6, you’ll see that the title is split over two lines:
 *   		there’s a carriage return after Language? And before A
 *
 * Desired output -->
 * +---+--------+--------------------+--------------------+-------------------+
 * | id|authorId|               title|                link|      releaseDateTS|
 * +---+--------+--------------------+--------------------+-------------------+
 * |  4|       1|Harry Potter and ...|http://amzn.to/2k...|2016-10-04 00:00:00|
 * |  6|       2|Development Tools...|http://amzn.to/2v...|2016-12-28 00:00:00|
 * +---+--------+--------------------+--------------------+-------------------+
 *
 * Note- releaseDateTS is generated from releaseDate after casting to TimestampType
 *
 */
object ReadComplexCSVInDataFrame {

  def main(args: Array[String]): Unit = {
    Logger.getLogger("org").setLevel(Level.ERROR)

    val spark = SparkSession.builder()
      .appName("ReadComplexCSVInDataFrame")
      .master("local[*]")
      .getOrCreate()

    val book_df = spark.read.format("csv")
      .option("header", true)
      .option("inferSchema", true)
      .option("delimiter", ";") // values are separated by ;
      //.option("sep", ";") // sep can also be used instead of delimiter option
      .option("multiline", true) // some records are multiline
      .option("quote", "*") // * as quote is used to prevent breaking the parsing
      .load(getClass.getResource("/sparksql/books.csv").getPath)

    println("############# Schema inferred by spark")
    book_df.printSchema()

    /* releaseDate will be inferred as String by inferSchema=true option.
     * We need to case it to TimestampType. To do this-
     * 1. convert releaseDate to unix_timestamp in seconds with given date pattern
     * 2. cast  unix_timestamp in seconds to TimestampType using cast()
     */
    import org.apache.spark.sql.functions._ // for unix_timestamp
    import spark.implicits._ // for $
    val book_df_1 = book_df
      .withColumn("releaseDateTS", unix_timestamp($"releaseDate", "MM/dd/yy").cast(TimestampType)) // add new column
      .drop("releaseDate") // drop the old StringType column from DataFrame

    println("############# Schema after converting releaseDate to timestamp")
    book_df_1.printSchema()

    println("############# Books data")
    book_df_1.createOrReplaceTempView("books") // this is created to fire sqlText query using spark.sql()
    spark.sql("select * from books").show()
    // book_df_1.show()
    
    println("############# Books released after 1 December 2016")
    book_df_1.where($"releaseDateTS" > "2016-12-01 00:00:00").show()
    //spark.sql("select * from books where releaseDateTS > '2016-12-01 00:00:00'").show()
    
  }
}