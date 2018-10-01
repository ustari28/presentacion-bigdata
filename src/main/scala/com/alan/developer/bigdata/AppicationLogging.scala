package com.alan.developer.bigdata


import java.sql.Timestamp
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

import kafka.serializer.{DefaultDecoder, StringDecoder}
import org.apache.spark.SparkConf
import org.apache.spark.storage.StorageLevel
import org.apache.spark.streaming.kafka.KafkaUtils
import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.slf4j.{Logger, LoggerFactory}

/**
  * Hello world!
  *
  */
object AppicationLogging {
  val log: Logger = LoggerFactory.getLogger(getClass)

  def main(args: Array[String]): Unit = {
    import org.elasticsearch.spark._
    val conf = new SparkConf().setAppName("kafkastreaming").setMaster("local[*]")
    val ssc = new StreamingContext(conf, Seconds(5))
    val params: Map[String, String] = Map(
      "metadata.broker.list" -> "127.0.0.1:9092",
      "zookeeper.connect" -> "127.0.0.1:2181",
      "group.id" -> "spark-streaming",
      "zookeeper.connection.timeout.ms" -> "5000"
    )
    val topics = List("logs-app")
    val kafkaStream = KafkaUtils.createStream[Array[Byte], String, DefaultDecoder, StringDecoder](ssc, params,
      topics.map((_, 1)).toMap, StorageLevel.MEMORY_ONLY_SER).map(_._2)
    val index = "logs-".concat(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHH")))
      .concat("/log")
    kafkaStream.map(linea => linea.split(";").toSeq)
      .map(x => Retail(x(0), x(1), x(2), x(3).toInt, x(4), x(5).replace(",", ".").toFloat,
        x(6), x(7), new Timestamp(System.currentTimeMillis())))
      .foreachRDD(rdd => rdd.saveToEs(index))
    ssc.start()
    ssc.awaitTermination()
  }
}

case class Retail(invoiceNo: String, stockCode: String, description: String, quantity: Integer, invoiceDate: String,
                  unitPrice: Float, customerID: String, country: String, createDate: Timestamp)