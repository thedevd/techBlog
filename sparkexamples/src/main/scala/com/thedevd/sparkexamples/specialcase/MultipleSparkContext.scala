package com.thedevd.sparkexamples.specialcase

import org.apache.spark.SparkContext
import org.apache.log4j.Logger
import org.apache.log4j.Level
import org.apache.spark.SparkConf

/*
 * What is SparkContext
 * ########################
 * SparkContext is entry point to access all spark functionality in the driver, this includes working with RDDs, creating accumulator, broadcast etc.  
 * It also holds the connection details to connect to spark cluster (where the master). 
 * 
 * Generally spark framework allows only one SparkContext per JVM. Creating more than one sparkContext will
 * throw this exception -
 *      ERRPR SparkContext: Multiple running SparkContexts detected in the same JVM!
 *      org.apache.spark.SparkException: Only one SparkContext may be running in this JVM (see SPARK-2243). 
 *      To ignore this error, set spark.driver.allowMultipleContexts = true 
 * 
 * From the error, we can say, Spark framework can allow multiple SparkContext by setting the property -
 *      spark.driver.allowMultipleContexts = true
 *      
 * Setting the above property will convert the error message to WARNING only, so still you will see that error message as WARN.     
 * 
 * But wait, allowing more than one sparkContext per spark application is very very bad practice.
 * Doing this will not guarantee that your pipeline will work as expected, apart from this, it will 
 * put lot of pressure on the hardware and may cause whole JVM to crash. So by default allowingMultipleContext
 * in one spark driver is disabled.
 * 
 * Creating more than one sparkcontext is basically used in testing purpose to test Apache Spark Library (By the spark developer).
 */
object MultipleSparkContext {
  
  def main(args: Array[String]): Unit = {
    
    Logger.getLogger("org").setLevel(Level.ERROR)
    
    /*
     * This property allows Spark framework to create more than one sparkContext
     */
    val sparkConfiguration = new SparkConf().set("spark.driver.allowMultipleContexts", "true") 
    
    val sparkContext1 = new SparkContext("local","SparkContext1", sparkConfiguration)
    val sparkContext2 = new SparkContext("local","SparkContext2", sparkConfiguration)
    
    val rdd1 = sparkContext1.parallelize(Seq(1,2,3,4,5))
    val rdd2 = sparkContext2.parallelize(Seq(6,7,8,9,10))
    
    rdd1.foreach(print)
    //rdd2.foreach(print) --> commented this because this is throwing exception - Task attempt 0 is already registered
  }
}