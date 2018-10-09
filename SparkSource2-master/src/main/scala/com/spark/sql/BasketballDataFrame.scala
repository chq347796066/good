package com.spark.sql

import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.fs.{FileSystem, Path}
import org.apache.spark.sql.types.{StructType, _}
import org.apache.spark.sql.{Row, SparkSession}

/**
  * Created by AnLei on 2017/4/29.
  */
object BasketballDataFrame {
  def main(args: Array[String]): Unit = {

    val spark = SparkSession.builder().master("local").appName("BasketballDataFrame").getOrCreate()

    val sc = spark.sparkContext
    val sqlContext = spark.sqlContext

    val DATA_PATH = "logs/basketball/BasketballData"
    val TMP_PATH = "logs/basketball/BasketballStatsWithYear"

    /**
      * 删除TMP_PATH文件夹
      */
    val fs = FileSystem.get(new Configuration())
    fs.delete(new Path(TMP_PATH), true)

    /**
      * 添加日期:数据文件中没有时间信息
      */
    for (i <- 1980 to 2016) {
      println(i)
      val yearStats = sc.textFile(s"${DATA_PATH}/leagues_NBA_$i*").repartition(sc.defaultParallelism)
      yearStats.filter(x => x.contains(",")).map(x => (i, x)).saveAsTextFile(s"${TMP_PATH}/$i/")
    }

    /**
      * 读取并缓存数据
      */
    val stats = sc.textFile(s"${TMP_PATH}/*/*").repartition(sc.defaultParallelism)
    val filteredStats = stats.filter(x => !x.contains("FG%")).filter(x => x.contains(","))
      .map(x => x.replace("*", "").replace(",,", ",0,"))
    filteredStats.cache()

    val schemaString = "Year,Rk,Player,Pos,Age,Tm,G,GS,MP,FG,FGA,FG%,3P,3PA,3P%,2P,2PA,2P%,eFG%,FT,FTA,FT%,ORB,DRB,TRB,AST,STL,BLK,TOV,PF,PTS"
    val schema = StructType(schemaString.split(",").map(fieldName => StructField(fieldName, StringType, true)))
    val basketballRDD = filteredStats.map(_.split(",")).map(p =>
      Row(
        p(0).replace("(", "").trim, p(1).trim, p(2).trim, p(3).trim, p(4).trim, p(5).trim, p(6).trim, p(7).trim, p(8).trim, p(9).trim,
        p(10).trim, p(11).trim, p(12).trim, p(13).trim, p(14).trim, p(15).trim, p(16).trim, p(17).trim, p(18).trim, p(19).trim,
        p(20).trim, p(21).trim, p(22).trim, p(23).trim, p(24).trim, p(25).trim, p(26).trim, p(27).trim, p(28).trim, p(29).trim,
        p(30).replace(")", "").trim
      )
    )
    val basketballDF = sqlContext.createDataFrame(basketballRDD, schema)
    basketballDF.createOrReplaceTempView("basketball")

    /**
      * 每个年龄段的参赛数目
      */
    basketballDF.groupBy("Age").count().sort("Age").show(100)

    /**
      * 查询Stephen Curry所有数据
      */
    val basketballSQL = spark.sql("SELECT * FROM basketball WHERE year = 2016 AND Player = 'Stephen Curry'")
    basketballSQL.show(10)

    /**
      * 三分球得分排名
      */
    val basketballSQL2 = spark.sql("SELECT Player,3P FROM basketball WHERE year = 2016 ORDER BY 3P DESC")
    basketballSQL2.show(10)
  }
}
