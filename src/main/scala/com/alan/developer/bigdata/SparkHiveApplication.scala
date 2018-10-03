package com.alan.developer.bigdata

import org.apache.spark.SparkContext
import org.apache.spark.sql.{Row, SQLContext}

object SparkHiveApplication {

  def main(args: Array[String]): Unit = {
    //val sc = new SparkContext(new SparkConf().setAppName("Spark Hive").setMaster("local[*]"))
    val sc = SparkContext.getOrCreate()
    val sqlContext = new SQLContext(sc)
    val fic = sc.textFile("/bigdata/retail/online-retail.txt")
    val df_retail = fic.map(_.split(";").toSeq).map(x => Row(x(0), x(1), x(2), Option(x(3)).getOrElse("0").toInt,
      x(4), Option(x(5)).getOrElse("0.0").replace(",", ".").toFloat, Option(x(6))
        .map(d => if (d.isEmpty) "0" else d).getOrElse("0").toLong, x(7)))
    val sc_retail = sqlContext.table("bigdata.retail").schema
    val df = sqlContext.createDataFrame(df_retail, sc_retail)
    df.write.mode("overwrite").insertInto("bigdata.retail")
    println(s"Total records ${df count}")
  }
}

case class Retail(invoiceNo: String, stockCode: String, description: String, quantity: Integer, invoiceDate: String,
                  unitPrice: Float, customerID: String, country: String)
