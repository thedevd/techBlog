package com.thedevd.sparkexamples.rdd

import org.apache.log4j.Logger
import org.apache.log4j.Level
import org.apache.spark.sql.SparkSession
import org.apache.commons.io.FileUtils
import java.io.File

/*
 * "/rdd/samehostproblem/nasa_19950701.tsv" file contains 10000 log lines from one of NASA's apache server for July 1st, 1995.
   "/rdd/samehostproblem/nasa_19950801.tsv" file contains 10000 log lines for August 1st, 1995

   Create a Spark program to generate a new RDD which contains the log lines from both July 1st and August 1st,
   take a 0.1 sample of those log lines and save it to "sample_nasa_logs.tsv" file.

   Keep in mind, that the original log files contains the following header lines.
   host	logname	time	method	url	response	bytes

   Make sure the head lines are removed in the resulting RDD.
*/

object RddSampleOperation {

  def main(args: Array[String]): Unit = {
    Logger.getLogger("org").setLevel(Level.OFF)

    val spark = SparkSession.builder()
      .appName("RddSampleOperationWithUnion")
      .master("local[*]")
      .getOrCreate()

    val julyAccessLogs = spark.sparkContext.textFile(getClass.getResource("/rdd/samehostproblem/nasa_19950701.tsv").getPath)
    val augAccessLogs = spark.sparkContext.textFile(getClass.getResource("/rdd/samehostproblem/nasa_19950801.tsv").getPath)

    val aggegatedAccessLogs = julyAccessLogs.union(augAccessLogs)

    // filter out the header after union
    val cleanedAggregatedAccessLogs = aggegatedAccessLogs.filter(isHeader(_))

    /*
     * About Spark RDD sample operation
     * -----------------------------------
     * Spark sampling functions allows to take different samples following binomial distributions.
     * In Spark, there are two sampling operation - sample(a Transformation) and takeSample (a Action).
     * 
     * takeSample is an action that returns an Array and not an RDD.
     * And this can be used if you want fixed size sample data which is not the case using only sample
     * But takeSample is slow with large rdd.
     *
     * In our example we are taking 10% sample of aggregatedLogs using .sample(true, 0.1).
     * It is to be noted that .sample(true, 0.1) doesn't guarantee to return fixed sized sample always,
     * that's because spark internally uses something called binomial distribution for taking the sample.
     * So the fraction argument here (which is 0.1) is just a approximation to 10% sample data.
     *
     * The first argument in sample is withReplacement.
     *  if you are sampling with replacement (true), you can get the same element in sample twice,
     *  and w/o replacement (false) you only get it once.
     *  So if your RDD has [Bob, Alice and Carol] then
     *  your "with replacement" sample can be [Alice, Alice],
     *  but w/o replacement sample can't have duplicates like that.
     */
    val sampleData = cleanedAggregatedAccessLogs.sample(true, 0.1)
    println("aggegatedAccessLogs: " + cleanedAggregatedAccessLogs.count() +
      ". After sample(true, 0.1): " + sampleData.count())
      
    val fixedSizeSampleArray = cleanedAggregatedAccessLogs.takeSample(true, 2000) // takeSample return an array not RDD 
    println(fixedSizeSampleArray.size) // this always be 2000
    val fixedSizeSampleRdd = spark.sparkContext.makeRDD(fixedSizeSampleArray)

    cleanupOutputDir("C://temp/sample-nasa-access-logs.tsv")
    sampleData.coalesce(1).saveAsTextFile("C://temp/sample-nasa-access-logs.tsv")
    //fixedSizeSampleRdd.coalesce(1).saveAsTextFile("C://temp/sample-nasa-access-logs.tsv")

  }

  def isHeader(x: String) = !x.startsWith("host") && !x.endsWith("bytes")
  
  def cleanupOutputDir(dir: String) = FileUtils.deleteDirectory(new File(dir))
}