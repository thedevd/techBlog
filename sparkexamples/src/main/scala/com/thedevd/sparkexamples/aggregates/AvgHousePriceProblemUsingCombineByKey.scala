package com.thedevd.sparkexamples.aggregates

import org.apache.log4j.Logger
import org.apache.log4j.Level
import org.apache.spark.sql.SparkSession
import org.apache.spark.rdd.RDD.rddToPairRDDFunctions

object AvgHousePriceProblemUsingCombineByKey {

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

    // Using combineByKey to find avg
    /* About CombineByKey
    * ---------------------
    *  1. GroupByKey is expensive
    *  		** Every single key value pair is shuffled
    *  2. CombileByKey is more generic than aggregateByKey
    *  3. Input and output type can be different.
    *  		** where as in reduceByKey they have to be same.
    *  4. Always operated on pairRDD
    *  
    *  combineByKey takes 3 inputs
    *  1. Create combine
    *  		Initial combiner to convert any value to key value pair.
    *  		** unlike aggregateByKey, need not to pass constant always, 
    *  		we can pass a function which can return a new value.
    *  2. Combiner Function
    *  		how to combine pair within a partition
    *  3. Merge Function
    *  		how to merge two partition to produce final aggregated output
    *  
    *  combineByKey(init_combiner, combiner_function, merge_function) 
    */
    val initCombiner = (housePrice: Double) => (1, housePrice)
    val partitionCombiner =
      (avgCount: (Int, Double), housePrice: Double) => {
        (avgCount._1 + 1, avgCount._2 + housePrice)
      }
    val partitionMerger =
      (part1: (Int, Double), part2: (Int, Double)) => {
        (part1._1 + part2._1, part1._2 + part2._2)
      }

    val housePriceTotal = housePricePairRdd
      .combineByKey(initCombiner, partitionCombiner, partitionMerger)

    val housePriceAvg = housePriceTotal
      .mapValues(avgCount => math.round(avgCount._2 / avgCount._1))

    for ((bedrooms, avgPrice) <- housePriceAvg.collect()) println(bedrooms + " : " + avgPrice)
  }
}