package com.spark.ml.feature.transformers

import org.apache.spark.ml.feature.{OneHotEncoder, StringIndexer}
import org.apache.spark.sql.SparkSession

/**
  * Created by AnLei on 2017/5/17.
  *
  * 独热编码
  * [a | b | c | a | a | c]
  * OneHotEncoder(transform) -> [(2,[0],[1.0]) | (2,[],[]) | (2,[1],[1.0]) |
  *                             (2,[0],[1.0]) | (2,[0],[1.0]) | (2,[1],[1.0])]
  *
  */
object OneHotEncoderExample {

  def main(args: Array[String]): Unit = {

    val spark = SparkSession.builder().master("local").appName(s"${this.getClass.getSimpleName}").getOrCreate()

    // $example on$
    val df = spark.createDataFrame(Seq(
      (0, "a"),
      (1, "b"),
      (2, "c"),
      (3, "a"),
      (4, "a"),
      (5, "c")
    )).toDF("id", "category")

    val indexer = new StringIndexer()
      .setInputCol("category")
      .setOutputCol("categoryIndex")
      .fit(df)
    val indexed = indexer.transform(df)

    val encoder = new OneHotEncoder()
      .setInputCol("categoryIndex")
      .setOutputCol("categoryVec")

    val encoded = encoder.transform(indexed)
    encoded.show()
    // $example off$

    spark.stop()
  }
}
