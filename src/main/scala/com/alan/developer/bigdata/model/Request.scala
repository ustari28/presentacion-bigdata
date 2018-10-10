package com.alan.developer.bigdata.model

case class Request(traceId: String, id: String, kind: String, name: String, timestamp: String, duration: Int,
                   localEndpoint: Endpoint, remoteEndpoint: Endpoint, tags: Map[String, String])

case class Endpoint(serviceName: Option[String], ipv4: Option[String], port: Option[Int])