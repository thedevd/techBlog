package com.thedevd.sparkexamples.sparksql

import org.apache.log4j.Logger
import org.apache.log4j.Level
import org.apache.spark.sql.SparkSession
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.Row
import org.apache.spark.sql.types.StructType
import org.apache.spark.sql.types.StructField
import org.apache.spark.sql.types.IntegerType
import org.apache.spark.sql.types.StringType
import org.apache.spark.sql.types.DoubleType

/*
 * In this, we will read /sparksql/student_marks.txt and select only the subject
 * where score is greater than 70 using DataFrame and DataSet. And then we
 * will look what could be wrong at runtime when opted for DataFrame and how dataset
 * help us in that case.
 */
object DataFrameVsDataSet {

  def main(args: Array[String]): Unit = {
    Logger.getLogger("org").setLevel(Level.ERROR)

    val spark = SparkSession.builder()
      .appName("DataFrameVsDataSet")
      .master("local[*]")
      .getOrCreate()

    val empRdd: RDD[Row] = spark.sparkContext.textFile(getClass.getResource("/sparksql/student_marks.txt").getPath)
      .map(line => {
        val split = line.split(",")
        Row(split(0).toInt, split(1), split(2), split(3).toDouble)
      })

    val schema = new StructType()
      .add(StructField("id", IntegerType, false))
      .add(StructField("name", StringType, false))
      .add(StructField("subject", StringType, true))
      .add(StructField("score", DoubleType, true))

    // ---------- DataFrame
    import org.apache.spark.sql.functions._
    val emp_df = spark.createDataFrame(empRdd, schema)
    // val emp_subject_with_more_than_70 = emp_df.filter("score > 70")  // sql style
    emp_df.filter(emp_df.col("score").gt(70)).show() // expression builder style

    // DataSet
    import spark.implicits._ // this import is needed to use encoders in as[]
    val emp_ds = emp_df.as[StudentReportCard]
    emp_ds.filter(_.score > 70).show()

    // ####### Now lets see difference b/w DataFrame and DataSet
    /*
     * RDD (since beginning) ---> DataFrame (v1.3) -----> DataSet(v1.6)
     * DataSet is basically improvement to DataFrame API with type-safety feature,
     * that is very much helpful for a developer, why because in DataFrame, we
     * select the column names via string alias, so there might be chance of
     * using wrong/nonexistent column name (df.select(“nonExistentColumn”)) or wrong type while matching something
     * and this which will fail the program execution at runtime (which could be costly).
     * So you notice your mistake only when you run your code when using dataframe.
     *
     *  where as with DataSet, any mismatch of typed-parameters or using wrong
     *  column name will be detected at compile time (which saves developer-time and costs).
     *  That is, in dataset compiler will detect any unknown property which is not part of schema/case class.
     *
     *  Lets see this in example-
     *
     *   emp_df.filter("score > 70a") // error here - mismatch in data type (70 -> 70a)
     *   emp_df.filter(emp_df.col("scoree").gt(70)).show() // error in score column name
     *
     *   These all can be avoided using typed API of DataFrame which is DataSet
     *
     *    emp_ds.filter(_.score > 70a).show() // compiler will catch this type mismatch error
     *    emp_ds.filter(_.scoree> 70).show() // compiler will catch this column not found error
     *
     *
     *
     *    Note- With Spark 2.0, Dataset and DataFrame are unified. “DataFrame” is an alias for “Dataset[Row]”.
     *
     */

  }

  case class StudentReportCard(id: Integer, name: String, subject: String, score: Double)

}

