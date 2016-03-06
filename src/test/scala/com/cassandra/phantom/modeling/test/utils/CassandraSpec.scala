package com.cassandra.phantom.modeling.test.utils

import org.scalatest.concurrent.ScalaFutures
import org.scalatest.{OptionValues, Inspectors, FlatSpec, Matchers}

abstract class CassandraSpec extends FlatSpec
  with Matchers
  with Inspectors
  with ScalaFutures
  with OptionValues