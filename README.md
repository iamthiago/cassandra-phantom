# Cassandra + Phantom Example

This project will give you the idea on how to design your cassandra tables in scala using the phantom dsl.

## Data Modeling Concepts

Cassandra tables should be designed using a query-driven approach. 
Now let's check our query requirements before create the tables.

### Requirements

- Our Query1 should find a song by it's id
- Our Query2 should find all songs from a specific artist.

### Modeling

So far we know cassandra uses the query-driven approach and we also know our requirements, let's create our tables.

```sql
CREATE KEYSPACE test WITH replication = {'class': 'SimpleStrategy', 'replication_factor': 1};
```

```sql
CREATE TABLE test.songs (
    song_id timeuuid PRIMARY KEY,
    album text,
    artist text,
    title text
);

CREATE TABLE test.songs_by_artist (
    artist text,
    song_id timeuuid,
    album text,
    title text,
    PRIMARY KEY (artist, song_id)
) WITH CLUSTERING ORDER BY (song_id ASC);
```

### Data

Let's put some data and see how it works with our tables.

```sql
insert into songs(song_id, title, album, artist) values(now(), 'Prison Song', 'Toxicity', 'System of a Down');
insert into songs(song_id, title, album, artist) values(now(), 'Chop Suey', 'Toxicity', 'System of a Down');
insert into songs(song_id, title, album, artist) values(now(), 'Aerials', 'Toxicity', 'System of a Down');

select * from songs;

song_id                               | album    | artist           | title
--------------------------------------+----------+------------------+-------------
 b49e4790-3abd-11e5-9032-1572798d8cfa | Toxicity | System of a Down | Prison Song
 b49e6ea1-3abd-11e5-9032-1572798d8cfa | Toxicity | System of a Down |   Chop Suey
 b49e6ea0-3abd-11e5-9032-1572798d8cfa | Toxicity | System of a Down |     Aerials
```

How about our songs_by_artist? In CQL we need to do it again.

```sql
insert into songs_by_artist(song_id, title, album, artist) values(now(), 'Prison Song', 'Toxicity', 'System of a Down');
insert into songs_by_artist(song_id, title, album, artist) values(now(), 'Chop Suey', 'Toxicity', 'System of a Down');
insert into songs_by_artist(song_id, title, album, artist) values(now(), 'Aerials', 'Toxicity', 'System of a Down');

select * from songs_by_artist;

artist           | song_id                               | album    | title
------------------+--------------------------------------+----------+-------------
 System of a Down | b49e4790-3abd-11e5-9032-1572798d8cfa | Toxicity | Prison Song
 System of a Down | b49e6ea0-3abd-11e5-9032-1572798d8cfa | Toxicity |     Aerials
 System of a Down | b49e6ea1-3abd-11e5-9032-1572798d8cfa | Toxicity |   Chop Suey
```

Thus we can lookup for songs from a specific artist.

```sql
select * from songs_by_artist where artist = 'System of a Down';

artist           | song_id                               | album    | title
------------------+--------------------------------------+----------+-------------
 System of a Down | b49e4790-3abd-11e5-9032-1572798d8cfa | Toxicity | Prison Song
 System of a Down | b49e6ea0-3abd-11e5-9032-1572798d8cfa | Toxicity |     Aerials
 System of a Down | b49e6ea1-3abd-11e5-9032-1572798d8cfa | Toxicity |   Chop Suey
```

### Phantom

Looking at the documentation, it shows simple examples on how to get your cassandra table into scala.

This project explore the scala generics to make our life easier and do not duplicate code when creating the same table with different partitions key.

## Features

#### Apart of data modeling, you will also find:

- Connect to a secure(user/password) Cassandra Cluster
- Set Consistency Level for any kind of statement
- Handle inserts/deletes for duplicate tables

## Resources

- [http://docs.datastax.com/en/cql/3.1/cql/ddl/dataModelingApproach.html](http://docs.datastax.com/en/cql/3.1/cql/ddl/dataModelingApproach.html)
- [https://github.com/websudos/phantom](https://github.com/websudos/phantom)
- [https://medium.com/@foundev/cassandra-batch-loading-without-the-batch-keyword-40f00e35e23e](https://medium.com/@foundev/cassandra-batch-loading-without-the-batch-keyword-40f00e35e23e)
