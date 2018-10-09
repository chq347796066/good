package be.icteam.demo

import com.typesafe.config.ConfigFactory

object DemoConfig {

  def main(args: Array[String]) : Unit = {
    println(s"appEnv: $appEnv")
    println(s"sparkMaster: $sparkMaster")
    println(s"zooKeeper: $zooKeeper")
    println(s"kafka: $kafka")
  }
//System.getProperty可以在vm option里面设置
  val appEnv =
    if(System.getProperty("appEnv") != null) System.getProperty("appEnv")
    else "local"
//加载配置文件，如果vm option没有设置，则加载的是application.local
  private val envConfig = ConfigFactory.load(s"application.$appEnv")
  private val commonConfig = ConfigFactory.load("application.common")
  //取env与common的集合，若有相同的key ,以envConfig为准
  val conf = envConfig.withFallback(commonConfig)


  val twitterTopicPrefix = conf.getString("twitterTopicPrefix")

  val transformTopicPrefix = conf.getString("transformTopicPrefix")
  val sparkMaster = conf.getString("sparkMaster")
  val zooKeeper = conf.getString("zooKeeper")
  val kafka = conf.getString("kafka")

  lazy val hostname = conf.getString("hostname")
  lazy val zooKeeperPort = conf.getInt("zooKeeperPort")
  lazy val kafkaPort = conf.getInt("kafkaPort")
  println("=========================================================")
  println(s"twitterTopicPrefix=$twitterTopicPrefix")
  println(s"transformTopicPrefix=$transformTopicPrefix")
  println(s"sparkMaster=$sparkMaster")
  println(s"zooKeeper=$zooKeeper")
  println(s"kafka=$kafka")
  println(s"hostname=$hostname")
  println(s"zooKeeperPort=$zooKeeperPort")
  println(s"kafkaPort=$kafkaPort")



}
