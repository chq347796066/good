package be.icteam.demo

import java.io.File
import java.util.Properties

import com.github.sakserv.minicluster.impl.{KafkaLocalBroker, ZookeeperLocalCluster}
import com.typesafe.scalalogging.StrictLogging
import org.apache.commons.io.FileUtils

object InMemoryKafkaClusterApp extends StrictLogging {

  def main(args: Array[String]) : Unit = run()

  def run() : Unit = {

    logger.info("starting zookeeper...")

    val zookeeperTempDir = "./target/embedded_zookeeper"
    FileUtils.deleteDirectory(new File(zookeeperTempDir))

    val zookeeperLocalCluster = new ZookeeperLocalCluster.Builder()
      .setPort(DemoConfig.zooKeeperPort)
      .setTempDir(zookeeperTempDir)
      .setZookeeperConnectionString(DemoConfig.zooKeeper)
      .setMaxClientCnxns(60)
      .setElectionPort(20001)
      .setQuorumPort(20002)
      .setDeleteDataDirectoryOnClose(true)
      .setServerId(1)
      .setTickTime(2000)
      .build()
    zookeeperLocalCluster.start()

    logger.info("starting kafka broker")

    val kafkaTempDir = "./target/embedded_kafka"
    FileUtils.deleteDirectory(new File(kafkaTempDir))

    val kafkaLocalBroker = new KafkaLocalBroker.Builder()
      .setKafkaHostname(DemoConfig.hostname)
      .setKafkaPort(DemoConfig.kafkaPort)
      .setKafkaBrokerId(0)
      .setKafkaProperties(new Properties())
      .setKafkaTempDir(kafkaTempDir)
      .setZookeeperConnectionString(DemoConfig.zooKeeper)
      .build()
    kafkaLocalBroker.start()

    logger.info("kafka cluster is up and running")

  }

}
