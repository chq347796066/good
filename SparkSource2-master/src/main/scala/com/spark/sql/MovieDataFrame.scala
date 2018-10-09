package com.spark.sql

import org.apache.spark.sql.{SaveMode, SparkSession}

/**
  * Created by AnLei on 2017/4/29.
  */
object MovieDataFrame {
  def main(args: Array[String]): Unit = {

    val spark = SparkSession.builder().master("local").appName("MovieDataFrame").getOrCreate()

    val df = spark.read.json("logs/ml-1m/users.json")
    val df2 = spark.read.json("logs/ml-1m/ratings.json")

    /**
      * show,toJSON,printSchema
      */
    df.show(5)
    df.limit(5).toJSON.foreach(row => println(row))
    df.printSchema()

    /**
      * collect,first,take,head
      */
    df.collect().foreach(row => println(row))
    println(df.first())
    df.take(2).foreach(row => println(row))
    df.head(2).foreach(row => println(row))

    /**
      * select,selectExpr
      */
    df.select("userId", "age").show(5)
    df.selectExpr("userId","ceil(age/10) as newAge").show(5)
    df.selectExpr("max(age)","min(age)","floor(avg(age)) as avg_age").show()

    /**
      * filter
      */
    df.filter(df("age") > 30).show(2)
    df.filter("age > 30 and occupation = 10").show

    /**
      * select + filter
      */
    df.select("userId", "age").filter("age > 30").show(2)
    df.filter("age > 30").select("userId","age").show(2)

    /**
      * groupBy,agg
      */
    df.groupBy("age").count().show()
    df.groupBy("age").agg(("gender","count"),("occupation","count")).show()
    df.groupBy("age").agg("gender" -> "count", "occupation" -> "count").show()

    /**
      * join
      */
    df2.filter("movieId=2116").join(df, "userId").select("gender", "age").groupBy("gender", "age").count().show()
    df2.filter("movieId=2116").join(df, df("userId") === df2("userId"), "inner").select("gender", "age").groupBy("gender", "age").count().show()

    /**
      * table
      */
    df.createOrReplaceTempView("users")
    val userSql = spark.sql("SELECT gender,age,COUNT(*) AS n FROM users GROUP BY gender,age")
    userSql.show()

    /**
      * df rdd
      */
    import spark.implicits._
    df.map{u => (u.getAs[String]("userId").toLong, u.getAs[String]("age").toInt + 1)}.take(10).foreach(println)
  }
}
