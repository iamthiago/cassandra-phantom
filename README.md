# Phantom DSL Example

[![Build Status](https://travis-ci.org/thiagoandrade6/cassandra-phantom.svg?branch=master)](https://travis-ci.org/thiagoandrade6/cassandra-phantom)

This project will give you the idea on how to design your cassandra tables in scala using the [phantom-dsl](https://github.com/outworkers/phantom). My inspiration is to bring here a more real world example based on this library.

## Features

Across the code, you will find the following features:

### Connect to a secure Cassandra Cluster

In the class ```Connector``` you will find two connectors:
- Connect to a Cassandra Cluster somewhere
- Connect using an embedded Cassandra (for tests only running through ```sbt test```)

### Set Consistency Level

Phantom-DSL offers to you an easy way to set the consistency level for any query you want to execute. Throught the ```GenericSongsModel``` you will find this piece of code ```.consistencyLevel_=(ConsistencyLevel.ONE)``` where you can change it to whatever you want, accordingly to your needs.

### Handle queries for multiple tables

This is maybe the main goal of this project, showing to you how to handle multiple versions of the same table. Here we have:

- Songs
- SongsByArtist

You will find how to insert and delete on both tables

### Testing using an embedded Cassandra in memory

Using ```sbt test``` you can run the tests without having any previously Cassandra instalation up and running.

### Simple streaming example using Akka Streams

Under the test folders, you will find a very simple Streaming example, creating a ```Source``` from a publisher provided by the phantom's reactive-streams module.

## Resources

- [http://docs.datastax.com/en/cql/3.1/cql/ddl/dataModelingApproach.html](http://docs.datastax.com/en/cql/3.1/cql/ddl/dataModelingApproach.html)
- [https://github.com/outworkers/phantom](https://github.com/outworkers/phantom)
- [https://medium.com/@foundev/cassandra-batch-loading-without-the-batch-keyword-40f00e35e23e](https://medium.com/@foundev/cassandra-batch-loading-without-the-batch-keyword-40f00e35e23e)

### Thanks

Special thanks to [Flavian](https://github.com/alexflav23) who helped me to find out the best way to use phantom to model our Cassandra tables.
