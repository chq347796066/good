package com.spark.rdd

import org.apache.spark.{SparkConf, SparkContext}

/**
  * Created by AnLei on 2017/4/12.
  * 广播变量
  */
object BroadCast {

  def main(args: Array[String]): Unit = {
    val conf = new SparkConf().setAppName("cartesian").setMaster("local")
    val sc = new SparkContext(conf)
    val data = Set(1,4,2,6,8,9,11,13,15,17)
    val bc = sc.broadcast(data)
    val rdd = sc.parallelize(1 to 100, 2)
    val observedSizes = rdd.map(_ => bc.value.size).reduce(_ + _)
    println(observedSizes)
    sc.stop()
  }

}
