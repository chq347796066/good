package spark_etl.parquet

import java.io.File

import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.fs.{FileSystem, RemoteIterator}
import spark_etl.ConfigError
import spark_etl.util.Validation
import spark_etl.util.Validation._

/**
  * Validate parquet paths on hdfs and local.
  * - validates path
  * - (optionally) validates path children
  *   - expects children to be _temporary, _SUCCESS, ._SUCCESS.crc
  *   - or if expecting partitions, children to contain "="
  */
object PathValidator {
  def validate(checkChildren: Boolean, expectPartition: Boolean, paths: String*): Validation[ConfigError, List[String]] = {
    val validated = paths.map { p =>
      if (p.toLowerCase.startsWith("hdfs://")) {
        val hadoopConf = new Configuration()
        val fs = org.apache.hadoop.fs.FileSystem.get(hadoopConf)
        val fsPath = new org.apache.hadoop.fs.Path(p)
        if (!fs.exists(fsPath))
          ConfigError(s"hdfs path doesn't exist: $p").failure[String]
        else if (checkChildren) {
          val children = hdfsList(fs, fsPath)
          if (areValid(children, expectPartition))
            p.success[ConfigError]
          else if (children.isEmpty)
            ConfigError(s"hdfs path is empty for $p").failure[String]
          else
            ConfigError(s"Unexpected hdfs children for $p: ${children.map(trimRoot(p)).mkString(", ")} ").failure[String]
        }
        else
          p.success[ConfigError]
      } else {
        val ioPath = new File(p)
        if (! ioPath.exists)
          ConfigError(s"Local path doesn't exist: $p").failure[String]
        else if (checkChildren) {
          val children = ioPath.list().toSeq
          if (areValid(children, expectPartition))
            p.success[ConfigError]
          else if (children.isEmpty)
            ConfigError(s"Local path is empty for $p").failure[String]
          else
            ConfigError(s"Unexpected local children for $p: ${children.map(trimRoot(p)).mkString(", ")}").failure[String]
        }
        else
          p.success[ConfigError]
      }
    }
    val res = validated.map(_.map(List(_))).reduce(_ +++ _)
    res
  }

  def hdfsList(fs: FileSystem, parent: org.apache.hadoop.fs.Path): Seq[String] = {
    // as per: https://issues.apache.org/jira/browse/HDFS-7921
    // listing recursively, including all files, then filtering distinct immediate children
    val parentStr = parent.toUri.toString
    toIterator(fs.listFiles(parent, true))
      .map {
        f =>
          val descendant = f.getPath.toUri.toString
          val relativeDescendant = descendant.substring(parentStr.length + 1)
          val immediateChild = relativeDescendant.split("/").head
          s"$parent/$immediateChild"
      }.toSeq.distinct
  }

  private def trimRoot(root: String)(path: String): String = {
    val root2 = if (root.endsWith("/"))
      root
    else
      root + "/"
    if (path.startsWith(root2))
      path.substring(root2.length)
    else
      path
  }

  private def toIterator[T](iter: RemoteIterator[T]) =
    new Iterator[T] {
      def hasNext = iter.hasNext
      def next = iter.next
    }

  private def areValid(paths: Seq[String], expectPartition: Boolean) = {
    lazy val hasValidChildren = paths.forall {
      path =>
        val lastElem = path.split("/").last
        lastElem == "_temporary" || lastElem == "_SUCCESS" || lastElem == "._SUCCESS.crc" || (expectPartition && lastElem.contains("="))
    }
    paths.nonEmpty && hasValidChildren
  }
}
