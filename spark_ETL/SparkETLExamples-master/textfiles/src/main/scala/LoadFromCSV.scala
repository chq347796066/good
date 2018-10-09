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

import com.datastax.spark.connector.cql.CassandraConnector
import org.apache.spark.sql.{SaveMode, SQLContext}
import org.apache.spark.{SparkContext, SparkConf}

import scala.io.Source

/**
  * Created by carybourgeois on 2/16/16.
  */
object LoadFromCSV extends App {

  val appName = "LoadFromCSV"

  val sparkConf = new SparkConf()
    .set("spark.cores.max", "2")
    .set("spark.executor.memory", "512M")
    .setAppName(appName)
  val sc = SparkContext.getOrCreate(sparkConf)

  val sqlContext = SQLContext.getOrCreate(sc)
  import sqlContext.implicits._
  import org.apache.spark.sql.functions._

  /*
    *   In this section we create a native session to Cassandra.
    *   This is done so that native CQL statements can be executed against the cluster.
    */
  CassandraConnector(sparkConf).withSessionDo { session =>
    /*
     * Make sure that the keyspace we want to use exists and if not create it.
     *
     * Change the topology an replication factor to suit your cluster.
     */
    session.execute(s"CREATE KEYSPACE IF NOT EXISTS test WITH replication = {'class' : 'SimpleStrategy', 'replication_factor' : 3}")

    /*
        Below the data table is DROPped and re-CREATEd to ensure that we are dealing with new data.
     */

    session.execute(s"DROP TABLE IF EXISTS test.iris")
    session.execute(s"CREATE TABLE IF NOT EXISTS test.iris (id int, petal_l double, petal_w double, sepal_l double, sepal_w double, species text, PRIMARY KEY ((id)) )")

    //Close the native Cassandra session when done with it. Otherwise, we get some nasty messages in the log.
    session.close()
  }

  case class Iris(id: Int, petal_l: Double, petal_w: Double, sepal_l:  Double, sepal_w: Double, species: String)

  val rdd = sc.parallelize(Source.fromFile("/home/dseuser/IrisData.csv").getLines.toSeq)

//  val df = sc.textFile("/home/dseuser/IrisData.csv")
  val df = rdd
      .map(line => line.split(","))
      .map(r => Iris(r(0).toInt, r(1).toDouble, r(2).toDouble, r(3).toDouble, r(4).toDouble, r(5).toString))
      .toDF()

  df
    .write
    .format("org.apache.spark.sql.cassandra")
    .mode(SaveMode.Append)
    .options(Map("keyspace" -> "test", "table" -> "iris"))
    .save()

  df.printSchema()

  df.show()
  println(s"${df.count()} rows processed")

}
