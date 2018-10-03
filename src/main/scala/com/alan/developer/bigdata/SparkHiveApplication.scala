package com.alan.developer.bigdata

import org.apache.spark.SparkContext
import org.apache.spark.sql.Row
import org.apache.spark.sql.hive.HiveContext

/**
  * DML hive tables from spark.
  */
object SparkHiveApplication {

  def main(args: Array[String]): Unit = {
    //val sc = new SparkContext(new SparkConf().setAppName("Spark Hive").setMaster("local[*]"))
    val sc = SparkContext.getOrCreate()
    val sqlContext = new HiveContext(sc)
    val fic = sc.textFile(args(0))
    val df_retail = fic.map(_.split(";").toSeq).map(x => Row(x(0), x(1), x(2), Option(x(3)).getOrElse("0").toInt,
      x(4), Option(x(5)).getOrElse("0.0").replace(",", ".").toFloat, Option(x(6))
        .map(d => if (d.isEmpty) "0" else d).getOrElse("0").toLong, x(7)))
    val sc_retail = sqlContext.table(args(1)).schema
    val df = sqlContext.createDataFrame(df_retail, sc_retail)
    df.write.mode("overwrite").insertInto(args(1))
    println("***************************")
    println(s"It has inserted ${df count} records")
    println("***************************")
  }
}

case class Retail(invoiceNo: String, stockCode: String, description: String, quantity: Integer, invoiceDate: String,
                  unitPrice: Float, customerID: String, country: String)
