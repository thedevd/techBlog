package com.thedevd.sparkexamples.differences

import org.apache.log4j.Logger
import org.apache.log4j.Level
import org.apache.spark.sql.SparkSession

/*
 * 1. fold() and reduce() in spark are action.
 * 2. fold() is similar to reduce() except that it takes a 'Zero value'(also called initial value)
 *    which will participate as initial value to the aggregate operation on each Partition.
 *
 *    foldByKey() is very similar to fold() except that it operates on a Pair RDD
 *
 * 3. Disadvantage : disadvantage of both reduce() & fold() is, the return type should be the same of the RDD element type.
 *    so aggregate() can be used to avoid this limitation.
 *
 * Lets try to find sum using both, then will look internal of fold.
 * Note - As per the Spark documentation, the initial value in fold should be neutral when using associative function.
 * It means does not matter how many time initial value is added to final result, it should not affect the final result.
 * For example, 0 in the case of addition, 1 in the case of multiplication, empty list in the case of list concatenation.
 *
 */
object FoldVsReduce {

  def main(args: Array[String]): Unit = {
    Logger.getLogger("org").setLevel(Level.ERROR)

    val spark = SparkSession.builder()
      .appName("FoldVsReduce")
      .master("local[*]")
      .getOrCreate()

    val rdd = spark.sparkContext.parallelize(Array(1, 2, 3, 4, 5), 3)
    // we have three partition - Array(Array(1), Array(2, 3), Array(4, 5))

    val sum_using_reduce = rdd.reduce((aggregateValue, value) => aggregateValue + value)
    val sum_using_fold = rdd.fold(0)((aggregateValue, value) => aggregateValue + value)

    println("sum_using_reduce: " + sum_using_reduce)
    println("sum_using_fold: " + sum_using_fold)

    // #### Now lets see what happens when we have non-neutral value in fold
    val finalResultWhenNonNeutralValue = rdd.fold(5)(_ + _)
    println("finalResultWhenNonNeutralValue: " + finalResultWhenNonNeutralValue)
    /*
     * The output of above will be 35. How lets see this =>
     *
     * The zeroValue is in this case added four times
     * (one for each partition, plus one when combining the results from the partitions).
     *
     * we have three partition -
     * (1) (2,3) (4,4)
     *
     * So the result is: zeroValue 5 will be added in each partition and then after combining results.
     * (5 + 1) + (5 + 2 + 3) + (5 + 4 + 5) + 5 // (extra one for combining results)
     *
     */

    // #### another example
    val newRdd = rdd.repartition(8)
    // now have 8 partitions - Array(Array(5), Array(), Array(2), Array(3), Array(), Array(), Array(1), Array(4))
    val new_result = newRdd.fold(5)(_ + _)
    /*
     * The output of new_result will be 60. How lets see this =>
     * We have 8 partition now, some of them will be empty
     * (5), (), (2), (3), (), (), (1), (4)
     *
     * so the result is: zeroValue 5 will be added in each partition and then after combining results.
     * (5+5), (5), (2+5), (3+5), (5), (5), (1+5), (4+5) + 5 (extra 5 for combining results)
     *
     */

  }
}