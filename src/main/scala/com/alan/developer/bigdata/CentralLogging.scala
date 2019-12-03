package com.alan.developer.bigdata

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

import com.alan.developer.bigdata.model.{JaegerLog, JaegerProcess, Reference, Tag}
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.spark.sql.SparkSession
import org.apache.spark.streaming._
import org.apache.spark.streaming.kafka010.ConsumerStrategies.Subscribe
import org.apache.spark.streaming.kafka010.LocationStrategies.PreferConsistent
import org.apache.spark.streaming.kafka010._
import org.elasticsearch.spark._
import org.slf4j.{Logger, LoggerFactory}

/**
 * Central de procesado de logs.
 */
object CentralLogging {

  val log: Logger = LoggerFactory.getLogger(getClass)

  def main(args: Array[String]): Unit = {

    val kafkaParams = Map[String, Object](
      "bootstrap.servers" -> args(0),
      "key.deserializer" -> classOf[StringDeserializer],
      "value.deserializer" -> classOf[StringDeserializer],
      "group.id" -> "jaeger-streaming",
      "auto.offset.reset" -> "earliest",
      "enable.auto.commit" -> (false: java.lang.Boolean)
    )
    val topics = Array("tracing")
    //val spark = SparkSession.builder.config(new SparkConf().setAppName("Central Loggin App").setMaster("local[*]").set("es.index.auto.create", "true")).getOrCreate()
    val spark = SparkSession.builder.appName("Jaeger processing").getOrCreate()
    val sc = spark.sparkContext
    val ssc = new StreamingContext(sc, Seconds(Option(args(1).toLong).getOrElse(5L)))
    val stream = KafkaUtils.createDirectStream[String, String](
      ssc,
      PreferConsistent,
      Subscribe[String, String](topics, kafkaParams)
    )
    val index = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy.MM.dd"))
    stream.map(record => {
      import spray.json._
      import DefaultJsonProtocol._
      implicit val tag = jsonFormat2(Tag)
      implicit val process = jsonFormat2(JaegerProcess)
      implicit val reference = jsonFormat2(Reference)
      implicit val log = jsonFormat11(JaegerLog)

      record.value.parseJson.convertTo[JaegerLog]
    })
      .map(r => r.copy(processDate = Option(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS"))),
        startTime = LocalDateTime.parse(r.startTime, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS"))))
      .foreachRDD(rdd => rdd.saveToEs("jaeger-v1-{process.serviceName}-" concat index))
    ssc.start()
    ssc.awaitTermination()
  }
}

//case class GenericLog(appName: String, timestamp: String, level: String, clazz: String, message: String)