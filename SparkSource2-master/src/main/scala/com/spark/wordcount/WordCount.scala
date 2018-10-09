package com.spark.wordcount

import org.apache.spark.{SparkConf, SparkContext}

/**
  * Created by AnLei on 2017/4/12.
  * 单词计数
  */
object WordCount {

  def main(args: Array[String]): Unit = {
    val conf = new SparkConf().setAppName(this.getClass.getSimpleName()).setMaster("local")
    val sc = new SparkContext(conf)

//    val data = sc.parallelize(List("hadoop","hdfs","mapreduce","hbase","spark","hadoop","hdfs","hbase","storm","hdfs","storm","hbase"))
//    val result = data.flatMap(_.split(" ")).map((_, 1)).reduceByKey(_ + _)
    val c=sc.parallelize(List((3,"Gnu"),(3,"Yak"),(5,"Mouse"),(3,"Dog")),2)
    val intToLong: collection.Map[Int, Long] = c.countByKey()
    println(intToLong)
    sc.stop()
  }
}
