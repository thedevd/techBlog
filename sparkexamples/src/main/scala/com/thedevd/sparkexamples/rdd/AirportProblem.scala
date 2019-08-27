package com.thedevd.sparkexamples.rdd

import org.apache.log4j.Logger
import org.apache.log4j.Level
import org.apache.spark.sql.SparkSession
import org.apache.commons.io.FileUtils
import java.io.File

/* Create a Spark program to read the airport data from rdd/airports.txt.
  *  find all the airports which are located in United States and latitude is more than 50
       and output the airport's name, city's name, country name and airport's latitude to airports_in_usa.txt.

    Each row of the input file contains the following columns:
    Airport ID, Name of airport, Main city served by airport, Country where airport is located, IATA/FAA code,
    ICAO Code, Latitude, Longitude, Altitude, Timezone, DST, Timezone in Olson format

  */
object AirportProblem {

  def main(args: Array[String]): Unit = {
    Logger.getLogger("org").setLevel(Level.OFF)

    val spark = SparkSession.builder()
      .appName("AirportProblem")
      .master("local[*]")
      .getOrCreate()

    val airportRdd = spark.sparkContext.textFile(getClass.getResource("/rdd/airports.txt").getPath)
    val airportInUsaWithRequiredLatitude = airportRdd
      .filter(airport => {
        val split = airport.split(",")
        split(3) == "\"United States\"" && split(6).toFloat > 50 // state data is in double quotes so make sure compare it with quote
      })
      .map(airport => {
        val split = airport.split(",")
        split(1) + "," + split(3) + "," + split(2) + "," + split(6)
      })
      
    // this is hack how to add header in a rdd by using union
    // This will work if having only one partition at the time of writing to output dir, 
    // Having one partition will create only one part file in output 
    val header = spark.sparkContext.parallelize(Array("airport_name,airport_state,airport_city,airport_latitude"))
    val airportDataWithHeader = header.union(airportInUsaWithRequiredLatitude)
    
    cleanupOutputDir("C://temp/airports_in_usa.txt")
    airportDataWithHeader.coalesce(1)
      .saveAsTextFile("C://temp/airports_in_usa.txt")
  }

  def cleanupOutputDir(dir: String) = FileUtils.deleteDirectory(new File(dir))

}