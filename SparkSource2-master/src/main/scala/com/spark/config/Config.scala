package com.spark.config

/**
  * Created by AnLei on 2017/5/15.
  */
object Config {
  val KAFKA_BROKERS: String = "ha01:9092,ha02:9092,ha03:9092"
  val KAFKA_USER_TOPIC: String = "user"
  val KAFKA_RECO_TOPIC: String = "reco6"

  val REDIS_SERVER: String = "ha01"
  val REDIS_PORT: Int = 6379
  val KAFKA_TOPIC: String = "kafka_topic"
  val KAFKA_GROUP: String = "kafka_topic"
  val ZOOKEEPER_QUORUM: String = "kafka_topic"
}
