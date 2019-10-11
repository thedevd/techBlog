package com.thedevd.sparkexamples.streaming.kafkasource

import org.apache.log4j.Logger
import org.apache.log4j.Level
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.types.StructType
import org.apache.spark.sql.types.IntegerType
import org.apache.spark.sql.types.StringType
import org.apache.spark.sql.types.DoubleType
import java.sql.Timestamp
import org.apache.spark.sql.streaming.OutputMode
import java.io.File
import org.apache.commons.io.FileUtils

/*
 * This example explains spark structured streaming integration with kafka.
 *
 * To stream the data from kafka source and process it using sql API we need to have following dependency added -
 *     spark-sql-kafka-0-10_2.11
 *
 * In this example, we are going to
 * Part 1. stream a directory containing movie data in the form of csv and store it in kafka topic named moviedata.
 * Part 2. stream the topic containing movie data, process it (converting movie duration to minutes) and output in console
 * 
 * Dataset Description - /src/main/resource/structured-streaming/kafkaintegration/moviedata.csv
 *
 * Column1: Movie ID,
 * Column2: Movie name,
 * Column3: Year of release,
 * Column4: Rating of the movie,
 * Column5: Movie duration in seconds
 * 
 * This demo requires a running kafka setup
 * ##########################################
 * I am running windows OS, so I have used .bat files in local kafka installation
 * 
 * 1. Start the zookeeper
 *    zookeeper-server-start.bat ..\..\config\zookeeper.properties
 * 2. Start kafka brokers
 *    kafka-server-start.bat ..\..\config\server.properties
 * 3. Create topic
 *    kafka-topics.bat --create --topic moviedata --zookeeper localhost:2181 --replication-factor 1 --partitions 1
 *    
 * 4. Start the SparkStructuredStreamingWithKafkaSource application and check the console for output.
 */
object SparkStructuredStreamingWithKafkaSource {

  def main(args: Array[String]): Unit = {
    Logger.getLogger("org").setLevel(Level.ERROR)

    /*
     * Create the SparkSession which is entry point of all functionality in spark
     */
    val spark = SparkSession.builder()
      .appName("SparkStructuredStreamingWithKafkaSource")
      .master("local[*]")
      .getOrCreate()
      
    val tempResourceDir = "C:/temp/structured-streaming/kafkaintegration/"
    copyResourceFolderToTempDirectory(getClass.getResource("/structured-streaming/kafkaintegration/").getPath, tempResourceDir)

    /*
     * Define the Schema for the data that we are going to read from csv file.
     */
    val movieSchema = new StructType()
      .add("movie_id", IntegerType)
      .add("movie_name", StringType)
      .add("release_year", IntegerType)
      .add("movie_rating", DoubleType)
      .add("movie_duration", IntegerType)

    /*
     * Create the Streaming DataFrame from fileSource using defined schema.
     * If you drop any csv file into dir, the file will automatically change into the streaming dataframe.
     */
    val movieStream = spark.readStream
      .schema(movieSchema)
      .option("sep", ",")
      .csv(tempResourceDir + "/movie")

    /*
     * Publish the stream to Kafka sink.
     * Since kafka has default schema as key, value, timestamp,
     * so create key and value column from streaming dataframe to tell what is key and what is value.
     * 
     * We will keep movie_id as KEY and json representation of all columns as VALUE.
     * Note- Use to_json() function to convert all columns into json
     */
    import spark.implicits._
    movieStream.selectExpr("CAST(movie_id AS STRING) AS key", "to_json(struct(*)) AS value") // --> Using to_json() function to convert all columns into json
      .writeStream
      .format("kafka")
      .option("kafka.bootstrap.servers", "localhost:9092")
      .option("topic", "moviedata")
      .option("checkpointLocation", tempResourceDir + "/chkpoint") // --> use checkpointLocation to maintain the offsets by the stream
      .start()

    /*
     * PART2
     * ###########################################
     * Subscribe the topic in Kafka and stream the data.
     * At this point we are reading data from kafka'topic 'moviedata' where we have stored movie data in part 1.
     */
    val kafkaStream = spark.readStream
      .format("kafka")
      .option("kafka.bootstrap.servers", "localhost:9092")
      .option("subscribe", "moviedata")
      .load()

    /*
     * Convert above Kafka Stream to DataSet of String type along with kafka's TimeStamp.
     * At this point we will have only two columns in dataset :
     *     value | timestamp
     */
    val movieDataSet = kafkaStream
      .selectExpr("CAST(value as STRING)", "CAST(timestamp as TIMESTAMP)")
      .as[(String, Timestamp)]

    import org.apache.spark.sql.functions._
    val movie_complete_df = movieDataSet.select(from_json($"value", movieSchema).as("data"), $"timestamp") // --> from_json() converts a column containing a JSON string into a StructType with the specified schema
      .select("data.*", "timestamp") // --> select all columns
    
    /*
     * At this point movie_complete will have all columns i.e.
     *     movie_id | movie_name | release_year | movie_rating | movie_duration | timestamp
     *     
     * Now lets convert movie_duration (in seconds) to hours
     */
    val result_df = movie_complete_df.withColumn("movie_duration_min", round($"movie_duration" / 60)).drop("movie_duration")
    
    val query = result_df.writeStream
      .format("console")
      .option("truncate", false)
      .outputMode(OutputMode.Append()) // --> Default is Append so do not forget to change it if using aggregation.
      .start()
      
      
    query.awaitTermination()
    
    /*
     * Sample output
     * +--------+------------------------------+------------+------------+-----------------------+------------------+
     * |movie_id|movie_name                    |release_year|movie_rating|timestamp              |movie_duration_min|
     * +--------+------------------------------+------------+------------+-----------------------+------------------+
     * |1       |The Nightmare Before Christmas|1993        |3.9         |2019-10-11 18:57:42.307|76.0              |
     * |2       |The Mummy                     |1932        |3.5         |2019-10-11 18:57:42.332|73.0              |
     */
  }
  
  
  def copyResourceFolderToTempDirectory(srcDir: String, destDir: String) = {
    FileUtils.deleteDirectory(new File(destDir)) // cleanup destination directory if exists already
    FileUtils.copyDirectory(new File(srcDir), new File(destDir))
  }
}