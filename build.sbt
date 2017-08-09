name := "cassandra-phantom"

version := "1.0"

scalaVersion := "2.12.2"

ivyScala := ivyScala.value map { _.copy(overrideScalaVersion = true) }

lazy val Versions = new {
  val phantom = "2.12.1"
  val util = "0.30.1"
  val scalatest = "3.0.1"
}

resolvers ++= Seq(
  Resolver.sonatypeRepo("releases"),
  Resolver.typesafeRepo("releases"),
  Resolver.bintrayRepo("websudos", "oss-releases")
)

libraryDependencies ++= Seq(
  "com.outworkers"  %%  "phantom-dsl"       % Versions.phantom,
  "com.outworkers"  %%  "phantom-streams"   % Versions.phantom,
  "com.outworkers"  %%  "util-testing"      % Versions.util % Test,
  "org.scalatest"   %%  "scalatest"         % Versions.scalatest % Test
)
