package com.spark.rdd

import org.apache.spark.{SparkConf, SparkContext}

import scala.math._

/**
  * Created by AnLei on 2017/4/12.
  * 累加器
  */
object Accumulator {

  def main(args: Array[String]): Unit = {
    val conf = new SparkConf().setAppName("accumulator").setMaster("local")
    val sc = new SparkContext(conf)

    val total_counter = sc.accumulator(0L, "total_counter")
    val counter0 = sc.accumulator(0L, "counter0")
    val counter1 = sc.accumulator(0L, "counter1")

    val count = sc.parallelize(1 to 100, 3).map(
      i => {
        total_counter += 1
        val x = random * 2 - 1
        val y = random * 2 - 1
        if(x * x + y * y < 1) {
          counter1 += 1
        } else {
          counter0 += 1
        }
        if(x * x + y * y < 1) 1 else 0
      }
    ).reduce(_+_)

    println(total_counter + " " + counter1 + " " + counter0)
    sc.stop()
  }

}
