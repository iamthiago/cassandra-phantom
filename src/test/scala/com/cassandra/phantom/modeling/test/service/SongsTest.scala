package com.cassandra.phantom.modeling.test.service

import com.cassandra.phantom.modeling.entity.Song
import com.cassandra.phantom.modeling.service.SongsService
import com.cassandra.phantom.modeling.test.utils.CassandraSpec
import com.datastax.driver.core.utils.UUIDs
import com.websudos.util.testing.{Sample, _}
import org.scalatest.BeforeAndAfterAll

import scala.concurrent.ExecutionContext.Implicits.global

class SongsTest extends CassandraSpec with BeforeAndAfterAll {

  override def beforeAll(): Unit = {
    SongsService.createTables()
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
    val future = SongsService.saveOrUpdate(sample)

    whenReady(future) { result =>
      result isExhausted() shouldBe true
      result wasApplied() shouldBe true
      SongsService.delete(sample)
    }
  }

  it should "find a song by id" in {
    val sample = gen[Song]

    val chain = for {
      store <- SongsService.saveOrUpdate(sample)
      get <- SongsService.getSongById(sample.id)
      delete <- SongsService.delete(sample)
    } yield get

    whenReady(chain) { res =>
      res shouldBe defined
      SongsService.delete(sample)
    }
  }

  it should "find songs by artist" in {
    val sample = gen[Song]
    val sample2 = gen[Song]
    val sample3 = gen[Song]

    val future = for {
      f1 <- SongsService.saveOrUpdate(sample.copy(title = "Toxicity"))
      f2 <- SongsService.saveOrUpdate(sample2.copy(title = "Aerials"))
      f3 <- SongsService.saveOrUpdate(sample3.copy(title = "Chop Suey"))
    } yield (f1, f2, f3)

    whenReady(future) { insert =>
      val songsByArtist = SongsService.getSongsByArtist("System of a Down")
      whenReady(songsByArtist) { searchResult =>
        searchResult shouldBe a [List[_]]
        searchResult should have length 3
        SongsService.delete(sample)
        SongsService.delete(sample2)
        SongsService.delete(sample3)
      }
    }
  }

  it should "be updated into cassandra" in {
    val sample = gen[Song]
    val updatedTitle = gen[String]

    val chain = for {
      store <- SongsService.saveOrUpdate(sample)
      unmodified <- SongsService.getSongById(sample.id)
      store <- SongsService.saveOrUpdate(sample.copy(title = updatedTitle))
      modified <- SongsService.getSongById(sample.id)
    } yield (unmodified, modified)

    whenReady(chain) {
      case (initial, modified) =>
        initial shouldBe defined
        initial.value.title shouldEqual sample.title

        modified shouldBe defined
        modified.value.title shouldEqual updatedTitle

        SongsService.delete(modified.get)
    }
  }
}