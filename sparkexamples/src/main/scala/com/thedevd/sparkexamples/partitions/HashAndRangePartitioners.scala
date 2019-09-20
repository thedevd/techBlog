package com.thedevd.sparkexamples.partitions

import org.apache.log4j.Logger
import org.apache.log4j.Level
import org.apache.spark.sql.SparkSession
import org.apache.spark.rdd.RDD
import scala.reflect.ClassTag
import org.apache.spark.HashPartitioner
import org.apache.spark.RangePartitioner
import org.apache.spark.RangePartitioner

/*
 * 1. Why partitions in spark
 *    In spark data of Rdds is divided into no of partitions. This is done to achieve
 *    high level of parallelism in distributed environment.
 *
 *    So more partitions means more parallelism and more parallelism mean high performance.
 *
 *    Properties of partitions-
 *    a. Each machine in the cluster contains one or more partitions but one partition can not span multiple machines
 *    b. The no of partitions an Rdd can have is configurable in spark. By default it is equal to total no of cores in cluster.
 *       i.e if your cluster has 20 cores, spark will create atleast 20 partitions. Although you can increase it at the time
 *       of creating RDD or using repartition(). (Usually it is recommended that RDD can have partitions 2-3 times of total cores)
 *
 *       No of partitions also depends on the file system. If you are loading file from HDFS, spark will create one partition
 *       for each block of the file.
 *    c. For non splittable files such as gz or zip, spark creates only one partition.
 *
 * 2. Types of partitioner available in spark.(used when data is key-value based)
 *    we can use these two partitioner to distribute data uniformly across partitions. they are-
 *    # HashPartitioners
 *    # RangePartitioners
 *
 *    Note- Spark’s partitioner feature is available on all RDDs of key/value pairs.
 *
 *    Although we can also create custom partitioner. How to use partitioner-
 *       # use partitionBy()
 *
 * 3. How partitioner improves performance
 *    In distributed computing, the main challenge is to minimize network traffic across nodes which
 *    is also know as shuffling. So partitioner may help reducing the overhead of shuffling till a great extent
 *    by distributing relevant data (based on key) in one place(node)
 *
 * 4. Keep in mind that partitioning will not be always helpful in all situations.
 *    For example, if a given RDD is scanned only once, there is no point in partitioning it in advance using Spark partitioner.
 *    It’s useful only when a dataset is key-value and it is reused multiple times (especially in wide transformations functions
 *    like reduceByKey(), groupByKey(), join() etc. because in these cases where shuffling is needed, partitioner plays very
 *    important role to avoid too much data shuffling across nodes.
 *
 * 5. What is HashPartitioner and RangePartitioners
 *    1. HashPartitioner -> It Uses Java’s Object.hashCode() method to determine
 *       the correct partition.
 *           partition = key.hashCode() % numPartitions.
 *
 *
 *    2. RangePartitioner -> It Uses a range to distribute data to the respective partitions. All keys falling in a particular range
 *       will go to same partition. This partitioner is suitable where there’s a natural ordering in the keys and the keys are non negative.
 *
 */
object HashAndRangePartitioners {

  def main(args: Array[String]): Unit = {
    Logger.getLogger("org").setLevel(Level.ERROR)

    val spark = SparkSession.builder()
      .appName("SparkPartitioning")
      .master("local[*]")
      .getOrCreate()

    /*
     * ##################### Default partitioning behavior
     * Default partitioning behavior depends upon  how the RDD has been created like
     *    a. whether it has been created from external sources like Cassandra table, HDFS file etc
     *    b. or by transforming one RDD into another new RDD.
     *
     * If there is no partitioner, then spark distributes data randomly and uniformed across node.
     */

    /*
     * Spark does not use any partitioner when
     * # parallelizing scala collection
     * # Reading file from hdfs or text file or from cassandraTable
     * # Creating RDD from other using generic collection such as filter(), map(), flatmap()
     */
    println("##### Partitioner when using sc.parallelize")
    val nums_rdd = spark.sparkContext.parallelize(Seq(1, 2, 3, 4, 5, 6, 7, 8, 9, 10))
    printPartitionDetails(nums_rdd)

    println("##### Partitioner when using generic transformation")
    val pair_rdd = nums_rdd.map(x => (x, x * x))
    printPartitionDetails(pair_rdd)

    /*
     * Spark uses HashPartitioner when creating RDD from another using
     * key-based transformation which usually requires shuffling such as-
     *   reduceByKey(), groupByKey(), foldByKey(), combineByKey()
     */
    println("##### HashPartitioner when using key-based transformation")
    val aggregated_rdd = pair_rdd.reduceByKey(_ + _)
    printPartitionDetails(aggregated_rdd)

    println("##### RangePartitioner when using sortByKey() transformation")
    val sorted_rdd = aggregated_rdd.sortByKey(ascending = false)
    printPartitionDetails(sorted_rdd)

    /*
     * ##################### Custom partitions with partitionBy()
     * To specify custom partitioning logic, spark provides partitionBy() transformation for this.
     *
     * Note-  to use partitionBy() RDD must be paired Rdd. It's a transformation, so a new RDD will be returned.
     *
     * Important: It is highly recommended to persist the result of partitionBy. Otherwise, the partitioning is
     * repeatedly applied (involved shuffling!) each time the partitioned RDD is used.
     *
     *
     */
    println("##### HashPartitioner using partitionBy() transformation")
    val nums_paired_rdd = nums_rdd.map(num => (num, num))
      .partitionBy(new HashPartitioner(3))
      .persist() // --> dont forget to persist RDD here.

    printPartitionDetails(nums_paired_rdd)
    /*
     * After applying HashPartitioner, the data is partitioned in 3 partitions like this-
     * ((2,2), (5,5), (8,8))
     * ((1,1), (4,4), (7,7), (10,10))
     * ((3,3), (6,6), (9,9))
     *
     * So you can see, HashPartitioner using formula-
     *  java object's hascode % no of partitions
     * It means all the keys with remainder 0 goes to same partition
     * and all the keys with remainder 1 goes to same partition
     * and so on....
     */

    println("##### RangePartitioner using parittionBy() transformation")
    val paired = for (i <- 1 to 20) yield (i, i)
    val ranged_pair_rdd = spark.sparkContext.parallelize(paired)
    val rdd_partitionedByRange = ranged_pair_rdd
      .partitionBy(new RangePartitioner(3, ranged_pair_rdd))
      .persist()
    printPartitionDetails(rdd_partitionedByRange)
    /*
     * RangePartitioner takes the same pairedRdd as second argument to create ranges for distribution.
     *
     * After applying RangePartitioner, the data is distributed in 3 partitions-
     * ((1,1), (2,2), (3,3), (4,4), (5,5), (6,6), (7,7))
     * ((8,8), (9,9), (10,10), (11,11), (12,12), (13,13), (14,14))
     * ((15,15), (16,16), (17,17), (18,18), (19,19), (20,20))
     *
     * So the keys falling in range 1 to 7, goes in same partition.
     * keys falling in range 8 to 14 goes in same partition and so on...
     *
     */

    /*
     * Lets see how RangePartitioner works on String keys-
     */
    println("##### RangePartitioner  on string keys")
    val alphabates = spark.sparkContext.parallelize('a' to 'z')
    .map(char => (char, 1))
      
    val alphabate_ranged_partitioner = alphabates.partitionBy(new RangePartitioner(3, alphabates))
    printPartitionDetails(alphabate_ranged_partitioner)
    /*
     * Applying RangePartitioner on String keys uses alphabates ranges to distribute keys.
     * Such as here we have 3 partitions data -
     * ((j,1), (k,1), (l,1), (m,1), (n,1), (o,1), (p,1), (q,1), (r,1)) 
     * ((s,1), (t,1), (u,1), (v,1), (w,1), (x,1), (y,1), (z,1))
     * ((a,1), (b,1), (c,1), (d,1), (e,1), (f,1), (g,1), (h,1), (i,1)) 
     * 
     * So the keys falling in range a to i goes in same partition.
     * Keys falling in range j to r goes to same partition
     * and Keys falling in range s to z goes to same partition
     */
  }

  def printPartitionDetails[U: ClassTag](rdd: RDD[U]) = {
    println("partitions: " + rdd.getNumPartitions)
    /*
     * Spark uses partitioner property to determine the algorithm to decide on which worker a particular record  of RDD should be stored on.
     * if partitioner is NONE that means partitioning is not based upon characteristic of data but distribution is random and guaranteed to be uniform across nodes.
     */
    println("partitioner used: " + rdd.partitioner)

    print("data in partitions: ")
    rdd.mapPartitions(iterator => {
      print(iterator.toList + " ")
      iterator
    }).collect()

    println()
  }
}