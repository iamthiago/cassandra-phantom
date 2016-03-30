package com.cassandra.phantom.modeling.service

import com.cassandra.phantom.modeling.connector.Connector
import com.cassandra.phantom.modeling.entity.Song
import com.cassandra.phantom.modeling.model.{ConcreteSongsByArtistModel, ConcreteSongsModel}
import com.websudos.phantom.db.DatabaseImpl
import com.websudos.phantom.dsl._

import scala.concurrent.duration._
import scala.concurrent.{Await, Future}


class SongsDatabase(override val connector: KeySpaceDef) extends DatabaseImpl(connector) {
  object songsModel extends ConcreteSongsModel with connector.Connector
  object songsByArtistsModel extends ConcreteSongsByArtistModel with connector.Connector
}

object DefaultDb extends SongsDatabase(Connector.connector)

trait DatabaseProvider {
  def database: SongsDatabase
}

trait DefaultDatabaseProvider extends DatabaseProvider {
  override val database = DefaultDb
}

object TestDb extends SongsDatabase(Connector.testConnector)

trait TestDatabaseProvider {
  val database = TestDb
}


/**
 *
 * Now that we have two tables, we need to insert, update and delete twice, but how?
 */
trait SongsService extends DatabaseProvider {

  /**
   * Create the tables if not exists
   *
   * @return
   */
  def createTables(): Unit = {
    val f = for {
      cre1 <- database.songsModel.createTable()
      cre2 <- database.songsByArtistsModel.createTable()
    } yield (cre1, cre2)

    Await.result(f, 5.seconds)
  }

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

object SongsService extends SongsService with DefaultDatabaseProvider