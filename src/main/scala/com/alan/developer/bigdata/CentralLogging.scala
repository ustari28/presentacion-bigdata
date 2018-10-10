package com.alan.developer.bigdata

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

import kafka.serializer.{DefaultDecoder, StringDecoder}
import org.apache.spark.SparkConf
import org.apache.spark.sql.SparkSession
import org.apache.spark.storage.StorageLevel
import org.apache.spark.streaming.kafka.KafkaUtils
import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.elasticsearch.spark._
import org.slf4j.{Logger, LoggerFactory}

/**
  * Central de procesado de logs.
  */
object CentralLogging {

  val log: Logger = LoggerFactory.getLogger(getClass)

  def main(args: Array[String]): Unit = {
    //val spark = SparkSession.builder.appName("Central Logging application").getOrCreate()

    val spark = SparkSession.builder.config(new SparkConf().setAppName("Central Loggin App").setMaster("local[*]"))
      .getOrCreate()
    @transient
    val sc = spark.sparkContext
    val ssc = new StreamingContext(sc, Seconds(5))
    val params: Map[String, String] = Map(
      "metadata.broker.list" -> args(0),
      "zookeeper.connect" -> args(1),
      "group.id" -> "spark-streaming",
      "zookeeper.connection.timeout.ms" -> "5000"
    )
    val topics = List("logging")
    val kafkaStream = KafkaUtils.createStream[Array[Byte], String, DefaultDecoder, StringDecoder](ssc, params,
      topics.map((_, 1)).toMap, StorageLevel.MEMORY_ONLY_SER).map(_._2)
    val index = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHH"))
    val allRdd = kafkaStream.map(linea => linea.split("\\|").toSeq)
      .filter(_.length == 5)
      .map(x => GenericLog(x(0).toLowerCase,
        x(1),
        x(2).trim, x(3), x(4)))
    allRdd.foreachRDD(rdd => rdd.saveToEs("logging-{appName}" + index + "/log"))

    ssc.start()
    ssc.awaitTermination()
  }
}

case class GenericLog(appName: String, timestamp: String, level: String, clazz: String, message: String)