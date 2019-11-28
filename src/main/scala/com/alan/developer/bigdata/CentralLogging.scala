package com.alan.developer.bigdata

import com.alan.developer.bigdata.model.{JaegerLog, JaegerProcess, Reference, Tag}
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.spark.SparkConf
import org.apache.spark.sql.SparkSession
import org.apache.spark.streaming._
import org.apache.spark.streaming.kafka010.ConsumerStrategies.Subscribe
import org.apache.spark.streaming.kafka010.LocationStrategies.PreferConsistent
import org.apache.spark.streaming.kafka010._
import org.slf4j.{Logger, LoggerFactory}
import org.elasticsearch.spark._

/**
 * Central de procesado de logs.
 */
object CentralLogging {

  val log: Logger = LoggerFactory.getLogger(getClass)

  def main(args: Array[String]): Unit = {
    //val spark = SparkSession.builder.appName("Central Logging application").getOrCreate()
    val kafkaParams = Map[String, Object](
      "bootstrap.servers" -> "localhost:9092",
      "key.deserializer" -> classOf[StringDeserializer],
      "value.deserializer" -> classOf[StringDeserializer],
      "group.id" -> "use_a_separate_group_id_for_each_stream",
      "auto.offset.reset" -> "latest",
      "enable.auto.commit" -> (false: java.lang.Boolean)
    )
    val topics = Array("tracing")
    val offsetRanges = Array(
      // topic, partition, inclusive starting offset, exclusive ending offset
      OffsetRange("tracing", 0, 0, 100)
    )
    val spark = SparkSession.builder.config(new SparkConf().setAppName("Central Loggin App").setMaster("local[*]").set("es.index.auto.create", "true"))
      .getOrCreate()

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
      record.value.parseJson.convertTo[Seq[JaegerLog]]
    }).foreachRDD(rdd => rdd.saveToEs("spark-{process.serviceName}/docs"))
    //val builder: StreamsBuilder = new StreamsBuilder
    /** val kafkaStream = KafkaUtils.createStream[Array[Byte], String, DefaultDecoder, StringDecoder](ssc, params,
     *topics.map((_, 1)).toMap, StorageLevel.MEMORY_ONLY_SER).map(_._2)
     * val kafkaStream: KTable[String, String] = builder.stream[String, String]("tracing")
     * val index = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHH")) concat "/log"
     * //kafkaStream.(li => println(li))
     *kafkaStream.map(linea => linea.split("\\|").toSeq)
     * .filter(_.length == 5)
     * .map(x => GenericLog(x(0).toLowerCase, x(1), x(2).trim, x(3), x(4)))
     * .foreachRDD(rdd => rdd.saveToEs("logging-{appName}" concat index))
     */
    ssc.start()
    ssc.awaitTermination()
  }
}

//case class GenericLog(appName: String, timestamp: String, level: String, clazz: String, message: String)