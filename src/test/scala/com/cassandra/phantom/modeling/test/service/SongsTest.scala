package com.cassandra.phantom.modeling.test.service

import com.cassandra.phantom.modeling.database.Database
import com.cassandra.phantom.modeling.entity.Song
import com.cassandra.phantom.modeling.test.utils.{CassandraSpec, SongsGenerator}
import com.outworkers.phantom.dsl._
import com.outworkers.util.testing._

import scala.concurrent.Future

/**
  * Tests Songs methods against an embedded cassandra
  *
  * Before executing it will create all necessary tables in our embedded cassandra
  * validating our model with the requirements described in the readme.md file
  */
class SongsTest extends CassandraSpec with SongsGenerator {

  override def beforeAll(): Unit = {
    super.beforeAll()
    database.create()
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
      _ <- this.store(sample)
      get <- database.SongsModel.getBySongId(sample.id)
      _ <- this.drop(sample)
    } yield get

    whenReady(chain) { res =>
      res shouldBe defined
      res.get shouldEqual sample
      this.drop(sample)
    }
  }

  it should "find songs by artist" in {
    val sample = gen[Song]
    val sample2 = gen[Song]
    val sample3 = gen[Song]

    val songsByArtist = (for {
      f1 <- this.store(sample.copy(title = "Toxicity"))
      f2 <- this.store(sample2.copy(title = "Aerials"))
      f3 <- this.store(sample3.copy(title = "Chop Suey"))
    } yield (f1, f2, f3)).flatMap { _ =>
      database.SongsByArtistsModel.getByArtist("System of a Down")
    }

    whenReady(songsByArtist) { searchResult =>
      searchResult shouldBe a[List[_]]
      searchResult should have length 3
      this.drop(sample)
      this.drop(sample2)
      this.drop(sample3)
    }
  }

  it should "be updated into cassandra" in {
    val sample = gen[Song]
    val updatedTitle = gen[String]

    val chain = for {
      _ <- this.store(sample)
      unmodified <- database.SongsModel.getBySongId(sample.id)
      _ <- this.store(sample.copy(title = updatedTitle))
      modified <- database.SongsModel.getBySongId(sample.id)
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

  /**
    * Utility method to store into both tables
    *
    * @param song the song to be inserted
    * @return a [[Future]] of [[ResultSet]]
    */
  private def store(song: Song): Future[ResultSet] = Database.saveOrUpdate(song)

  /**
    * Utility method to delete into both tables
    *
    * @param song the song to be deleted
    * @return a [[Future]] of [[ResultSet]]
    */
  private def drop(song: Song) = Database.delete(song)
}
