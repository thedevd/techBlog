package com.thedevd.sparkexamples.sparksql

import org.apache.log4j.Logger
import org.apache.log4j.Level
import org.apache.spark.sql.SparkSession

/* Create a Spark program to read the house data from rdd/real_estate.csv,
  group by location, aggregate the average price per SQ Ft and sort by average price per SQ Ft.

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

        +----------------+-----------------+
        |        Location| avg_Price_SQ_FT|
        +----------------+-----------------+
        |          Oceano|             95.0|
        |         Bradley|            206.0|
        | San Luis Obispo|            359.0|
        |      Santa Ynez|            491.4|
        |         Cayucos|            887.0|
        |................|.................|
        |................|.................|
        |................|.................|
         */
object HouseAvgPriceProblem {

  def main(args: Array[String]): Unit = {

    Logger.getLogger("org").setLevel(Level.OFF)

    val spark = SparkSession.builder()
      .appName("HouseAvgPriceProblemUsingSparkSql")
      .master("local[*]")
      .getOrCreate()

    val houseDF = spark.read.format("csv")
      .option("header", "true")
      .option("inferSchema", true)
      .load(getClass.getResource("/rdd/real_estate.csv").getPath)

    houseDF.printSchema()

    /* There is problem in /rdd/real_estate.csv that the some location have leading whitespaces
     so if you directly go for group by location, you will end up with two different avg
     with same location. i.e.
    +-------------------+------------------+
    |           Location|  avg(Price SQ Ft)|
    +-------------------+------------------+
    |             Lompoc|             149.9|
    |             Lompoc|159.87115384615387|

    so we need to tackle the leading and trailing whitespaces on location before we go for groupBy.

    To trim spaces we can use spark sql built in function called trim.
    To use them import org.apache.spark.sql.functions._

    This url has good example of dealing with white spaces in a dataframe -
    	https://medium.com/@mrpowers/whitespace-data-munging-with-spark-173988e6b32f

    */
    import org.apache.spark.sql.functions._
    val trimmedHouseDF = houseDF.withColumn("location", trim(houseDF("location"))) // remove white-spaces from location

    // --------- Using sparksession.sql(SqlText) way
    trimmedHouseDF.createOrReplaceTempView("houseprice")
    spark.sql("""SELECT location, AVG(`Price SQ Ft`) as avg_Price_SQ_FT FROM houseprice GROUP BY location order by avg_Price_SQ_FT""")
      .show()
    // make a note here is that column name with whitespace is used within `` in avg function

    // ---------- Using DataFrame APIs
    trimmedHouseDF.groupBy("Location") // group by
      .avg("Price SQ Ft") // avg
      .orderBy("avg(Price SQ Ft)") // orderBy
      .withColumnRenamed("avg(Price SQ Ft)", "avg_Price_SQ_FT") // renaming the column
      .show()
  }

}