cassandra/run:
	- docker run --name cassandra-phantom -d -e CASSANDRA_BROADCAST_ADDRESS=localhost -p 9042:9042 cassandra:latest

cassandra/clean:
	- docker rm -f cassandra-phantom

cassandra: cassandra/clean cassandra/run