package com.alan.developer.bigdata.model

case class Request(traceId: String, id: String, kind: String, name: String, timestamp: Long, duration: Int,
                   localEndpoint: Endpoint, remoteEndpoint: Endpoint, tags: Map[String, String],
                   createDate: Option[String])

case class Endpoint(serviceName: Option[String], ipv4: Option[String], port: Option[Int])