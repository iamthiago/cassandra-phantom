name := "cassandra-phantom"

version := "1.0"

scalaVersion := "2.12.8"

lazy val Versions = new {
  val phantom = "2.39.0"
  val util = "0.50.0"
  val scalatest = "3.0.5"
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
