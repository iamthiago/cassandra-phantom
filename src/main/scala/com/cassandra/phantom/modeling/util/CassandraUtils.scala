package com.cassandra.phantom.modeling.util


import com.datastax.driver.core.{ConsistencyLevel, ResultSet, Session, Statement}
import com.websudos.phantom.dsl.Row

import scala.collection.JavaConverters._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
 * Created by thiago on 11/23/15.
 *
 * Helper class to handle Cassandra consistency level operations from a statement
 */
object CassandraUtils {

  implicit class ExecuteStatement(statement: Statement) {

    /**
     * Execute a statement from a session with a specific consistency level
     *
     * @param consistencyLevel
     * @param session
     * @return
     */
    def runWith(consistencyLevel: ConsistencyLevel)(implicit session: Session): Future[ResultSet] = {
      statement.setConsistencyLevel(consistencyLevel)
      Future(session.execute(statement))
    }

    /**
     * Execute a statement from a session with a specific consistency level to get a single [[Row]]
     *
     * @param consistencyLevel
     * @param session
     * @return
     */
    def getOneWith(consistencyLevel: ConsistencyLevel)(implicit session: Session): Row = {
      statement.setConsistencyLevel(consistencyLevel)
      session.execute(statement).one()
    }

    /**
     * Execute a statement from a session with a specific consistency level to get a List of [[Row]]
     *
     * @param consistencyLevel
     * @param session
     * @return
     */
    def getListWith(consistencyLevel: ConsistencyLevel)(implicit session: Session): List[Row] = {
      statement.setConsistencyLevel(consistencyLevel)
      session.execute(statement).all().asScala.toList
    }
  }
}