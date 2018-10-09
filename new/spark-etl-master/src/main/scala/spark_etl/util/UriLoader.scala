package spark_etl.util

import spark_etl.ConfigError
import spark_etl.util.Validation._

import scala.io.Source

object UriLoader {
  private val fileProtocol = "file:"
  private val resourceProtocol = "resource:"

  def load(uri: String, filePathRoot: String, env: Map[String, String]): Validation[ConfigError, String] =
    for {
      contents <-
      if (uri.startsWith(fileProtocol)) {
        val filePath = uri.substring(fileProtocol.length)
        val fqFilePath = if (filePath.startsWith("/"))
          filePath
        else if (filePathRoot.endsWith("/"))
          s"$filePathRoot$filePath"
        else
          s"$filePathRoot/$filePath"
        loadFile(fqFilePath, env)
      } else if (uri.startsWith(resourceProtocol))
        loadResource(uri.substring(resourceProtocol.length), env)
      else
        loadResource(uri, env)
      withIncludes <- {
        // load #include<xxx>
        val includePattern = "(?m)^\\s*#include\\s*<(.+)>.*$".r
        val includeUris = includePattern.findAllIn(contents).matchData.map(_.group(1))
        if (includeUris.isEmpty)
          contents.success[ConfigError]
        else {
          val byUriIncludes = getIncludes(includeUris, filePathRoot, env)
          byUriIncludes.map(includes => includePattern.replaceAllIn(contents, m => includes(m.group(1))))
        }
      }
      withEnvVars <- envVarSub(uri, withIncludes, env)
    } yield withEnvVars

  private def loadResource(uri: String, env: Map[String, String]): Validation[ConfigError, String] = {
    val fqUri = getClass.getResource(uri)
    if (fqUri != null)
      Source.fromURL(fqUri).mkString.success[ConfigError]
    else
      ConfigError(s"Failed to read resource $uri").failure[String]
  }

  private def loadFile(uri: String, env: Map[String, String]): Validation[ConfigError, String] = {
    val file = new java.io.File(uri)
    if (file.canRead)
      scala.io.Source.fromFile(file).mkString.success[ConfigError]
    else
      ConfigError(s"Failed to read file $uri").failure[String]
  }

  private def envVarSub(uri: String, contents: String, env: Map[String, String]): Validation[ConfigError, String] = {
    // replace all ${k}, with v, ensuring v can contain '$'
    val contents2 = env.foldLeft(contents) { case (soFar, (k, v)) => soFar.replaceAll("\\$\\{" + k + "\\}", v.replaceAll("\\$", "\\\\\\$")) }
    val remainingVars = "\\$\\{.*?\\}".r.findAllIn(contents2)
    if (remainingVars.isEmpty)
      contents2.success[ConfigError]
    else {
      val varNames = remainingVars.toList.distinct
      ConfigError(s"Unresolved env vars in $uri: ${varNames.mkString(", ")}").failure[String]
    }
  }

  private def getIncludes(uris: Iterator[String], filePathRoot: String, env: Map[String, String]): Validation[ConfigError, Map[String, String]] = {
    uris.map(uri => load(uri, filePathRoot, env).map(contents => Map(uri -> contents))).reduce(_ +++ _)
  }
}
