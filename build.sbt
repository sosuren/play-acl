name := "play-acl"

organization := "com.myproject"

version := "1.0"

scalaVersion := "2.11.7"

lazy val playAcl = (project in file(".")).enablePlugins(PlayScala)

libraryDependencies ++= Seq(
  cache,
  specs2 % Test,
  "org.scala-lang.modules" %% "scala-async" % "0.9.4"
)


resolvers += "scalaz-bintray" at "https://dl.bintray.com/scalaz/releases"
