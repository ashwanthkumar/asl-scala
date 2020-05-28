import sbt._
import Dependencies._

// Makes life easy when using sbt shell
Global / onChangedBuildSource := ReloadOnSourceChanges

val libVersion = sys.env.get("TRAVIS_TAG") orElse sys.env.get("BUILD_LABEL") getOrElse s"1.0.0-${System.currentTimeMillis / 1000}-SNAPSHOT"

lazy val asl = (project in file("."))
  .settings(
    inThisBuild(
      List(
        organization := "in.ashwanthkumar",
        scalaVersion := "2.12.11",
        crossScalaVersions := Seq("2.12.11", "2.11.11"),
        version := libVersion,
        scalafmtOnCompile := true,
      )
    ),
    name := "asl",
    libraryDependencies ++= Seq(
      scalaTest,
      sprayJsonLens
    )
  )
