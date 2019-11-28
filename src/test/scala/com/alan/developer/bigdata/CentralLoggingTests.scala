package com.alan.developer.bigdata

import java.util.Properties

import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.serialization.{LongDeserializer, StringDeserializer, StringSerializer}
import org.apache.kafka.streams.state.KeyValueStore
import org.apache.kafka.streams.test.{ConsumerRecordFactory, OutputVerifier}
import org.apache.kafka.streams.{StreamsBuilder, StreamsConfig, TopologyTestDriver}

class CentralLoggingTests extends BaseSpec {
/**
  test("Testing kafka streaming") {
    val builder = new StreamsBuilder();
    builder.stream("logging") to ("other");
    val topology = builder.build();


    val props = new Properties()
    props.put(StreamsConfig.APPLICATION_ID_CONFIG, "test")
    props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "dummy:1234")
    val testDriver = new TopologyTestDriver(topology, props)

    val factory: ConsumerRecordFactory[String, String] =
      new ConsumerRecordFactory[String, String]("logging", new StringSerializer,
        new StringSerializer)
    testDriver.pipeInput(factory.create("key", 42L))

    val outputRecord: ProducerRecord[String, String] = testDriver
      .readOutput("output-topic", new StringDeserializer(), new StringDeserializer())

    OutputVerifier.compareKeyValue(outputRecord, "key", 42L); // throws AssertionError if key or value does not match
    testDriver.advanceWallClockTime(20L)
    val store: KeyValueStore[String, String] = testDriver.getKeyValueStore("store-name")
    store.put("loggin1", "logging")


    //finish
    testDriver close
  }*/
}
