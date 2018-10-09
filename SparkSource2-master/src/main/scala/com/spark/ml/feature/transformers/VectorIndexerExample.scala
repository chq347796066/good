package com.spark.ml.feature.transformers

import org.apache.spark.ml.feature.VectorIndexer
import org.apache.spark.sql.SparkSession

/**
  * Created by AnLei on 2017/6/3.
  *
  * 向量索引
  * 0 128:51 129:159 130:253 131:159
  * VectorIndexer(transform) -> 0.0 (692,[127,128,129,130],[51.0,159.0,253.0,159.0])
  *
  */
object VectorIndexerExample {

  def main(args: Array[String]): Unit = {

    val spark = SparkSession.builder().master("local").appName(s"${this.getClass.getSimpleName}").getOrCreate()

    val data = spark.read.format("libsvm").load("data/mllib/sample_libsvm_data.txt")

    val indexer = new VectorIndexer()
      .setInputCol("features")
      .setOutputCol("indexed")
      .setMaxCategories(10)

    val indexerModel = indexer.fit(data)

    val categoricalFeatures: Set[Int] = indexerModel.categoryMaps.keys.toSet
    println(s"Chose ${categoricalFeatures.size} categorical features: " +
      categoricalFeatures.mkString(", "))

    // Create new column "indexed" with categorical values transformed to indices
    val indexedData = indexerModel.transform(data)
    indexedData.show(false)
  }

}
