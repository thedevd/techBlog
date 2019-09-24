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
 */
object CostBasedOptimizer {

  def main(args: Array[String]): Unit = {
    Logger.getLogger("org").setLevel(Level.ERROR)

    val spark = SparkSession.builder()
      .appName("CostBasedOptimizer")
      .master("local[*]")
      .getOrCreate()

    /*
     * Lets create three tables with one of them as small.
     * To create table we will use spark.range() which will give you
     * dataset with default column name as "id"
     */
    cleanupSparkWarehouse()
    spark.range(1000).write.saveAsTable("small")
    spark.range(10000000).write.saveAsTable("huge")
    spark.range(10000000).withColumn("double", col("id") * 2).write.saveAsTable("twice")

    /*
     * CASE-1
     * =======
     * Joining three tables using inner join
     * 1. when CBO is not enabled
     * 2. when CBO is enabled
     */
    joinTablesWhenCBOIsDisabled(spark)
    joinTablesWhenCBOIsEnabled(spark)

    /*
     * CASE-1
     * ========
     * Joining three table with filter condition in one of the huge table
     * 1. when CBO is not enabled
     * 2. when CBO is enabled
     */
    joinTablesWithFilterWhenCBOIsDisabled(spark)
    joinTablesWithFilterWhenCBOIsEnabled(spark)

    while (true) {}
  }

  def joinTablesWhenCBOIsDisabled(spark: SparkSession) = {
    println("########## Joining tables with inner join when CBO is disabled")

    // Lets see, if CBO is disabled
    println("spark.sql.cbo.enabled: " + spark.conf.get("spark.sql.cbo.enabled"))

    /*
     *  Now join all three tables with inner join.
     *  here we are joining two huge tables first then their result with small table.
     *  (huge + double) + small
     */
    val join_query = spark.table("huge").join(spark.table("twice"), "id")
      .join(spark.table("small"), "id")

    join_query.take(1) // --> calling action to trigger join

    /*
     * What is the behavious here, when CBO is disabled-
     * ###################################################
     * Go to spark UI at 4040 port, and look for take query under SQL tab.
     *
     * Look at the execution plan, where two huge tables (huge and twice) are joined first
     * using SortMergeJoin (with Exchange stage followed with Sort).
     * And then result having 10000000 rows is joined with small table (1000 rows) using BroadCastHashJoin.
     *
     * It could have been much better if spark had re-ordered the join to first
     * join small table with one of the huge table and then with other huge table.
     * Thus this would have reduced amount of data shuffling. (This we will see when CBO is enabled)
     *
     */

  }

  def joinTablesWhenCBOIsEnabled(spark: SparkSession) = {
    println("########## Joining tables with inner join when CBO is Enabled")

    // Enable CBO
    spark.conf.set("spark.sql.cbo.enabled", true)
    println("spark.sql.cbo.enabled: " + spark.conf.get("spark.sql.cbo.enabled"))

    // After CBO is enabled, enable joinReorder in cbo
    spark.conf.set("spark.sql.cbo.joinReorder.enabled", true)
    println("spark.sql.cbo.joinReorder.enabled: " + spark.conf.get("spark.sql.cbo.joinReorder.enabled"))

    /*
     * In order to make CBO work, enabling it is not enough. We have to collect table statistics to allow
     * CBO to calculate cost of the join queries and come up with cheapest execution plan.
     * So lets ANALYZE the tables participating in join using ANALYZE TABLE command.
     */
    spark.sql("analyze table small compute statistics")
    spark.sql("analyze table huge compute statistics")
    spark.sql("analyze table twice compute statistics")

    // After collection statistics, execute the same inner join on id
    val join_query = spark.table("huge").join(spark.table("twice"), "id")
      .join(spark.table("small"), "id")

    join_query.take(2) // --> calling action to trigger join

    /*
     * Lets see how CBO optimized the join query here
     * #################################################
     * CBO has performed re-ordering of the join after analyzing the tables statistics.
     * Re-ordering joins here has improved the performance. Lets see how-
     *
     * Go to spark UI and open SQL tab, look for recent 'take' query, there you will see
     * spark has reordered the join sequence, I mean it has first joined to small table with
     * one of the huge table using BroadcastHashJoin and that resulted only 1000 rows.
     *
     * These 1000 rows are then joined with another huge table using SortMergeJoin (with a Exchange stage).
     * So here CBO has reduced amount of data shuffle which was very high when joining
     * two huge tables first and then with small table (without CBO)
     *
     */

  }

  def joinTablesWithFilterWhenCBOIsDisabled(spark: SparkSession) = {
    println("########## Joining tables with inner join when filter is applied and CBO is disabled")

    // Disabling CBO as it was enabled before
    spark.conf.set("spark.sql.cbo.enabled", false)
    println("spark.sql.cbo.enabled: " + spark.conf.get("spark.sql.cbo.enabled"))

    /*
     * Joining three tables here, with a filter condition on one of the huge table.
     */
    val join_query = spark.table("huge").join(spark.table("twice").filter(col("double") < 2000), "id")
      .join(spark.table("small"), "id")
    join_query.take(3)

    /*
     * What is the behaviour of the join query when cbo is disabled
     * ##############################################################
     * Go to spark UI at 4040, open SQL tab and look for recent 'take' query =>
     *
     * The huge table is joined with twice table (with 1000 rows after filter condition) using
     * SortMergeJoin (Exachange stages happened) and this join's outcome is joined with small
     * table with 1000 rows using BroadcastHashJoin (as table is very small so broadcasthash join happens)
     *
     * Ideally, the spark should have applied BroadcastHashJoin when joining the 'huge' table with
     * filtered twice table, because after filter condition applied on twice table the output table
     * has become very small and this satisfies the broadcasthash join threshold. Doing this
     * would have been prevented Data Exchange in this joining operation. We will see the same when
     * CBO is enabled.
     */
  }

  def joinTablesWithFilterWhenCBOIsEnabled(spark: SparkSession) = {
    println("########## Joining tables with inner join when filter is applied and CBO is enabled")

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
    spark.sql("analyze table small compute statistics")
    spark.sql("analyze table huge compute statistics")
    spark.sql("analyze table twice compute statistics")
    spark.sql("analyze table twice compute statistics for columns double") //--> also analyze statistics on column 'double' where filter is applied

    val join_query = spark.table("huge").join(spark.table("twice").filter(col("double") < 2000), "id")
      .join(spark.table("small"), "id")
    join_query.take(4)
    
    /*
     * Lets see how CBO optimized the join query here
     * #################################################
     * The huge table is joined with filtered result of twice table using Broadcastjoin.
     * So spark has broadcasted filtered result from twice table.
     * 
     * Then output of first join is merge with small table using BroadcastJoin again.
     * So no independent Exchange job is present. This proves CBO has reduced data shuffling. 
     */

  }

  def cleanupSparkWarehouse() = {
    FileUtils.deleteDirectory(new File("spark-warehouse/"))
  }

}