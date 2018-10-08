package com.alan.developer.bigdata

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

import kafka.serializer.{DefaultDecoder, StringDecoder}
import org.apache.spark.SparkConf
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.SparkSession
import org.apache.spark.storage.StorageLevel
import org.apache.spark.streaming.dstream.DStream
import org.apache.spark.streaming.kafka.KafkaUtils
import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.elasticsearch.spark._
import org.slf4j.{Logger, LoggerFactory}

object CentralLogging {

  val log: Logger = LoggerFactory.getLogger(getClass)

  def main(args: Array[String]): Unit = {
    //val spark = SparkSession.builder.appName("Central Logging application").getOrCreate()

    val spark = SparkSession.builder.config(new SparkConf().setAppName("Central Loggin App").setMaster("local[*]"))
      .getOrCreate()
    val sc = spark.sparkContext
    val ssc = new StreamingContext(sc, Seconds(5))
    val params: Map[String, String] = Map(
      "metadata.broker.list" -> "localhost:9092",
      "zookeeper.connect" -> "localhost:2181",
      "group.id" -> "spark-streaming",
      "zookeeper.connection.timeout.ms" -> "5000"
    )
    val topics = List("logging")
    val kafkaStream = KafkaUtils.createStream[Array[Byte], String, DefaultDecoder, StringDecoder](ssc, params,
      topics.map((_, 1)).toMap, StorageLevel.MEMORY_ONLY_SER).map(_._2)
    val index = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHH")).concat("/log")
    val allRdd = kafkaStream.map(linea => linea.split("\\|").toSeq)
      .filter(_.length == 5)
      .map(x => (x(0), GenericLog(x(0),
        LocalDateTime.parse(x(1), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS")),
        x(2), x(3), x(4))))
    val grouped = allRdd.groupByKey.map(pairs => (pairs._1, sc.parallelize(pairs._2.toSeq)))

    grouped.foreachRDD(group => group.foreach(rdd => rdd._2.saveToEs("logger-".concat(rdd._1).concat(index))))

    //.foreachRDD(rdd => rdd..saveToEs(index))

    ssc.start()
    ssc.awaitTermination()
  }
}

case class GenericLog(appName: String, timestamp: LocalDateTime, level: String, clazz: String, message: String)