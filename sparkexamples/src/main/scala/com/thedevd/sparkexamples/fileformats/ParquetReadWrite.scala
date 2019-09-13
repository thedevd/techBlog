package com.thedevd.sparkexamples.fileformats

import org.apache.log4j.Logger
import org.apache.log4j.Level
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.types.StructType
import org.apache.spark.sql.types.StructField
import org.apache.spark.sql.types.IntegerType
import org.apache.spark.sql.types.StringType
import org.apache.spark.sql.types.DoubleType
import org.apache.spark.sql.functions._
import org.apache.spark.sql.Dataset
import org.apache.spark.sql.Row
import org.apache.commons.io.FileUtils
import java.io.File
import org.apache.spark.sql.SaveMode

/*
 * 1. Parquet is widely adopted file format in big data used in Spark, Hive, Impala, Pig.
 * 2. Parquet stores the binary data in a column-oriented way, where the values of each and every column are
 *    stored alltogether and this allow better compression ratio.
 * 3. It is especially good for the queries which read columns from a “wide” (with many columns) table
 *    since only needed columns are read and the IO is minimized.
 *
 * 4. The difference b/w Parquet and Avro is -
 *    # Parquet stores data in column-oriented way whereas Avro stores in row-oriented way.
 *    # Parquet is best choice for read-intensive operations whereas Avro is for write-intensive operation.
 *
 * Note - Unlike avro, to work with Parquet files in spark, we do not need to specify any extra dependency.
 * Spark by default has provided support for Parquet files.
 *
 * Write DF to parquet file
 * #########################
 *   df.write.parquet("C:/tmp/person.parquet")
 *
 * Write DF to partitioned parquet File
 * #####################################
 *   df.write.partitionBy("gender","salary")
        .parquet("C:/tmp/person-partitioned.parquet")
 *
 * Read parquet file to DF
 * #########################
 *   spark.read.parquet("/tmp/person.parquet")
 *
 * Read partitioned parquet file
 * ###############################
 *   spark.read.parquet("C:/tmp/person-partitioned.parquet/gender=M")
 *   
 * 
 * How to use specific compression with Parquet
 * #############################################
 * 1. We can set the compression codec when writting parquet file-
 *   df.write.option("compression","none").parquet()
 *   
 * 2. Or we can also set this configuration to spark sqlContext
 * 
 *    spark.sqlContext.setConf("spark.sql.parquet.compression.codec", "snappy")
 * 
 * Acceptable values include: none, uncompressed, snappy, gzip, lzo, brotli, lz4, zstd.
 * snappy is default compression.
 */
object ParquetReadWrite {

  def main(args: Array[String]): Unit = {
    Logger.getLogger("org").setLevel(Level.ERROR)

    val spark = SparkSession.builder()
      .appName("ParquetReadWrite")
      .master("local[*]")
      .getOrCreate()

    val schema = new StructType()
      .add(StructField("id", IntegerType, false)) // not-nullable. This will converted to true in parquet
      .add(StructField("name", StringType, false)) // not-nullable. This will converted to true in parquet
      .add(StructField("subject", StringType, true))
      .add(StructField("score", DoubleType, true))

    val student_df = spark.read.schema(schema)
      .option("delimiter", ",")
      .csv(getClass.getResource("/sparksql/student_marks.txt").getPath)

    val report_card_df = student_df.withColumn("result", when(col("score") >= 70, "pass").otherwise("fail"))
    report_card_df.show(2)
    /* +---+------+-------+-----+------+
    *  | id|  name|subject|score|result|
    *  +---+------+-------+-----+------+
    *  |  1|Joseph|  Maths| 83.0|  pass|
    *  |  1|Joseph|Physics| 74.0|  pass|
    *  +---+------+-------+-----+------+*/

    /*
     * // setting compression codec here to sqlContext
     * spark.sqlContext.setConf("spark.sql.parquet.compression.codec", "gzip")
     */
    
    /*
     * Write to parquet file
     */
    writeToParquetFile(report_card_df)

    /*
     * Write to parquet file partitioned by result column
     */
    writeToPartitionedParquetFile(report_card_df)

    /*
     * Read parquet file
     */
    readParquetFile(spark)

    /*
     * Read partitioned parquet file
     */
    readPartitionedParquetFile(spark)

    /*
     * Read parquet file using SparkSql
     */
    readParquetWithSparkSql(spark)

  }

  def writeToParquetFile(df: Dataset[Row]) = {

    val outputDir = "C:/temp/student_report_card_parquet/result.parquet"
    cleanOutputDirectory(outputDir)

    df.write.parquet(outputDir)
    // df.write.option("compression","gzip").mode(SaveMode.Overwrite).parquet(outputDir)
  }

  def writeToPartitionedParquetFile(df: Dataset[Row]) = {

    val outputDir = "C:/temp/student_report_card_parquet/result-partitioned.parquet"
    cleanOutputDirectory(outputDir)

    df.write.partitionBy("result").parquet(outputDir)
    /*
     * Here we have partitioned  data by result column which has two values- pass and fail.
     * So if you look at output directory C:/temp/student_report_card_parquet/result-partitioned.parquet, you will see two more subdirectories there-
     *    1. result=fail
     *    2. result=pass
     *
     *  	So here if you will read the data by same partition using where clause, then spark will know exactly which directory to lookup,
     *    this improves the read performance.
     */
  }

  def readParquetFile(spark: SparkSession) = {
    val parquet_df = spark.read.parquet("C:/temp/student_report_card_parquet/result.parquet")
    parquet_df.printSchema()
    /*root
     *  |-- id: integer (nullable = true)
     *   |-- name: string (nullable = true)
     *    |-- subject: string (nullable = true)
     *     |-- score: double (nullable = true)
     *      |-- result: string (nullable = true)
     *
     * Writing Spark DataFrame to Parquet format preserves the column names and data types,
     * But all columns are automatically converted to be nullable for compatibility reasons.
     *
     **/

    parquet_df.show(2)
    /* +---+------+-------+-----+------+
    * | id|  name|subject|score|result|
    * +---+------+-------+-----+------+
    * |  1|Joseph|  Maths| 83.0|  pass|
    * |  1|Joseph|Physics| 74.0|  pass|
    * +---+------+-------+-----+------+*/
  }

  def readPartitionedParquetFile(spark: SparkSession) = {
    spark.read
      .parquet("C:/temp/student_report_card_parquet/result-partitioned.parquet/result=fail")
      .show(2)
    /* +---+-----+-------+-----+
     * | id| name|subject|score|
     * +---+-----+-------+-----+
     * |  2|Jimmy|  Maths| 69.0|
     * |  2|Jimmy|Physics| 62.0|
     * +---+-----+-------+-----+*/
  }

  def readParquetWithSparkSql(spark: SparkSession) = {
    val result_df = spark.read.parquet("C:/temp/student_report_card_parquet/result.parquet")
    result_df.createOrReplaceTempView("result_from_parquet")
    spark.sql("select * from result_from_parquet where result='fail'").show(5)
    /* +---+-----+---------+-----+------+
     * | id| name|  subject|score|result|
     * +---+-----+---------+-----+------+
     * |  2|Jimmy|    Maths| 69.0|  fail|
     * |  2|Jimmy|  Physics| 62.0|  fail|
     * |  3| Tina|Chemistry| 68.0|  fail|
     * |  6| Cory|    Maths| 56.0|  fail|
     * |  6| Cory|  Physics| 65.0|  fail|
     * +---+-----+---------+-----+------+*/
  }

  def cleanOutputDirectory(dir: String) = {
    FileUtils.deleteDirectory(new File(dir))
  }
}