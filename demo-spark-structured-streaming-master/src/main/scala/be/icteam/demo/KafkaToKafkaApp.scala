package be.icteam.demo

import java.util.concurrent.TimeUnit
import java.util.regex.Pattern

import be.icteam.demo.pooling.Pool
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.spark.rdd.RDD
import org.apache.spark.streaming.StreamingContext
import org.apache.spark.streaming.kafka010.LocationStrategies.PreferConsistent
import org.apache.spark.streaming.kafka010._

import scala.util.Try

object KafkaToKafkaApp extends StreamingApp {

  override def getSparkAppName(): String = "KafkaToKafka"

  override def configureStreamingContext(ssc: StreamingContext) : Unit = {

    logger.info("Defining KafkaToKafkaApp...")

    val kafkaParams = Map[String, Object](
      "bootstrap.servers" -> DemoConfig.kafka,
      "key.deserializer" -> classOf[StringDeserializer],
      "value.deserializer" -> classOf[StringDeserializer],
      "group.id" -> "use_a_separate_group_id_for_each_stream",
      "auto.offset.reset" -> "earliest",
      "enable.auto.commit" -> (false: java.lang.Boolean)
    )

    val topicPattern = Pattern.compile(s"${DemoConfig.twitterTopicPrefix}(.*?)")
    val consumerStrategy = ConsumerStrategies.SubscribePattern[String, String](topicPattern, kafkaParams)
    val stream = KafkaUtils.createDirectStream[String, String](ssc, PreferConsistent, consumerStrategy)

    stream.foreachRDD((rdd: RDD[ConsumerRecord[String, String]]) => rdd.foreachPartition(processRDD))

    logger.info("Defined KafkaToKafkaApp.")
  }

  def processRDD(msgs: Iterator[ConsumerRecord[String, String]]) : Unit = {

    val producer = Pool.kafkaProducers.borrowObject()

    val results = msgs
      .map(x => new ProducerRecord[String, String](DemoConfig.transformTopicPrefix, x.value()))
      .map(x => producer.send(x))
      .map(x => Try(x.get(30, TimeUnit.SECONDS)))
      .toList

    val (successes, failures) = results.partition(x => x.isSuccess)

    logger.info(s"processed ${results.length}, ok: ${successes.length} bad: ${failures.length}")
    failures.foreach(x => logger.error(s"failed to process a message", x.failed.get))

    producer.flush()
    Pool.kafkaProducers.returnObject(producer)
  }
}
