package com.thedevd.sparkexamples.rdd

import org.apache.log4j.Logger
import org.apache.log4j.Level
import org.apache.spark.sql.SparkSession

object WordCount {

  def main(args: Array[String]): Unit = {
    Logger.getLogger("org").setLevel(Level.OFF)

    val spark = SparkSession.builder()
      .appName("wordCount")
      .master("local[*]")
      .getOrCreate()

    val textLines = spark.sparkContext
      .textFile(getClass.getResource("/rdd/word_count.txt").getPath)
      
    // The file is having empty lines 
    // as two lines are separated by another end line character
    // Due to this, this will also count them as (,2) which we dont want, so lets do filter
    val validTextLines = textLines.filter(line => line.length() > 0)

    // The file is having words separated by more than one space
    // so use line.split("\\s+") instead line.split(" ")
    val words = validTextLines.flatMap(line => line.split("\\s+"))
    
    val wordCount = words.map(word => (word, 1))
    
    val finalWordCount = wordCount.reduceByKey((a, b) => a + b)
    
    // Sort by value in descending order. For ascending order remove 'false' argument from sortBy
    val sortedWordCount = finalWordCount.sortBy(pair => pair._2, false)
    
    sortedWordCount.collect().foreach(println)
  }
}