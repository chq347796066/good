package com.spark.ml.pipelines

import org.apache.spark.ml.classification.LogisticRegression
import org.apache.spark.ml.feature.{HashingTF, Tokenizer}
import org.apache.spark.ml.linalg.Vector
import org.apache.spark.sql.{Row, SparkSession}

/**
  * Created by AnLei on 2017/5/17.
  * 逻辑回归 - 非管道
  */
object PipelineWithoutExample {

  def main(args: Array[String]): Unit = {

    val spark = SparkSession.builder().master("local").appName(s"${this.getClass.getSimpleName}").getOrCreate()

    val training = spark.createDataFrame(Seq(
      (0L, "a b c d e spark", 1.0),
      (1L, "b d", 0.0),
      (2L, "spark f g h", 1.0),
      (3L, "hadoop mapreduce", 0.0)
    )).toDF("id", "text", "label")

    val tokenizer = new Tokenizer().setInputCol("text").setOutputCol("words")
    val wordsData = tokenizer.transform(training)

    val hashingTF = new HashingTF().setNumFeatures(1000).setInputCol(tokenizer.getOutputCol).setOutputCol("features")
    val featurizedData = hashingTF.transform(wordsData)

    val lr = new LogisticRegression().setMaxIter(10).setRegParam(0.001)
    val model = lr.fit(featurizedData)

    val test = spark.createDataFrame(Seq(
      (4L, "spark i j k"),
      (5L, "l m n"),
      (6L, "spark hadoop spark"),
      (7L, "apache hadoop")
    )).toDF("id", "text")

    val tokenizerTest = new Tokenizer().setInputCol("text").setOutputCol("words")
    val wordsDataTest = tokenizerTest.transform(test)

    val hashingTFTest = new HashingTF().setNumFeatures(1000).setInputCol(tokenizerTest.getOutputCol).setOutputCol("features")
    val featurizedDataTest = hashingTFTest.transform(wordsDataTest)

    model.transform(featurizedDataTest)
      .select("id", "text", "probability", "prediction")
      .collect()
      .foreach { case Row(id: Long, text: String, prob: Vector, prediction: Double) =>
        println(s"($id, $text) --> prob=$prob, prediction=$prediction")
      }

    spark.stop()
  }

}
