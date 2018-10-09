package com.spark.ml.feature.transformers

import org.apache.spark.ml.feature.StringIndexer
import org.apache.spark.sql.SparkSession
/**
  * Created by AnLei on 2017/5/18.
  *
  * 字符串索引
  * [a | b | c | a | a | c]
  * StringIndexer(fit) -> [0.0 | 2.0 | 1.0 | 0.0 | 0.0 | 1.0]
  *
  */
object StringIndexerExample {

  def main(args: Array[String]): Unit = {

    val spark = SparkSession.builder().master("local").appName(s"${this.getClass.getSimpleName}").getOrCreate()

    val df = spark.createDataFrame(
      Seq((0, "a"), (1, "b"), (2, "c"), (3, "a"), (4, "a"), (5, "c"))
    ).toDF("id", "category")

    val indexer = new StringIndexer()
      .setInputCol("category")
      .setOutputCol("categoryIndex")

    val indexed = indexer.fit(df).transform(df)
    indexed.show()
  }

}
