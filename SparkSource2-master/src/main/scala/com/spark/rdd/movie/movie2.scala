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
  * 年龄段在“18-24”岁的男性年轻人，最喜欢看哪10部电影
  * (American Beauty (1999),529)
  * (Star Wars: Episode V - The Empire Strikes Back (1980),458)
  * (Star Wars: Episode VI - Return of the Jedi (1983),457)
  * (Star Wars: Episode IV - A New Hope (1977),448)
  * (Matrix, The (1999),447)
  * (Terminator 2: Judgment Day (1991),440)
  * (Braveheart (1995),429)
  * (Saving Private Ryan (1998),427)
  * (Jurassic Park (1993),426)
  * (Star Wars: Episode I - The Phantom Menace (1999),410)
  */
object movie2 {

  def main(args: Array[String]): Unit = {
    val _split = "::"

    val conf = new SparkConf().setAppName("movie").setMaster("local")
    val sc = new SparkContext(conf)
    val users = sc.textFile("logs/ml-1m/users.dat")
    val movies = sc.textFile("logs/ml-1m/movies.dat")
    val ratings = sc.textFile("logs/ml-1m/ratings.dat")

    // (movieid,title)
    val moviesRdd = movies.map(_.split(_split)).filter{x => x != null && x.length == 3}.map{x => (x(0),x(1))}
    // (userid,movieid)
    val ratingsRdd = ratings.map(_.split(_split)).filter{x => x != null && x.length == 4}.map{x => (x(0),x(1))}
    // (userid,age) gender == "M" && age >= 18 && age <= 24
    val usersRdd = users.map(_.split(_split)).filter{x => x != null && x.length == 5 && x(1).equals("M") && x(2).toInt >= 18 && x(2).toInt <= 24}.map{x => (x(0),x(2))}
    // (title,count)
    ratingsRdd.join(usersRdd).map{case(userid, (movieid, age)) => (movieid, age)}.join(moviesRdd).map{case(movieid, (age, title)) => (title, 1)}.reduceByKey(_+_)
      .top(10)(Ordering.by(_._2)).foreach(println)

      // .sortBy(_._2, false).take(10).foreach(println)
      // .map{case(title,count) => (count,title)}.sortByKey(false).take(10).map{case(count,title) => (title,count)}.foreach(println)

    sc.stop()
  }

}
