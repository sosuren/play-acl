val projectSettings = Seq(
  name := "play-acl",
  organization := "com.myproject",
  version := "0.0.1",
  scalaVersion := "2.11.7"
)

lazy val playAcl = (project in file("."))
                      .settings(projectSettings: _*)

val playVersion = "2.5.0"

libraryDependencies ++= Seq(
  "com.typesafe.play" %% "play" % playVersion,
  "org.scala-lang.modules" %% "scala-async" % "0.9.4",
  "org.specs2" %% "specs2-core" % "3.6.6" % "test",
  "org.specs2" %% "specs2-mock" % "3.6.6" % "test"
)


resolvers += "Typesafe releases" at "http://repo.typesafe.com/typesafe/releases"
