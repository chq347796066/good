package com.spark.rdd.movie

import org.apache.spark.{SparkConf, SparkContext}

/**
  * Created by AnLei on 2017/4/15.
  *
  * 已知列
  * store,product,amount,units
  *
  * 实现
  * Select store,product,SUM(amount),MIN(amount),MAX(amount),SUM(units) FROM
  * salse GROUP BY store,product
  */
object movie4 {

  def main(args: Array[String]): Unit = {

    val conf = new SparkConf().setAppName("movie").setMaster("local")
    val sc = new SparkContext(conf)
    val sales = sc.parallelize(List(
          ("West", "Apple", 2.0, 10),
          ("West", "Apple", 3.0, 15),
          ("West", "Orange", 5.0, 15),
          ("South", "Orange", 3.0, 9),
          ("South", "Orange", 6.0, 18),
          ("East", "Milk", 5.0, 5)))
    sales.map{case(store,product,amount,units) => ((store,product),(amount,amount,amount,units))}
      .reduceByKey((x,y) => (x._1 + y._1,math.min(x._2, y._2),math.max(x._3, y._3),x._4 + y._4))
      .foreach(println)
    sc.stop()
  }

}
