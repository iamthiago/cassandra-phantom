package com.cassandra.phantom.modeling.model

import java.util.UUID

import com.cassandra.phantom.modeling.entity.Songs
import com.datastax.driver.core.Row
import com.websudos.phantom.CassandraTable
import com.websudos.phantom.column.TimeUUIDColumn
import com.websudos.phantom.dsl.StringColumn
import com.websudos.phantom.keys.{ClusteringOrder, PartitionKey}

/**
 * Created by Thiago Pereira on 8/4/15.
 */
trait GenericSongsModel {

  def model: InnerGeneric

  private[model] trait InnerGeneric extends CassandraTable[InnerGeneric, Songs] {
    
    def songId: TimeUUIDColumn[InnerGeneric, Songs]
    def artist: StringColumn[InnerGeneric, Songs]
    
    object title extends StringColumn(this)
    object album extends StringColumn(this)

    override def fromRow(r: Row): Songs = Songs(songId(r), title(r), album(r), artist(r))
  }
}

class SongsModel extends GenericSongsModel {
  object model extends InnerGeneric {

    override val tableName = "songs"

    object songId extends TimeUUIDColumn(this) with PartitionKey[UUID] { override lazy val name = "song_id" }
    object artist extends StringColumn(this)
  }
}

class SongsByArtistModel extends GenericSongsModel {
  object model extends InnerGeneric {

    override val tableName = "songs_by_artist"

    object artist extends StringColumn(this) with PartitionKey[String]
    object songId extends TimeUUIDColumn(this) with ClusteringOrder[UUID] { override lazy val name = "song_id" }
  }
}