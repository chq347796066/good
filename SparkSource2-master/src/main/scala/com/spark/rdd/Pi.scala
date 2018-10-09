package com.spark.rdd

import org.apache.spark.{SparkConf, SparkContext}

import scala.math.random

/**
  * Created by AnLei on 2017/4/12.
  * Pi
  */
object Pi {

  def main(args: Array[String]): Unit = {
    val conf = new SparkConf().setAppName("pi").setMaster("local")
    val sc = new SparkContext(conf)
    val slices = 2
    val n = 1000000 * slices
    val count = sc.parallelize(1 to n, slices).map(
      i => {
        val x = random * 2 - 1
        val y = random * 2 - 1
        if(x * x + y * y < 1) 1 else 0
      }
    ).reduce(_+_)
    println("pi :" + 4.0 * count / n)
    sc.stop()
  }

}
