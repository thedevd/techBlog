package com.thedevd.sparkexamples.fileformats

import org.apache.log4j.Logger
import org.apache.log4j.Level
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.types.StructType
import org.apache.spark.sql.types.StructField
import org.apache.spark.sql.types.IntegerType
import org.apache.spark.sql.types.StringType
import org.apache.spark.sql.types.DoubleType
import org.apache.spark.sql.Dataset
import org.apache.spark.sql.Row
import org.apache.commons.io.FileUtils
import java.io.File
import org.apache.spark.sql.functions._

/*
 * 1. Avro is row-based storage format plus serialization/deserialization framework.
 *    It serializes the data in compact binary format using a schema.
 * 
 * 2. Avro purely relies on a schema. And avro schema are defined with JSON internally.
 * When Avro data is stored in a file, its schema is also stored with it. It means
 * When Avro data is read, the schema that is used for writing is always present.
 *
 * 3. Spark SQL supports reading and writing Spark DataFrames from and to a Avro data file
 *
 * Writing dataframe to avro format
 * #################################
 *   df.write.format("avro").save("person.avro")
 *
 * Writing Partition avro Data
 * ################################
 *  df.write.partitionBy("dob_year","dob_month)
        .format("avro").save("person_partition.avro")
 *
 * Reading avro data file
 * #################################
 *   val personDF = spark.read.format("avro").load("person.avro")
 *
 * Reading Partition avro Data
 * #################################
 *  spark.read
 *    .format("avro")
 *    .load("person_partition.avro")
 *    .where(col("dob_year") === 2010)
 *    .show()
 *    
 * Reading avro data file with SparkSQL
 * #####################################
 *  spark.sqlContext.sql("CREATE TEMPORARY VIEW person USING avro OPTIONS (path \"C:/tmp/person.avro\")")
 *  spark.sqlContext.sql("select * from person").show()
 */
object AvroReadWrite {

  def main(args: Array[String]): Unit = {
    Logger.getLogger("org").setLevel(Level.ERROR)

    val spark = SparkSession.builder()
      .appName("AvroReadWrite")
      .master("local[*]")
      .getOrCreate()

    val schema = new StructType()
      .add(StructField("id", IntegerType, false))
      .add(StructField("name", StringType, false))
      .add(StructField("subject", StringType, false))
      .add(StructField("score", DoubleType, false))

    val student_score_card_df = spark.read.schema(schema)
      .option("delimiter", ",") // best practice to always specify delimiter
      .csv(getClass.getResource("/sparksql/student_marks.txt").getPath)

    import spark.implicits._
    //import org.apache.spark.sql.functions._  ---> this is imported at top
    val result_df = student_score_card_df.withColumn("result", when($"score" >= 70, "pass").otherwise("fail"))
    result_df.show(5)
    /* +---+------+---------+-----+------+
    * | id|  name|  subject|score|result|
    * +---+------+---------+-----+------+
    * |  1|Joseph|    Maths| 83.0|  pass|
    * |  1|Joseph|  Physics| 74.0|  pass|
    * |  1|Joseph|Chemistry| 91.0|  pass|
    * |  1|Joseph|  Biology| 82.0|  pass|
    * |  2| Jimmy|    Maths| 69.0|  fail|
    * */

    /*
     * Write Avro file
     */
    writeToAvroFormat(result_df)

    /*
     * Write Partitioned avro file
     */
    writeToPartitionedAvroFormat(result_df)

    /*
     * Read Avro file
     */
    readAvroFormat(spark)

    /*
     * Read partitioned avro file
     */
    readPartitionedAvroFormat(spark)

    /*
     * Read avro file using SparkSql
     */
    readAvroUsingSparkSql(spark)

  }

  def writeToAvroFormat(df: Dataset[Row]) = {

    val outputDir = "C:/temp/student_report_card_avro/result.avro"
    cleanupOutputDirectory(outputDir)

    // Converting a normal data into an Avro file is called a Serialization.
    df.write.format("com.databricks.spark.avro").save(outputDir)
  }

  def writeToPartitionedAvroFormat(df: Dataset[Row]) = {

    val outputDir = "C:/temp/student_report_card_avro/result-partitioned.avro"
    cleanupOutputDirectory(outputDir)

    // Writing avro data by partition improves performance on reading by reducing Disk I/O.
    df.write.partitionBy("result").format("com.databricks.spark.avro").save(outputDir)

    /*
     * Here we have partitioned avro data by result column which has two values- pass and fail.
     * So if you look at output directory C:/temp/student_report_card/result-partitioned.avro, you will see two more subdirectories there-
     *    1. result=fail
     *    2. result=pass
     *
     *  	So here if you will read the data by same partition, then spark will know exactly which directory to lookup,
     *    this improves the read performance.
     */
  }

  def readAvroFormat(spark: SparkSession) = {

    spark.read.format("avro")
      .load("C:/temp/student_report_card_avro/result.avro")
      .show(5)
    /* +---+------+---------+-----+------+
    * | id|  name|  subject|score|result|
    * +---+------+---------+-----+------+
    * |  1|Joseph|    Maths| 83.0|  pass|
    * |  1|Joseph|  Physics| 74.0|  pass|
    * |  1|Joseph|Chemistry| 91.0|  pass|
    * |  1|Joseph|  Biology| 82.0|  pass|
    * |  2| Jimmy|    Maths| 69.0|  fail|
    * +---+------+---------+-----+------+*/
  }

  def readPartitionedAvroFormat(spark: SparkSession) = {

    spark.read.format("avro")
      .load("C:/temp/student_report_card_avro/result-partitioned.avro")
      .where(col("result") === "pass") // the condition of partitioned column
      .show(5)

    /* +---+------+---------+-----+------+
     * | id|  name|  subject|score|result|
     * +---+------+---------+-----+------+
     * |  1|Joseph|    Maths| 83.0|  pass|
     * |  1|Joseph|  Physics| 74.0|  pass|
     * |  1|Joseph|Chemistry| 91.0|  pass|
     * |  1|Joseph|  Biology| 82.0|  pass|
     * |  2| Jimmy|Chemistry| 97.0|  pass|
     * +---+------+---------+-----+------+*/

  }

  def readAvroUsingSparkSql(spark: SparkSession) = {

    spark.sqlContext.sql("CREATE TEMPORARY VIEW result USING avro OPTIONS (path \"C:/temp/student_report_card_avro/result.avro\")")
    spark.sqlContext.sql("SELECT * FROM result").show(2)
    /*  +---+------+-------+-----+------+
     *  | id|  name|subject|score|result|
     *  +---+------+-------+-----+------+
     *  |  1|Joseph|  Maths| 83.0|  pass|
     *  |  1|Joseph|Physics| 74.0|  pass|
     *  +---+------+-------+-----+------+*/
  }

  def cleanupOutputDirectory(dir: String) = {
    FileUtils.deleteDirectory(new File(dir))
  }
}