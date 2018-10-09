package be.icteam.demo

import com.typesafe.scalalogging.StrictLogging
import org.apache.spark.sql.SparkSession
import org.apache.spark.streaming.{Seconds, StreamingContext}

trait StreamingApp extends StrictLogging {

  def main(args: Array[String]) : Unit = {
    val ssc = getSparkStreamingContext(getSparkAppName())
    configureStreamingContext(ssc)



    ssc.start()
    ssc.awaitTermination()
  }

  def getSparkAppName() : String

  def getSparkStreamingContext(sparkAppName: String) : StreamingContext = {
    val spark: SparkSession = SparkSession
      .builder()
      .appName(sparkAppName)
      .master(DemoConfig.sparkMaster)
      .getOrCreate()

    val sc = spark.sparkContext
    val ssc = StreamingContext.getActiveOrCreate(() => new StreamingContext(sc, Seconds(5)))
    ssc
  }

  def configureStreamingContext(ssc: StreamingContext) : Unit

}
