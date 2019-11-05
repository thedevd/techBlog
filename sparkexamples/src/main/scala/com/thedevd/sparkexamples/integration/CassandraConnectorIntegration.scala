package com.thedevd.sparkexamples.integration

import org.apache.log4j.Logger
import org.apache.log4j.Level
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.types.StructType
import org.apache.spark.sql.SaveMode

/*
 * Official url of Spark-Cassandra-Integration -
 *    https://github.com/datastax/spark-cassandra-connector
 *
 *
 * What is Cassandra and Why Cassandra
 * #####################################
 * 1. Cassandra is open source - distributed - NoSql database that offers high availabilty, fault tolerance
 * and high speed read/write performance.
 * 2. Most distinguishing feature of Cassandra is the high read/write performance over huge volume table data.
 *    Due to this feature it is most suitable - when you have requirement to store huge data in the form of tables
 *    and you already know the set of known queries you need to run, then Cassandra is great choice.
 *
 *    However, the downside of Cassandra is that it does not offer relational queries (Join/grouping) and
 *    ACID transaction.
 *
 * 3. Cassandra has SQL-like querying language called CQL. It is very much familiar with SQL which makes
 * cassandra easy to learn.
 * 4. Cassandra offers replication accross multiple datacenter, this results in high-availability of the data.
 * 5. In cassandra there is no master-slave architecture, each node serves as Master, so failure of one machine
 * does not affect retrieving the needed data, this results in fault-tolerance.
 *
 * Use cases where Cassandra is majorly used
 * ###############################################
 * The major use case of Cassandra is to serve as reporting layer for big data platform, where you have
 * set of known queries to be run in order to generate business reports.
 *
 * For bigdata OLTP systems which require high write performance to store transaction details,
 * Cassandra is used majorly in these areas.
 *
 *
 * Spark Cassandra connector
 * ###########################
 * spark-cassandra-connector library of com.datastax.spark provides an easy way to integrate
 * spark with cassandra.
 *
 * <dependency>
 *     <groupId>com.datastax.spark</groupId>
 *     <artifactId>spark-cassandra-connector_2.11</artifactId>
 *     <version>2.4.0</version>
 * </dependency>
 *
 * Reading and writing to Cassandra table is just like reading or writing csv files.
 * Means you just have to provide the format("org.apache.spark.sql.cassandra") and couple of required options such as
 * cassandra host, cassandra port, keyspace and table.
 *
 * This demo requires a running cassandra instance, and sparkdb.employee table created prior to running the example.
 *   create KEYSPACE sparkdb WITH replication = {'class':'SimpleStrategy', 'replication_factor':1};
 *   create TABLE sparkdb.employee (id bigint, name text, age int, city text, state text, pincode text, primary key(id));
 *   
 * In this example we will be writting /sparksql/employee.json in employee table of cassandra,
 * and in next part we will read the same data from cassandra.
 */
object CassandraConnectorIntegration {

  def main(args: Array[String]): Unit = {
    Logger.getLogger("org").setLevel(Level.ERROR)

    val spark = SparkSession.builder()
      .appName("CassandraConnectorIntegration")
      .master("local[*]")
      .getOrCreate()

    writeToCassandraTable(spark) // part1 - writing 
    readFromCassandraTable(spark) // part2 - reading

  }

  def writeToCassandraTable(spark: SparkSession) = {

    /*
     * Read employee.json using spark.read
     */
    val empDF = spark.read
      .option("multiline", true) // to handle multiline record in json
      .json(getClass.getResource("/sparksql/employee.json").getPath)

    empDF.printSchema()

    import spark.implicits._
    val empDS = empDF.as[Employee]
    val dfOut = empDS.select("id", "name", "age", "address.*") // flattened the address struct type using wildcard selection

    /*
     * Saving records into cassandra table using spark.write
     */
    dfOut.write
      .format("org.apache.spark.sql.cassandra")
      .mode(SaveMode.Overwrite)
      .option("confirm.truncate", "true") // this mode is required when using Overwrite mode
      .option("spark.cassandra.connection.host", "127.0.0.1") // cassandra host
      .option("spark.cassandra.connection.port", "9042") // cassandra port
      .option("keyspace", "sparkdb") // keyspace
      .option("table", "employee") // table
      .save()
  }

  def readFromCassandraTable(spark: SparkSession) = {

    val readingFromCassandra = spark.read
      .format("org.apache.spark.sql.cassandra")
      .option("spark.cassandra.connection.host", "127.0.0.1")
      .option("spark.cassandra.connection.port", "9042")
      .option("keyspace", "sparkdb")
      .option("table", "employee")
      .load()
      
      readingFromCassandra.printSchema()
      readingFromCassandra.show()
      /*
       * +----+---+------+-----+-------+-----+
       * |  id|age|  city| name|pincode|state|
       * +----+---+------+-----+-------+-----+
       * |1001| 36| noida| ravi| 201501|   UP|
       * |1003| 28| noida|ankit| 201201|   UP|
       * |1000| 30| noida|  dev| 201201|   UP|
       * |1002| 29|indore| atul| 485201|   MP|
       * +----+---+------+-----+-------+-----+
       */
      
      // Casting to typed DataSet API
      /*
       * import spark.implicits._
       * //val newDS = readingFromCassandra.map(row => Employee(row.getLong(0), row.getString(3), row.getInt(1).toLong, Address(row.getString(2), row.getString(5),row.getString(4))))
       * val newDS = readingFromCassandra.map(row => Employee(row.getAs("id"), row.getAs("name"), row.getAs("age"), Address(row.getAs("city"), row.getAs("state"),row.getAs("pincode"))))
       * newDS.show()
       * +----+-----+---+--------------------+
       * |  id| name|age|             address|
       * +----+-----+---+--------------------+
       * |1001| ravi| 36| [noida, UP, 201501]|
       * |1003|ankit| 28| [noida, UP, 201201]|
       * |1000|  dev| 30| [noida, UP, 201201]|
       * |1002| atul| 29|[indore, MP, 485201]|
       * +----+-----+---+--------------------+
       */
  }

  case class Address(city: String, state: String, pincode: String)
  case class Employee(id: Long, name: String, age: Long, address: Address)
}