package com.thedevd.sparkexamples.aggregates

import org.apache.log4j.Logger
import org.apache.log4j.Level
import org.apache.spark.sql.SparkSession
import org.apache.spark.rdd.RDD.rddToPairRDDFunctions

object AvgHousePriceProblemUsingAggregateByKey {

  def main(args: Array[String]): Unit = {
    Logger.getLogger("org").setLevel(Level.OFF)

    val spark = SparkSession.builder()
      .appName("AvgHousePriceProblem")
      .master("local[*]")
      .getOrCreate()

    val realStateData = spark.sparkContext.textFile(getClass.getResource("/rdd/real_estate.csv").getPath)
    val housePricePairRdd = realStateData
      .filter(line => !line.contains("Bedrooms"))
      .map(data => {
        val split = data.split(",")
        (split(3), split(2).toDouble) // Bedrooms no, housePrice
      })

    // Using AggregateByKey to find avg
    /*
     * About aggregatebyKey
     * ---------------------
     * aggregateByKey(zeroValue)(seqOp, combOp, [numTasks]).
     * 
     * aggregateByKey function in Spark accepts total 3 parameters,
     * 1. Initial value or Zero value
     * 		# it can be 0 if aggregation is type of sum of all values.
     * 		# We can have this value as Double.MaxValue if aggregation objective is to find minimum value.
     * 		# We can also use Double.MinValue value if aggregation objective is to find maximum value
     *
     * 2. Sequence operation function which merges data within partition
     * 3. Combination operation function which merge output between partitions
     * 
     *  
     * aggregateByKey is logically same as reduceByKey() but it lets you return 
     * result in different type. 
     * 
     * In another words, it lets you have a input as type x and aggregate result as type y. 
     * For example (1,2),(1,4) as input and (1,"two"),(1,"four") as output. 
     * It also takes zero-value that will be applied with each key at time of beginning the aggregation.
     * 
     * Prefer to Use of reduceByKey when dataset is large and 
     * the input and output value types are of same type
     */
    val zeroValues = (0, 0.0) // always constant
    val partitionCombiner =
      (avgCount: (Int, Double), housePrice: Double) => {
        (avgCount._1 + 1, avgCount._2 + housePrice)
      }
    val partitionMerger =
      (part1: (Int, Double), part2: (Int, Double)) => {
        (part1._1 + part2._1, part1._2 + part2._2)
      }

    val housePriceTotal = housePricePairRdd
      .aggregateByKey(zeroValues)(partitionCombiner, partitionMerger)

    val housePriceAvg = housePriceTotal
      .mapValues(avgCount => math.round(avgCount._2 / avgCount._1))

    for ((bedrooms, avgPrice) <- housePriceAvg.collect()) println(bedrooms + " : " + avgPrice)
  }
}