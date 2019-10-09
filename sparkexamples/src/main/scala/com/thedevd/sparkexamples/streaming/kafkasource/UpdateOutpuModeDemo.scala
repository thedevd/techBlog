package com.thedevd.sparkexamples.streaming.kafkasource

import org.apache.log4j.Logger
import org.apache.log4j.Level
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.streaming.OutputMode

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
object UpdateOutpuModeDemo {

  def main(args: Array[String]): Unit = {
    Logger.getLogger("org").setLevel(Level.ERROR)

    /*
     * Initialize SparkSession
     */
    val spark = SparkSession.builder()
      .appName("UpdateOutpuModeDemo")
      .master("local[*]")
      .getOrCreate()

    /*
     * Initialize a ReadStream to stream data from Kafka source
     */
    val kafkaStream = spark.readStream
      .format("kafka") // source type - kafka, file or socket
      .option("kafka.bootstrap.servers", "localhost:9092")
      .option("subscribe", "wordcount")
      //.option("startingOffsets", "earliest") //earliest, latest or offset location. default is latest for streaming
      //.schema(schema)  : we cannot set a schema for kafka source. Kafka source has a fixed schema of (key, value)
      .load()

    /*
     * Create a DataSet representing the stream of input lines from Kafkastream.
     * Here 'value' column in kafkastream represents the input line, so select it as String and convert to dataset using as[]
     */
    import spark.implicits._
    val lines = kafkaStream
      .selectExpr("CAST(value as STRING)") // cast value as String as everything in kafka is in bytes form
      .as[String]

    /*
     * Now perform WordCount on lines DataSet
     */
    val wordCounts = lines.flatMap(_.split(" "))
      .groupBy("value").count()

    /*
     * Tell the spark where to output means specify the sink
     */
    val query = wordCounts.writeStream
      .format("console") // sink type - console, memory, kafka 
      .queryName("Spark structured streaming kafka wordcount- Update Mode")
      .outputMode(OutputMode.Update) // --> ouput new and updated
      //.outputMode(OutputMode.Complete) // output everything
      //.outputMode(OutputMode.Append) // --> Append output mode not supported when there are streaming aggregations on streaming DataFrames/DataSets without watermark
      //.option("checkpointLocation", "/tmp/sparkcheckpoint/") //recommended when not memory or console output
      .start()

    query.awaitTermination()
    
    /*
     * Now lets see the update mode in action. 
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
     * |      is|    2|
     * |   Hello|    2|
     * |    ravi|    1|
     * |    this|    2|
     * +--------+-----+
     * 
     * So you can see here, only the updated word's count (Hello, this, is) and new word's count (Ravi) is written to 
     * sink. So Update output mode only output new and updated rows.
     * 
     * Comparing the Complete mode, count of Devendra word from last batch would have also displayed in Complete mode.
     */
  }

}