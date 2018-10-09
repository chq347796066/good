val globalSettings = Seq(
  version := "0.1",
  scalaVersion := "2.10.5"
)

val sparkVersion = "1.4.1"
val sparkCassandraConnectorVersion = "1.4.1"

lazy val textfiles = (project in file("textfiles"))
  .settings(name := "textfiles")
  .settings(globalSettings:_*)
  .settings(libraryDependencies ++= textfilesDeps)

lazy val textfilesDeps = Seq(
  "com.datastax.spark" % "spark-cassandra-connector_2.10" % sparkCassandraConnectorVersion,
  "org.apache.spark"   % "spark-mllib_2.10"           % sparkVersion % "provided",
  "org.apache.spark"   % "spark-graphx_2.10"          % sparkVersion % "provided",
  "org.apache.spark"   % "spark-sql_2.10"             % sparkVersion % "provided",
  "com.databricks"     % "spark-csv_2.10"             % "1.3.0"
)

lazy val jdbc = (project in file("jdbc"))
  .settings(name := "jdbc")
  .settings(globalSettings:_*)
  .settings(libraryDependencies ++= jdbcDeps)

lazy val jdbcDeps = Seq(
  "com.datastax.spark" % "spark-cassandra-connector_2.10" % sparkCassandraConnectorVersion,
  "org.apache.spark"   % "spark-core_2.10"            % sparkVersion % "provided",
  "org.apache.spark"   % "spark-sql_2.10"             % sparkVersion % "provided",
  "mysql"              % "mysql-connector-java"       % "5.1.34"
)