name := "slick-distinct-bug"

mainClass in Compile := Some("SlickDistinctBug")

scalaVersion := "2.11.11"

libraryDependencies ++= List(
  "com.typesafe.slick" %% "slick" % "3.2.1",
  "org.slf4j" % "slf4j-nop" % "1.7.10",
  "com.h2database" % "h2" % "1.4.187",
  "org.scalatest" %% "scalatest" % "2.2.4" % "test"
)

fork in run := true
