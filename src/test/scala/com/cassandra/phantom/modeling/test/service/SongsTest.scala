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
    val sample = gen[Song]
    val future = this.store(sample)

    whenReady(future) { result =>
      result isExhausted() shouldBe true
      result wasApplied() shouldBe true
      this.drop(sample)
    }
  }

  it should "find a song by id" in {
    val sample = gen[Song]

    val chain = for {
      store <- this.store(sample)
      get <- database.songsModel.getBySongId(sample.id)
      delete <- this.drop(sample)
    } yield get

    whenReady(chain) { res =>
      res shouldBe defined
      this.drop(sample)
    }
  }

  it should "find songs by artist" in {
    val sample = gen[Song]
    val sample2 = gen[Song]
    val sample3 = gen[Song]

    val future = for {
      f1 <- this.store(sample.copy(title = "Toxicity"))
      f2 <- this.store(sample2.copy(title = "Aerials"))
      f3 <- this.store(sample3.copy(title = "Chop Suey"))
    } yield (f1, f2, f3)

    whenReady(future) { insert =>
      val songsByArtist = database.songsByArtistsModel.getByArtist("System of a Down")
      whenReady(songsByArtist) { searchResult =>
        searchResult shouldBe a [List[_]]
        searchResult should have length 3
        this.drop(sample)
        this.drop(sample2)
        this.drop(sample3)
      }
    }
  }

  it should "be updated into cassandra" in {
    val sample = gen[Song]
    val updatedTitle = gen[String]

    val chain = for {
      store <- this.store(sample)
      unmodified <- database.songsModel.getBySongId(sample.id)
      store <- this.store(sample.copy(title = updatedTitle))
      modified <- database.songsModel.getBySongId(sample.id)
    } yield (unmodified, modified)

    whenReady(chain) {
      case (initial, modified) =>
        initial shouldBe defined
        initial.value.title shouldEqual sample.title

        modified shouldBe defined
        modified.value.title shouldEqual updatedTitle

        this.drop(modified.get)
    }
  }

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