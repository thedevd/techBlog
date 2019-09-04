package com.thedevd.sparkexamples.pairRdd

import org.apache.log4j.Logger
import org.apache.log4j.Level
import org.apache.spark.sql.SparkSession

/*
 * Problem - How to remove the elements with a key present in any other RDD?
 * You have two pairRdds -
 * 	pairRdd1 = {(1, 2), (3, 4), (3, 5)}
 * 	pairRdd2 = {(3,10)}
 *
 * Task is to remove the element in pairRdd1 having the key present in pairRdd2 i.e.
 * pairRdd1 - pairRdd2 =
 * 	{(1,2)} is expected output
 *
 * Solution- this can be achieved using substractByKey()
 *
 */
object SubtractByKey {

  def main(args: Array[String]): Unit = {
    Logger.getLogger("org").setLevel(Level.ERROR)

    val spark = SparkSession.builder()
      .appName("SubtractByKey")
      .master("local[*]")
      .getOrCreate()
      
    val rdd1 = spark.sparkContext.parallelize(Seq((1,2), (3,4), (3,5)))
    val rdd2 = spark.sparkContext.parallelize(Seq((3,10)))
    
    val result = rdd1.subtractByKey(rdd2)
    result.foreach(println) // (1,2)
    
    val rejected = rdd1.subtract(result)
    rejected.foreach(print) // (3,5)(3,4)
    
  }
}