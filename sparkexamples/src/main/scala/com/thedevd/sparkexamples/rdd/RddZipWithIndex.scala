package com.thedevd.sparkexamples.rdd

import org.apache.log4j.Logger
import org.apache.log4j.Level
import org.apache.spark.sql.SparkSession

/*
 * How to map elements of RDD to their indices.
 * Example - create RDD of an Array("a","b","c","d","e","f","g")
 * output should be - (element, element's indices)
 * (a,0)(b,1)(c,2)(d,3)(e,4)(f,5)(g,6)
 */
object RddZipWithIndex {
  
  def main(args: Array[String]): Unit = {
    Logger.getLogger("org").setLevel(Level.ERROR)
    
    val spark = SparkSession.builder()
    .appName("RddZipWithIndex")
    .master("local[*]")
    .getOrCreate()
    
    val rdd1 = spark.sparkContext.parallelize(List("a", "b", "c", "d", "e", "f", "g"), 3)
    val rdd2 = rdd1.zipWithIndex()
    rdd2.foreach(println)
    /*
    It is to be noted that it won't preserve the original ordering of the data from which RDD is created.
    But in most cases you might see the ordering, but this is not always guranteed
    (c,2)
    (d,3)
    (a,0)
    (b,1)
    (e,4)
    (f,5)
    (g,6)
    */
    
    /*
     * Note
     * ##########
     * zipWithIndex can be used to assign unique row no to the records in dataframe
     * because in distributed environment it is very difficult to maintain sequence no without duplicate
     * 
     * com.thedevd.sparkexamples.sparksql.SequenceNumberInDataFrameUsingZipWithIndex.scala
     */
   
  }
}