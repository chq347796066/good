package com.spark.ml.feature.transformers

import org.apache.spark.ml.feature.StandardScaler
import org.apache.spark.sql.SparkSession

/**
  * Created by AnLei on 2017/6/7.
  *
  * StandardScaler
  * 0 128:51 129:159 130:253 131:159
  * VectorIndexer(transform) -> 0.0 (692,[127,128,129,130],[51.0,159.0,253.0,159.0])
  *
  */
object StandardScalerExample {

  def main(args: Array[String]): Unit = {

    val spark = SparkSession.builder().master("local").appName(s"${this.getClass.getSimpleName}").getOrCreate()

    val dataFrame = spark.read.format("libsvm").load("data/mllib/sample_libsvm_data.txt")

    val scaler = new StandardScaler()
      .setInputCol("features")
      .setOutputCol("scaledFeatures")
      .setWithStd(true)
      .setWithMean(false)

    // Compute summary statistics by fitting the StandardScaler.
    val scalerModel = scaler.fit(dataFrame)

    // Normalize each feature to have unit standard deviation.
    val scaledData = scalerModel.transform(dataFrame)
    scaledData.show(false)
  }

}
