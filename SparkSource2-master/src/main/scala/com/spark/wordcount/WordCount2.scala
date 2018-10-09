package com.spark.wordcount

import org.apache.spark.{SparkConf, SparkContext}

/**
  * Created by AnLei on 2017/4/12.
  * 单词计数
  */
object WordCount2 {

  def main(args: Array[String]): Unit = {
    val conf = new SparkConf().setAppName(this.getClass.getSimpleName()).setMaster("local")
    val sc = new SparkContext(conf)
    val log = sc.textFile("logs/wordcount.log")

    // val result = log.flatMap(_.split(" ")).map((_,1)).reduceByKey(_+_)
    // val result = log.flatMap(line => line.split(" ")).map(word => (word, 1)).reduceByKey((a, b) => a + b)
    // result.foreach(println _)

    val result = log.flatMap(line => line.split(" ")).map(word => (word, 1)).groupByKey().map(m => {
      val word = m._1
      var count = 0
      m._2.foreach(item => {
        count += 1
      })
      (count, word)
    }).sortByKey(false).map{case(count,word) => (word, count)} //.map(m => (m._2, m._1))
    result.foreach(item => {println(item._1 + " - " + item._2)}) // result.foreach(println _) // result.foreach(item => {println(item)})
    sc.stop()
  }
}
