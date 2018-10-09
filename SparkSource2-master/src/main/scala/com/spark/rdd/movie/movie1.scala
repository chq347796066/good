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
  * 看过“Lord of the Rings, The (1978)”用户性别分布
  * (M,376)
  * (F,62)
  *
  * 看过“Lord of the Rings, The (1978)”用户年龄分布
  * (18,81)
  * (35,79)
  * (45,29)
  * (56,10)
  * (50,25)
  * (25,197)
  * (1,17)
  */
object movie1 {

  def main(args: Array[String]): Unit = {
    val _split = "::"
    val _title = "Lord of the Rings, The (1978)"

    val conf = new SparkConf().setAppName("movie").setMaster("local")
    val sc = new SparkContext(conf)
    val users = sc.textFile("logs/ml-1m/users.dat")
    val movies = sc.textFile("logs/ml-1m/movies.dat")
    val ratings = sc.textFile("logs/ml-1m/ratings.dat")

    // (movieid,title) title = Lord of the Rings, The (1978)
    val moviesRdd = movies.map(_.split(_split)).filter{x => x != null && x.length == 3 && x(1).equals(_title)}.map{x => (x(0),x(1))}
    // (movieid,userid)
    val ratingsRdd = ratings.map(_.split(_split)).filter{x => x != null && x.length == 4}.map{x => (x(1),x(0))}
    // (userid,gender,age)
    val usersRdd = users.map(_.split(_split)).filter{x => x != null && x.length == 5}.map{x => (x(0),(x(1),x(2)))}
    // (gender,age)
    val ratMov=ratingsRdd.join(moviesRdd)
    val genderAgeRdd = ratingsRdd.join(moviesRdd).map{case(movieid, (userid, title)) => (userid, title)}.join(usersRdd)
      .map{case(userid, (title, (gender,age))) => (gender,age)}

    genderAgeRdd.cache()

    // gender result
    genderAgeRdd.map{case(gender,age) => (gender, 1)}.reduceByKey(_+_).foreach(println)
    // age result
    genderAgeRdd.map{case(gender,age) => (age, 1)}.reduceByKey(_+_).foreach(println)

    sc.stop()
  }

}
