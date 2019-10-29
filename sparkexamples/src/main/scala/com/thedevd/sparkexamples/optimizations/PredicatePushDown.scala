package com.thedevd.sparkexamples.optimizations

import org.apache.log4j.Logger
import org.apache.log4j.Level
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.types.IntegerType
import org.apache.spark.sql.types.StringType
import org.apache.spark.sql.types.DoubleType
import org.apache.spark.sql.types.StructType

/*
 * Predicate PushDown is aka Filter pushdown.
 *
 * What is PredicatePushDown (Predicate + Push down)
 * ####################################################
 * 1. In general, a predicate is a condition/function that returns boolean (true or false).
 * 2. In SQL world, the basic idea of predicate pushdown is query optimization by reducing
 *    the amount of data to be read from source.
 *
 *    Most of SQL engine use this idea to optimize the query, where in certain parts of the
 *    SQL queries (i.e the predicate used in WHERE clause) is pushed to the source where
 *    the data lives. This optimization drastically reduce query processing time by
 *    filtering out data before further processing.
 *
 *    Benefits of this filtering data in distributed environment is -
 *    - less amount of data need to transfer over the network means low network IO.
 *    - and loading only the needed data into memory.
 *
 * Advantages of predicate pushdown in distributed model
 * ####################################################################
 * So from the above discussion we can say, predicate pushdown is actually going to
 * reduce network traffic where -
 *  1.you issue a query in one place to run against huge volume data which is stored in another place,
 *    you could end up in a lot of network traffic, which could be slow and costly.
 *    The more data you move around the longer a job takes to finish. Predictive pushdown let's you speed up that jobs.
 *
 *  2.  So to reduce network traffic, Predictivepushdown will 'push down' predicates of the query to the source where the data is stored,
 *      and thus filter out most of the data to be transfer over the network.
 *
 *  Note- some file formats such as Parquet use predicate pushdown that let you run filter operations
 *  at the file storage layer so you don't even need to read whole file from disk.
 *
 * Predicate PushDown in Spark
 * ##################################
 * 1. PushDownPredicate is part of SparkSQL catalyst optimizer.
 *
 * 2. In spark, when you execute WHERE or FILTER after loading dataset, SparkSQL will try to push the
 * where/filter predicates down to the data source and creates a optimized logical plan.
 * As result of this, when action is performed, the whole data set is not loaded, only filtered
 * data is loaded into memory. Thus this reduces the physical query plan execution time.
 *
 * In the below, example we will see, internally spark will use PushDownPredicate when using where or filter on dataset.
 * 
 * Restriction of predicate pushdown
 * #####################################
 * Predicate pushdown does not work with typed DataSet API, it only works with untyped DataSet API.
 * see the post - https://stackoverflow.com/questions/50129411/why-is-predicate-pushdown-not-used-in-typed-dataset-api-vs-untyped-dataframe-ap
 * This will see in the gotchas section
 */
object PredicatePushDown {

  def main(args: Array[String]): Unit = {

    Logger.getLogger("org").setLevel(Level.ERROR)

    val spark = SparkSession.builder()
      .appName("PredicatePushDownDemo")
      .master("local[*]")
      .getOrCreate()

    val schema = new StructType()
      .add("id", IntegerType)
      .add("name", StringType)
      .add("subject", StringType)
      .add("marks", DoubleType)

    val data = spark.read
      .schema(schema)
      .option("sep", ",")
      .csv(getClass.getResource("/sparksql/student_marks.txt").getPath)

    /*
     * Applying predicate (filter clause)
     */
    val passed_subject = data.filter("marks > 70")

    passed_subject.explain() // explain() is Used to print physical plan only, use explain(true) if want both logical and physical plan
    
    /*
     * Look at the physical execution plan (line no 93)
     *        PushedFilters: [IsNotNull(marks), GreaterThan(marks,70.0)]
     * You can see sparkSql catalyst optimizer has pushed down 'marks > 70' predicate along with default predicate IsNotNull()
     * 
     * == Physical Plan ==
     * *(1) Project [id#0, name#1, subject#2, marks#3]
     * +- *(1) Filter (isnotnull(marks#3) && (marks#3 > 70.0))
     *    +- *(1) FileScan csv [id#0,name#1,subject#2,marks#3] Batched: false, Format: CSV, 
     *         Location: InMemoryFileIndex[file:/C:/Users/Dell/mygithub/techBlog/sparkexamples/target/classes/sparksql/stu..., PartitionFilters: [], 
     *         PushedFilters: [IsNotNull(marks), GreaterThan(marks,70.0)], ReadSchema: struct<id:int,name:string,subject:string,marks:double>
     * 
     */
    
    
    // -####################### Gotchas
    /*
     * Predicate pushdown does not work with typed Dataset APIs. The reason of this is -
     * Typed transformations are black boxes, and effectively create analysis barrier for the catalyst optimizer. 
     * So projection/predicates cannot be be pushed down over typed transformation.
     * 
     * Lets see this with example
     */
    import spark.implicits._
    val student_ds = data.as[Student]
   
    /*
     *  Find all the subject where student scored passing criteria (more than 70)
     */
    
    // This will work, you have compile time safety... but it will not use predicate pushdown!!!
    val passed_typedAPI = student_ds.filter(_.marks > 70) // typed API transformation
    
    // This will work as expected and use predicate pushdown!!!, but you have no compile time safety
    val passed_untypedAPI = student_ds.filter("marks > 70") // untypedAPI transformation
    
    println("################### passed_typedAPI physical plan ")
    passed_typedAPI.explain()
    
    println("################### passed_untypedAPI physical plan ")
    passed_untypedAPI.explain()
    
    /*
     * Although student_ds.filter(_.marks > 70) and student_ds.filter("marks > 70") will produce the same result
     * but student_ds.filter(_.marks > 70) will not perform predicate Pushdown.
     * 
     * See PushedFilters list in both the cases. (The untyped API has the items in PushedFilters list -
     * 
     *  ################### passed_typedAPI physical plan 
     *  == Physical Plan ==
     *  *(1) Filter <function1>.apply
     *  +- *(1) FileScan csv [id#0,name#1,subject#2,marks#3] Batched: false, Format: CSV, 
     *  Location: InMemoryFileIndex[file:/C:/Users/Dell/mygithub/techBlog/sparkexamples/target/classes/sparksql/stu..., PartitionFilters: [], 
     *  PushedFilters: [], ReadSchema: struct<id:int,name:string,subject:string,marks:double>
     *  
     *  ################### passed_untypedAPI physical plan 
     *  == Physical Plan ==
     *  *(1) Project [id#0, name#1, subject#2, marks#3]
     *  +- *(1) Filter (isnotnull(marks#3) && (marks#3 > 70.0))
     *     +- *(1) FileScan csv [id#0,name#1,subject#2,marks#3] Batched: false, Format: CSV, 
     *     Location: InMemoryFileIndex[file:/C:/Users/Dell/mygithub/techBlog/sparkexamples/target/classes/sparksql/stu..., PartitionFilters: [], 
     *     PushedFilters: [IsNotNull(marks), GreaterThan(marks,70.0)], ReadSchema: struct<id:int,name:string,subject:string,marks:double>
     *  
     */

  }
  
  case class Student(id: Int, name: String, subject: String, marks: Double)
}