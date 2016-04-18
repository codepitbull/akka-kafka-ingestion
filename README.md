This is a little demo project to show how a combination of a [akka-streams](http://www.lightbend.com/activator/template/akka-stream-scala),
 [Reactive Kafka](https://github.com/akka/reactive-kafka) and [Kafka](https://kafka.apache.org/) can be used as the ingestion part
 of a fast data system.

#Setup
First you will have to set up a Kafka/Zookeeper combo.

On Mac OS X do the following (partly taken from [here](https://hub.docker.com/r/greytip/kafka/)):

```
export DOCKER_IP="$(docker-machine ip default)"

docker run -d --name zookeeper --publish 2181:2181 jplock/zookeeper:3.4.6
docker run -d --hostname $DOCKER_IP --name kafka --publish 9092:9092 --publish 7203:7203 --env KAFKA_ADVERTISED_HOST_NAME=$DOCKER_IP --env ZOOKEEPER_IP=$DOCKER_IP greytip/kafka
```

#Ingestion
Start the ingestion by launching _de.codepitbull.akka.ingestion.Ingestion_ from your IDE using DOCKER_IP as main-Parameter.

#Digestion
There are two ways to verify the written data.

Launch _de.codepitbull.akka.ingestion.Ingestion_ from your IDE using DOCKER_IP as main-Parameter.

Verify inside the Kafka-Docker container:
```
docker exec -it kafka bash
export JMX_PORT=6666
bin/kafka-console-consumer.sh --zookeeper `<USE DOCKER IP HERE>:2181 --topic valid --from-beginning
``