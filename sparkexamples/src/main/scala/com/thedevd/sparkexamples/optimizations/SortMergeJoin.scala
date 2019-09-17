package com.thedevd.sparkexamples.optimizations

import org.apache.log4j.Logger
import org.apache.log4j.Level
import org.apache.spark.sql.SparkSession

/*
 * SparkSQL uses these three algorithms to join the tables -
 * 1. BroadcastHashJoin (aka Map-side join) - for smaller tables
 * 2. ShuffleHashJoin
 * 3. SortMergeJoin -- for very large tables
 *
 * SortMergeJoin
 * ###############
 *
 * 1. From spark 2.3, SortMergeJoin is the default algorithm to join datasets.
 *    However this can be changed using spark internal property =>
 *       spark.sql.join.preferSortMergeJoin -- true or false
 *
 * 2. As the name indicates, SortMergeJoin does joining in two steps -
 *    a. Sort - Sort the dataset
 *    b. Merge - Merge the sorted dataset in partition into a single partition.
 *       This is done by simply iterating over the rows and joining the rows having the same value of the joining key given.
 *
 *       follow this url for better understanding - https://www.waitingforcode.com/apache-spark-sql/sort-merge-join-spark-sql/read
 *
 *    During merge, it's important to ensure that all rows having the same value for the join key are stored in the same partition.
 *    This pre-requirement obviously requires data shuffling between executors.
 */
object SortMergeJoin {

  def main(args: Array[String]): Unit = {
    Logger.getLogger("org").setLevel(Level.ERROR)

    val spark = SparkSession.builder()
      .appName("SortMergeJoin")
      .master("local[*]")
      .getOrCreate()

    /*
     * Disable auto broadcasting so that BroadcastHashJoin will not take over SortMergeJoin
     * This is being done for demo only as we are using small tables here.
     */
    spark.conf.set("spark.sql.autoBroadcastJoinThreshold", -1)

    println("SortMergeJoin is default join from spark 2.3: " +
      spark.conf.get("spark.sql.join.preferSortMergeJoin")) // --> true

    val huge_table_1 = spark.range(10e6.toLong)
    val huge_table_2 = spark.range(10e5.toLong)

    huge_table_1.join(huge_table_2, Seq("id"), "inner").foreach(_ => ())

    /*
      while(true) {}
     */

    /*
     * Uncomment the infinite loop code above to check the spark UI at 4040 port.
     * Go to SQL tab, and look for "foreach at SortMergeJoin" Query.
     * You will see SortMergeJoin is used.
     */
  }

}