package com.thedevd.sparkexamples.streaming.kafkasource

import org.apache.log4j.Logger
import org.apache.log4j.Level
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.streaming.OutputMode
import org.apache.spark.sql.Dataset

/*
 * This example requires a running kafka broker instance.
 * ########################################################
 * I am running windows OS, so I have used .bat files in local kafka installation
 * 
 * 1. Start the zookeeper
 *    zookeeper-server-start.bat ..\..\config\zookeeper.properties
 * 2. Start kafka brokers
 *    kafka-server-start.bat ..\..\config\server.properties
 * 3. Create topic
 *    kafka-topics.bat --create --topic wordcount --zookeeper localhost:2181 --replication-factor 1 --partitions 1
 * 4. Start the UpdateOutputModeDemo spark structured streaming application
 * 5. Open kafka console producer and start producing the inputs
 *    kafka-console-producer.bat --topic wordcount --broker-list localhost:9092 
 */
object CompleteOutputModeDemo {

  def main(args: Array[String]): Unit = {
    Logger.getLogger("org").setLevel(Level.ERROR)

    val spark = SparkSession.builder()
      .appName("CompleteOutputModeDemo")
      .master("local[*]")
      .getOrCreate()

    /*
     * Open readStream from kafka source
     */
    val kafkaStream = spark.readStream
      .format("kafka")
      .option("kafka.bootstrap.servers", "localhost:9092")
      .option("subscribe", "wordcount")
      //.option("startingOffset", "latest")
      .load()

    /*
     * Select value column from kafka stream schema and cast it to String type DataSet
     */
    import spark.implicits._
    val lines : Dataset[String] = kafkaStream
      .selectExpr("CAST(value as STRING)") // This is a variant of 'select' that accepts SQL expressions.
      .as[String] // --> using encoders to create Typed DataSet

    /*
     * Perform wordcount aggregation
     */
    val wordCount = lines.flatMap(_.split("\\s+"))
      .groupBy("value")
      .count()

    /*
     * Open writeStream to produce output into sink 
     */
    val query = wordCount.writeStream
      .format("console") // --> sink type - kafka, console, memory or file
      .outputMode(OutputMode.Complete()) // --> output everything i.e. old + updated rows from last batch and new rows in current batch
      .queryName("Spark Structured Streaming - Complete Output mode")
      .start() // --> Starts the execution of the streaming query,

    query.awaitTermination() //--> this is to keep the query execution running in background until calling query.stop or exception occurs.

    /*
     * Now lets see the Complete mode in action. 
     * Open the kafka console producer against wordcount topic.
     * #### And first push this message -
     *     Hello this is Devendra
     *     
     * Check the console of the running application you will see the batch -
     * -------------------------------------------
     * Batch: 1
     * -------------------------------------------
     * +--------+-----+
     * |   value|count|
     * +--------+-----+
     * |Devendra|    1|
     * |      is|    1|
     * |   Hello|    1|
     * |    this|    1|
     * +--------+-----+ 
     * 
     * #### Push second message - 
     *      Hello this is Ravi
     * 
     * Check the console of the running application you will see the next batch -
     * -------------------------------------------
     * Batch: 2
     * -------------------------------------------
     * +--------+-----+
     * |   value|count|
     * +--------+-----+
     * |Devendra|    1|
     * |      is|    2|
     * |   Hello|    2|
     * |    ravi|    1|
     * |    this|    2|
     * +--------+-----+
     * 
     * So here you can see that old word count (Devendra:1) + Updated wordCount (is:2, Hello:2, this:2)
     * from the last batch + new word count (ravi:1) from the current batch is written to sink.
     * 
     * It means every thing is output in Complete mode.
     *
     */
  }
}