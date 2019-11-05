## I. Spark Integration with Cassandra
### What is Cassandra and Why Cassandra
   1. Cassandra is open source - distributed - NoSql database that offers high availabilty, fault tolerance and high speed read/write performance.
   2.  Most distinguishing feature of Cassandra is the high read/write performance over huge volume table data.
   Due to this feature it is most suitable - when you have requirement to store huge data in the form of tables
   and you already know the set of known queries you need to run, then Cassandra is great choice.
   ```
   However, the downside of Cassandra is that it does not offer relational queries (Join/grouping) and
   ACID transaction.
   ```
   3. Cassandra has SQL-like querying language called CQL. It is very much familiar with SQL which makes cassandra easy to learn.
   4. Cassandra offers replication across multiple datacenter, this results in high-availability of the data.
   5. In cassandra there is no master-slave architecture, each node serves as Master, so failure of one machine does not affect retrieving the needed data, this results in fault-tolerance.
   
 ### Use cases where Cassandra can be used
 1. The major use case of Cassandra is to serve as reporting layer for big data platform, where you have set of known queries to be run in order to generate some business reports.
 2. For bigdata OLTP systems where high write performance is required to store transaction details at very fast speed, Cassandra is used majorly in these areas.
 
 ###  Spark Cassandra connector
 1.  spark-cassandra-connector library from com.datastax.spark provides an easy way to integrate spark with Cassandra. Just add spark-cassandra-connector dependency to your project and you are all set to go-
 ```
 <dependency>
   <groupId>com.datastax.spark</groupId>
   <artifactId>spark-cassandra-connector_2.11</artifactId>
   <version>2.4.0</version>
 </dependency>
```
2. Reading and writing to Cassandra table is just like reading or writing csv files. Means you just have to provide the source/sink format using option format("org.apache.spark.sql.cassandra") along with couple of required options such as spark.cassandra.connection.host, spark.cassandra.connection.port, keyspace and table.

> code snippet to write- 
```javascript
   dfOut.write
      .format("org.apache.spark.sql.cassandra")
      .mode(SaveMode.Overwrite)
      .option("confirm.truncate", "true") // this mode is required when using Overwrite mode
      .option("spark.cassandra.connection.host", "127.0.0.1") // cassandra host
      .option("spark.cassandra.connection.port", "9042") // cassandra port
      .option("keyspace", "sparkdb") // keyspace
      .option("table", "employee") // table
      .save()
```

> code snippet to read from cassandra table
```javascript
    val readingFromCassandra = spark.read
      .format("org.apache.spark.sql.cassandra")
      .option("spark.cassandra.connection.host", "127.0.0.1")
      .option("spark.cassandra.connection.port", "9042")
      .option("keyspace", "sparkdb")
      .option("table", "employee")
      .load()
```

## II. Spark Integration with JDBC Sources
1. Spark can easily be integrated with any JDBC source (MySql, Oracle, Postgres) using specific jdbc connector library.
   For example to connect with Mysql use this library -
   ```
   <dependency>
     <groupId>mysql</groupId>
     <artifactId>mysql-connector-java</artifactId>
     <version>8.0.15</version>
   </dependency>
   ```
   
 2. Reading from any jdbc source or writing to any jdbc sink is very straight-forward. You just have to provide jdbc source/sink type using option format("jdbc") along with other required options such as name of driver, url, user, password and the dbtable name. Thats all.
 
 > code snippet to write into mysql table
 ```javascript
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
```

> code snipped to read from mysql table
```javascript
  val score_result = spark.read
      .format("jdbc")
      .option("driver", "com.mysql.cj.jdbc.Driver")
      .option("url", "jdbc:mysql://127.0.0.1:33306/sparkdb")
      .option("user", "root")
      .option("password", "root")
      .option("dbtable", "student_score")
      .load()
```
