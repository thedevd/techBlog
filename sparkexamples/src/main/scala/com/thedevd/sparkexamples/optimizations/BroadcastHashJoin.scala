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
 * BroadcastHashJoin (or Map-side Join) - An optimization to JOIN queries
 * ######################################################################
 * 1. SparkSql uses broadcastHashJoin to optimize performance of join queries
 *    when the size of one of the table in join is below this property-
 *       spark.sql.autoBroadcastJoinThreshold (which is default 10mb)
 *
 *    Therefore if any table's size in join is less than spark.sql.autoBroadcastJoinThreshold,
 *    spark will use BroadcastHashJoin bydefault.
 *
 * 2. How it avoids data shuffle-
 *    the smaller table (aka dimension table) will be broadcasted to all worker nodes.
 *    Thus this avoids shuffling of large table data over the network.
 *
 * 3. Although, we can also give a hint to sparkSql about marking one of the table (small table) in join
 *    as broadcast table.
 *      import org.apache.spark.sql.functions.broadcast
 *      largedataframe.join(broadcast(smalldataframe), "joining_column")
 *
 * BroadcastHashJoin conditions =>
 * ####################################
 * 1. Table needs to be broadcasted should be less than spark.sql.autoBroadcastJoinThreshold configured value, default 10M.
 *    (or add a broadcast join hint)
 * 2. Base table(large table) can not be broadcasted
 *    such as the left outer join, only broadcast the right table (dimension table).
 *
 * Drawbacks =>
 * ##############
 * Only suitable of small sized table which can fit into memory without creating other memory issues,
 * otherwise cost of broadcasting relatively large table may be greater than cost of shuffle.
 * Or it may cause outOfMemory issue in Worker nodes.
 *
 */
object BroadcastHashJoin {

  def main(args: Array[String]): Unit = {
    Logger.getLogger("org").setLevel(Level.ERROR)

    val spark = SparkSession.builder()
      .appName("BroadcastHashJoin")
      .master("local[*]")
      .getOrCreate()

    val autoBroadcastHashJoinThreashold = spark.conf.get("spark.sql.autoBroadcastJoinThreshold").toInt // This is in bytes
    println("auto broadcastHashJoin table size threshold: " 
        + autoBroadcastHashJoinThreashold / 1024 / 1024 + "Mb")

    val small_table = spark.range(1000)
    val large_table = spark.range(10e6.toLong)

    val join_data = large_table.join(small_table, "id") // <--- small table will be auto broadcasted 
    /* 
     * import org.apache.spark.sql.functions.broadcast
     * val join_data = large_table.join(broadcast(small_table), "id") // <--- forcing to broadcast the table
     * 
     */
    
    join_data.foreach(_ => ()) // <--- trigger the join operation
    
    /*
     * while(true) {
     * }
     * 
     * Using the above while loop, open the spark UI at 4040 port. And look at
     * "foreach at BroadcastHashJoin" Query. There you will see BroadcastHashJoin is used.
     */

  }

}