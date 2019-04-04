# Phantom DSL Example

[![Build Status](https://travis-ci.org/iamthiago/cassandra-phantom.svg?branch=master)](https://travis-ci.org/iamthiago/cassandra-phantom)

This project will give you the idea on how to design your cassandra tables in scala using the [phantom-dsl](https://github.com/outworkers/phantom). My inspiration is to bring here a more real world example based on this library.

### Connect to a secure Cassandra Cluster

In the class ```Connector``` you will find a connector that connects to a Cassandra Cluster

### Set Consistency Level

Phantom-DSL offers to you an easy way to set the consistency level for any query you want to execute. You can set that for each query. You can see an example of it in the following classes: ```SongsModel``` and ```SongsByArtistModel```. You will find this piece of code ```.consistencyLevel_=(ConsistencyLevel.ONE)``` where you can change it to whatever you want, accordingly to your needs.

### Handle queries for multiple tables

This is maybe the main goal of this project, showing to you how to handle multiple versions of the same table. Here we have:

- Songs
- SongsByArtist

You will find how to insert and delete on both tables.

### Requirements

This project uses docker in order to get up and running a cassandra instance.

### Testing

We are using the official cassandra docker image to setup a simple host.

For convenience we are using Makefile where you will find the following commands:

    - make cassandra/run
        This command starts a cassandra instance in your localhost and name it as cassandra-phantom.
        You can see it using the `docker ps` command.
        
    - make cassandra/clean 
        It removes the existing image from your docker.
        
    - make cassandra 
        Runs both command in sequence.

If you don't know Makefile, please check the links below on the Resources section.

After that you can test using `sbt test`.

## Resources

- [http://docs.datastax.com/en/cql/3.1/cql/ddl/dataModelingApproach.html](http://docs.datastax.com/en/cql/3.1/cql/ddl/dataModelingApproach.html)
- [https://github.com/outworkers/phantom](https://github.com/outworkers/phantom)
- [https://store.docker.com/images/cassandra](https://store.docker.com/images/cassandra)
- [https://github.com/docker-library/cassandra](https://github.com/docker-library/cassandra)
- [https://en.wikipedia.org/wiki/Makefile](https://en.wikipedia.org/wiki/Makefile)

### Thanks

Special thanks to [Flavian](https://github.com/alexflav23) who have helped me to find out the best way to use phantom to model our Cassandra tables.
