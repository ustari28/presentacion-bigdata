package com.alan.developer.bigdata

import com.alan.developer.bigdata.model.{Endpoint, Request}

class AppTest extends BaseSpec {

  val endpoint = Endpoint(Option("serviceName"), Option("ip4v"), Option(4))
  val request = Request("traceId", "id", "kind", "name", 1232131232, 34, endpoint, endpoint, Map("t" -> "t"), Option("createDate"))

  test("Testing functional verbs") {
    request.traceId should be equals ("traceId")
  }

  test("Testing case class with Inside trait") {
    inside(request) {
      case Request(traceId, id, kind, name, timestamp, duration, localEndpoint, remoteEndpoint, tags, createDate) =>
        traceId should be equals ("traceId")
        id should not equal (Nil)

    }
  }

  test("Testing endpoint case class") {
    endpoint should not be eq(Nil)
  }

}


