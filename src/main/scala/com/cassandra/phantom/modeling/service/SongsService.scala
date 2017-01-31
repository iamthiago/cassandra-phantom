package com.cassandra.phantom.modeling.service

import com.cassandra.phantom.modeling.database.ProductionDatabase
import com.cassandra.phantom.modeling.entity.Song
import com.outworkers.phantom.dsl._

import scala.concurrent.Future

/**
  *
  * Now that we have two tables, we need to insert, update and delete twice, but how?
  *
  * We are going to use the methods we have implemented in our two models
  * [[com.cassandra.phantom.modeling.model.SongsModel]] and
  * [[com.cassandra.phantom.modeling.model.SongsByArtistModel]]
  * taking advantage of the futures and running it in parallel through for loop
  *
  */
trait SongsService extends ProductionDatabase {

  /**
    * Find songs by Id
    *
    * @param id
    * @return
    */
  def getSongById(id: UUID): Future[Option[Song]] = {
    database.songsModel.getBySongId(id)
  }

  /**
    * Find songs by Artist
    *
    * @param artist
    * @return
    */
  def getSongsByArtist(artist: String): Future[List[Song]] = {
    database.songsByArtistsModel.getByArtist(artist)
  }

  /**
    * Save a song in both tables
    *
    * @param songs
    * @return
    */
  def saveOrUpdate(songs: Song): Future[ResultSet] = {
    for {
      byId <- database.songsModel.store(songs)
      byArtist <- database.songsByArtistsModel.store(songs)
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
      byID <- database.songsModel.deleteById(song.id)
      byArtist <- database.songsByArtistsModel.deleteByArtistAndId(song.artist, song.id)
    } yield byArtist
  }
}

/**
  * Let available a singleton instance of this service class, to prevent unnecessary instances
  */
object SongsService extends SongsService with ProductionDatabase
