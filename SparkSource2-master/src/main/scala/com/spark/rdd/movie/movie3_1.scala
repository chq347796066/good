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
  * 得分最高的10部电影
  * (American Beauty (1999),14800)
  * (Star Wars: Episode IV - A New Hope (1977),13321)
  * (Star Wars: Episode V - The Empire Strikes Back (1980),12836)
  * (Star Wars: Episode VI - Return of the Jedi (1983),11598)
  * (Saving Private Ryan (1998),11507)
  * (Raiders of the Lost Ark (1981),11257)
  * (Silence of the Lambs, The (1991),11219)
  * (Matrix, The (1999),11178)
  * (Sixth Sense, The (1999),10835)
  * (Terminator 2: Judgment Day (1991),10751)
  */
object movie3_1 {

  def main(args: Array[String]): Unit = {
    val _split = "::"

    val conf = new SparkConf().setAppName("movie").setMaster("local")
    val sc = new SparkContext(conf)
    val movies = sc.textFile("logs/ml-1m/movies.dat")
    val ratings = sc.textFile("logs/ml-1m/ratings.dat")

    // (movieid,title)
    val moviesRdd = movies.map(_.split(_split)).filter{x => x != null && x.length == 3}.map{x => (x(0),x(1))}
    // (movieid,rating)
    val ratingsRdd = ratings.map(_.split(_split)).filter{x => x != null && x.length == 4}.map{x => (x(1),x(2))}

    // title,sumrating
    ratingsRdd.join(moviesRdd).map{case(movieid, (rating, title)) => (title, rating.toInt)}.reduceByKey(_+_)
      .top(10)(Ordering.by(_._2)).foreach(println)

      // .sortBy(_._2, false).take(10).foreach(println)
      // .map{case(title, rating) => (rating, title)}.sortByKey(false).take(10).map{case(rating, title) => (title, rating)}.foreach(println)

    sc.stop()
  }

}
