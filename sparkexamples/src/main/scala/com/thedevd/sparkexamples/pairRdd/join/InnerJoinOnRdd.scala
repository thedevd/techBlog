package com.thedevd.sparkexamples.pairRdd.join

import org.apache.log4j.Logger
import org.apache.log4j.Level
import org.apache.spark.sql.SparkSession
import org.apache.commons.io.FileUtils
import java.io.File

/*
 * Read the emp details from /rdd/join/emp.txt and department details from /rdd/join/dept.txt
 *
 * emp.txt has the details of dept_id which is acting as foreign key for dept_id in dept.txt.
 *
 * Write a spark program to display the emp_details with their dept_name i.e.
 * emp_id,emp_name,dept_id,dept_name
 *
 */
object InnerJoinOnRdd {

  def main(args: Array[String]): Unit = {
    Logger.getLogger("org").setLevel(Level.OFF)

    val spark = SparkSession.builder()
      .appName("InnerJoinOnRdd")
      .master("local[*]")
      .getOrCreate()

    val empRdd = spark.sparkContext.textFile(getClass.getResource("/rdd/join/emp.txt").getPath)
    val deptRdd = spark.sparkContext.textFile(getClass.getResource("/rdd/join/dept.txt").getPath)

    val empPairRdd = empRdd.filter(!_.contains("dept_id")) // removing header from input
      .map(empData => {
        val split = empData.split(",")
        (split(0), split(1), split(2)) // (emp_id,emp_name,dept_id)
      })

    val deptPairRdd = deptRdd.filter(!_.contains("dept_id"))
      .map(deptData => {
        val split = deptData.split(",")
        (split(0), split(1)) // (dept_id, dept_name)
      })

    val modifiedEmp = empPairRdd.keyBy(_._3) //  (dept_id, (emp_id,emp_name,dept_id))
    val modifiedDept = deptPairRdd.keyBy(_._1) // (dept_id, (dept_id, (dept_name))

    val empJoinDeptOnId = modifiedEmp.join(modifiedDept) // (dept_id, ((emp_id,emp_name,dept_id), (dept_id, (dept_name))
    val cleaned_empJoinDeptOnId = empJoinDeptOnId.map(t => (t._2._1, t._2._2._2)) // (emp_id,emp_name,dept_id) (dept_name)
      .sortBy(_._1._1, ascending = true)

    // This is done just to display the output in cleaner form
    println("emp_id,emp_name,dept_id,dept_name")
    val modified_cleaned_empJoinDeptOnId = cleaned_empJoinDeptOnId
      .map(t => t._1._1 + "," + t._1._2 + "," + t._1._3 + "," + t._2)
      .collect().foreach(println)

  }

}