package com.spark.ml.feature.transformers

import org.apache.spark.sql.SparkSession
import org.apache.spark.ml.feature.StopWordsRemover

/**
  * Created by AnLei on 2017/5/18.
  *
  * 去除停用词
  * [I, saw, the, red, balloon]
  * StopWordsRemover(transform) -> [saw, red, balloon]
  */
object StopWordsRemoverExample {

  def main(args: Array[String]): Unit = {

    val spark = SparkSession.builder().master("local").appName(s"${this.getClass.getSimpleName}").getOrCreate()

    val remover = new StopWordsRemover()
      .setInputCol("raw")
      .setOutputCol("filtered")

    val dataSet = spark.createDataFrame(Seq(
      (0, Seq("I", "saw", "the", "red", "balloon")),
      (1, Seq("Mary", "had", "a", "little", "lamb"))
    )).toDF("id", "raw")

    remover.transform(dataSet).show(false)

    spark.stop()
  }

}
