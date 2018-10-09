package com.techmonad.pipeline.persist

import com.techmonad.pipeline.Record
import com.techmonad.pipeline.util.Status
import org.apache.spark.rdd.RDD
import org.elasticsearch.spark._

object ESPersistence {

  implicit class ESPersistence(val self: RDD[Record]) extends AnyVal {
    def saveToES(): Unit = {
      val validRecords = self.collect { case record if (record.status != Status.ERROR) => record.data }
      if (!validRecords.isEmpty())
        validRecords.saveToEs("data_index/twitter")
      val invalidRecords = self.collect { case record if (record.status == Status.ERROR) => record.data + ("reason" -> record.reason.getOrElse("")) }
      if (!invalidRecords.isEmpty())
        invalidRecords.saveToEs("invalid_data_index/twitter")
    }
  }

}
