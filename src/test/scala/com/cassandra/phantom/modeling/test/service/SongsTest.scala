package com.cassandra.phantom.modeling.test.service

import java.util.UUID

import com.cassandra.phantom.modeling.entity.Songs
import com.cassandra.phantom.modeling.service.SongsService
import com.cassandra.phantom.modeling.test.utils.CassandraSpec
import com.datastax.driver.core.utils.UUIDs

/**
 * Created by Thiago Pereira on 8/4/15.
 */
class SongsTest extends CassandraSpec with SongsService {

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
    val songsList = List(
      fixture(UUIDs.timeBased()).songs,
      fixture(UUIDs.timeBased()).songs.copy(title = "Aerials"),
      fixture(UUIDs.timeBased()).songs.copy(title = "Chop Suey")
    )

    val future = service.saveList(songsList)

    whenReady(future) { insertResult =>
      val songsByArtist = service.getSongsByArtist("System of a Down")
      whenReady(songsByArtist) { searchResult =>
        insertResult.size shouldEqual searchResult.size
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
}