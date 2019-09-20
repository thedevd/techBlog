package com.thedevd.sparkexamples.partitions

import org.apache.log4j.Logger
import org.apache.log4j.Level
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.types.StructType
import org.apache.spark.sql.types.StructField
import org.apache.spark.sql.types.IntegerType
import org.apache.spark.sql.types.StringType
import org.apache.spark.sql.types.DoubleType
import org.apache.spark.sql.SaveMode

/*
 * 1. There is spark conf property related to dataframes -
 *   spark.sql.shuffle.partitions => controls the number of partitions for DataFrames (default is 200)
 *
 * 2. spark.sql.shuffle.partitions property  comes into picture only when you perform shuffling
 *    related operations on dataframe like joins, aggregations or repartition(<based_on_column>)
 *
 * 3. Default value of spark.sql.shuffle.partitions is 200. But you can change it using -
 *    sqlContext.setConf("spark.sql.shuffle.partitions", 300)
 *
 * But in which situation, changing this value is useful
 * ######################################################
 * spark.sql.shuffle.partitions can play very important role when you have memory congestion issue or you see this error message-
 * " java.lang.IllegalArgumentException: Size exceeds Integer.MAX_VALUE "
 *
 * The reason for this error is that in Spark, you cannot have shuffle block greater than 2GB.
 * Shuffle block — data transferred across stages between executors.
 *
 * This error especially comes for SparkSQl (when you perform join or aggregations on large dataframes)
 * when shuffling is done. So what can be do to handle this situation is -
 * 1. Increase the number of shuffle partitions (thereby, reducing the average partition size)
 *    by increasing the value of spark.sql.shuffle.partitions for Spark SQL.
 * 2. Or by calling repartition() and increase partition of dataframe.
 *
 *
 * How this is different of spark.default.parallelism
 * ####################################################
 * The value of spark.default.parallelism is equal to total no of cores in cluster and it is used
 * to decide default no of partitions of RDD.
 *
 * spark.sql.shuffle.partition is used to decide no of partition for DataFrame only when
 * shuffling operation is done.
 */
object SparkSqlShufflePartitions {

  def main(args: Array[String]): Unit = {
    Logger.getLogger("org").setLevel(Level.ERROR)

    val spark = SparkSession.builder()
      .appName("SparkSqlShufflePartitions")
      .master("local[*]")
      .getOrCreate()

    val schema = new StructType()
      .add(StructField("id", IntegerType, true))
      .add(StructField("name", StringType, true))
      .add(StructField("subject", StringType, true))
      .add(StructField("marks", DoubleType, true))

    val student_marks_df = spark.read.schema(schema)
      .option("delimiter", ",")
      .csv(getClass.getResource("/sparksql/student_marks.txt").getPath)

    println("Initial partitions of dataframe upon reading csv file: " + student_marks_df.rdd.getNumPartitions) // 1
    println("default value of spark.sql.shuffle.partitions: " + spark.conf.get("spark.sql.shuffle.partitions")) // 200

    import org.apache.spark.sql.functions._
    val max_marks_df = student_marks_df.groupBy("name").agg(max("marks"))
    println("Dataframe partitions on applying aggregations: " + max_marks_df.rdd.getNumPartitions) // 200

    /*
     * Dataframe has a modified repartition() method that takes column as parameter.
     *
     * Now if you repartition the dataframe using column name,
     * this will leads total no of partition to 200 according to spark.sql.shuffle.partitions, so in this case
     * if dataframe is small, then some of the partitions will be empty and
     * this reduces performance due to overhead of reading empty partitions.
     *
     * So it is important to repartition the dataframe with good no of partitions
     * using another overloaded repartition() method that takes number as argument.
     *
     */
    val df_repartitionbyColumn = student_marks_df.repartition(student_marks_df("subject"))
    println("Dataframe partitions on repartitioning with column : " + df_repartitionbyColumn.rdd.getNumPartitions) // 200

    val df_good_partitioned = student_marks_df.repartition(4, student_marks_df("subject"))
    println(df_good_partitioned.rdd.getNumPartitions) // 4
    println("partitioner used: " + df_good_partitioned.rdd.partitioner) // None

    df_good_partitioned.write
      .format("csv")
      .mode(SaveMode.Overwrite)
      .save("C:/temp/dfpartitionbycolumnandnumber") 
    /*
     * You can notice that, at output directory spark will created only 3 csv files (at my machine)
     * The reason is spark using no partitioner and out if 4, one partition is becoming empty.
     */
  }
}