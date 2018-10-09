package com.datastax.demo

/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import org.apache.spark.{SparkContext, SparkConf}
import org.apache.spark.sql.SQLContext

/**
  * Created by carybourgeois on 2/18/16.
  */
object JoinCassJDBC extends App {

  val appName = "JoinCassJDBC"

  val sparkConf = new SparkConf()
    .set("spark.cores.max", "2")
    .set("spark.executor.memory", "512M")
    .setAppName(appName)
  val sc = SparkContext.getOrCreate(sparkConf)

  val sqlContext = SQLContext.getOrCreate(sc)
  import sqlContext.implicits._
  import org.apache.spark.sql.functions._

  val cassDf = sqlContext
    .read
    .format("org.apache.spark.sql.cassandra")
    .options(Map("table" -> "iris", "keyspace" -> "test"))
    .load()


  val cassJDBC = sqlContext
    .read
    .format("org.apache.spark.sql.jdbc")
    .option("url", "jdbc:mysql://localhost:3306/test")
    .option("driver", "com.mysql.jdbc.Driver")
    .option("dbtable", "iris")
    .option("user", "dseuser")
    .option("password", "datastax")
    .load()

  val cassRdd = cassDf.select("species").distinct.rdd

  val interDf = for (line <- cassRdd.toLocalIterator) yield {
    cassJDBC.select("id", "species").filter($"species" === line(0).toString)
  }
  val uniondDF = interDf.reduce(_.unionAll(_))

  uniondDF.show(150)
  println(s"${uniondDF.count()} rows in unionDF")
}
