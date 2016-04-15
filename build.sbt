name := "play-acl"

version := "1.0"

scalaVersion := "2.11.7"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

libraryDependencies ++= Seq(
  cache,
  specs2 % Test,
  "com.google.guava" % "guava-io" % "r03",
  "org.scala-lang.modules" %% "scala-async" % "0.9.4"
)


resolvers += "scalaz-bintray" at "https://dl.bintray.com/scalaz/releases"