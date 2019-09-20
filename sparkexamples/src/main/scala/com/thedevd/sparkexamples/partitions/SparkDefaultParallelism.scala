package com.thedevd.sparkexamples.partitions

import org.apache.log4j.Logger
import org.apache.log4j.Level
import org.apache.spark.sql.SparkSession

/*
 * In spark, if no partitions is given by user at time of creating RDD either by parallelize or 
 * from another RDD using transformation, then spark.default.parallelism is used to assign
 * default partition in RDD.
 * 
 * By default spark.default.parallelism  is number of cores available to application.
 * 
 * This is not applicable on dataframes partition if reading df directly from csv file or other.
 * 
 */
object SparkDefaultParallelism {

  def main(args: Array[String]): Unit = {
    Logger.getLogger("org").setLevel(Level.ERROR)

    val spark = SparkSession.builder()
      .appName("SparkDefaultParallelism")
      // Sets the Spark master URL to connect to, such as "local" to run locally, "local[4]" to run locally with 4 cores, or "spark://master:7077" to run on a Spark standalone cluster. 
      .master("local[2]")
      .getOrCreate()
    
    /*
     * For local[*], whatever cores are specified in local mode, that no will be defaultParallelism
     * and if the master url is cluster mode, then it is total no of Cores in cluster.
     */
    println("spark.sparkContext.defaultParallelism: " + spark.sparkContext.defaultParallelism) //2
    
    val rdd = spark.sparkContext.parallelize(Range(1,100)) // --> since no partition is given so defaultParallelism is used
    println(rdd.getNumPartitions) //2
    
    
    val pair_rdd = spark.sparkContext.parallelize(Seq((1,1),(2,2),(3,3),(4,4),(5,5)))
    val r = pair_rdd.reduceByKey(_+_, 4) // --> numOfPartitions is specified, so this is used
    println(r.getNumPartitions) // 4
    
    /*
     * Loading single csv file into Dataframe create only single partition
     * And also generic transformation applied to dataframe will also lead one partition
     * except for the shuffling kind operation (join etc) where it is used spark.sql.shuffle.partitions value
     */
    val df = spark.read.format("csv")
    .option("header", false)
    .option("delimiter", ",")
    .option("inferSchema", true)
    .load(getClass.getResource("/sparksql/student_marks.txt").getPath)
    
    println(df.rdd.getNumPartitions) // 1
    
    val passed_student = df.filter(df("_c3") >= 70)
    passed_student.show()
    println(passed_student.rdd.getNumPartitions) // still 1 partition
    
    val repartitioned_df = passed_student.repartition(spark.sparkContext.defaultParallelism)
    println(repartitioned_df.rdd.getNumPartitions) // 2
    

  }
}