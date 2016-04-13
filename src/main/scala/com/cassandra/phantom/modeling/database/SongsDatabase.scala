package com.cassandra.phantom.modeling.database

import com.cassandra.phantom.modeling.model.{ConcreteSongsByArtistModel, ConcreteSongsModel}
import com.websudos.phantom.db.DatabaseImpl
import com.websudos.phantom.dsl._
import com.cassandra.phantom.modeling.connector.Connector._

/**
  * Created by thiago on 4/13/16.
  */
class SongsDatabase(override val connector: KeySpaceDef) extends DatabaseImpl(connector) {
  object songsModel extends ConcreteSongsModel with connector.Connector
  object songsByArtistsModel extends ConcreteSongsByArtistModel with connector.Connector
}

/**
  * Production Database
  */
object ProductionDb extends SongsDatabase(connector.keySpace(keyspace))

trait ProductionDatabaseProvider {
  def database: SongsDatabase
}

trait ProductionDatabase extends ProductionDatabaseProvider {
  override val database = ProductionDb
}

/**
  * Embedded Database
  */
object EmbeddedDb extends SongsDatabase(testConnector)

trait EmbeddedDatabaseProvider {
  def database: SongsDatabase
}

trait EmbeddedDatabase extends EmbeddedDatabaseProvider {
  override val database = EmbeddedDb
}