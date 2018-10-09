package com.spark.ml.feature.transformers

import org.apache.spark.ml.feature.NGram
import org.apache.spark.sql.SparkSession

/**
  * Created by AnLei on 2017/5/16.
  *
  * 相邻词搭配
  * [Hi, I, heard, about, Spark]
  * NGram(transform) -> [Hi I, I heard, heard about, about Spark]
  */
object NGramExample {

  def main(args: Array[String]): Unit = {

    val spark = SparkSession.builder().master("local").appName(s"${this.getClass.getSimpleName}").getOrCreate()

    // $example on$
    val wordDataFrame = spark.createDataFrame(Seq(
      (0, Array("Hi", "I", "heard", "about", "Spark")),
      (1, Array("I", "wish", "Java", "could", "use", "case", "classes")),
      (2, Array("Logistic", "regression", "models", "are", "neat"))
    )).toDF("id", "words")

    val ngram = new NGram().setN(2).setInputCol("words").setOutputCol("ngrams")

    val ngramDataFrame = ngram.transform(wordDataFrame)
    ngramDataFrame.select("id","words","ngrams").show(false)
    // $example off$

    spark.stop()
  }

}
