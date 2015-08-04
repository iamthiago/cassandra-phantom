package com.cassandra.phantom.modeling.service

import java.util.UUID

import com.cassandra.phantom.modeling.connector.CassandraConnector
import com.cassandra.phantom.modeling.entity.Songs
import com.cassandra.phantom.modeling.model.{GenericSongsModel, SongsByArtistModel, SongsModel}
import com.websudos.phantom.dsl._

import scala.concurrent.Future

/**
 * Created by Thiago Pereira on 8/4/15.
 */
trait SongsService extends CassandraConnector {

  object service {

    val songsModel = new SongsModel
    val songsByArtistModel = new SongsByArtistModel

    def getBySongsId(id: UUID): Future[Option[Songs]] = {
      songsModel.model.select.where(_ => songsModel.model.songId eqs id).one()
    }

    def getSongsByArtist(artist: String): Future[List[Songs]] = {
      songsByArtistModel.model.select.where(_ => songsByArtistModel.model.artist eqs artist).fetch()
    }

    def saveOrUpdate(songs: Songs): Future[ResultSet] = {
      saveGeneric(songsModel, songs)
      saveGeneric(songsByArtistModel, songs)
    }

    def saveList(list: List[Songs]): Future[List[Songs]] = {
      list.foreach(saveOrUpdate)
      Future.successful(list)
    }

    def delete(songs: Songs): Future[ResultSet] = {
      songsModel.model.delete.where(_ => songsModel.model.songId eqs songs.songId).future()
      songsByArtistModel.model.delete.where(_ => songsByArtistModel.model.artist eqs songs.artist).and(_ => songsByArtistModel.model.songId eqs songs.songId).future()
    }

    private def saveGeneric(genericSongsModel: GenericSongsModel, songs: Songs): Future[ResultSet] = {
      genericSongsModel.model.insert
        .value(_.songId, songs.songId)
        .value(_.title, songs.title)
        .value(_.album, songs.album)
        .value(_.artist, songs.artist)
        .future()
    }
  }
}