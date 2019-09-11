package com.thedevd.sparkexamples.sharedvariables

import org.apache.log4j.Logger
import org.apache.log4j.Level
import org.apache.spark.sql.SparkSession
import org.apache.spark.util.AccumulatorV2

/*
 * 1. Accumulators are another type of shared variables in spark that are used to
 *    perform write operations like counters and sum operations across workers nodes.
 *
 *    One of the most common use of Accumulator is count particular events that may help in debugging process.
 * 2. Accumulator are write-only variables for executors. 
 *    They can be added to by executors and read by the driver only.
 * 
 * 3. Accumulator is created using 
 *    sparkContext.longAccumulator() or sparkContext.doubleAccumulator() or sparkContext.collectionAccumulator()
 * 4. Accumulator is updated using add() method
 * 5. At drive, value() is used to read the final value.
 *
 * For ex: we have input file /sparksql/accumulator.txt consisting blank lines which we want to count.
 *
 */
object Accumulator {

  def main(args: Array[String]): Unit = {
    Logger.getLogger("org").setLevel(Level.ERROR)

    val spark = SparkSession.builder()
      .appName("Accumulator")
      .master("local[*]")
      .getOrCreate()
      
    val counter = spark.sparkContext.longAccumulator("blanklines")
    
    val blank_lines = spark.sparkContext.textFile(getClass.getResource("/sparksql/accumulator.txt").getPath, 4)
     .filter(_.trim().length() == 0)
     .foreach(line => counter.add(1)) // add() is used to write into accumulator
     
     
    println("no of blank lines: " + counter.value) // no of blank lines: 4
     
      
  }

}