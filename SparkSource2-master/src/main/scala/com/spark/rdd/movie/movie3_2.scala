package com.spark.rdd.movie

import org.apache.spark.{SparkConf, SparkContext}

/**
  * Created by AnLei on 2017/4/14.
  *
  * users.dat
  * userid,gender,age,occupation,zip-code
  * movies.dat
  * movieid,title,genres
  * ratings.dat
  * userid,movieid,rating,timestamp
  *
  * 看过电影最多的前10个人
  * (4169,2314)
  * (1680,1850)
  * (4277,1743)
  * (1941,1595)
  * (1181,1521)
  * (889,1518)
  * (3618,1344)
  * (2063,1323)
  * (1150,1302)
  * (1015,1286)
  */
object movie3_2 {

  def main(args: Array[String]): Unit = {
    val _split = "::"

    val conf = new SparkConf().setAppName("movie").setMaster("local")
    val sc = new SparkContext(conf)
    val users = sc.textFile("logs/ml-1m/users.dat")
    val ratings = sc.textFile("logs/ml-1m/ratings.dat")

    // (userid,movieid)
    val ratingsRdd = ratings.map(_.split(_split)).filter{x => x != null && x.length == 4}.map{x => (x(0),x(1))}
    // (userid,age)
    val usersRdd = users.map(_.split(_split)).filter{x => x != null && x.length == 5}.map{x => (x(0),x(2))}
    // (userid,count)
    ratingsRdd.join(usersRdd).map{case(userid, (movieid, age)) => (userid, 1)}.reduceByKey(_+_)
      .top(10)(Ordering.by(_._2)).foreach(println)

      // .sortBy(_._2, false).take(10).foreach(println)
      // .map{case(userid, count) => (count, userid)}.sortByKey(false).take(10).map{case(count, userid) => (userid, count)}.foreach(println)

    sc.stop()
  }

}
