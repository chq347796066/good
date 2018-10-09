package com.spark.sql

import org.apache.spark.sql.SparkSession

/**
  * Created by AnLei on 2017/4/29.
  */
object TypeChanges {
  case class Users(userID: String, gender: String, age: String, occupation: String, zipcode: String)

  def main(args: Array[String]): Unit = {
    val spark = SparkSession.builder().master("local").appName("TypeChanges").getOrCreate()

    import spark.implicits._

    val df = spark.read.json("logs/ml-1m/users.json") // DataFrame
    val ds = df.as[Users] // DataFrame -> DataSet
    val df2 = ds.toDF() // DataSet -> DataFrame
    var rdd = df.rdd  // DataFrame -> RDD
    var rdd2 = ds.rdd // DataSet -> RDD
  }
}
