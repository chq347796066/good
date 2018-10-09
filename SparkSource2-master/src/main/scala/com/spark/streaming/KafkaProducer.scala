package com.spark.streaming

import java.util.Properties

import com.alibaba.fastjson.JSONObject
import com.spark.config.Config
import kafka.javaapi.producer.Producer
import kafka.producer.{KeyedMessage, ProducerConfig}

/**
  * Created by AnLei on 2017/7/17.
  */
object KafkaProducer {

  private val users = Array(
    "张三", "李四",
    "王五", "赵六",
    "钱七", "孙八",
    "杨九", "吴十")

  def main(args: Array[String]): Unit = {
    val topic = Config.KAFKA_TOPIC
    val brokers = Config.KAFKA_BROKERS

    val properties: Properties = new Properties
    properties.put("metadata.broker.list", brokers)
    properties.put("serializer.class", "kafka.serializer.StringEncoder")

    val kafkaConfig = new ProducerConfig(properties)
    val producer = new Producer[String, String](kafkaConfig)

    var index = 0
    while(true){
      val event = new JSONObject()
      val name: String = users(index % users.length)
      index += 1
      
      event.put("id", index)
      event.put("name", name)
      event.put("count", 1)
      event.put("time", System.currentTimeMillis.toString)

      val message: KeyedMessage[String, String] = new KeyedMessage[String, String](topic, event.toString)
      producer.send(message)
      println(event)

      Thread.sleep(200)
    }
  }

}
