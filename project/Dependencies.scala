import sbt._

object Dependencies {
  lazy val scalaTest = "org.scalatest" %% "scalatest" % "3.0.3" % Test
  lazy val sprayJsonLens = "net.virtual-void" %% "json-lenses" % "0.6.2"
}
