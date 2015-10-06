package com.cassandra.phantom.modeling.service

import java.util.UUID

import com.cassandra.phantom.modeling.connector.CassandraConnector
import com.cassandra.phantom.modeling.entity.Songs
import com.cassandra.phantom.modeling.model.{GenericSongsModel, SongsByArtistModel, SongsModel}
import com.websudos.phantom.builder.Unspecified
import com.websudos.phantom.builder.query.InsertQuery
import com.websudos.phantom.dsl._

import scala.concurrent.duration._
import scala.concurrent.{Await, Future}

/**
 * Created by Thiago Pereira on 8/4/15.
 *
 * Now that we have two tables, we need to insert, update and delete twice, but how?
 */
trait SongsService extends CassandraConnector {

  object service {

    /**
     * Lets define our models
     */
    val songsModel = new SongsModel
    val songsByArtistModel = new SongsByArtistModel

    /**
     * Find in the songs table by song_id
     *
     * @param id
     * @return
     */
    def getBySongsId(id: UUID): Future[Option[Songs]] = {
      songsModel.model.select.where(_ => songsModel.model.songId eqs id).one()
    }

    /**
     * Find in the songs_by_artist by artist
     *
     * @param artist
     * @return
     */
    def getSongsByArtist(artist: String): Future[List[Songs]] = {
      songsByArtistModel.model.select.where(_ => songsByArtistModel.model.artist eqs artist).fetch()
    }

    /**
     * Save a song in both tables
     *
     * @param songs
     * @return
     */
    def saveOrUpdate(songs: Songs): Future[ResultSet] = {
      saveGeneric(songsModel, songs).future()
      saveGeneric(songsByArtistModel, songs).future()
    }

    /**
     * Iterate over a list of songs and insert it, better for performance than batchUpdate
     *
     * @param list
     * @return
     */
    def saveList(list: List[Songs]): Future[List[Songs]] = {
      list.foreach(saveOrUpdate)
      Future(list)
    }

    /**
     * Batches can be unlogged, logged and count.
     *
     * More on: [https://medium.com/@foundev/cassandra-batch-loading-without-the-batch-keyword-40f00e35e23e]
     *
     * @param list
     * @return
     */
    def batchUpdate(list: List[Songs]): Future[ResultSet] = {
      val batch = for(song <- list) yield (saveGeneric(songsModel, song), saveGeneric(songsByArtistModel, song))

      val (l1, l2) = batch.unzip

      Batch.unlogged.add(l1.toIterator).future()
      Batch.unlogged.add(l2.toIterator).future()
    }

    /**
     * Delete a song in both table
     *
     * @param songs
     * @return
     */
    def delete(songs: Songs): Future[ResultSet] = {
      songsModel.model.delete.where(_ => songsModel.model.songId eqs songs.songId)
      songsByArtistModel.model.delete.where(_ => songsByArtistModel.model.artist eqs songs.artist).and(_ => songsByArtistModel.model.songId eqs songs.songId).future()
    }

    /**
     * Create the tables if not exists
     *
     * @return
     */
    def createTables = {
      Await.ready(songsModel.model.create.ifNotExists().future(), 3.seconds)
      Await.ready(songsByArtistModel.model.create.ifNotExists().future(), 3.seconds)
    }

    /**
     * Helper method to upsert(insert and update) the table according to a model
     *
     * @param genericSongsModel
     * @param songs
     * @return
     */
    private def saveGeneric(genericSongsModel: GenericSongsModel, songs: Songs): InsertQuery[genericSongsModel.InnerGeneric, Songs, Unspecified] = {
      genericSongsModel.model.insert
        .value(_.songId, songs.songId)
        .value(_.title, songs.title)
        .value(_.album, songs.album)
        .value(_.artist, songs.artist)
    }
  }
}