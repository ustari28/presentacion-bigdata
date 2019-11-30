package com.alan.developer.bigdata.model

import spray.json.{DefaultJsonProtocol, JsNumber, JsValue, JsonFormat}

case class JaegerLog(traceId: String, spanId: String, operationName: String, references: Option[Array[Reference]],
                     flags: Int, startTime: String, duration: String , tags: Array[Tag], process: JaegerProcess)
case class Reference(traceId: String, spanId: String)
case class Tag(key: String, vStr: Option[String])
case class JaegerProcess(serviceName: String, tags: Array[Tag])

object FloatJsonFormat extends JsonFormat[Float] {
  override def read(json: JsValue): Float = json.toString().replace(".", ",").toFloat

  override def write(obj: Float): JsValue = JsNumber(obj)
}