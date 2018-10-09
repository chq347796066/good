package be.icteam.demo

import com.typesafe.config.ConfigFactory

object DemoConfig {

  def main(args: Array[String]) : Unit = {
    println(s"appEnv: $appEnv")
    println(s"sparkMaster: $sparkMaster")
    println(s"zooKeeper: $zooKeeper")
    println(s"kafka: $kafka")
  }

  val appEnv =
    if(System.getProperty("appEnv") != null) System.getProperty("appEnv")
    else "local"

  private val envConfig = ConfigFactory.load(s"application.$appEnv")
  private val commonConfig = ConfigFactory.load("application.common")
  val conf = envConfig.withFallback(commonConfig)


  val twitterTopicPrefix = conf.getString("twitterTopicPrefix")
  val transformTopicPrefix = conf.getString("transformTopicPrefix")
  val sparkMaster = conf.getString("sparkMaster")
  val zooKeeper = conf.getString("zooKeeper")
  val kafka = conf.getString("kafka")

  lazy val hostname = conf.getString("hostname")
  lazy val zooKeeperPort = conf.getInt("zooKeeperPort")
  lazy val kafkaPort = conf.getInt("kafkaPort")



}
