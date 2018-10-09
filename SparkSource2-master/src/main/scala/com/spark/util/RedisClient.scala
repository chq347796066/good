package com.spark.util

import com.spark.config.Config
import redis.clients.jedis.{JedisPool, JedisPoolConfig}

object RedisClient extends Serializable {
  private val MAX_IDLE: Int = 200
  private val TIMEOUT: Int = 10000
  private val TEST_ON_BORROW: Boolean = true

  lazy val config: JedisPoolConfig = {
    val config = new JedisPoolConfig
    config.setMaxIdle(MAX_IDLE)
    config.setTestOnBorrow(TEST_ON_BORROW)
    config
  }

  lazy val pool = new JedisPool(config, Config.REDIS_SERVER,
    Config.REDIS_PORT, TIMEOUT)

  lazy val hook = new Thread {
    override def run = {
      println("Execute hook thread: " + this)
      pool.destroy()
    }
  }
  sys.addShutdownHook(hook.run)
}