package com.alan.developer.bigdata

import java.time.format.DateTimeFormatter
import java.time.{Instant, LocalDateTime, ZoneOffset}

import com.alan.developer.bigdata.model.{Endpoint, Request}
import kafka.serializer.{DefaultDecoder, StringDecoder}
import org.apache.spark.sql.SparkSession
import org.apache.spark.storage.StorageLevel
import org.apache.spark.streaming.kafka.KafkaUtils
import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.elasticsearch.spark._
import org.slf4j.{Logger, LoggerFactory}

/**
  * Central de procesado de logs.
  */
object RequestLogging {

  val log: Logger = LoggerFactory.getLogger(getClass)

  def main(args: Array[String]): Unit = {

    val spark = SparkSession.builder.appName("Central Logging application").getOrCreate()
    //val spark = SparkSession.builder.config(new SparkConf().setAppName("Request Logging App").setMaster("local[*]"))
    //.getOrCreate()
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
    kafkaStream.map(linea => {
      println(linea)
      import spray.json._
      import DefaultJsonProtocol._
      implicit val endpointFomat = jsonFormat3(Endpoint)
      implicit val requestFomat = jsonFormat10(Request)
      linea.parseJson.convertTo[Seq[Request]]
    }).flatMap(identity)
      .map(r => r.copy(createDate = Option(Instant.ofEpochMilli((r.timestamp / 1000)).atOffset(ZoneOffset.UTC)
        .toLocalDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))))
      .foreachRDD(rdd => rdd.saveToEs("logsapp-{name}" concat index))

    ssc.start()
    ssc.awaitTermination()
  }
}

