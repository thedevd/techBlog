package com.thedevd.sparkexamples.pairRdd.groupByKey

import org.apache.log4j.Logger
import org.apache.log4j.Level
import org.apache.spark.sql.SparkSession

/*
 * Avoid using GroupByKey always, because groupByKey directly goes for shuffling the keys across partitions
 * means each single key-value is shuffled. This increase lot of network IO. So performance gets decreased.\
 * In groupByKey there is no combining at mapped side.
 *
 * But on other hand in reduceByKey, Data is combined at each partition and
 * only one output for one key at each partition is sent over network. 
 * Then the function is called again to reduce all the values from each partition to produce one final result.
 * So performance is more better in reduceByKey as compared to groupByKey
 * 
 * Follow this url for detailed difference
 * https://databricks.gitbooks.io/databricks-spark-knowledge-base/content/best_practices/prefer_reducebykey_over_groupbykey.html
 */
object GroupByKeyVsReduceByKey {

  def main(args: Array[String]): Unit = {
    Logger.getLogger("org").setLevel(Level.OFF)

    val spark = SparkSession.builder()
      .appName("GroupByKeyVsReduceByKey")
      .master("local[*]")
      .getOrCreate()

    val lines = spark.sparkContext.textFile(getClass.getResource("/rdd/word_count.txt").getPath)
    val words = lines.filter(_.trim().length() > 0) // some lines are empty
      .flatMap(line => line.split("\\s+"))
      .map(word => (word, 1))

    import spark.implicits;
    val wordCountUsingReduceByKey = words.reduceByKey(_+_)
    val wordCountUsingGroupByKey = words.groupByKey().map(t => (t._1, t._2.sum))

    println("WordCount using reduceByKey => " + wordCountUsingReduceByKey.collect().toMap)
    println("WordCount using groupByKey => " + wordCountUsingGroupByKey.collect().toMap)

  }
}