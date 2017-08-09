package com.cassandra.phantom.modeling.test.utils

import com.cassandra.phantom.modeling.database.{Database, SongsDatabase}
import com.outworkers.phantom.database.DatabaseProvider
import org.scalatest._
import org.scalatest.concurrent.ScalaFutures

trait SongsDbProvider extends DatabaseProvider[SongsDatabase] {
  override def database: SongsDatabase = Database
}

trait CassandraSpec extends FlatSpec
  with Matchers
  with Inspectors
  with ScalaFutures
  with OptionValues
  with BeforeAndAfterAll
  with SongsDbProvider