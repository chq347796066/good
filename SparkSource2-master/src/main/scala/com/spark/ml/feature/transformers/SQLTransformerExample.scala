package com.spark.ml.feature.transformers

import org.apache.spark.ml.feature.SQLTransformer
import org.apache.spark.sql.SparkSession

/**
  * Created by AnLei on 2017/6/13.
  *
  * SQLTransformer
  *
  */
object SQLTransformerExample {

  def main(args: Array[String]): Unit = {
    val spark = SparkSession.builder().master("local").appName(s"${this.getClass.getSimpleName}").getOrCreate()

    val df = spark.createDataFrame(
      Seq((0, 1.0, 3.0), (2, 2.0, 5.0))).toDF("id", "v1", "v2")

    val sqlTrans = new SQLTransformer().setStatement(
      "SELECT *, (v1 + v2) AS v3, (v1 * v2) AS v4 FROM __THIS__")

    sqlTrans.transform(df).show()
  }

}
