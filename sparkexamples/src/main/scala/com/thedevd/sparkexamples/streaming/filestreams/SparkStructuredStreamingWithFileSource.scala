package com.thedevd.sparkexamples.streaming.filestreams

import org.apache.log4j.Level
import org.apache.log4j.Logger
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.streaming.OutputMode
import org.apache.spark.sql.types.DoubleType
import org.apache.spark.sql.types.IntegerType
import org.apache.spark.sql.types.StringType
import org.apache.spark.sql.types.StructType
import org.apache.commons.io.FileUtils
import java.io.File

/*
 * 1. Spark Structured streaming allows to process the stream data using analytical sql queries .
 * 2. As name suggest, spark structured streaming assume the streaminig data has some fixed structured to be processed.
 *    So it relies on schema.
 * 3. Structured streaming engine is build on top of SparkSQL engine. And it is intruduced in spark 2.0
 * 4. Spark Structured streaming streams the data as an infinite table (in the form of DataSet<Row>) where
 *    each rows is considered as Dataset of Row type.
 *
 * 5. Spark stuctured streaming is actually Micro Batch based Streaming means it is near realtime.
 *    Under the hood similar to its predecessor (DStream) , the structured streamin uses micro-batching to process streams.
 *
 *    So what happens, spark waits for a very small interval (called batch interval) say 1 second and batches together all the events
 *    that were received during that interval into a micro batch.
 *    This micro batch is then scheduled by the Driver to be executed as Tasks at the Executors side.
 *    After a micro-batch execution is completed, the next batch is collected and scheduled again.
 *
 * In spark structured streaming we can have following built-in input sources which can be considered as producing
 * stream of data -
 * 1. File source - Reads files from a specific directory. Supported file formats are text, csv, json, orc, parquet.
 * 2. Kafka source - Read data from kafka topic. It is compatible with Kafka broker versions 0.10.0 or higher.
 * 3. Socket(for testing only) - Reads data from socket connection.
 *
 * In this example we will see Use case of File Streams.
 * ######################################################
 * File Streams continuously looks for files in a folder.
 * it is useful in scenarios where we have tools like flume dumping the logs from a source to HDFS folder continuously.
 * We can treat that hdfs folder as stream and read that data into spark structured streaming for further processing.
 *
 * USE-CASE here is -
 * We have 2 directories =>
 * 1. src/main/resources/structured-streaming/filestream/
 *    which contains a static file customer_info.csv with Customer information
 *
 * 2. src/main/resources/structured-streaming/filestream/orders/
 *    which contains CSV files with order details and the files are dropped periodically.
 *    This directory is partitioned date-wise.
 *
 * Objective here is to join the order details with the customer information file, and
 * write the resulting data to JSON file as output in real-time.
 *
 */
object SparkStructuredStreamingWithFileSource {

  def main(args: Array[String]): Unit = {
    Logger.getLogger("org").setLevel(Level.ERROR)

    val spark = SparkSession.builder()
      .appName("SparkStructuredStreamingWithFileSource")
      .master("local[*]")
      .getOrCreate()
    
    val tempResourceDir = "C:/temp/structured-streaming/filestream/input"
    copyResourceFolderToTempDirectory(getClass.getResource("/structured-streaming/filestream/").getPath, tempResourceDir)

    /*
     * In order to stream data from CSV file, we need to define a schema for the data.
     * Spark structured streaming wont allow streaming data without schema.
     */
    val orderSchema = new StructType()
      .add("order_id", IntegerType)
      .add("customer_id", IntegerType)
      .add("date", StringType)
      .add("amount", DoubleType)

    /*
     * Create DataStreamReader to stream files from a folder location
     */
    val orderStreamDF = spark.readStream
      .option("delimiter", ",")
      .option("header", true) // --> since header is there
      .schema(orderSchema)
      .csv(tempResourceDir + "/orders/*/") // --> used wildcard

    /*
     * Read static customer information from csv file
     */
    val customerSchema = new StructType()
      .add("customer_id", IntegerType)
      .add("customer_name", StringType)
      .add("customer_mobile", StringType)
      .add("customer_location", StringType)

    val customerDF = spark.read
      .option("header", true)
      .option("delimiter", ",")
      .schema(customerSchema)
      .csv(tempResourceDir + "/customer_info.csv")

    /*
    * Join the streaming dataframe ordersStreamDF with customerDF on the customer_id field.
    */
    val finalResult = orderStreamDF.join(customerDF, Seq("customer_id"), "inner")

    /*
     * Create a output sink, without this streaming job wont start
     */

    val query = finalResult.writeStream
      .queryName("customer-order-info")
      //.format("console")
      .format("json")
      .option("path", "C:/temp/structured-streaming/filestream/output")
      .option("checkpointLocation", "C:/temp/structured-streaming/filestream/chkpoint_dir")
      .partitionBy("date") 
      .outputMode(OutputMode.Append())
      .start()
    
    query.awaitTermination()
    
    /*
     * So here As soon as the new file is detected by the Spark engine in the C:/temp/structured-streaming/filestream/input/orders/*/
     * directory, the streaming job is initiated and a new json file is created immediately by joining customer and order details.
     */
     */

  }
  
  def copyResourceFolderToTempDirectory(srcDir: String, destDir: String) = {
    FileUtils.copyDirectory(new File(srcDir), new File(destDir))
  }
}