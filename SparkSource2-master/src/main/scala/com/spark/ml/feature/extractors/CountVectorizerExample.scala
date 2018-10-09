package com.spark.ml.feature.extractors

import org.apache.spark.ml.feature.{CountVectorizer, CountVectorizerModel}
import org.apache.spark.sql.SparkSession

/**
  * Created by AnLei on 2017/5/18.
  *
  * 文本特征提取
  * [a, b, c]
  * CountVectorizer(fit) -> (3,[0,1,2],[1.0,1.0,1.0])
  */
object CountVectorizerExample {

  def main(args: Array[String]): Unit = {

    val spark = SparkSession.builder().master("local").appName(s"${this.getClass.getSimpleName}").getOrCreate()

    val df = spark.createDataFrame(Seq(
      (0, Array("a", "b", "c")),
      (1, Array("a", "b", "b", "c", "a")),
      (2,Array("f","e","h","x"))
    ))
      .toDF("id", "words")

    // fit a CountVectorizerModel from the corpus
    val cvModel: CountVectorizerModel = new CountVectorizer()
      .setInputCol("words")
      .setOutputCol("features")
      .setVocabSize(3)
      .setMinDF(2)
      .fit(df)

    // alternatively, define CountVectorizerModel with a-priori vocabulary
    val cvm = new CountVectorizerModel(Array("a", "b", "c"))
      .setInputCol("words")
      .setOutputCol("features")
    println("===================")
    cvModel.transform(df).show(false)

    spark.stop()
  }

}
