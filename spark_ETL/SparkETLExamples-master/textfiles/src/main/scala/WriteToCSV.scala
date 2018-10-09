package com.datastax.demo

import java.io.{PrintWriter, File}

import org.apache.spark.sql.SQLContext
import org.apache.spark.{SparkContext, SparkConf}

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

/**
  * Created by carybourgeois on 2/16/16.
  */
object WriteToCSV extends App {

  val appName = "WriteToCSV"

  val sparkConf = new SparkConf()
    .set("spark.cores.max", "2")
    .set("spark.executor.memory", "512M")
    .setAppName(appName)
  val sc = SparkContext.getOrCreate(sparkConf)

  val sqlContext = SQLContext.getOrCreate(sc)
  import sqlContext.implicits._
  import org.apache.spark.sql.functions._

  val df = sqlContext
      .read
      .format("org.apache.spark.sql.cassandra")
      .options(Map("table" -> "iris", "keyspace" -> "test"))
      .load()

  df.printSchema()

  df.show()

  println(s"${df.count()} rows processed")

  val rdd = df.rdd

  val pw = new PrintWriter(new File("/home/dseuser/IrisDataOut.csv"))

  for (line <- rdd.toLocalIterator) {pw.println(line)}

  pw.close()


}
