package com.alan.developer.bigdata

import java.sql.Timestamp


/**
  * Logging application. Arguments kafka zookeper.
  *
  */
object AppicationLogging {
  /**
  val log: Logger = LoggerFactory.getLogger(getClass)

  def main(args: Array[String]): Unit = {

    val spark = SparkSession.builder.appName("Retail application").getOrCreate()
    val ssc = new StreamingContext(spark.sparkContext, Seconds(5))
    val params: Map[String, String] = Map(
      "metadata.broker.list" -> args(0),
      "zookeeper.connect" -> args(1),
      "group.id" -> "retail-streaming",
      "zookeeper.connection.timeout.ms" -> "5000"
    )
    val topics = List("retail-app")
    val kafkaStream = KafkaUtils.createStream[Array[Byte], String, DefaultDecoder, StringDecoder](ssc, params,
      topics.map((_, 1)).toMap, StorageLevel.MEMORY_ONLY_SER).map(_._2)
    val index = "retail-".concat(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHH")))
      .concat("/log")
    kafkaStream.map(linea => linea.split(";").toSeq)
      .map(x => Retail(x(0), x(1), x(2), x(3).toInt, x(4), x(5).replace(",", ".").toFloat,
        x(6), x(7), new Timestamp(System.currentTimeMillis())))
      .foreachRDD(rdd => rdd.saveToEs(index))
    ssc.start()
    ssc.awaitTermination()
  }
   */
}

case class Retail(invoiceNo: String, stockCode: String, description: String, quantity: Integer, invoiceDate: String,
                  unitPrice: Float, customerID: String, country: String, createDate: Timestamp)