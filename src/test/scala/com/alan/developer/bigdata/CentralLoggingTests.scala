package com.alan.developer.bigdata

import java.util.Properties

import org.apache.kafka.common.serialization.StringSerializer
import org.apache.kafka.streams.test.ConsumerRecordFactory
import org.apache.kafka.streams.{StreamsBuilder, StreamsConfig, TopologyTestDriver}

class CentralLoggingTests extends BaseSpec {

  test("Testing kafka streaming") {
    val builder = new StreamsBuilder();
    builder.stream("logging") to ("other");
    val topology = builder.build();


    val props = new Properties()
    props.put(StreamsConfig.APPLICATION_ID_CONFIG, "test")
    props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "dummy:1234")
    val testDriver = new TopologyTestDriver(topology, props)

    val factory: ConsumerRecordFactory[String, String] =
      new ConsumerRecordFactory[String, String]("input-topic", new StringSerializer,
        new StringSerializer)
    testDriver.pipeInput(factory.create("XXXXX"))
  }
}
