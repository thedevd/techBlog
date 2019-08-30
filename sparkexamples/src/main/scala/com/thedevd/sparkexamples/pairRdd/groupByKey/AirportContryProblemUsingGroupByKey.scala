package com.thedevd.sparkexamples.pairRdd.groupByKey

import org.apache.log4j.Logger
import org.apache.log4j.Level
import org.apache.spark.sql.SparkSession
import org.apache.spark.rdd.RDD.rddToPairRDDFunctions

/* Create a Spark program to read the airport data from rdd/airports.txt,
       output the the list of the names of the airports located in each country.

       Each row of the input file contains the following columns:
       Airport ID, Name of airport, Main city served by airport, Country where airport is located, IATA/FAA code,
       ICAO Code, Latitude, Longitude, Altitude, Timezone, DST, Timezone in Olson format

       Sample output:

       "Canada", List("Bagotville", "Montreal", "Coronation", ...)
       "Norway" : List("Vigra", "Andenes", "Alta", "Bomoen", "Bronnoy",..)
       "Papua New Guinea",  List("Goroka", "Madang", ...)
       ...
  */

object AirportContryProblemUsingGroupByKey {

  def main(args: Array[String]): Unit = {
    Logger.getLogger("org").setLevel(Level.OFF)

    val spark = SparkSession.builder()
      .appName("AirportContryProblemUsingGroupByKey")
      .master("local[*]")
      .getOrCreate()

    val airportData = spark.sparkContext.textFile(getClass.getResource("/rdd/airports.txt").getPath)
    val airportPairRdd = airportData.map(airport => {
      val split = airport.split(",")
      (split(3), split(1)) // (country, airportname)
    })

    val airportNamesByCountry = airportPairRdd
      .groupByKey()
      .collectAsMap()
      .mapValues(v => v.toList) // (CountryName, List(airportNames))
      
    /*val airportNamesByCountry = airportPairRdd
        .reduceByKey((v1,v2) => (v1 + "," + v2)) // input and output should be same i.e. String
        .collectAsMap()
 		*/     

    for ((country, airportName) <- airportNamesByCountry) println(country + ": " + airportName)
  }

}