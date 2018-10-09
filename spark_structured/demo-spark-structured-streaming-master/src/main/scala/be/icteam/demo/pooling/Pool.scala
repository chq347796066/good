package be.icteam.demo.pooling

import java.util.Properties

import be.icteam.demo.DemoConfig

object Pool {

  val props = new Properties()
  props.put("bootstrap.servers", DemoConfig.kafka)
  props.put("acks", "all")
  props.put("retries", "0")
  props.put("batch.size", "10000")
  props.put("linger.ms", "1")
  props.put("buffer.memory", "33554432")
  props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer")
  props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer")

  val kafkaProducers = new KafkaProducerPool[String, String](new KafkaProducerFactory[String, String](props))

}
