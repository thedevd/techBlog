package com.thedevd.sparkexamples.specialcase

import org.apache.log4j.Logger
import org.apache.log4j.Level
import org.apache.spark.sql.SparkSession

/*
 * One of the disadvantage of using SparkContext is that you have instantiate separate context for different pipelines, means -
 *   HiveContext to work with Hive,
 *   SparkStreamingContext to work with streaming and
 *   SQLContext to work with sparkSql.
 *
 * SparkSession comes to help with this. From spark 2.0, SparkSession is now common entry point to work
 * with different pipelines. Means no need to create separate contexts.
 * You can access them with SparkSession (sparkSession.sparkContext, sparkSession.sqlContext)
 *
 * Multiple SparkSession sharing same SparkContext
 * ###################################################
 * It is possible to have multiple spark session for application, but internally they share the same sparkContext.
 * So stopping one sparksession will also stop context of another session.
 *
 * To create new SparkSession, spark has straight-forward API newSession(),
 * But if we stay with the builder (getOrCreate() method), we'll always get the same SparkSession instance.
 * 
 * When to use Multiple SparkSession
 * #####################################
 * when you want to process the data with different sparkSession and each session need to have different set of configuration. 
 * For example - 
 *   One of the session need crossJoin (carition product) enabled and no of sql shuffle partitions to 100
 *     sparkSession1.conf.set("spark.sql.crossJoin.enabled", "true") 
 *     sparkSession1.conf.set("spark.sql.shuffle.partitions", 100) 
 *  
 *   Another session does not need crossJoin enabled and no of sql shuffle partitions to 300    
 *     val sparkSession2 = sparkSession1.newSession() 
 *     sparkSession2.conf.set("spark.sql.crossJoin.enabled", "false") 
 *     sparkSession2.conf.set("spark.sql.shuffle.partitions", 300) 
 */
object MultipleSparkSession {

  def main(args: Array[String]): Unit = {
    Logger.getLogger("org").setLevel(Level.ERROR)

    val sparkSession1 = SparkSession.builder().appName("sparkSession#1").master("local[2]").getOrCreate()
    /*
     * //getOrCreate() is a factory method, so creating another session using this will always give you same instance.
     * val sparkSession2 = SparkSession.builder().appName("sparkSession#2").master("local[*]").getOrCreate()
     * //So sparkSession2 isEqualTo sparkSession1
     *
     * To create a new isolated sparkSession, spark has straight-forward API newSession() ->
     * Calling it on existing sparkSession instance will starts a new session with isolated SQLContext,
     * temporary tables, registered functions are also isolated.
     * But all sparkSession will be sharing the underlying SparkContext and cached data.
     * 
     */

    val sparkSession2 = sparkSession1.newSession()

    println("sparkSession1 isEqualTo sparkSession2: "
      + sparkSession1.equals(sparkSession2)) // --> false
    println("sparkSession1.sparkContext isEqualTo sparkSession2.sparkContext: "
      + sparkSession1.sparkContext.equals(sparkSession2.sparkContext)) // --> true, bcz underlying sparkContext is same
    println("sparkSession1.sqlContext isEqualTo sparkSession2.sqlContext: "
      + sparkSession1.sqlContext.equals(sparkSession2.sqlContext)) // --> false, bcz each session has isolated sqlContext
      

    /*
     * Lets see isolation of sqlContext among session1 and session2. 
     * Creating the tables using one session will not be available in other
     */
      
    val small_df = sparkSession1.range(1000)
    small_df.createOrReplaceTempView("small_table") // creating temporary table using sparkSession1
    sparkSession1.sql("show tables").show()
   /* +--------+-----------+-----------+
    * |database|  tableName|isTemporary|
    * +--------+-----------+-----------+
    * |        |small_table|       true|
    * +--------+-----------+-----------+*/
    
     sparkSession2.sql("show tables").show() // it shows nothing, this proves isolation of sqlContext and temporary tables
     /*+--------+---------+-----------+
      * |database|tableName|isTemporary|
      * +--------+---------+-----------+
      * +--------+---------+-----------+*/
     
     /*
      * Isolation of configuration
      */
     sparkSession1.conf.set("spark.sql.shuffle.partitions", 100)
     sparkSession2.conf.set("spark.sql.shuffle.partitions", 300)

     println("### sparkSession1.conf.getAll: " + sparkSession1.conf.getAll) // --> spark.sql.shuffle.partitions -> 100
     println("### sparkSession2.conf.getAll: " + sparkSession2.conf.getAll) // --> spark.sql.shuffle.partitions -> 300

  }
}