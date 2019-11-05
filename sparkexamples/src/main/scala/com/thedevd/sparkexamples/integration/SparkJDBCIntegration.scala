package com.thedevd.sparkexamples.integration

import org.apache.log4j.Logger
import org.apache.log4j.Level
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.types.StructField
import org.apache.spark.sql.types.StructType
import org.apache.spark.sql.types.IntegerType
import org.apache.spark.sql.types.StringType
import org.apache.spark.sql.types.DoubleType
import org.apache.spark.sql.SaveMode

/*
 * 1. Spark can easily be integrated with any JDBC source (MySql, Oracle, Postgres) using 
 * specific jdbc connector library. 
 * For example to connect with Mysql use this library -
 * 
 *   <dependency>
 *   			<groupId>mysql</groupId>
 *   			<artifactId>mysql-connector-java</artifactId>
 *   			<version>8.0.15</version>
 *   </dependency>
 *   
 * 2. Reading from any jdbc source or writing to any jdbc sink is very straight-forward. 
 * You just have to provide format("jdbc") as jdbc and provide other required options such as
 * name of driver, url, user, password and the dbtable name. Thats all.
 * 
 * This example requires a running mysql instance. 
 * Create a database and user to access that database, then you are all set to go.
 *      create database sparkdb;
 */
object SparkJDBCIntegration {

  def main(args: Array[String]): Unit = {
    Logger.getLogger("org").setLevel(Level.ERROR)

    val spark = SparkSession.builder()
      .appName("SparkJDBCIntegration")
      .master("local[*]")
      .getOrCreate()

    writeToMysqlUsingJdbcConnector(spark)
    readFromMysqlUsingJdbcConnector(spark)
  }

  def writeToMysqlUsingJdbcConnector(spark: SparkSession) = {

    val schema = new StructType()
      .add("id", IntegerType, false)
      .add("name", StringType, false)
      .add("subject", StringType, false)
      .add("marks", DoubleType, false)

    val student_scorecard_df = spark.read
      .schema(schema)
      .csv(getClass.getResource("/sparksql/student_marks.txt").getPath)

    import spark.implicits._
    import org.apache.spark.sql.functions._
    val avg_score = student_scorecard_df
      .groupBy("id", "name")
      .agg(avg("marks").alias("avg_score"))
      .withColumn("result", when($"avg_score" > 70, "pass").otherwise("fail"))

    avg_score.show(2)
    /*
     * +---+------+---------+------+
     * | id|  name|avg_score|result|
     * +---+------+---------+------+
     * |  8|  Juan|     64.0|  fail|
     * |  1|Joseph|     82.5|  pass|
     * +---+------+---------+------+
     */

    avg_score.write
      .format("jdbc") // format tell type of sink to output
      .mode(SaveMode.Overwrite)
      .option("truncate", true) // this option is must when using Overwrite SaveMode
      .option("driver", "com.mysql.cj.jdbc.Driver") // com.mysql.jdbc.Driver' is deprecated in new version of mysql connector
      .option("url", "jdbc:mysql://127.0.0.1:33306/sparkdb") // db url
      .option("user", "root") // username
      .option("password", "root") // password
      .option("dbtable", "student_score") // table in db
      .save()
      
      /*
       * It is to be noted that if table does not exists, it will be created on the fly.
       */
  }

  def readFromMysqlUsingJdbcConnector(spark: SparkSession) = {

    val score_result = spark.read
      .format("jdbc")
      .option("driver", "com.mysql.cj.jdbc.Driver")
      .option("url", "jdbc:mysql://127.0.0.1:33306/sparkdb")
      .option("user", "root")
      .option("password", "root")
      .option("dbtable", "student_score")
      .load()
      
    println("data read from mysql table student_score ########################")
    score_result.show()
    /*
     * +---+---------+---------+------+
     * | id|     name|avg_score|result|
     * +---+---------+---------+------+
     * |  8|     Juan|     64.0|  fail|
     * |  1|   Joseph|     82.5|  pass|
     * |  3|     Tina|     76.5|  pass|
     * |  6|     Cory|     65.0|  fail|
     * |  7|Jackeline|     76.5|  pass|
     * |  2|    Jimmy|     77.0|  pass|
     * |  4|   Thomas|    86.25|  pass|
     * +---+---------+---------+------+
     */

  }
}