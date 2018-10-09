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

import java.sql.{DriverManager, ResultSet}
import java.util.Properties

import com.datastax.spark.connector.cql.CassandraConnector
import org.apache.spark.sql.cassandra.CassandraSQLContext
import org.apache.spark.{SparkContext, SparkConf}
import org.apache.spark.sql.{SaveMode, SQLContext}
import org.apache.spark.rdd.JdbcRDD

/**
  * Created by carybourgeois on 2/15/16.
  */
object LoadFromJDBC extends App {

  val appName = "LoadFromJDBC"

  val sparkConf = new SparkConf()
    .set("spark.cores.max", "2")
    .set("spark.executor.memory", "512M")
    .setAppName(appName)

  // create the spark context
  val sc = SparkContext.getOrCreate(sparkConf)

  // create a new SparkSQLContext
  val csc = new CassandraSQLContext(sc)

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
    session.execute(s"CREATE KEYSPACE IF NOT EXISTS test WITH REPLICATION = { 'class':'SimpleStrategy', 'replication_factor':3}")

    /*
        Below the data table is DROPped and re-CREATEd to ensure that we are dealing with new data.
     */

    session.execute(s"DROP TABLE IF EXISTS test.iris")
    session.execute(s"CREATE TABLE IF NOT EXISTS test.iris (id int, petal_l double, petal_w double, sepal_l double, sepal_w double, species text, PRIMARY KEY ((id)) )")

    //Close the native Cassandra session when done with it. Otherwise, we get some nasty messages in the log.
    session.close()
  }


  /*
   * In this section we create an RDD of type JdbcRDD. This entire process is handled in the
   * simple act of creating the RDD object. It is VERY important to understand each of the parameters
   * in the object creation call.
   *
   *  1. sc -> is the Spark Context created at the beginning of the program and passed to this function
   *  2. () -> JDBC Driver Manager specification. The details of this call will be specific to the JDBC
   *           driver being used. This on corresponds to the version of MySQL we setup earlier.'
   *  3. SQL -> This is the SELECT command that will be passed to the database. NOTICE the WHERE clause
   *            and specifically the primary key range that is specified. This plays a key roll in how
   *            Spark will partition the results. Each partition will query the database individually
   *            passing in the SQL that represents the range of Primary keys for that partition.
   *  4. 1 -> The minimum value of the partition key used in the SQL command.
   *  5. 150 -> The maximum value of the partition key used inthe SQL command.
   *  6. 3 -> The number of Spark partitions to divide the keys across.
   *  7. (r: ResultSet) -> Mapping function that translates the returned rows to an standard RDD.
   */

  case class Iris(id: Int, petal_l: Double, petal_w: Double, sepal_l:  Double, sepal_w: Double, species: String)

  val rdd = new JdbcRDD(
    sc,
    () => { DriverManager.getConnection("jdbc:mysql://10.0.0.3:3306/test", "dseuser", "datastax") },
    "SELECT id, petal_l, petal_w, sepal_l, sepal_w, species value FROM iris WHERE ? <= id AND id <= ?",
    1, 150, 3,
    (r: ResultSet) => { Iris(r.getInt(1), r.getDouble(2), r.getDouble(3), r.getDouble(4), r.getDouble(5), r.getString(6)) } )

  val url = "jdbc:mysql://10.0.0.3:3306/test"
  val prop = new java.util.Properties()
  prop.setProperty("user", "dseuser")
  prop.setProperty("password", "datastax")
  val df = sqlContext.read.jdbc("jdbc:mysql://10.0.0.3:3306/test", "iris", prop)

  /*
   * Save the RDD to Cassandra Keyspace and Table previously prepared.
   */
//  val df = rdd.toDF()
  df
      .write
      .format("org.apache.spark.sql.cassandra")
      .mode(SaveMode.Append)
      .options(Map("keyspace" -> "test", "table" -> "iris"))
      .save()

  df.show(5)
  println(s"${df.count()} rows processed")

}

