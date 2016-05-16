name := "cassandra-phantom"

version := "1.0"

scalaVersion := "2.11.7"

resolvers ++= Seq(
  Resolver.sonatypeRepo("releases"),
  Resolver.sonatypeRepo("snapshots"),
  Resolver.bintrayRepo("websudos", "oss-releases"),
  "spray repo"                       at "http://repo.spray.io",
  "Typesafe repository snapshots"    at "http://repo.typesafe.com/typesafe/snapshots/",
  "Typesafe repository releases"     at "http://repo.typesafe.com/typesafe/releases/",
  "Sonatype repo"                    at "https://oss.sonatype.org/content/groups/scala-tools/",
  "Sonatype releases"                at "https://oss.sonatype.org/content/repositories/releases",
  "Sonatype snapshots"               at "https://oss.sonatype.org/content/repositories/snapshots",
  "Sonatype staging"                 at "http://oss.sonatype.org/content/repositories/staging",
  "Java.net Maven2 Repository"       at "http://download.java.net/maven/2/",
  "Twitter Repository"               at "http://maven.twttr.com"
)

libraryDependencies ++= {

  val akkaV = "2.4.4"
  val phantomV = "1.22.0"

  Seq(
    "com.websudos"        %%  "phantom-dsl"                 % phantomV,
    "com.websudos"        %%  "phantom-reactivestreams"     % phantomV,
    "com.websudos"        %%  "util-testing"                % "0.13.0"    % "test, provided",
    "org.scalatest"       %%  "scalatest"                   % "2.2.4"     % "test",
    "com.typesafe.akka"   %%  "akka-actor"                  % akkaV,
    "com.typesafe.akka"   %%  "akka-stream"                 % akkaV,
    "com.typesafe.akka"   %%  "akka-slf4j"                  % akkaV,
    "com.typesafe.akka"   %%  "akka-testkit"                % akkaV,
    "com.typesafe.akka"   %%  "akka-stream-testkit"         % akkaV,
    "com.typesafe.play"   %%  "play-streams-experimental"   % "2.4.6"
  )
}

PhantomSbtPlugin.projectSettings