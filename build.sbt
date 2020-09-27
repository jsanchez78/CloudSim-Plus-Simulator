name := "HW01"

version := "0.1"

scalaVersion := "2.13.3"

mainClass in (Compile, run) := Some("Test")

ThisBuild / useCoursier := false

libraryDependencies ++= Seq(
  //Typesafe configuration
  "com.typesafe" % "config" % "1.4.0",
  // CloudSim Plus
  "org.cloudsimplus" % "cloudsim-plus" % "4.3.4",
  // Logback logging framework
  "ch.qos.logback" % "logback-classic" % "1.2.3",
  "com.typesafe.scala-logging" %% "scala-logging" % "3.9.2",
  "org.gnieh" % "logback-config" % "0.3.1",
  //JUnit Testing Framework
  "junit" % "junit" % "4.13"
)