package com.thedevd.sparkexamples.pairRdd.aggregates

import org.apache.log4j.Logger
import org.apache.log4j.Level
import org.apache.spark.sql.SparkSession
import org.apache.spark.rdd.RDD.rddToPairRDDFunctions

/* Create a Spark program to read the house data from rdd/real_estate.csv,
       output the average price for houses with different number of bedrooms.

    The houses dataset contains a collection of recent real estate listings in San Luis Obispo county and
    around it. 

    The dataset contains the following fields:
    1. MLS: Multiple listing service number for the house (unique ID).
    2. Location: city/town where the house is located. Most locations are in San Luis Obispo county and
    northern Santa Barbara county (Santa Maria­Orcutt, Lompoc, Guadelupe, Los Alamos), but there
    some out of area locations as well.
    3. Price: the most recent listing price of the house (in dollars).
    4. Bedrooms: number of bedrooms.
    5. Bathrooms: number of bathrooms.
    6. Size: size of the house in square feet.
    7. Price/SQ.ft: price of the house per square foot.
    8. Status: type of sale. Thee types are represented in the dataset: Short Sale, Foreclosure and Regular.

    Each field is comma separated.

    Sample output:

       (3, 325000)
       (1, 266356)
       (2, 325000)
       ...

       3, 1 and 2 mean the number of bedrooms. 325000 means the average price of houses with 3 bedrooms is 325000.
*/
object AvgHousePriceProblemUsingReduceByKey {

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
        (split(3), (1, split(2).toDouble)) // (Bedrooms no, (1, housePrice)
      })

    // Counting the no of rows and totalHousePrice
    val bedroomToCountToTotalPricePairRdd = housePricePairRdd
      .reduceByKey((v1: (Int, Double), v2: (Int, Double)) => {
        (v1._1 + v2._1, v1._2 + v2._2) //  totalCount, totalPrice
      })

    val houseAvgPrice = bedroomToCountToTotalPricePairRdd
      .mapValues(v => math.round(v._2 / v._1)) // totalPrice / totalCount

    for ((bedrooms, avgPrice) <- houseAvgPrice.collect()) {
      println(bedrooms + " : " + avgPrice)
    }

  }
}