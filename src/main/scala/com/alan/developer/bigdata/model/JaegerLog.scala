package com.alan.developer.bigdata.model

case class JaegerLog(traceId: String, spanId: String, operationName: String, references: Array[Reference],
                     flags: Int, startTime: String, duration: Float, tags: Array[Tag], process: JaegerProcess)
case class Reference(traceId: String, spanId: String)
case class Tag(key: String, vStr: String)
case class JaegerProcess(serviceName: String, tags: Array[Tag])
