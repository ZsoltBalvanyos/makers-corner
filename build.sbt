import Dependencies._
import play.sbt.PlayImport.specs2
import sbtassembly.AssemblyPlugin.autoImport.assemblyMergeStrategy

name := "makers-corner"

version := "1.0"

scalaVersion := "2.12.4"

enablePlugins(PlayScala)

assemblyMergeStrategy in assembly := {
  case PathList("META-INF", _) => MergeStrategy.discard
  case _                       => MergeStrategy.first
}

libraryDependencies ++= Seq(
  ehcache,
  guice,
  h2,
  mysql,
  postgresql,
  pgSlick,
  typeSafeConfig,
  fs2.core,
  Circe.core,
  Circe.generic,
  Circe.parser,
  Circe.shapes,
  Cats.core,
  PlaySlick.slick,
  PlaySlick.slickEvolutions,
  specs2 % Test,
  scalaTest,
  scalaCheck,
  scalaCheckShapeless
)