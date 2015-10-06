package com.cassandra.phantom.modeling.connector

import java.net.InetAddress

import com.datastax.driver.core.Cluster
import com.typesafe.config.ConfigFactory
import com.websudos.phantom.connectors.{KeySpace, SimpleConnector}
import com.websudos.phantom.dsl.Session

import scala.collection.JavaConversions._

/**
 * Created by Thiago Pereira on 6/9/15.
 *
 * Cassandra Connector extends the Simple Connector from phantom-dsl,
 * establishing a connection to a secure cluster with username and password
 */
trait CassandraConnector extends SimpleConnector {
  val config = ConfigFactory.load()
  val hosts = config.getStringList("cassandra.host")
  val inets = hosts.map(InetAddress.getByName)
  implicit val keySpace: KeySpace = KeySpace(config.getString("cassandra.keyspace"))
  val cluster = Cluster.builder().addContactPoints(inets).withCredentials(config.getString("cassandra.username"), config.getString("cassandra.password")).build()
  override implicit lazy val session: Session = cluster.connect(keySpace.name)
}