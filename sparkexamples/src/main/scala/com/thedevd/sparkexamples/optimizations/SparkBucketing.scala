package com.thedevd.sparkexamples.optimizations

import org.apache.log4j.Logger
import org.apache.log4j.Level
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.Dataset
import org.apache.spark.sql.SaveMode

/*
 * 1. Bucketing is optimization technique in spark for JOIN query, which is used to optimize the
 *    JOIN queries performance by avoiding data shuffle aka exhanges in physical execution plan.
 *
 *    With bucketing, the Exchanges are no longer needed (as the tables are already pre-shuffled).
 *
 * 2. Bucketing is enabled by default in spark.
 *    spark.sql.sources.bucketing.enabled is the property.
 *
 * 3. If you are using bucketing explicitly by using bucketBy() API, then there are some condition which
 *    Spark optimizer looks in order to avoid data exchange for join query -
 *    a. The number of partitions on both sides of a join has to be exactly the same.
 *    b. No of buckets should be between 0 and 100000
 *
 * 4. In Bucketing, data gets pre-shuffled at the time of writing to disk. So at the time of join query
 *    data exchange stage is not needed.
 *
 * 5. To avoid exchanges in bucketing, the data is written as bucketed table using file based sources
 *    such as parquet,orc,json,csv.
 *
 *    # To save bucketed table we use saveAsTable() along with bucketBy() on dataframe/dataset. Example
 *      people.write
 *       .bucketBy(5, "name") // <-- first param is no of buckets
 *       .sortBy("age") // <-- sorting is optional
 *       .saveAsTable("people_bucketed")
 *
 *
 *    # Bucketed table is then read using
 *       spark.table() method.
 *
 * 6. How spark bucketing differs from Hive bucketing is that, in spark multiple bucket files may be
 *    created per bucket, whereas in hive only one bucket file per bucket.
 *
 *    Total no of bucket files = no of partition * no of buckets
 *
 * 7. Whether a table is bucketed or not can be told by DESCRIBE EXTENDED <tableName> command.
 *
 */
object SparkBucketing {

  def main(args: Array[String]): Unit = {
    Logger.getLogger("org").setLevel(Level.ERROR)

    val spark = SparkSession.builder()
      .appName("SparkBucketing")
      .master("local[*]")
      .getOrCreate()
      
    /*
     * This is done just to cleanup spark-warehouse directory, so that this program
     * can be run multiple times, otherwise second time you run the program, you get
     * spark analysis exception saying the "associated location already exists"  
     */
    spark.conf.set("spark.sql.legacy.allowCreatingManagedTableUsingNonemptyLocation","true")

    /*
     *  For demo, we will be using spark.range to create very large type of dataset.
     *
     *  spark.range(end:Long) --> Creates a Dataset with a single LongType column named id,
     *  containing elements in a range from 0 to end (exclusive) with step value 1
     */
    val large_dataset: Dataset[java.lang.Long] = spark.range(10e6.toLong) // 10,000,000
    val huge_dataset: Dataset[java.lang.Long] = spark.range(10e7.toLong) // 100,000,000 rows
    println("large_dataset partition no " + large_dataset.rdd.getNumPartitions) // 4
    println("huge_dataset partition no " + huge_dataset.rdd.getNumPartitions) // 4

    /*
     * Saving dataset as bucketed table on id as bucket column with 4 bucket
     */
    large_dataset.write
      .bucketBy(4, "id") // <-- 4 buckets on id column
      .sortBy("id") // <-- optionally. Used to sort buckets
      .mode(SaveMode.Overwrite)
      .saveAsTable("large")

    huge_dataset.write
      .bucketBy(4, "id")
      .sortBy("id")
      .mode(SaveMode.Overwrite)
      .saveAsTable("huge")

    /*
     * At this point, after saving dataset in bucketed table, you may see two folders
     * created in spark-warehouse location
     *    |-- huge
     *    |-- large
     *
     * Each of these folder stores 16 bucket files in parquet format. How 16 ? =>
     *    No of partitions(4) * no of buckets (4) => 16
     *
     */

    /*
     * Read bucketed table using spark.table()
     */
    val bucketed_large_table = spark.table("large")
    val bucketed_huge_table = spark.table("huge")

    /*
     * trigger execution of the join query using foreach
     */
    bucketed_huge_table.join(bucketed_large_table, "id").foreach(_ => ())

    /*
     * Open the spark-UI: with port 4040. go to SQL tab and take a look to "foreach at SparkBucketing" Query.
     * 
     * You can notice in the UI that, the above join query of the bucketed tables shows no Exchange stage in physical plan,
     * because shuffling has already been done at the time of writing dataset.
     * 
     * Note - To keep the spark UI open for this program, uncomment the below given code
     * 
     *   while(true){}
     */
    
  }

}