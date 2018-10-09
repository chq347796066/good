package com.spark.ml.feature.transformers

import org.apache.spark.sql.SparkSession
import org.apache.spark.ml.feature.PCA
import org.apache.spark.ml.linalg.Vectors

/**
  * Created by AnLei on 2017/5/18.
  *
  * 特征降维
  * [2.0,0.0,3.0,4.0,5.0]
  * PCA(transform) -> [-2.4283882256838014,-1.520613640803106,2.1088849435651986]
  *
  */
object PCAExample {

  def main(args: Array[String]): Unit = {
    val spark = SparkSession.builder().master("local").appName(s"${this.getClass.getSimpleName}").getOrCreate()

    val data = Array(
      Vectors.sparse(5, Seq((1, 1.0), (3, 7.0), (2, 5.0))),
      Vectors.dense(2.0, 0.0, 3.0, 4.0, 5.0),
      Vectors.dense(4.0, 0.0, 0.0, 6.0, 7.0)
    )
    val df = spark.createDataFrame(data.map(Tuple1.apply)).toDF("features")

    val pca = new PCA()
      .setInputCol("features")
      .setOutputCol("pcaFeatures")
      .setK(3)
      .fit(df)

    val result = pca.transform(df).select("features", "pcaFeatures")
    result.show(false)
  }

}
