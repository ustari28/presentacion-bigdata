package com.alan.developer.bigdata

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Properties

import org.apache.kafka.streams.kstream.KTable
import org.apache.kafka.streams.{StreamsBuilder, StreamsConfig}
import org.apache.spark.SparkConf
import org.apache.spark.sql.SparkSession
import org.slf4j.{Logger, LoggerFactory}
import spray.json._

/**
  * Central de procesado de logs.
  */
object CentralLogging {
  import org.apache.kafka.streams.scala.Serdes._

  val log: Logger = LoggerFactory.getLogger(getClass)

  def main(args: Array[String]): Unit = {
    //val spark = SparkSession.builder.appName("Central Logging application").getOrCreate()

    val spark = SparkSession.builder.config(new SparkConf().setAppName("Central Loggin App").setMaster("local[*]"))
      .getOrCreate()
    val props: Properties = {
      val p = new Properties()
      p.put(StreamsConfig.APPLICATION_ID_CONFIG, "Central Logging application")
      p.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092")
      p
    }
    /**
    val sc = spark.sparkContext
    val ssc = new StreamingContext(sc, Seconds(5))
    val params: Map[String, String] = Map(
      "metadata.broker.list" -> args(0),
      "zookeeper.connect" -> args(1),
      "group.id" -> "logging-streaming",
      "zookeeper.connection.timeout.ms" -> "5000"
    )
    val topics = List("logging")
     */
    val builder: StreamsBuilder = new StreamsBuilder
    /**val kafkaStream = KafkaUtils.createStream[Array[Byte], String, DefaultDecoder, StringDecoder](ssc, params,
      topics.map((_, 1)).toMap, StorageLevel.MEMORY_ONLY_SER).map(_._2)*/
    val kafkaStream: KTable[String, String] = builder.stream[String, String]("tracing")
    val index = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHH")) concat "/log"
    kafkaStream.mapValues()
    /**kafkaStream.map(linea => linea.split("\\|").toSeq)
      .filter(_.length == 5)
      .map(x => GenericLog(x(0).toLowerCase, x(1), x(2).trim, x(3), x(4)))
      .foreachRDD(rdd => rdd.saveToEs("logging-{appName}" concat index))
*/
    ssc.start()
    ssc.awaitTermination()
  }
}

case class GenericLog(appName: String, timestamp: String, level: String, clazz: String, message: String)