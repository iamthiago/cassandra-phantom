package com.cassandra.phantom.modeling.test.stream

import akka.NotUsed
import akka.actor.ActorSystem
import akka.stream._
import akka.stream.scaladsl._
import com.cassandra.phantom.modeling.connector.Connector
import com.cassandra.phantom.modeling.database.ProductionDatabase
import com.cassandra.phantom.modeling.entity.Song
import com.cassandra.phantom.modeling.service.SongsService
import com.datastax.driver.core.utils.UUIDs

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.concurrent.{Await, Future}

/**
  * Created by thiago on 4/13/16.
  *
  * This is a very simple example, actually taken from the akka streams
  * quick start guide example on how to play with streams.
  *
  * The point here is how to get a stream from the database using phantom-dsl.
  *
  * http://doc.akka.io/docs/akka/2.4.4/scala/stream/stream-quickstart.html
  *
  */
object SongsStreaming extends ProductionDatabase with Connector.connector.Connector {

  def main(args: Array[String]) {

    val truncate = database.autotruncate().future()

    val insert = Future.sequence(List(
      SongsService.saveOrUpdate(Song(UUIDs.timeBased(), "Prison Song", "Toxicity", "System of a Down")),
      SongsService.saveOrUpdate(Song(UUIDs.timeBased(), "Aerials", "Toxicity", "System of a Down")),
      SongsService.saveOrUpdate(Song(UUIDs.timeBased(), "Toxicity", "Toxicity", "System of a Down"))
    ))

    val f = for {
      f1 <- truncate
      f2 <- insert
    } yield f2

    Await.result(f, 10.seconds)

    implicit val system = ActorSystem("QuickStart")
    implicit val materializer = ActorMaterializer()

    val songs = Source.fromPublisher(SongsService.playPublisher)

    val count: Flow[Song, Int, NotUsed] = Flow[Song].map(_ => 1)

    val sumSink: Sink[Int, Future[Int]] = Sink.fold[Int, Int](0)(_ + _)

    val counterGraph: RunnableGraph[Future[Int]] =
      songs
        .via(count)
        .toMat(sumSink)(Keep.right)

    val sum: Future[Int] = counterGraph.run()

    sum.foreach(c => println(s"Total songs processed: $c"))
  }
}