package com.thedevd.sparkexamples.pairRdd.sort

import org.apache.log4j.Logger
import org.apache.log4j.Level
import org.apache.spark.sql.SparkSession

/*
 * The problem is to read /rdd/word_count.txt
 * and write a spark program to find count of words sorted by descending order of their count
 * means word with higher count should come first and so on..
 * 
 * use sortBy transformation
 */
object WordCountProblemSortedByCount {
  
  def main(args: Array[String]): Unit = {
    Logger.getLogger("org").setLevel(Level.OFF)
    
    val spark = SparkSession.builder()
    .appName("WordCountProblemSortedByCount")
    .master("local[*]")
    .getOrCreate();
    
    val lines = spark.sparkContext.textFile(getClass.getResource("/rdd/word_count.txt").getPath)
    val wordCountSortByCount = lines.filter(_.trim().length() > 0)
    .flatMap(line => line.split("\\s+"))
    .map(word => (word, 1))
    .reduceByKey(_+_)
    .sortBy(wordCount => wordCount._2, ascending = false) // ascending = false means DESC
    .collect() // do not do collect().toMap or collectAsMap, collecting as map will destroy sorting sequence.
    .foreach(print)
    
  }
}