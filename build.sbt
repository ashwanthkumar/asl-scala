import sbt._
import Dependencies._
import sbt.Keys._

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
  .settings(publishSettings: _*)

lazy val publishSettings = Seq(
  publishArtifact := true,
  /* START - sonatype publish related settings */
  pgpSecretRing := file("local.secring.gpg"),
  pgpPublicRing := file("local.pubring.gpg"),
  // GPG Passphrase has to be set via PGP_PASSPHRASE environment variable
  /* END - sonatype publish related settings */

  publishMavenStyle := true,
  // disable publishing test jars
  publishArtifact in Test := false,
  // Ref - https://github.com/xerial/sbt-sonatype/issues/30#issuecomment-342532067
  // we'll not need this once this issue is updated / fixed -- https://issues.sonatype.org/browse/OSSRH-58035
  sources in (Compile, doc) := Seq(),
  // we need to publish javadoc for Sonatype
  publishArtifact in (Compile, packageDoc) := true,
  publishArtifact in (Compile, packageSrc) := true,
  publishTo := {
    val nexus = "https://oss.sonatype.org/"
    if (isSnapshot.value)
      Some("snapshots" at nexus + "content/repositories/snapshots")
    else
      Some("releases" at nexus + "service/local/staging/deploy/maven2")
  },
  pomExtra :=
    <url>https://github.com/ashwanthkumar/asl-scala</url>
      <licenses>
        <license>
          <name>Apache2</name>
          <url>http://www.apache.org/licenses/LICENSE-2.0</url>
        </license>
      </licenses>
      <scm>
        <url>https://github.com/ashwanthkumar/asl-scala</url>
        <connection>scm:git:https://github.com/ashwanthkumar/asl-scala.git</connection>
        <tag>HEAD</tag>
      </scm>

      <developers>
        <developer>
          <email>ashwanthkumar@googlemail.com</email>
          <name>Ashwanth Kumar</name>
          <url>https://ashwanthkumar.in/</url>
          <id>ashwanthkumar</id>
        </developer>
      </developers>
)
