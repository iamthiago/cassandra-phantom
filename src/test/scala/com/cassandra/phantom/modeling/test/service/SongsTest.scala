package com.cassandra.phantom.modeling.test.service

import java.util.UUID

import com.cassandra.phantom.modeling.entity.Songs
import com.cassandra.phantom.modeling.service.SongsService
import com.cassandra.phantom.modeling.test.utils.CassandraSpec
import com.datastax.driver.core.utils.UUIDs
import org.scalatest.BeforeAndAfterAll

/**
 * Created by Thiago Pereira on 8/4/15.
 */
class SongsTest extends CassandraSpec with BeforeAndAfterAll with SongsService {

  override def beforeAll(): Unit = {
    super.beforeAll()
    service.createTables
  }

  override def afterAll(): Unit = {
    super.afterAll()
  }

  def fixture(id: UUID) = new {
    val songs = Songs(id, "Prison Song", "Toxicity", "System of a Down")
  }

  "A Song" should "be inserted into cassandra" in {
    val uuid = UUIDs.timeBased()
    val future = service.saveOrUpdate(fixture(uuid).songs)

    whenReady(future) { result =>
      result isExhausted() shouldBe true
      result wasApplied() shouldBe true
      service.delete(fixture(uuid).songs)
    }
  }

  it should "find a song by id" in {
    val uuid = UUIDs.timeBased()
    val future = service.saveOrUpdate(fixture(uuid).songs)

    whenReady(future) { _ =>
      val found = service.getBySongsId(fixture(uuid).songs.songId)
      whenReady(found) { found =>
        found shouldBe defined
        service.delete(fixture(uuid).songs)
      }
    }
  }

  it should "find songs by artist" in {
    val uuid1 = UUIDs.timeBased()
    val uuid2 = UUIDs.timeBased()
    val uuid3 = UUIDs.timeBased()

    val songsList = List(
      fixture(uuid1).songs,
      fixture(uuid2).songs.copy(title = "Aerials"),
      fixture(uuid3).songs.copy(title = "Chop Suey")
    )

    val future = service.saveList(songsList)

    whenReady(future) { insertResult =>
      val songsByArtist = service.getSongsByArtist("System of a Down")
      whenReady(songsByArtist) { searchResult =>
        insertResult.size shouldEqual searchResult.size
        service.delete(fixture(uuid1).songs)
        service.delete(fixture(uuid2).songs)
        service.delete(fixture(uuid3).songs)
      }
    }
  }

  it should "be updated into cassandra" in {
    val uuid = UUIDs.timeBased()
    val future = service.saveOrUpdate(fixture(uuid).songs)

    whenReady(future) { _ =>
      val foundInserted = service.getBySongsId(fixture(uuid).songs.songId)
      whenReady(foundInserted) { inserted =>
        val updated = inserted.get.copy(title = "Aerials")
        val futureToBeUpdated = service.saveOrUpdate(updated)
        whenReady(futureToBeUpdated) { x =>
          val found = service.getBySongsId(fixture(uuid).songs.songId)
          whenReady(found) { toBeChecked =>
            toBeChecked shouldBe defined
            toBeChecked.get.title shouldEqual "Aerials"
            service.delete(fixture(uuid).songs)
          }
        }
      }
    }
  }

  it should "do batch update" in {
    val uuid1 = UUIDs.timeBased()
    val uuid2 = UUIDs.timeBased()
    val uuid3 = UUIDs.timeBased()

    val songsList = List(
      fixture(uuid1).songs,
      fixture(uuid2).songs.copy(title = "Aerials"),
      fixture(uuid3).songs.copy(title = "Chop Suey")
    )

    val future = service.batchUpdate(songsList)

    whenReady(future) { _ =>
      service.delete(fixture(uuid1).songs)
      service.delete(fixture(uuid2).songs)
      service.delete(fixture(uuid3).songs)
    }
  }
}