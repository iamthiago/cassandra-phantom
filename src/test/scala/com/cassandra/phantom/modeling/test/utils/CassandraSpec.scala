package com.cassandra.phantom.modeling.test.utils

import com.cassandra.phantom.modeling.service.TestDatabaseProvider
import org.scalatest._
import org.scalatest.concurrent.ScalaFutures

import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._

abstract class CassandraSpec extends FlatSpec
  with Matchers
  with Inspectors
  with ScalaFutures
  with OptionValues
  with BeforeAndAfterAll
  with TestDatabaseProvider {

  override def beforeAll(): Unit = {
    implicit val session = database.session
    implicit val keyspace = database.space

    Await.result(database.autocreate().future(), 5.seconds)
  }
}