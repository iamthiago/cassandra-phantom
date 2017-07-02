package com.cassandra.phantom.modeling.model

import java.util.UUID

import com.cassandra.phantom.modeling.entity.Song
import com.outworkers.phantom.dsl._

import scala.concurrent.Future

/**
  * Create the Cassandra representation of the Songs table
  */
abstract class SongsModel extends Table[SongsModel, Song] {

  override def tableName: String = "songs"

  object id extends TimeUUIDColumn with PartitionKey {
    override lazy val name = "song_id"
  }

  object artist extends StringColumn
  object title extends StringColumn
  object album extends StringColumn

  def getBySongId(id: UUID): Future[Option[Song]] = {
    select
      .where(_.id eqs id)
      .consistencyLevel_=(ConsistencyLevel.ONE)
      .one()
  }

  def deleteById(id: UUID): Future[ResultSet] = {
    delete
      .where(_.id eqs id)
      .consistencyLevel_=(ConsistencyLevel.ONE)
      .future()
  }
}