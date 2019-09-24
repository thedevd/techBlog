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
 * CASE-1
 * =======
 * Joining three tables using inner join
 * 1. when CBO is not enabled
 * 2. when CBO is enabled
 */
object CostBasedOptimizerWithJoinQuery {

  def main(args: Array[String]): Unit = {
    Logger.getLogger("org").setLevel(Level.ERROR)

    val spark = SparkSession.builder()
      .appName("CostBasedOptimizerWithJoinQuery")
      .master("local[*]")
      .getOrCreate()

    cleanupSparkWarehouse()

    /*
     * CASE-1
     * =======
     * Joining three tables using inner join
     * 1. when CBO is not enabled
     * 2. when CBO is enabled
     */
    joinTablesWhenCBOIsDisabled(spark)
    joinTablesWhenCBOIsEnabled(spark)

    while (true) {}
  }

  def joinTablesWhenCBOIsDisabled(spark: SparkSession) = {
    println("########## Joining tables with inner join when CBO is disabled")
    createTables(spark, "d")

    // Lets see, if CBO is disabled
    println("spark.sql.cbo.enabled: " + spark.conf.get("spark.sql.cbo.enabled"))

    /*
     *  Now join all three tables with inner join.
     *  here we are joining two huge tables first then their result with small table.
     *  (huge_d + twice_d) + small_d
     */
    val join_query = spark.table("huge_d").join(spark.table("twice_d"), "id")
      .join(spark.table("small_d"), "id")

    join_query.take(1) // --> calling action to trigger join

    /*
     * What is the behaviour here, when CBO is disabled-
     * ###################################################
     * Go to spark UI at 4040 port, and look for 'take' query under SQL tab.
     *
     * Look at the execution plan, where two huge tables (huge_d and twice_d) are joined first
     * using SortMergeJoin (with Exchange stage followed with Sort).
     * And then result is joined with small_d table (1000 rows) using BroadCastHashJoin.
     *
     * It could have been much better if spark had re-ordered the join to first
     * join small_d table with one of the huge table and then with other huge table.
     * Thus this would have reduced amount of data shuffling. (This we will see when CBO is enabled)
     * 
     * so -
     * No of overall Exchange stages in physical plan = 2
     *   1. Exchange 1 = 152.6 MB data size
     *   2. Exchange 2 = 228.9 MB data size
     */

  }

  def joinTablesWhenCBOIsEnabled(spark: SparkSession) = {
    println("########## Joining tables with inner join when CBO is Enabled")
    createTables(spark, "e")

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
    spark.sql("analyze table small_e compute statistics")
    spark.sql("analyze table huge_e compute statistics")
    spark.sql("analyze table twice_e compute statistics")

    // After collecting statistics, execute the same inner join on id
    val join_query = spark.table("huge_e").join(spark.table("twice_e"), "id")
      .join(spark.table("small_e"), "id")

    join_query.show(1) // --> calling action to trigger join

    /*
     * Lets see how CBO optimized the join query here
     * #################################################
     * CBO has performed re-ordering of the join after analyzing the tables statistics.
     * Re-ordering joins here has improved the performance. Lets see how-
     *
     * Go to spark UI and open SQL tab, look for recent 'show' query, there you will see
     * spark has reordered the join sequence, I mean it has first joined small_e table with
     * one of the huge table using BroadcastHashJoin without Exchange stage and that resulted only 1000 rows.
     *
     * These 1000 rows are then joined with another huge table using SortMergeJoin (with Exchange stage).
     * So here CBO has reduced amount of data shuffle which was very high when joining
     * two huge tables first and then with small table (without CBO)
     *
     * so -
     * No of overall Exchange stages in physical plan = 2
     *   1. Exchange 1 = 15.6 KB data size (previously it was 152.6 MB)
     *   2. Exchange 2 = 228.9 MB data size
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