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
 * ShuffleHashJoin
 * ################
 * 1. For very large tables, Spark chooses Shuffle Hash join when Sort merge join is turned off.
 * 2. As the name indicates, it shuffles that dataframes based on joining key value and then
 *    join them, so at the end, rows having same keys will be stored in same partition.
 *
 * Note- you will hardly see shuffled hash joins in your structured queries.
 *
 * Spark planer Use shuffleHashJoin requirements -
 * 1. Disable BroadcastHashJoin. To do this set autoBroadcastJoinThreshold  to some small value atleaset 1
 *    spark.conf.set("spark.sql.autoBroadcastJoinThreshold", 2)
 *
 * 2. Disable SortMergeJoin which is default from spark 2.3
 *     spark.conf.set("spark.sql.join.preferSortMergeJoin", false)
 *
 * 3. Query plan stats size -
 *    canBuildLocalHashMap(right || left)
 *        |-> plan.stats.sizeInBytes < conf.autoBroadcastJoinThreshold * conf.numShufflePartitions
 *
 * 4. That one side of the join is much smaller than other alteast 3 times
 *    muchSmaller(right, left) || muchSmaller(left, right)
 *        |-> a.stats.sizeInBytes * 3 <= b.stats.sizeInBytes
 *
 */
object ShuffleHashJoin {

  def main(args: Array[String]): Unit = {
    Logger.getLogger("org").setLevel(Level.ERROR)

    val spark = SparkSession.builder()
      .appName("ShuffleHashJoin")
      .master("local[*]")
      .getOrCreate()

    /*
    * Disable auto broadcasting of table and SortMergeJoin
    */
    spark.conf.set("spark.sql.autoBroadcastJoinThreshold", 2)
    spark.conf.set("spark.sql.join.preferSortMergeJoin", false)

    import spark.implicits._
    val left = Seq(
      (0, "playing"),
      (1, "with"),
      (2, "ShuffledHashJoinExec")).toDF("id", "token")

    val right = Seq(
      (0, "token0"),
      (1, "token1"),
      (2, "token2"),
      (3, "token3"),
      (4, "token4"),
      (5, "token5"),
      (6, "token6"),
      (7, "token7"),
      (8, "token8"),
      (9, "token9")).toDF("id", "token") // --> making one of table atleast 3 times bigger than other

    /*
     * Lets verify formula before joining-
     *   plan.stats.sizeInBytes < conf.autoBroadcastJoinThreshold * conf.numShufflePartitions
     */
    val left_plan_stats_sizeInBytes = left.queryExecution.optimizedPlan.stats.sizeInBytes
    println("## left_plan_stats_sizeInBytes: " + left_plan_stats_sizeInBytes)

    val right_plan_stats_sizeInBytes = right.queryExecution.optimizedPlan.stats.sizeInBytes
    println("## right_plan_stats_sizeInBytes: " + right_plan_stats_sizeInBytes)

    val autoBroadcastJoinThreashold = spark.conf.get("spark.sql.autoBroadcastJoinThreshold")
    val sparkSqlShufflePartitions = spark.conf.get("spark.sql.shuffle.partitions")
    println("## spark.sql.autoBroadcastJoinThreshold: " + autoBroadcastJoinThreashold)
    println("## spark.sql.shuffle.partitions: " + sparkSqlShufflePartitions)

    println("## left_plan_stats_sizeInBytes < conf.autoBroadcastJoinThreshold * conf.numShufflePartitions :"
      + (left_plan_stats_sizeInBytes < autoBroadcastJoinThreashold.toInt * sparkSqlShufflePartitions.toInt))
    println("## right_plan_stats_sizeInBytes < conf.autoBroadcastJoinThreshold * conf.numShufflePartitions :"
      + (right_plan_stats_sizeInBytes < autoBroadcastJoinThreashold.toInt * sparkSqlShufflePartitions.toInt))

    /*
     * Joining dataset
     */
    val joined_data = left.join(right, Seq("id"), "inner")
    joined_data.foreach(_ => ())

    /*
     * def explain(extended: Boolean): Unit
     * Prints the plans (logical and physical) to the console for debugging purposes. 
     */
    joined_data.explain(true) // --> for debugging purpose
    /* == Physical Plan ==
    * *(1) Project [id#5, token#6, token#15]
    * +- ShuffledHashJoin [id#5], [id#14], Inner, BuildLeft
    *    :- Exchange hashpartitioning(id#5, 200)
    *    :  +- LocalTableScan [id#5, token#6]
    *    +- Exchange hashpartitioning(id#14, 200)
    *       +- LocalTableScan [id#14, token#15]
    */

  }

}