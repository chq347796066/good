package com.spark.sql

import org.apache.spark.SparkConf
import org.apache.spark.sql.SparkSession

/**
  * Created by AnLei on 2017/4/29.
  */
object ParquetDataFrame {
  def main(args: Array[String]): Unit = {

    val spark = SparkSession.builder().master("local").appName("ParquetDataFrame").getOrCreate()

    // val sc = spark.sparkContext
    // val sqlContext = spark.sqlContext

    // val users = sqlContext.read.parquet("logs/ml-1m/users.parquet")
    // val users = spark.read.format("parquet").load("logs/ml-1m/users.parquet")
    val df = spark.read.parquet("logs/ml-1m/users.parquet")
    df.show(10)
  }
}
