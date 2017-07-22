name := """play-getting-started"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.7"

libraryDependencies ++= Seq(
  jdbc,
  cache,
  "org.postgresql" % "postgresql" % "9.4-1201-jdbc41",
  ws,
  "com.twitter" %% "finagle-http" % "6.41.0",
  "com.google.firebase" % "firebase-admin" % "5.2.0",
  "org.mongodb" %% "casbah" % "3.1.1",
  "org.slf4j" % "slf4j-simple" % "1.6.4",
  "org.scalaj" % "scalaj-http_2.11" % "2.3.0",
  "com.pauldijou" %% "jwt-play" % "0.5.1"
)

libraryDependencies <+= scalaVersion("org.scala-lang" % "scala-compiler" % _ )
