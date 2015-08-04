package com.cassandra.phantom.modeling.connector

import com.typesafe.config.ConfigFactory
import com.websudos.phantom.connectors.{ContactPoints, KeySpace, SimpleConnector}
import com.websudos.phantom.dsl.Session
import scala.collection.JavaConversions._

/**
 * Created by Thiago Pereira on 6/9/15.
 */
trait CassandraConnector extends SimpleConnector {
  val config = ConfigFactory.load()
  val hosts: Seq[String] = config.getStringList("cassandra.host").toList
  implicit val keySpace: KeySpace = KeySpace(config.getString("cassandra.keyspace"))
  override implicit lazy val session: Session = ContactPoints(hosts).keySpace(keySpace.name).session
}