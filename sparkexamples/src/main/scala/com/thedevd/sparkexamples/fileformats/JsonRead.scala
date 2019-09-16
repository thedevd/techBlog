package com.thedevd.sparkexamples.fileformats

import org.apache.log4j.Logger
import org.apache.log4j.Level
import org.apache.spark.sql.SparkSession

/*
 * 1. Spark can automatically infer the schema of a JSON dataset and load it as a Dataset[Row].
 * 2. JSON files can be in single-line or multi-line mode. So always use multiline option when
 *    loading json file into dataframe.
 * 3. Reading json file =>
 *        spark.read.json("path-here")
 *
 *  In this example we will be reading sparksql/employee.json from resource directory.
 *  The json file is mix of single line and multiline records.
 */
object JsonRead {

  def main(args: Array[String]): Unit = {
    Logger.getLogger("org").setLevel(Level.ERROR)

    val spark = SparkSession.builder()
      .appName("JsonRead")
      .master("local[*]")
      .getOrCreate()

    val emp_df = spark.read
      .option("multiline", true) // since json file having multiline records
      .json(getClass.getResource("/sparksql/employee.json").getPath)

    emp_df.printSchema()
    /*root
     *  |-- address: struct (nullable = true)
     *  |    |-- city: string (nullable = true)
     *  |    |-- pincode: string (nullable = true)
     *  |    |-- state: string (nullable = true)
     *  |-- age: long (nullable = true)
     *  |-- id: long (nullable = true)
     *  |-- name: string (nullable = true)
     */
    
    //emp_df.show()
    import spark.implicits._
    val emp_ds = emp_df.as[Employee] // Using encoders to convert dataframe to dataset
    emp_ds.filter(_.address.state == "UP").show()
   /* +-------------------+---+----+-----+
    * |            address|age|  id| name|
    * +-------------------+---+----+-----+
    * |[noida, 201201, UP]| 30|1000|  dev|
    * |[noida, 201501, UP]| 36|1001| ravi|
    * |[noida, 201201, UP]| 28|1003|ankit|
    * +-------------------+---+----+-----+*/

  }
  
  case class Employee(id: Long, name: String, age: Long, address: Address)
  case class Address(city: String, state: String, pincode: String)
}