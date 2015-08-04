package com.cassandra.phantom.modeling.test.utils

import org.scalatest.concurrent.ScalaFutures
import org.scalatest.{FlatSpec, Matchers}

/**
 * Created by Thiago Pereira on 7/8/15.
 */
abstract class CassandraSpec extends FlatSpec with Matchers with ScalaFutures