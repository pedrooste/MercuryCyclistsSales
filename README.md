# CSIT318-Project

Welcome to CSIT318 project repo for Mercury Cyclists Sales Microservice

## Maven install commands
## install dependencies
mvn clean install

## Build
mvn -B package --file pom.xml

## Run
mvn run

##Kafka setup
In order to run this project, please use Kafka 2.8.0
Before the application is started we must start the kafka environment and create the topic.
For windows

###Start zookeeper
`.\bin\windows\zookeeper-server-start.bat config\zookeeper.properties`

###Start kafka broker
`.\bin\windows\kafka-server-start.bat config\server.properties`

###Create topic
`.\bin\windows\kafka-topics.bat --create --topic backorder --bootstrap-server localhost:9092`

###Test send a message
`.\bin\windows\kafka-console-producer.bat --topic backorder --bootstrap-server localhost:9092`
enter message