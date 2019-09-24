package com.thedevd.sparkexamples.optimizations

import org.apache.log4j.Logger
import org.apache.log4j.Level
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions._
import org.apache.commons.io.FileUtils
import java.io.File
import org.apache.spark.sql.SaveMode

/*
 * Cost Based Optimizer (aka CBO)
 * ################################
 * 1. CBO is an optimization engine for SparkSQL which was introduced with spark version 2.2.0.
 *
 * 2. CBO identifies the cheapest physical execution plan for sparkSQL query based on severals
 *    table statistics.
 *
 * 3. By default, CBO is disabled and can be enabled using property =>
 *       "spark.sql.cbo.enabled"
 *        i.e. spark.conf.set("spark.sql.cbo.enabled","true")
 *
 * 4. To identify cheapest query execution plan, CBO has to calculate cost of different logical plans
 *    and to find cost, CBO needs several statistics of the tables.
 *
 *    Statistics of tables can be collected using ANALYSE TABLE query =>
 *        "ANALYZE TABLE <table_name> COMPUTE STATISTICS"
 *
 *    Statistics of a particular column cab be collected using =>
 *        "ANALYZE TABLE <table_name> COMPUTE STATISTICS FOR COLUMNS <col1>, <col2>"
 *
 *  CBO can play major role to optimize JOIN queries, where it can reorder the join operations
 *  to reduce the data shuffle till great extent.
 *
 *  To demonstrate this, we will try to join three tables with inner join and will see how
 *  CBO optimize the query execution plan when it is enabled.
 *  
 * CASE-2
 * ========
 * Joining three table with filter condition in one of the huge table
 * 1. when CBO is not enabled
 * 2. when CBO is enabled
 */

object CostBasedOptimizerWithJoinAndFilterQuery {

  def main(args: Array[String]): Unit = {
    Logger.getLogger("org").setLevel(Level.ERROR)

    val spark = SparkSession.builder()
      .appName("CostBasedOptimizerWithJoinAndFilterQuery")
      .master("local[*]")
      .getOrCreate()

    cleanupSparkWarehouse()
    /*
     * CASE-2
     * ========
     * Joining three table with filter condition in one of the huge table
     * 1. when CBO is not enabled
     * 2. when CBO is enabled
     */
    joinTablesWithFilterWhenCBOIsDisabled(spark)
    joinTablesWithFilterWhenCBOIsEnabled(spark)

    while (true) {}
  }

  def joinTablesWithFilterWhenCBOIsDisabled(spark: SparkSession) = {
    println("########## Joining tables with inner join when filter is applied and CBO is disabled")
    createTables(spark, "d")

    // Disabling CBO if in case it was enabled before
    spark.conf.set("spark.sql.cbo.enabled", false)
    println("spark.sql.cbo.enabled: " + spark.conf.get("spark.sql.cbo.enabled"))

    /*
     * Joining three tables here, with a filter condition on one of the huge table.
     */
    val join_query = spark.table("huge_d").join(spark.table("twice_d").filter(col("double") < 2000), "id")
      .join(spark.table("small_d"), "id")
    join_query.take(1)

    /*
     * What is the behaviour of the join query when cbo is disabled
     * ##############################################################
     * Go to spark UI at 4040, open SQL tab and look for recent 'take' query =>
     *
     * The huge_d table is joined with twice_d table (having 1000 rows after filter condition) using
     * SortMergeJoin (Exchange stages happened) and this join's outcome is joined with small_d
     * table having 1000 rows using BroadcastHashJoin (as table is very small so broadcasthash join happens)
     *
     * Ideally, the spark should have applied BroadcastHashJoin when joining the 'huge_d' table with
     * filtered twice_d table, because after filter condition applied on twice_d table, the output table
     * will become very small and this will satisfies the broadcasthash join threshold. Doing this
     * would have been prevented Data Exchange in this joining operation. We will see the same when
     * CBO is enabled.
     * 
     * so overall 2 Exchanges happenend-
     * 1st Exchange - 152.6 MB data size
     * 2nd Exchange - 23 KB data size
     */
  }

  def joinTablesWithFilterWhenCBOIsEnabled(spark: SparkSession) = {
    println("########## Joining tables with inner join when filter is applied and CBO is enabled")
    createTables(spark, "e")
    
    // Enable CBO
    spark.conf.set("spark.sql.cbo.enabled", true)
    println("spark.sql.cbo.enabled: " + spark.conf.get("spark.sql.cbo.enabled"))

    // lets keep joinReorder false to show how CBO takes benefit of filter condition.
    spark.conf.set("spark.sql.cbo.joinReorder.enabled", false)
    println("spark.sql.cbo.joinReorder.enabled: " + spark.conf.get("spark.sql.cbo.joinReorder.enabled"))

    /*
     * In order to make CBO work, enabling it is not enough. We have to collect table statistics to allow
     * CBO to calculate cost of the join queries and come up with cheapest execution plan.
     * So lets ANALYZE the tables participating in join using ANALYZE TABLE command.
     */
    spark.sql("analyze table small_e compute statistics")
    spark.sql("analyze table huge_e compute statistics")
    spark.sql("analyze table twice_e compute statistics")
    spark.sql("analyze table twice_e compute statistics for columns double") //--> also analyze statistics on column 'double' where filter is applied

    val join_query = spark.table("huge_e").join(spark.table("twice_e").filter(col("double") < 2000), "id")
      .join(spark.table("small_e"), "id")
    join_query.show(1)
    
    /*
     * Lets see how CBO optimized the join query here
     * #################################################
     *  Go to spark UI at 4040, open SQL tab and look for recent 'show' query =>
     *  
     * The huge_e table is joined with filtered result of twice_e table using Broadcastjoin.
     * So spark has broadcasted filtered result from twice_e table.
     * 
     * Then output of first join is merge with small_e table using BroadcastJoin again.
     * So no independent Exchange job is present. This proves CBO has reduced data shuffling. 
     * 
     * so overall-
     * No Exchange stages happened as result of CBO optimizations.
     */

  }
  
  def createTables(spark: SparkSession, suffix: String) = {
    
    /*
     * Lets create three tables with one of them as small.
     * To create table we will use spark.range() which will give you
     * dataset with default column name as "id"
     */
    spark.range(1000).write.saveAsTable("small" + "_" + suffix)
    spark.range(10000000).write.saveAsTable("huge" + "_" + suffix)
    spark.range(10000000).withColumn("double", col("id") * 2).write.saveAsTable("twice" + "_" + suffix)

  }

  def cleanupSparkWarehouse() = {
    FileUtils.deleteDirectory(new File("spark-warehouse/"))
  }

}