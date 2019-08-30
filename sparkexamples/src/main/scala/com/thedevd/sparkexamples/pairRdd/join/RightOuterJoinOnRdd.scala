package com.thedevd.sparkexamples.pairRdd.join

import org.apache.log4j.Logger
import org.apache.log4j.Level
import org.apache.spark.sql.SparkSession

/*
 * Read the emp details from /rdd/join/emp.txt and department details from /rdd/join/dept.txt
 * 
 * emp.txt has the details of dept_id which is acting as foreign key for dept_id in dept.txt.
 * 
 * Write a spark program to show the dept details where no employee is yet working (in following format-
 * 
 * dept_id,dept_name
 */

object RightOuterJoinOnRdd {

  def main(args: Array[String]): Unit = {
    Logger.getLogger("org").setLevel(Level.OFF)

    val spark = SparkSession.builder()
      .appName("RightOuterJoinOnRdd")
      .master("local[*]")
      .getOrCreate()

    val empRddByDeptId = spark.sparkContext.textFile(getClass.getResource("/rdd/join/emp.txt").getPath)
      .filter(!_.contains("dept_id"))
      .map(empData => {
        val split = empData.split(",")
        (split(0), split(1), split(2))
      })
      .keyBy(_._3)
      
      val deptRddByDeptId = spark.sparkContext.textFile(getClass.getResource("/rdd/join/dept.txt").getPath)
      .filter(!_.contains("dept_id"))
      .map(deptData => {
        val split = deptData.split(",")
        (split(0), split(1))
      })
      .keyBy(_._1)
      
      val emp_right_join_dept_on_deptId = empRddByDeptId.rightOuterJoin(deptRddByDeptId) // (dept_id, ((emp_id,emp_name_dept_id), (dept_id, dept_name)))
      //emp_right_join_dept_on_deptId.collect().foreach(println)
      
      val deptWhereNoEmpWorking = emp_right_join_dept_on_deptId.filter(_._2._1 == None) // Where emp details is None
      .sortBy(_._1, ascending= true)
      
      println("dept_id,dept_name")
      for( t <- deptWhereNoEmpWorking.collect() ) println(t._2._2._1 + "," + t._2._2._2)
     
  }
}