package com.spark.recommend.offline

import com.spark.util.RedisClient
import org.apache.spark.mllib.linalg.Vectors
import org.apache.spark.mllib.linalg.distributed.{MatrixEntry, RowMatrix}
import org.apache.spark.{SparkConf, SparkContext}
import org.training.spark.proto.Spark.{ItemList, ItemSimilarities, ItemSimilarity}

object MoviesSimilarityGenerator {
  def main(args: Array[String]) {
    var masterUrl = "local[4]"
    var dataPath = "logs/ml-1m/ratings10000.dat"
    if (args.length > 0) {
      masterUrl = args(0)
    } else if (args.length > 1) {
      dataPath = args(1)
    }

    // Create a SparContext with the given master URL
    val conf = new SparkConf().setMaster(masterUrl).setAppName("FilmsSimilarityGenerator")
    val sc = new SparkContext(conf)

    val inputFile = dataPath
    val threshold = 0.1

    // Load and parse the data file.
    /**
      * (userid,movieid,rating)
      */
    val rows = sc.textFile(inputFile).map(_.split("::")).map { p =>
      (p(0).toLong, p(1).toInt, p(2).toDouble)
    }

    /**
      * size
      */
    val maxMovieId = rows.map(_._2).max() + 1

    val rowRdd = rows.map { p =>
      /**
        * (userid,(movieid,rating))
        */
      (p._1, (p._2, p._3))
    }.groupByKey().map { kv =>
      /**
        * 稀疏向量
        * size,[movieid,movieid2..],[rating,rating2...]
        */
      Vectors.sparse(maxMovieId, kv._2.toSeq)
    }.cache()

    /**
      * 行矩阵
      * (size,[movieid,movieid2..],[rating,rating2...])
      * (size,[movieid,movieid2..],[rating,rating2...])
      * ...
      */
    val mat = new RowMatrix(rowRdd)

    println(s"mat row/col number: ${mat.numRows()}, ${mat.numCols()}")

    // Compute similar columns perfectly, with brute force.
    /**
      * 计算每列之间相似度，采用抽样方法进行计算，参数为阈值
      * MatrixEntry(movieid,movieid2,相似度)
      * MatrixEntry(movieid,movieid2,相似度)
      * ...
      */
    val similarities = mat.columnSimilarities(0.1)

    // Save movie-movie similarity to redis
    similarities.entries.map { case MatrixEntry(i, j, u) =>
      /**
        * (movieid,(movieid2,相似度))
        * (movieid,(movieid2,相似度))
        * ...
        */
      (i, (j, u))
    }.groupByKey(2).map { kv =>
      /**
        * (movieid,Map(movieid2 -> 相似度, movieid3 -> 相似度, ...)top20
        * (movieid,Map(movieid2 -> 相似度, movieid3 -> 相似度, ...)top20
        * ...
        */
      (kv._1, kv._2.toSeq.sortWith(_._2 > _._2).take(20).toMap)
    }.mapPartitions { iter =>
      /**
        * key:II:2134,value:itemSimilarites {
        *   itemId: 2403
        *   similarity: 0.5648457825594065
        * }
        * itemSimilarites {
        *   itemId: 2520
        *   similarity: 0.6575959492214292
        * }
        */
      val jedis = RedisClient.pool.getResource
      iter.map { case (i, j) =>
        val key = ("II:%d").format(i)
        val builder = ItemSimilarities
            .newBuilder()
        j.foreach { item =>
          val itemSimilarity = ItemSimilarity.newBuilder().setItemId(item._1).setSimilarity(item._2)
          builder.addItemSimilarites(itemSimilarity)
        }
        val value = builder.build()
        println(s"key:${key},value:${value.toString}")
        jedis.set(key, new String(value.toByteArray))
      }
    }.count

    // Save user-movie similarity to redis
    rows.map { case (i, j, k) =>
      /**
        * (userid,movieid)
        */
      (i, j)
    }.groupByKey(2).mapPartitions { iter =>
      /**
        * key:UI:68,value:itemIds: 3863
        * itemIds: 1256
        * itemIds: 2997
        * itemIds: 1
        * itemIds: 2064
        * itemIds: 1265
        * itemIds: 1197
        * itemIds: 1270
        */
      val jedis = RedisClient.pool.getResource
      iter.map { case (i, j) =>
        val key = ("UI:%d").format(i)
        val builder = ItemList.newBuilder()
        j.foreach { id =>
          builder.addItemIds(id)
        }
        val value = builder.build()
        println(s"key:${key},value:${value.toString}")
        jedis.append(key, new String(value.toByteArray))
      }
    }.count

      sc.stop()
    }
  }
