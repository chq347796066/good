package com.spark.sql

import org.apache.spark.SparkConf
import org.apache.spark.sql.{Row, SaveMode, SparkSession}
import org.apache.spark.sql.types.{StringType, StructField, StructType}

/**
  * Created by AnLei on 2017/4/29.
  */
object RddDataFrame {
  def main(args: Array[String]): Unit = {

    val conf = new SparkConf().setMaster("local")

    val spark = SparkSession
      .builder()
      .appName("RddDataFrame")
      .config(conf)
      .getOrCreate()

    val sc = spark.sparkContext
    val sqlContext = spark.sqlContext

    val users = sc.textFile("logs/ml-1m/users.dat")
    val schemaString = "userId gender age occupation zipcode"
    val schema = StructType(schemaString.split(" ").map(fieldName => StructField(fieldName, StringType, true)))
    val usersRDD = users.map(_.split("::")).map(p => Row(p(0), p(1).trim, p(2).trim, p(3).trim, p(4).trim))
    // usersRDD.take(10).foreach(println)
    val df = sqlContext.createDataFrame(usersRDD, schema)

    val ratings = sc.textFile("logs/ml-1m/ratings.dat")
    val schemaString2 = "userId movieId rating timestamp"
    val schema2 = StructType(schemaString2.split(" ").map(fieldName => StructField(fieldName, StringType, true)))
    val ratingsRDD = ratings.map(_.split("::")).map(p => Row(p(0), p(1), p(2).trim, p(3).trim))
    // ratingsRDD.take(10).foreach(println)
    val df2 = sqlContext.createDataFrame(ratingsRDD, schema2)

    df.write.mode(SaveMode.Overwrite).json("logs/ml-1m/users.json")
    df.write.mode(SaveMode.Overwrite).parquet("logs/ml-1m/users.parquet")
    df2.write.mode(SaveMode.Overwrite).json("logs/ml-1m/ratings.json")
  }
}
