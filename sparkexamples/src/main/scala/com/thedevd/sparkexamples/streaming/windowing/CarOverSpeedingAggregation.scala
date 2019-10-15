package com.thedevd.sparkexamples.streaming.windowing

import org.apache.log4j.Logger
import org.apache.log4j.Level
import org.apache.spark.sql.SparkSession
import java.sql.Timestamp
import org.apache.spark.sql.Dataset
import org.apache.spark.sql.functions._
import org.apache.spark.sql.streaming.OutputMode
import org.apache.spark.sql.Dataset
import org.apache.spark.sql.Row
import org.apache.spark.sql.streaming.Trigger

/*
 *
 * Lets suppose you have started a car pooling company and there are thousands of cars
 * running in the city to do your business. You have installed a sensor in each
 * of the car which is producing car's speed every second to one of the kafka topic.
 *
 * Now you want check which cars are over-speeding in every 60 seconds.
 * To achieve this we will create a simple near real-time streaming application to calculate the
 * average speed of vehicles every few seconds. During this we will also see concept of -
 * SlidingWindow, TumblingWindow, EventTime, ProcessingTime, Watermarks and Kafka Source & Sink.
 *
 * This demo requires a running kafka setup
 * ##############################################
 * I am running windows OS, so I have used .bat files in local kafka installation
 *
 * 1. Start the zookeeper
 *    zookeeper-server-start.bat ..\..\config\zookeeper.properties
 * 2. Start kafka brokers
 *    kafka-server-start.bat ..\..\config\server.properties
 * 3. Create topics
 *    kafka-topics.bat --create --topic cars --zookeeper localhost:2181 --replication-factor 1 --partitions 1
 *    kafka-topics.bat --create --topic overspeedingcars --zookeeper localhost:2181 --replication-factor 1 --partitions 1
 *    
 * 4. Run the RandomCarsKafkaProducer.scala to produce random carEvents with thier speeds
 * 5. Run the streaming application 'CarOverSpeedingAggregation.scala' and check the console + kafka topic 'overspeedingcars'
 *    for the output.
 *    
 *    
 * Terminologies in window based aggregations-
 * ##############################################
 * 1. EventTime & ProcessingTime
 *    EventTime is the time at which an event is generated at its source, 
 *    whereas ProcessingTime is the time at which that event is processed by the system.
 * 
 * 2. Windowing
 *    Grouping the events into some fixed interval is called windowing.
 *    Such as in this example we are grouping car events in 60 seconds window based on their eventTime (timestamp column)
 *    
 *    Windowing in spark is done by adding 'window' column in groupBy clause.
 *    
 * 3. Tumbling Window & Sliding Window
 *    # A tumbling window is a non-overlapping window, that tumbles over every “window-size”. 
 *    e.g. for a Tumbling window of size 4 seconds, there could be window for [00:00 to 00:04), [00:04: 00:08), [00:08: 00:12) etc . 
 *    If an incoming event has EventTime 00:05, that event will be assigned the window - [00:04 to 00:08)
 *    
 *    # A SlidingWindow is a over-lapping window of a given size(say 4 seconds) that slides every given interval (say 2 seconds). 
 *    e.g. for a window of size 4 seconds, that slides every 2 seconds there could windows [00:00 to 00:04), [00:02 to 00:06), [00:04 to 00:08) etc. 
 *    Notice that the windows 1 and 2 are overlapping here. If an event with EventTime 00:05 comes in, that event will belong to the windows 
 *    [00:02 to 00:06 and 00:04 to 00:08].
 *
 * 4. Watermark
 *    In spark, watermark is used to decide when to clear the state of a window.
 *    This is useful to deal the late arrival of data.
 *    
 *    Based on the delay you specify, Watermark lags behind the maximum eventTime seen so far by engine. 
 *    e.g., if delay is 10 seconds and current max event time is 12:00:14 then the watermark is set at 12:00:04. 
 *    This means that Spark will clear the state of windows who’s end time is less than watermark time i.e. 12:00:04.
 *    
 *    Lets undestand this more closely w.r.t word count problem
 *    ###########################################################
 *    Suppose we have tumbling window of size 10 min that slides every 5 min and watermark is set to 10 minutes.
 *    So watermark is always set as (max event time - '10 mins')
 *    
 *    For example, when the engine observes the data (12:14, dog) --> here max eventTime is 12:14 
 *    it sets the watermark for the next trigger as 12:04. 
 *    
 *    This watermark lets the engine maintain intermediate state for additional 10 minutes to allow late data to be counted. 
 *    For example, the data (12:09, cat) is out of order and late, and it falls in windows [12:00 - 12:10 and 12:05 - 12:15]. 
 *    Since, it is still ahead of the watermark 12:04 (12:09 > 12:04), the engine still maintains the intermediate counts for this and 
 *    correctly updates the counts of the related windows. 
 *    However, as soon as the watermark is updated to 12:11, the intermediate state for window [12:00 - 12:10] is cleared, because this window's end time 12:10 is less than watermark 12:11.
 *    and all subsequent data (e.g. (12:04, donkey)) arrived later is considered “too late” and therefore ignored.
 *    
 *    
 *    
 *
 */
object CarOverSpeedingAggregation {

  def main(args: Array[String]): Unit = {
    Logger.getLogger("org").setLevel(Level.ERROR)

    val spark = SparkSession.builder()
      .appName("CarOverSpeedingAggregation")
      .master("local[*]")
      .getOrCreate()

    /*
     * Read car events from Kafka source
     */
    val carEventStream = spark.readStream
      .format("kafka")
      .option("kafka.bootstrap.servers", "localhost:9092")
      .option("subscribe", "cars")
      .load()

    /*
     * Next, we parse the raw data coming from kafka into a case class,
     * so that we have a structure to work with.
     */
    import spark.implicits._
    val carEvents: Dataset[CarEvents] = carEventStream
      .selectExpr("CAST(value as STRING)") // select value column from kafka stream
      .map(row => CarEvents(row.getString(0))) // converts each Row to case class CarEvents

    /*
    * Perform aggregation
    */

    /*
     * // aggregation without window
     * carEvents.groupBy("carId").avg("speed")
     *
     * But we want to calculate the average speed of a vehicle over last 60 seconds.
     * Also, we would like to calculate it based on the EventTime of the events
     * i.e. based on the time at which the event generated at the sensor source (this is called EventTime),
     * not based on when it was processed in the system or executor (This is called ProcessingTime)
     *
     * To achieve this we need to group the events into 60-second interval time groups, based on its EventTime.
     * This grouping is called Windowing.
     *
     * In Spark, Windowing is done by adding an additional column 'window' in the groupBy clause.
     * Lets see that in action -->
    */

    val aggregates = carEvents
      .withWatermark("timestamp", "10 seconds") // setting the watermark on the same column where windowing will be applied
      .groupBy(window($"timestamp", "60 seconds"), $"carId") //tumbling window of size 60 seconds on eventTime
      //.groupBy(window($"timestamp", "60 seconds", "30 seconds"), $"carId") //sliding window of size 60 seconds, that slides every 30 second
      .agg(avg("speed").alias("speed"))
      .where($"speed" > 70)

    aggregates.printSchema()

    /*
     * Writing aggregates to sink. 
     * We are making use of foreachBatch to output in more than one sink.
     */
    val writeToConsoleAndKafka = aggregates
      .writeStream
      .outputMode(OutputMode.Update()) //ouput new and updated
      .foreachBatch((batchDf: Dataset[Row], batchId: Long) => {
        batchDf.persist() // --> persist for better performance 
        
        println("-----------------------------------")
        println("Batch: " + batchId)
        println("-----------------------------------")

        // console sink
        batchDf.write
          .format("console")
          .option("truncate", "false") // prevent trimming output fields
          .save()

        // kafka sink
        batchDf
          .selectExpr("CAST(carId as STRING) as key", "CAST(speed as STRING) as value")
          .write
          .format("kafka")
          .option("kafka.bootstrap.servers", "localhost:9092")
          .option("topic", "overspeedingcars")
          .option("checkpointLocation", "C:/temp/structured-streaming/caroverspeeding/chkpointdir") // recommended when using kafka sink,  it enables failure recovery and exactly once processing
          .save()

        batchDf.unpersist() // --> unpersist dataframe after it is written to sink
      }).start()

    writeToConsoleAndKafka.awaitTermination()

    /* // This is another way of sending the output to more than one sink separately but
     * // this approach will trigger processing two times for each writeStream
     val writeToConsole = aggregates
      .writeStream
      .format("console")
      .option("truncate", "false") //prevent trimming output fields
      .queryName("kafka spark streaming console sink")
      .outputMode("update")
      .start()

    val writeToKafka = aggregates
      .selectExpr("CAST(carId AS STRING) AS key", "CAST(speed AS STRING) AS value")
      .writeStream
      .format("kafka")
      .option("kafka.bootstrap.servers", "localhost:9092")
      .option("topic", "overspeedingcars")
      //.option("startingOffsets", "earliest") //earliest, latest or offset location. default latest for streaming
      .option("checkpointLocation", "C:/temp/structured-streaming/caroverspeeding/chkpointdir") //must when not memory or console output
      .queryName("kafka spark streaming - Car over speeding")
      //.outputMode("append")  // only supported when we set watermark. output only new
      .outputMode("update") //ouput new and updated
      .start()

    spark.streams.awaitAnyTermination() //running multiple streams at a time
    */
    
    
    /*
     * Sample output after running this example
     * +------------------------------------------+-----+-----------------+
     * |window                                    |carId|speed            |
     * +------------------------------------------+-----+-----------------+
     * |[2019-10-15 15:23:00, 2019-10-15 15:24:00]|car9 |88.0             |
     * |[2019-10-15 15:24:00, 2019-10-15 15:25:00]|car2 |89.0             |
     * |[2019-10-15 15:24:00, 2019-10-15 15:25:00]|car5 |85.0             |
     */
  }

  case class CarEvents(carId: String, speed: Integer, timestamp: Timestamp)
  object CarEvents {
    def apply(raw: String) = {
      val splits = raw.split(",")
      new CarEvents(splits(0), splits(1).toInt, new Timestamp(splits(2).toLong))
    }
  }

}