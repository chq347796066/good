package com.spark.rdd

import org.apache.spark.{SparkConf, SparkContext}

/**
  * Created by AnLei on 2017/4/12.
  * Union
  */
object Union {

  def main(args: Array[String]): Unit = {
    val conf = new SparkConf().setAppName("union").setMaster("local")
    val sc = new SparkContext(conf)
    val visits = sc.parallelize(List(("index.html","1.2.3.4"),("about.html","3.4.5.6"),("index.html","1.3.3.1")))
    val pageNames = sc.parallelize(List(("index.html","Home"),("about.html","About")))
    visits.union(pageNames).foreach(println(_))
    sc.stop()
  }

}
