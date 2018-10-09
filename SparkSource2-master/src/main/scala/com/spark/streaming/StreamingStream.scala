package com.spark.streaming

import com.alibaba.fastjson.JSON
import com.spark.config.Config
import org.apache.spark.SparkConf
import org.apache.spark.streaming.kafka.KafkaUtils
import org.apache.spark.streaming.{Seconds, StreamingContext}

/**
  * Created by AnLei on 2017/7/17.
  */
object StreamingStream {

  def main(args: Array[String]): Unit = {
    val topic = Config.KAFKA_TOPIC
    val zkQuorum = Config.ZOOKEEPER_QUORUM
    val group = Config.KAFKA_GROUP

    val conf = new SparkConf().setAppName("StreamingDemo").setMaster("local[*]")
    val ssc = new StreamingContext(conf, Seconds(5))
    ssc.checkpoint("checkpoint")

    val numThreads: Int = 2
    val topicMap = topic.split(",").map((_, numThreads)).toMap

    val kafkaStream = KafkaUtils.createStream(ssc, zkQuorum, group, topicMap)

    val events = kafkaStream.flatMap(line => {
      println(s"line ${line}.")
      val data = JSON.parseObject(line._2)
      Some(data)
    })

    val counts = events.map(x => (x.getString("name"), x.getLong("count"))).reduceByKey(_ + _)

    counts.foreachRDD(rdd => {
      rdd.foreachPartition(records => {
        records.foreach(record => {
          try {
            val id = record._1
            val count = record._2
            println(s"id ${id} to ${count}.")
          } catch {
            case e: Exception => println("error:" + e)
          }
        })
      })
    })

    ssc.start()
    ssc.awaitTermination()
  }

}
