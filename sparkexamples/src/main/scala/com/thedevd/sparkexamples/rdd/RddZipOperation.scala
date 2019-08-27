package com.thedevd.sparkexamples.rdd

import org.apache.spark.sql.SparkSession
import org.apache.log4j.Logger
import org.apache.log4j.Level
import org.apache.spark.annotation.InterfaceStability
import scala.collection.Seq

/*
 * Problem is you have two rdds having numbers
 * You have to multiply first element of first rdd to
 * first element of second rdd and so on.
 *
 * Ex-
 * rdd1 -> 1, 2, 3, 4
 * rdd2 -> 5, 6, 7, 8
 *
 * output rdd -> 5, 12, 21, 32
 */
object RddZipOperation {

  def main(args: Array[String]): Unit = {

    Logger.getLogger("org").setLevel(Level.OFF)

    val spark = SparkSession.builder()
      .appName("RddZipOperation")
      .master("local[*]")
      .getOrCreate()

    val rdd1 = spark.sparkContext.parallelize(Seq(1, 2, 3, 4))
    val rdd2 = spark.sparkContext.parallelize(Seq(5, 6, 7, 8))

    val rdd1ZipRdd2 = rdd1.zip(rdd2) // Array[(Int,Int)] ((1,5),(2,6),(3,7),(4,8))

    // Now we have rdd3 having items in the tuple form
    val resultRdd = rdd1ZipRdd2.map(t => t._1 * t._2) // Array(Int) (5,12,21,32)

    resultRdd.collect().foreach(print)
    // Note - collect is not a good idea, though,
    // when the RDD has billions of lines. Use take() to take just a few to print out
    // resultRdd.take(4).foreach(println)

  }
}