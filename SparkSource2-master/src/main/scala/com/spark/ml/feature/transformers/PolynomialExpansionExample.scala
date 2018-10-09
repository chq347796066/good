package com.spark.ml.feature.transformers

import org.apache.spark.sql.SparkSession
import org.apache.spark.ml.feature.PolynomialExpansion
import org.apache.spark.ml.linalg.Vectors

/**
  * Created by AnLei on 2017/5/18.
  *
  * 多项式核转换
  * [2.0,1.0]
  * PolynomialExpansion(transform) -> [2.0,4.0,8.0,1.0,2.0,4.0,1.0,2.0,1.0]
  *
  */
object PolynomialExpansionExample {

  def main(args: Array[String]): Unit = {

    val spark = SparkSession.builder().master("local").appName(s"${this.getClass.getSimpleName}").getOrCreate()

    val data = Array(
      Vectors.dense(2.0, 1.0),
      Vectors.dense(0.0, 0.0),
      Vectors.dense(3.0, -1.0)
    )
    val df = spark.createDataFrame(data.map(Tuple1.apply)).toDF("features")

    val polyExpansion = new PolynomialExpansion()
      .setInputCol("features")
      .setOutputCol("polyFeatures")
      .setDegree(3)

    val polyDF = polyExpansion.transform(df)
    polyDF.show(false)
  }

}
