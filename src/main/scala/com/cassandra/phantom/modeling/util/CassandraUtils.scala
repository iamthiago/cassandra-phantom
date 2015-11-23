package com.cassandra.phantom.modeling.util

import com.datastax.driver.core.{ConsistencyLevel, ResultSet, Session, Statement}

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
  }
}