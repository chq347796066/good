package com.spark.wordcount

import org.apache.spark.{SparkConf, SparkContext}

/**
  * Created by AnLei on 2017/4/12.
  * 单词计数
  */
object WordCountServer {

  def main(args: Array[String]): Unit = {
    val conf = new SparkConf().setAppName(this.getClass.getSimpleName())//.setMaster("local")
    val sc = new SparkContext(conf)

    val path = args(0)

    val log = sc.textFile(path)
    val result = log.flatMap(_.split(" ")).map((_, 1)).reduceByKey(_ + _)

    result.collect().foreach(println(_))
    sc.stop()
  }
}
