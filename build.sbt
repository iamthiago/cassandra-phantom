name := "cassandra-phantom"

version := "1.0"

scalaVersion := "2.11.8"
ivyScala := ivyScala.value map { _.copy(overrideScalaVersion = true) }

lazy val Versions = new {
  val akka = "2.4.4"
  val phantom = "2.7.5"
  val util = "0.30.1"
  val scalatest = "3.0.1"
}

resolvers ++= Seq(
  Resolver.sonatypeRepo("releases"),
  Resolver.typesafeRepo("releases"),
  Resolver.bintrayRepo("websudos", "oss-releases")
)

libraryDependencies ++= {

  Seq(
    "com.outworkers"      %%  "phantom-dsl"                 % Versions.phantom,
    "com.outworkers"      %%  "phantom-streams"             % Versions.phantom,
    "com.outworkers"      %%  "util-testing"                % Versions.util % Test,
    "org.scalatest"       %%  "scalatest"                   % Versions.scalatest % Test,
    "com.typesafe.akka"   %%  "akka-actor"                  % Versions.akka,
    "com.typesafe.akka"   %%  "akka-stream"                 % Versions.akka,
    "com.typesafe.akka"   %%  "akka-slf4j"                  % Versions.akka,
    "com.typesafe.akka"   %%  "akka-testkit"                % Versions.akka,
    "com.typesafe.akka"   %%  "akka-stream-testkit"         % Versions.akka
  )
}

PhantomSbtPlugin.projectSettings
