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
  * 女(男)性看过最多的10部电影
  * (M,American Beauty (1999),2482)
  * (M,Star Wars: Episode IV - A New Hope (1977),2344)
  * (M,Star Wars: Episode V - The Empire Strikes Back (1980),2342)
  * (M,Star Wars: Episode VI - Return of the Jedi (1983),2230)
  * (M,Terminator 2: Judgment Day (1991),2193)
  * (M,Jurassic Park (1993),2099)
  * (M,Saving Private Ryan (1998),2078)
  * (M,Matrix, The (1999),2076)
  * (M,Men in Black (1997),2000)
  * (M,Back to the Future (1985),1944)
  * (F,American Beauty (1999),946)
  * (F,Shakespeare in Love (1998),798)
  * (F,Silence of the Lambs, The (1991),706)
  * (F,Sixth Sense, The (1999),664)
  * (F,Groundhog Day (1993),658)
  * (F,Fargo (1996),657)
  * (F,Star Wars: Episode VI - Return of the Jedi (1983),653)
  * (F,Star Wars: Episode V - The Empire Strikes Back (1980),648)
  * (F,Star Wars: Episode IV - A New Hope (1977),647)
  * (F,Forrest Gump (1994),644)
  */
object movie3_3 {

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
    // (userid,gender)
    val usersRdd = users.map(_.split(_split)).filter{x => x != null && x.length == 5}.map{x => (x(0),x(1))}
    // (gender,title,count)
    ratingsRdd.join(usersRdd).map{case(userid, (movieid, gender)) => (movieid, gender)}.join(moviesRdd).map{case(movieid, (gender, title)) => ((title,gender), 1)}
      .reduceByKey(_+_).sortBy(_._2, false)
      .groupBy(_._1._2)
      .map {
        x => (x._2.take(10))
      }.flatMap{x => x}.map{case((title, gender), count) => (gender, title, count)}.foreach(println)

    sc.stop()
  }

}
