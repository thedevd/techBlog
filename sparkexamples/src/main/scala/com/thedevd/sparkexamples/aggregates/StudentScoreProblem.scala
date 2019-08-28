package com.thedevd.sparkexamples.aggregates

import org.apache.log4j.Logger
import org.apache.log4j.Level
import org.apache.spark.sql.SparkSession
import org.apache.spark.rdd.RDD

/*
 * finding maximum marks in a single subject of a student using
 * aggregateByKey and combineByKey
 *
 * output shoud contain
 * student_id, student_name, max_scoring_subjectname, subject_mark
 *
 * source file - rdd/student_marks.txt
 */
object MaxScoringSubjectProblem {

  def main(args: Array[String]): Unit = {
    Logger.getLogger("org").setLevel(Level.OFF)

    val spark = SparkSession.builder()
      .appName("MaxScoringSubjectProblem")
      .master("local[*]")
      .getOrCreate()

    val students = spark.sparkContext.textFile(getClass.getResource("/rdd/student_marks.txt").getPath)
    val studentPairRdd = students
      .filter(!_.contains("subject_name"))
      .map(studentData => {
        val split = studentData.split(",")
        (split(0), (split(1), split(2), split(3).toDouble)) // pairRDD (studentId, (student_name, subject, score))
      })

    val maxScores = maxScoringSubject(studentPairRdd)
    val minScores = minScoringSubject(studentPairRdd)
    val avg = scoreAvg(studentPairRdd)

    println("MaxScoringSubject")
    maxScores.collect().foreach(println)
    println("##########################")
    println("MinScoringSubject")
    minScores.collect().foreach(println)
    println("##########################")
    println("AvgScore")
    avg.collect().foreach(println)
  }

  def maxScoringSubject(studentPairRdd: RDD[(String, (String, String, Double))]) = {
    val zeroValues = ("", "", Double.MinValue) // (student_name, subject, max_score)
    val partitionCombiner = (accumulator: (String, String, Double), element: (String, String, Double)) => {
      if (accumulator._3 > element._3) accumulator else element
    }
    val mergeFunction = (part1: (String, String, Double), part2: (String, String, Double)) => {
      if (part1._3 > part2._3) part1 else part2
    }

    val maxScoringSubject = studentPairRdd
      .aggregateByKey(zeroValues)(partitionCombiner, mergeFunction)

    maxScoringSubject // pairRdd (studentId, (student_name, subject, max_score))
  }

  def minScoringSubject(studentPairRdd: RDD[(String, (String, String, Double))]) = {
    val zeroValue = ("", "", Double.MaxValue) // (student_name, subject, min_score)
    val paritionCombiner = (accumulator: (String, String, Double), element: (String, String, Double)) => {
      if (accumulator._3 < element._3) accumulator else element
    }
    val mergeFunction = (part1: (String, String, Double), part2: (String, String, Double)) => {
      if (part1._3 < part2._3) part1 else part2
    }

    val minScoringSubjet = studentPairRdd
      .aggregateByKey(zeroValue)(paritionCombiner, mergeFunction)

    minScoringSubjet //pairRdd (studentId, (student_name, subject, min_score))
  }

  def scoreAvg(studentPairRdd: RDD[(String, (String, String, Double))]) = {
    val zeroValue = (0, "", 0.0) // (count, student_name, score)
    val partitionCombiner = (accumulator: (Int, String, Double), element: (String, String, Double)) => {
      (accumulator._1 + 1, element._1, accumulator._3 + element._3)
    }
    val mergeFunction = (part1: (Int, String, Double), part2: (Int, String, Double)) => {
      (part1._1 + part2._1, part1._2, part1._3 + part2._3)
    }

    val avg = studentPairRdd
      .aggregateByKey(zeroValue)(partitionCombiner, mergeFunction)
      .mapValues(t => (t._2, math.round((t._3) / t._1))) // (student_name, total/count)

    avg // pairRdd (studentId, (student_name, avg_score))
  }

}