package be.icteam.demo

import java.util.concurrent.TimeUnit

import be.icteam.demo.pooling.Pool
import com.fasterxml.jackson.databind.ObjectMapper
import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord}
import org.apache.spark.rdd.RDD
import org.apache.spark.streaming.StreamingContext
import org.apache.spark.streaming.twitter.TwitterUtils
import twitter4j.Status

import scala.util.Try

object TwitterToKafkaApp extends StreamingApp {

  override def getSparkAppName(): String = "TwitterToKafka"

  def configureStreamingContext(ssc: StreamingContext) : Unit = {

    logger.info("Defining TwitterToKafkaApp...")

    val stream = TwitterUtils.createStream(ssc, None)
    stream.foreachRDD((rdd: RDD[Status]) => rdd.foreachPartition(processRDD))

    logger.info("Defined TwitterToKafkaApp.")
  }

  def processRDD(statuses: Iterator[Status]) : Unit = {

    val producer = Pool.kafkaProducers.borrowObject()

    val mapper = new ObjectMapper()

    val topic = s"${DemoConfig.twitterTopicPrefix}-1"

    val results = statuses
      .map(x => mapper.writeValueAsString(x))
      .map(x => new ProducerRecord[String, String](topic, x))
      .map(x => producer.send(x))
      .map(x => Try(x.get(30, TimeUnit.SECONDS)))
      .toList

    producer.flush()
    Pool.kafkaProducers.returnObject(producer)

    val (successes, failures) = results.partition(x => x.isSuccess)

    logger.info(s"processed ${results.length}, ok: ${successes.length} bad: ${failures.length}")
    failures.foreach(x => logger.error(s"failed to process a message", x.failed.get))


  }

}
