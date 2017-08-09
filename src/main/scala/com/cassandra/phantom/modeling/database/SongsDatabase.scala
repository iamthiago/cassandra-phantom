package com.cassandra.phantom.modeling.database

import com.cassandra.phantom.modeling.connector.Connector._
import com.cassandra.phantom.modeling.entity.Song
import com.cassandra.phantom.modeling.model.{SongsByArtistModel, SongsModel}
import com.outworkers.phantom.connectors.CassandraConnection
import com.outworkers.phantom.dsl._

import scala.concurrent.Future

/**
  * This is our Database object that wraps our two existing tables,
  * giving the ability to receive different connectors
  * for example: One for production and other for testing
  */
class SongsDatabase(override val connector: CassandraConnection) extends Database[SongsDatabase](connector) {
  object SongsModel extends SongsModel with connector.Connector
  object SongsByArtistsModel extends SongsByArtistModel with connector.Connector

  /**
    * Save a song in both tables
    *
    * @param songs
    * @return
    */
  def saveOrUpdate(songs: Song): Future[ResultSet] = {
    for {
      _ <- SongsModel.store(songs).future()
      byArtist <- SongsByArtistsModel.store(songs).future
    } yield byArtist
  }

  /**
    * Delete a song in both table
    *
    * @param song
    * @return
    */
  def delete(song: Song): Future[ResultSet] = {
    for {
      _ <- SongsModel.deleteById(song.id)
      byArtist <- SongsByArtistsModel.deleteByArtistAndId(song.artist, song.id)
    } yield byArtist
  }
}

/**
  * This is the database, it connects to a cluster with multiple contact points
  */
object Database extends SongsDatabase(connector)
