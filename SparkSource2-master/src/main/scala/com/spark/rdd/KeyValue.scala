package com.spark.rdd

import org.apache.spark.{SparkConf, SparkContext}

/**
  * Created by AnLei on 2017/4/12.
  * KeyValue的Rdd
  */
object KeyValue {

  def main(args: Array[String]): Unit = {
    val conf = new SparkConf().setAppName("keyvalue").setMaster("local")
    val sc = new SparkContext(conf)
    val pets = sc.parallelize(List(("cat", 1),("dog", 1),("cat", 2)))
    pets.reduceByKey(_+_).foreach(println(_))
    pets.groupByKey().foreach(println(_))
    pets.sortByKey().foreach(println(_))
    sc.stop()
  }

}
