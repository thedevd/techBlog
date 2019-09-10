package com.thedevd.sparkexamples.differences

import org.apache.log4j.Logger
import org.apache.log4j.Level
import org.apache.spark.sql.SparkSession

/*
 * mapPartitionsWithIndex() transformation is similar to mapPartitions(), but 
 * mapPartitionsWithIndex() provides an Int value representing index of the partition.
 * 
 * So mapPartitionsWithIndex() is useful when you also want to track the partition's index.
 */
object MapPartitionsVsMapPartitionsWithIndex {

  def main(args: Array[String]): Unit = {
    Logger.getLogger("org").setLevel(Level.ERROR)

    val spark = SparkSession.builder()
      .appName("MapPartitionsVsMapPartitionsWithIndex")
      .master("local[*]")
      .getOrCreate()
      
    val rdd = spark.sparkContext.parallelize(Array(1,2,3), 2)
    val sum = rdd.mapPartitionsWithIndex(sumUsingMapPartitionsWithIndex)
    sum.collect().toMap.foreach(print)
    
   /* output =>
    * Called for partition: 1
    * Called for partition: 0
    * (0,1)(1,5)
    * */
  }
  
  def sumUsingMapPartitionsWithIndex(index: Int, iterator: Iterator[Int]) = {
    println("function Called for partition: " + index)
    val sum = iterator.reduce(_+_)
    Iterator((index, sum)) // return type must be of Iterator type
  }
}