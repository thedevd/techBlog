package com.thedevd.sparkexamples.differences

import org.apache.log4j.Logger
import org.apache.log4j.Level
import org.apache.spark.sql.SparkSession

object CoalesceVsRepartition {

  def main(args: Array[String]): Unit = {
    Logger.getLogger("org").setLevel(Level.ERROR)

    val spark = SparkSession.builder().appName("CoalesceVsRepartition").master("local[*]").getOrCreate()
    val intRdd = spark.sparkContext.parallelize(Range(1,13))
    val copyRdd = intRdd
    
    println("intRdd.getNumPartitions= " + intRdd.getNumPartitions) // 4
    println("copyRdd.getNumPartitions= " + copyRdd.getNumPartitions) // 4
    
    val modified_intRdd = intRdd.coalesce(2) // reducing the partitions
    val modfied_copyRdd = copyRdd.repartition(10) // increasing the partitions
    
    println("intRdd.coalesce(2).getNumPartitions= " + modified_intRdd.getNumPartitions) // 2
    println("copyRdd.repartition(10).getNumPartitions= " + modfied_copyRdd.getNumPartitions) // 10
    
    // try to increase the partition using coalesce will not work
    println("modified_intRdd.coalesce(4).getNumPartitions= " + modified_intRdd.coalesce(4).getNumPartitions) // still prints 2
    
    /*
     * Detailed difference
     * #####################
     * 1. Coalesce can only be used to reduce partitions
     * whereas repartition can be used to increase or decrease partitition
     * 
     * 2. Coalesce avoids full data shuffle thus minimize unneccessary data movement
     * whereas repartition does full data shuffle. lets understand with an example
     * 
     * If we have 4 patitions with following data -
     * 		part-00000 = 1,2,3
     * 		part-00001 = 4,5,6
     * 		part-00002 = 7,8,9
     * 		part-00003 = 10,11,12
     * 		
     * 		Then coalesce(2) will do something like this- 
     * 		part-00000 = 1,2,3 + (10,11,12)
     * 		part-00001 = 4,5,6 + (7,8,9) 
     * 
     * 		Notice that data of part-00000 and 00001 did not move. So this is more efficient.
     * 
     * 		Doing the same using repartition(2) may endup like this -
     * 		part-00000 = 1,3,5,7,9,10,12
     * 		part-00001 = 2,4,6,8,11
     * 		
     * 		So full data shuffling happens with repartition.
     * 
     * This url has a good article on managing spark partitions manually using coalesce and repartition-
     * https://medium.com/@mrpowers/managing-spark-partitions-with-coalesce-and-repartition-4050c57ad5c4
     * 
     */
    
  }
}