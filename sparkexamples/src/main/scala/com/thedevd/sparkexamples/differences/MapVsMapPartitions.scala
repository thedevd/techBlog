package com.thedevd.sparkexamples.differences

import org.apache.log4j.Logger
import org.apache.log4j.Level
import org.apache.spark.sql.SparkSession

/*
 * mapPartitions() vs map()
 * ###########################
 * 1. mapPartitions() and map() both are transformations in spark
 * 2. mapParitions() is much faster than map, because mapPatitions() calls your function
 * 		once per partition, whereas map calls your function once for each element of RDD.
 *
 * 		so mapPartitions() ---> once/partition
 * 		map() ---> once/elements of RDD.
 *
 * 3. When to choose mapPartitions() transformation over map()
 * 		----------------------------------------------------------
 * 		Sometimes you have heavy initialization that should not be done for each element of RDD
 * 		rather you want to do it once per partition.
 *
 * 		Take an example of initializing connection to the db. Now it is not at all ideal to initialize
 * 		the db connection for each element in RDD using map(), the performance is going to be very poor in this case.
 * 		Rather this, you may want to initialize db connection once for each partition, as this is going to be
 * 		performance improvement to your application. In such case mapPartitions() is useful where you want to do
 * 		certain thing only once per partition.
 *
 * 		Something like this -->
 *    val newRd = myRdd.mapPartitions(partition => {
 *      val connection = new DbConnection //creates a db connection per partition
 *
 *      val newPartition = partition.map(record => {
 *      readMatchingFromDB(record, connection)
 *      }).toList // consumes the iterator, thus calls readMatchingFromDB
 *
 *    connection.close() // close dbconnection here
 *    newPartition.iterator // create a new iterator
 *    })
 *
 * Note- mapPartitions() takes function of type Iterator<T> => Iterator<U> as an argument
 * when running on RDD of type T.
 *
 */
object MapVsMapPartitions {

  def main(args: Array[String]): Unit = {
    Logger.getLogger("org").setLevel(Level.ERROR)

    val spark = SparkSession.builder()
      .appName("MapVsMapPartitions")
      .master("local[*]")
      .getOrCreate()

    val rdd = spark.sparkContext.parallelize(Array(1, 2, 3), 2) //partition1 =>  Array(1), partition2 => Array(2, 3))

    println("##### Using mapPartition()")
    val result = rdd.mapPartitions(iterator => {
      println("function Called once per partition") // since there are two partitions, so this message will come two times
      val squared = iterator.map(x => x * x)
      squared // Iterator[Int]
    })

    println(result.getNumPartitions) // 2, partition1 => 1, partition2 => 4,9
    println(result.collect().toArray.mkString(","))
    /* output--->
    * ##### Using mapPartition()
    * function Called once per partition
    * function Called once per partition
    * 1,4,9
    *
    * */

    println("##### Using map()")
    val mapResult = rdd.map(x => {
      println("function Called for each element") // this will be printed for all three elements of RDD
      x * x
    })
    println(mapResult.collect().toArray.mkString(","))

    /*output -->
     * ##### Using map()
     * function Called for each element
     * function Called for each element
     * function Called for each element
     * 1,4,9
     *
     * */
  }

}