package com.spark.ml.feature.transformers

import org.apache.spark.sql.SparkSession
import org.apache.spark.ml.feature.Binarizer

/**
  * Created by AnLei on 2017/5/18.
  *
  * 二值化
  * [0.1 | 0.8 | 0.2 | 10.1]
  * Binarizer(transform) -> [0.0 | 1.0 | 0.0 | 1.0]
  */
object BinarizerExample {

  def main(args: Array[String]): Unit = {

    val spark = SparkSession.builder().master("local").appName(s"${this.getClass.getSimpleName}").getOrCreate()

    val data = Array((0, 0.1), (1, 0.8), (2, 0.2), (3, 10.1))
    val dataFrame = spark.createDataFrame(data).toDF("id", "feature")

    val binarizer: Binarizer = new Binarizer()
      .setInputCol("feature")
      .setOutputCol("binarized_feature")
      .setThreshold(0.5)

    val binarizedDataFrame = binarizer.transform(dataFrame)

    println(s"Binarizer output with Threshold = ${binarizer.getThreshold}")
    binarizedDataFrame.show()
  }

}
