package com.thedevd.sparkexamples.sparksql

import org.apache.log4j.Logger
import org.apache.log4j.Level
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.types.StructType
import org.apache.spark.sql.types.StringType
import org.apache.spark.sql.types.StructField
import org.apache.spark.sql.types.IntegerType
import org.apache.spark.sql.types.DoubleType
import org.apache.spark.sql.Row
import org.apache.spark.rdd.RDD

/*
 * Read /sparksql/student_marks.txt in Rdd form and try to convert the Rdd to DataFrame
 *
 */
object RddToDataFrameConversion {

  def main(args: Array[String]): Unit = {
    Logger.getLogger("org").setLevel(Level.ERROR)

    val spark = SparkSession.builder()
      .appName("RddToDataFrameConversion")
      .master("local[*]")
      .getOrCreate()

    val empRdd = spark.sparkContext.textFile(getClass.getResource("/sparksql/student_marks.txt").getPath)
      .map(line => {
        val split = line.split(",")
        (split(0), split(1), split(2), split(3))
      }) // val empRdd: RDD[(String, String, String, String)]

    // Different approaches to create DataFrame manually from an RDD
    /*
    * Method 1 - Using toDF()
    * ########################
    * 1. toDF can be accessed by after importing spark.implicits._
    * 2. toDF() is suitable for local testing, but production not recommended.
    * 3. Other limitations of using toDF are -
    * 		# Column type and nullable flag cannot be customized as there is
    * 			no such overloaded method of toDF that can take schema as param.
    *
    * 			As in the below example, all columns have been taken as String by default and nullable=true.
    * 			And this can not be customized.
    *
    * 		# toDF will not work with RDD of Row type. toDF can only work with following RDD type-
    * 			a. RDD[Int]
    * 			b. RDD[Long]
    * 			c. RDD[String]
    * 			d. RDD[T <: scala.Product]
    * 				( This means that it can work for an RDD of tuples or an RDD of case classes.
    * 				because tuples and case classes are subclasses of scala.Product)
    *
    */
    println(" -------- Method 1 - Using toDF()")
    import spark.implicits._
    val empDF_toDF = empRdd.toDF("id", "name", "subject", "marks")
    empDF_toDF.printSchema()
    empDF_toDF.show(10)

    /*
     * Method 2 - Using createDataFrame(RDD obj)
     * ###########################################
     * Benefit of using this over toDF is that, it can work with even RDD of Row type
     * and schema can be customized.
     */
    println(" -------- Method 2 - Using createDataFrame(RDD obj)")
    val empDF_withoutSchemaAndColumnName = spark.createDataFrame(empRdd)
    empDF_withoutSchemaAndColumnName.printSchema()
    empDF_withoutSchemaAndColumnName.show(10)
    /*
    +---+------+---------+---+
    | _1|    _2|       _3| _4|
    +---+------+---------+---+
    |  1|Joseph|    Maths| 83|
    |  1|Joseph|  Physics| 74|

    */
    println("---------------------------------------")
    val empDF_withoutSchemaButWithColNames = spark.createDataFrame(empRdd)
      .toDF("id", "name", "subject", "score")
    empDF_withoutSchemaButWithColNames.printSchema()
    empDF_withoutSchemaButWithColNames.show(10)
    /*
    +---+------+---------+-----+
		| id|  name|  subject|score|
		+---+------+---------+-----+
		|  1|Joseph|    Maths|   83|
		|  1|Joseph|  Physics|   74|

    */
    
    println("---------------------------------------")
    // This way requires the input rdd should be of type RDD[Row].
    /*
     * Conceptually, DataFrame is a collection of generic objects Dataset[Row], 
     * where a Row is a generic untyped JVM object.
     */
    val empRddOfRowType: RDD[Row] = empRdd.map(t => Row(t._1.toInt, t._2, t._3,t._4.toDouble))
    
    val schema = new StructType()
      .add(StructField("id", IntegerType, false))
      .add(StructField("name", StringType, false))
      .add(StructField("subject", StringType, true))
      .add(StructField("score", DoubleType, true))
      
    val empDF_withSchema = spark.createDataFrame(empRddOfRowType, schema)
    empDF_withSchema.printSchema()
   /* root
 			|-- id: integer (nullable = false)
 			|-- name: string (nullable = false)
 			|-- subject: string (nullable = true)
 			|-- score: double (nullable = true)
 		*/
    
    empDF_withSchema.show(10)
    
  }
}