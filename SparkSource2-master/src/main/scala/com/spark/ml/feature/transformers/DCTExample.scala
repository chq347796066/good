package com.spark.ml.feature.transformers

import org.apache.spark.sql.SparkSession
import org.apache.spark.ml.feature.DCT
import org.apache.spark.ml.linalg.Vectors

/**
  * Created by AnLei on 2017/5/18.
  *
  * 离散余弦变换
  * [0.0,1.0,-2.0,3.0]
  * DCT(transform) -> [1.0,-1.1480502970952693,2.0000000000000004,-2.7716385975338604]
  *
  */
object DCTExample {

  def main(args: Array[String]): Unit = {

    val spark = SparkSession.builder().master("local").appName(s"${this.getClass.getSimpleName}").getOrCreate()

    val data = Seq(
      Vectors.dense(0.0, 1.0, -2.0, 3.0),
      Vectors.dense(-1.0, 2.0, 4.0, -7.0),
      Vectors.dense(14.0, -2.0, -5.0, 1.0))

    val df = spark.createDataFrame(data.map(Tuple1.apply)).toDF("features")

    val dct = new DCT()
      .setInputCol("features")
      .setOutputCol("featuresDCT")
      .setInverse(false)

    val dctDf = dct.transform(df)
    dctDf.select("features", "featuresDCT").show(false)
  }

}
