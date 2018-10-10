package com.alan.developer.bigdata

import java.time.format.DateTimeFormatter
import java.time.{Instant, LocalDateTime, ZoneOffset}

import com.alan.developer.bigdata.model.Request
import kafka.serializer.{DefaultDecoder, StringDecoder}
import org.apache.spark.SparkConf
import org.apache.spark.sql.SparkSession
import org.apache.spark.storage.StorageLevel
import org.apache.spark.streaming.kafka.KafkaUtils
import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.elasticsearch.spark._
import org.json4s.DefaultFormats
import org.json4s.jackson.JsonMethods
import org.slf4j.{Logger, LoggerFactory}

/**
  * Central de procesado de logs.
  */
object RequestLogging {

  val log: Logger = LoggerFactory.getLogger(getClass)
  implicit val formats = DefaultFormats

  def main(args: Array[String]): Unit = {
    //val spark = SparkSession.builder.appName("Central Logging application").getOrCreate()
    val spark = SparkSession.builder.config(new SparkConf().setAppName("Request Logging App").setMaster("local[*]"))
      .getOrCreate()
    @transient
    val sc = spark.sparkContext
    val ssc = new StreamingContext(sc, Seconds(5))
    val params: Map[String, String] = Map(
      "metadata.broker.list" -> args(0),
      "zookeeper.connect" -> args(1),
      "group.id" -> "requests-streaming",
      "zookeeper.connection.timeout.ms" -> "5000"
    )
    val topics = List("zipkin")
    val kafkaStream = KafkaUtils.createStream[Array[Byte], String, DefaultDecoder, StringDecoder](ssc, params,
      topics.map((_, 1)).toMap, StorageLevel.MEMORY_ONLY_SER).map(_._2)
    val index = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHH")) concat "/log"
    kafkaStream.map(linea => JsonMethods.parse(linea).extract[Seq[Request]]).flatMap(identity)
      .map(r => r.copy(timestamp = Instant.ofEpochMilli((r.timestamp.toLong / 1000)).atOffset(ZoneOffset.UTC)
        .toLocalDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))))
      .foreachRDD(rdd => rdd.saveToEs("logsapp-{name}" concat index))

    ssc.start()
    ssc.awaitTermination()
  }
}
