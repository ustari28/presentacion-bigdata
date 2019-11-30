package com.alan.developer.bigdata

import com.alan.developer.bigdata.model.{JaegerLog, JaegerProcess, Reference, Tag}
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.spark.SparkConf
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
    val ssc = new StreamingContext(sc, Seconds(5))
    val stream = KafkaUtils.createDirectStream[String, String](
      ssc,
      PreferConsistent,
      Subscribe[String, String](topics, kafkaParams)
    )
    stream.map(record => {
      import spray.json._
      import DefaultJsonProtocol._
      implicit val tag = jsonFormat2(Tag)
      implicit val process = jsonFormat2(JaegerProcess)
      implicit val reference = jsonFormat2(Reference)
      implicit val log = jsonFormat9(JaegerLog)

      record.value.parseJson.convertTo[JaegerLog]
    })
      .foreachRDD(rdd => rdd.saveToEs("jaeger-v1-{process.serviceName}-{@timestamp|yyyy.MM.dd}")
    )
    ssc.start()
    ssc.awaitTermination()
  }
}

//case class GenericLog(appName: String, timestamp: String, level: String, clazz: String, message: String)