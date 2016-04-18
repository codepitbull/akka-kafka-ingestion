name := "akka-kafka-ingestion"

version := "1.0"

scalaVersion := "2.11.8"


libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-slf4j" % "2.4.4",
  "com.typesafe.akka" %% "akka-stream" % "2.4.4",
  "com.typesafe.akka" %% "akka-stream-kafka" % "0.11-M2",
  "com.github.tototoshi" %% "scala-csv" % "1.2.2",
  "org.slf4j" % "slf4j-simple" % "1.7.21"
)

parallelExecution in Test := false