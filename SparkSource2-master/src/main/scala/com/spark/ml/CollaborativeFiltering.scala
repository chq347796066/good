package com.spark.ml

import org.apache.spark.sql.SparkSession

/**
  * Created by AnLei on 2017/7/14.
  *
  * CollaborativeFiltering
  */
object CollaborativeFiltering {

  def main(args: Array[String]): Unit = {

    val spark = SparkSession.builder().master("local").appName(s"${this.getClass.getSimpleName}").getOrCreate()
  }

}
