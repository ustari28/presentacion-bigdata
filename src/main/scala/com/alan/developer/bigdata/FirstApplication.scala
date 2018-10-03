package com.alan.developer.bigdata

import org.apache.spark.SparkContext

/**
  * First application.
  */
object FirstApplication {


  def main(args: Array[String]): Unit = {
    val sc = SparkContext.getOrCreate()
    //val conf = new SparkConf().setAppName("First app").setMaster("local[*]")
    //val sc = new SparkContext(conf)
    val file = sc textFile (args(0))
    val rdd = file.map(_.split(";").toSeq) map (a => (a(1), a(2), a(3), a(4), a(5), a(6), a(7)))
    rdd.take(10).foreach(println(_))

  }
}
