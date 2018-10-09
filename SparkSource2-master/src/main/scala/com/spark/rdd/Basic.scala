package com.spark.rdd

import org.apache.spark.{SparkConf, SparkContext}

/**
  * Created by AnLei on 2017/4/12.
  * Basic
  */
object Basic {

  def main(args: Array[String]): Unit = {
    val conf = new SparkConf().setAppName("basic").setMaster("local")
    val sc = new SparkContext(conf)
    val listRdd = sc.parallelize(List(1,2,3),1)
    listRdd.map(x => x * x).foreach(print(_))
    listRdd.filter(_ % 2 == 0).foreach(print(_))
    listRdd.flatMap(x => 1 to x).foreach(print(_))
    sc.stop()
  }

}
