package com.thedevd.sparkexamples.differences

import org.apache.log4j.Logger
import org.apache.log4j.Level
import org.apache.spark.sql.SparkSession
import org.apache.spark.rdd.RDD

/*
 * 1. reduce is implemented as an ACTION
 * 		while reduceByKey is implemented as a TRANSFORMATION
 *
 * 2. reduce operates on RDD of objects i.e. RDD[T]
 * 		while reduceByKey operates on PairRDD having key-value pairs i.e. RDD[K,V]
 *
 * 3. reduce aggregates or reduces all the elements of RDD by applying specified commutative
 * 		and associative operation and returns final output to the driver. To produce final single output
 * 		reduce must pull the entire dataset down into a single location because it is reducing to one final value.
 *
 * 		while reduceByKey first aggregates the value of same key in same machine or partition before shuffling
 * 		and then gives the partial result in the form of RDD to reducer to do further aggregation.
 */
object ReduceVsReduceByKey {

  def main(args: Array[String]): Unit = {
    Logger.getLogger("org").setLevel(Level.ERROR)

    val spark = SparkSession.builder()
      .appName("ReduceVsReduceByKey")
      .master("local[*]")
      .getOrCreate()
      
    val rdd = spark.sparkContext.parallelize(Seq(1,2,3,4,5,6,7,8,9,10), 4)
    println(rdd.getNumPartitions) // 4
    
    // calculation sum
    val result : Int = rdd.reduce((a,b) => a + b) // see- reduce returns single final value instead of RDD
    // rdd.reduce(_+_)
    println("total using reduce: " + result) // total using reduce: 55
    
    // Lets do the same using reduceByKey, for thiw we need pairRdd
    val pairRdd = rdd.map(num => ("total",num)) // taken total as key for all elements
    val result1: RDD[(String,Int)] = pairRdd.reduceByKey((v1, v2) => v1 + v2)
    result1.collect().foreach(println) //(total,55)
  }

}