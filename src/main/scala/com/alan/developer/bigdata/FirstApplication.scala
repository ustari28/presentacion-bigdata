package com.alan.developer.bigdata

import org.apache.spark.SparkConf
import org.apache.spark.sql.types.StructType
import org.apache.spark.sql.{Row, SparkSession}

/**
  * First application.
  */git
object FirstApplication {

  val schema: StructType =
  def main(args: Array[String]): Unit = {
    //val spark = SparkSession.builder.appName("First application").getOrCreate()
    val spark = SparkSession.builder.config(new SparkConf().setAppName("First application")
      .setMaster("local[*]")).getOrCreate()
    val sc = spark.sparkContext
    val file = sc textFile (args(0))
    val rdd = file.map(_.split(";").toSeq) map (a => (a(1), a(2), a(3), a(4), a(5), a(6), a(7)))
    rdd.take(10).foreach(println(_))

  }
}
