package com.cassandra.phantom.modeling.entity

import java.util.UUID

/**
  *
  * This is the Scala representation of Songs, following the Datastax example
  */
case class Song(
                 id: UUID,
                 title: String,
                 album: String,
                 artist: String
               )
