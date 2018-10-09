package be.icteam.demo

import org.apache.spark.streaming.StreamingContext

import scala.concurrent.{ExecutionContext, Future}

object RunAllApp extends StreamingApp {

  override def getSparkAppName(): String = "ALL"

  override def configureStreamingContext(ssc: StreamingContext) : Unit = {

    implicit val ec: ExecutionContext = ExecutionContext.global
    val f1 = Future { InMemoryKafkaClusterApp.run() }

    TwitterToKafkaApp.configureStreamingContext(ssc)
    KafkaToKafkaApp.configureStreamingContext(ssc)
  }

}
