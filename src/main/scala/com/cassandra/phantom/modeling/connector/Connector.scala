package com.cassandra.phantom.modeling.connector

import com.outworkers.phantom.connectors.{CassandraConnection, ContactPoints}
import com.typesafe.config.ConfigFactory

import scala.collection.JavaConverters._

object Connector {
  private val config = ConfigFactory.load()

  private val hosts = config.getStringList("cassandra.host").asScala
  private val keyspace = config.getString("cassandra.keyspace")

  /**
    * Create a connector with the ability to connects to multiple hosts in a cluster
    *
    * If you need to connect to a secure cluster, use:
    * {{{
    * ContactPoints(hosts)
    *   .withClusterBuilder(_.withCredentials(username, password))
    *   .keySpace(keyspace)
    * }}}
    *
    */
  lazy val connector: CassandraConnection = ContactPoints(hosts).keySpace(keyspace)

}
