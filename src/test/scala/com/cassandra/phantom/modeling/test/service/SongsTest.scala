package com.cassandra.phantom.modeling.test.service

import com.cassandra.phantom.modeling.entity.Song
import com.cassandra.phantom.modeling.service.{DefaultDatabaseProvider, SongsService}
import com.cassandra.phantom.modeling.test.utils.CassandraSpec
import com.datastax.driver.core.utils.UUIDs
import com.websudos.util.testing.{Sample, _}
import org.scalatest.BeforeAndAfterAll

import scala.concurrent.ExecutionContext.Implicits.global

class SongsTest extends CassandraSpec with BeforeAndAfterAll {

  object service extends SongsService with DefaultDatabaseProvider

  override def beforeAll(): Unit = {
    service.createTables()
  }


  implicit object SongGenerator extends Sample[Song] {
    override def sample: Song = {
      Song(
        UUIDs.timeBased(),
        gen[ShortString].value,
        gen[ShortString].value,
        gen[ShortString].value
      )
    }
  }


  "A Song" should "be inserted into cassandra" in {
    val sample = gen[Song]
    val future = service.saveOrUpdate(sample)

    whenReady(future) { result =>
      result isExhausted() shouldBe true
      result wasApplied() shouldBe true
      service.delete(sample)
    }
  }

  it should "find a song by id" in {
    val sample = gen[Song]

    val chain = for {
      store <- service.saveOrUpdate(sample)
      get <- service.getSongById(sample.id)
      delete <- service.delete(sample)
    } yield get

    whenReady(chain) { res =>
      res shouldBe defined
      service.delete(sample)
    }
  }

  it should "find songs by artist" in {
    val sample = gen[Song]
    val sample2 = gen[Song]
    val sample3 = gen[Song]

    val future = for {
      f1 <- service.saveOrUpdate(sample)
      f2 <- service.saveOrUpdate(sample2.copy(title = "Aerials"))
      f3 <- service.saveOrUpdate(sample3.copy(title = "Chop Suey"))
    } yield (f1, f2, f3)

    whenReady(future) { insert =>
      val songsByArtist = service.getSongsByArtist("System of a Down")
      whenReady(songsByArtist) { searchResult =>
        searchResult shouldBe a [List[_]]
        searchResult should have length 3
        service.delete(sample)
        service.delete(sample2)
        service.delete(sample3)
      }
    }
  }

  it should "be updated into cassandra" in {
    val uuid = UUIDs.timeBased()
    val sample = gen[Song]
    val updatedTitle = gen[String]

    val chain = for {
      store <- service.saveOrUpdate(sample)
      unmodified <- service.getSongById(sample.id)
      store <- service.saveOrUpdate(sample.copy(title = updatedTitle))
      modified <- service.getSongById(sample.id)
    } yield (unmodified, modified)

    whenReady(chain) {
      case (initial, modified) => {
        initial shouldBe defined
        initial.value.title shouldEqual sample.title

        modified shouldBe defined
        modified.value.title shouldEqual updatedTitle
      }
    }
  }
}