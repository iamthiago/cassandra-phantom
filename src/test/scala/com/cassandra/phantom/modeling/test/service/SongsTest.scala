package com.cassandra.phantom.modeling.test.service

import com.cassandra.phantom.modeling.connector.Connector
import com.cassandra.phantom.modeling.database.EmbeddedDatabase
import com.cassandra.phantom.modeling.entity.Song
import com.cassandra.phantom.modeling.test.utils.CassandraSpec
import com.datastax.driver.core.utils.UUIDs
import com.websudos.util.testing.{Sample, _}

import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._

class SongsTest extends CassandraSpec with EmbeddedDatabase with Connector.testConnector.Connector {

  override def beforeAll(): Unit = {
    Await.result(database.autocreate().future(), 5.seconds)
  }

  implicit object SongGenerator extends Sample[Song] {
    override def sample: Song = {
      Song(
        UUIDs.timeBased(),
        gen[ShortString].value,
        album = "Toxicity",
        artist = "System of a Down"
      )
    }
  }

  "A Song" should "be inserted into cassandra" in {
    val song = gen[Song]
    val future = this.store(song)

    whenReady(future) { result =>
      result isExhausted() shouldBe true
      result wasApplied() shouldBe true
      this.drop(song)
    }
  }

  /*it should "find a song by id" in {
    val sample = gen[Song]

    val chain = for {
      store <- songsService.saveOrUpdate(sample)
      get <- songsService.getSongById(sample.id)
      delete <- songsService.delete(sample)
    } yield get

    whenReady(chain) { res =>
      res shouldBe defined
      songsService.delete(sample)
    }
  }

  it should "find songs by artist" in {
    val sample = gen[Song]
    val sample2 = gen[Song]
    val sample3 = gen[Song]

    val future = for {
      f1 <- songsService.saveOrUpdate(sample.copy(title = "Toxicity"))
      f2 <- songsService.saveOrUpdate(sample2.copy(title = "Aerials"))
      f3 <- songsService.saveOrUpdate(sample3.copy(title = "Chop Suey"))
    } yield (f1, f2, f3)

    whenReady(future) { insert =>
      val songsByArtist = songsService.getSongsByArtist("System of a Down")
      whenReady(songsByArtist) { searchResult =>
        searchResult shouldBe a [List[_]]
        searchResult should have length 3
        songsService.delete(sample)
        songsService.delete(sample2)
        songsService.delete(sample3)
      }
    }
  }

  it should "be updated into cassandra" in {
    val sample = gen[Song]
    val updatedTitle = gen[String]

    val chain = for {
      store <- songsService.saveOrUpdate(sample)
      unmodified <- songsService.getSongById(sample.id)
      store <- songsService.saveOrUpdate(sample.copy(title = updatedTitle))
      modified <- songsService.getSongById(sample.id)
    } yield (unmodified, modified)

    whenReady(chain) {
      case (initial, modified) =>
        initial shouldBe defined
        initial.value.title shouldEqual sample.title

        modified shouldBe defined
        modified.value.title shouldEqual updatedTitle

        songsService.delete(modified.get)
    }
  }*/

  private def store(song: Song) = {
    for {
      byId <- database.songsModel.store(song)
      byArtist <- database.songsByArtistsModel.store(song)
    } yield byArtist
  }

  private def drop(song: Song) = {
    for {
      byID <- database.songsModel.deleteById(song.id)
      byArtist <- database.songsByArtistsModel.deleteByArtistAndId(song.artist, song.id)
    } yield byArtist
  }
}