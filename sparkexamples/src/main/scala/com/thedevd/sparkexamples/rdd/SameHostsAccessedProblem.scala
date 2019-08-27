package com.thedevd.sparkexamples.rdd

import org.apache.log4j.Logger
import org.apache.log4j.Level
import org.apache.spark.sql.SparkSession

/*
 * "rdd/nasa_19950701.tsv" file contains 10000 log lines from one of NASA's apache server for July 1st, 1995.
       "rdd/nasa_19950801.tsv" file contains 10000 log lines for August 1st, 1995
       Create a Spark program to generate a new RDD which contains the hosts which are accessed on BOTH days.
       Save the resulting RDD to "rdd/out/nasa_logs_same_hosts.csv" file.

       Example output:
       vagrant.vf.mmc.com
       www-a1.proxy.aol.com
       .....

       Keep in mind, that the original log files contains the following header lines.
       host	logname	time	method	url	response	bytes

       Make sure the head lines are removed in the resulting RDD.
*/
object SameHostsAccessedProblem {

  def main(args: Array[String]): Unit = {
    Logger.getLogger("org").setLevel(Level.OFF)

    val spark = SparkSession.builder()
      .appName("SameHostsAccessedProblem")
      .master("local[*]")
      .getOrCreate()

    val julyAccessLogs = spark.sparkContext
      .textFile(getClass.getResource("/rdd/samehostproblem/nasa_19950701.tsv").getPath)
    val augAccessLogs = spark.sparkContext
      .textFile(getClass.getResource("/rdd/samehostproblem/nasa_19950801.tsv").getPath)

    val julyAccessLogsWithoutHeader = julyAccessLogs.filter(!_.startsWith("host"))
    val augAccessLogsWithoutHeader = augAccessLogs.filter(!_.startsWith("host"))

    val hostAccessedInBothDays = julyAccessLogsWithoutHeader
      .map(_.split("\t")(0)).intersection(augAccessLogsWithoutHeader.map(_.split("\t")(0)))
      .distinct()

    // Or instead of doing two times filter lets just find intersection first and then filter header
    /*
    val hostsAccessedInJuly = julyAccessLogs.map(log => log.split("\t")(0))
    val hostAccessedInAug = augAccessLogs.map(log => log.split("\t")(0))

    val hostAccessedInBothDays = hostsAccessedInJuly.intersection(hostAccessedInAug)
    val cleanedHostAccessedInfo = hostAccessedInBothDays
      .distinct()
      .filter(host => host != "host")

    cleanedHostAccessedInfo.collect().foreach(println)
    */

    hostAccessedInBothDays
    .coalesce(1) // This is done just to force spark to create one part file to have output
    .saveAsTextFile("C://temp//logs_same_host.csv")

  }

}