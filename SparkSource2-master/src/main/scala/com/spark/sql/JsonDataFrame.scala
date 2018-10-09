package com.spark.sql

import org.apache.spark.SparkConf
import org.apache.spark.sql.SparkSession

/**
  * Created by AnLei on 2017/4/29.
  */
object JsonDataFrame {
  def main(args: Array[String]): Unit = {

    val spark = SparkSession.builder().master("local").appName("JsonDataFrame").getOrCreate()

    // val sc = spark.sparkContext
    // val sqlContext = spark.sqlContext

    // val users = sqlContext.read.json("logs/ml-1m/users.json")
    // val users = spark.read.format("json").load("logs/ml-1m/users.json")
    val df = spark.read.json("logs/ml-1m/users.json")
    df.show(10)
  }
}
