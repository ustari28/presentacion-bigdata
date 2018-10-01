# Launches application spark-submit
We use shade plugin for packaging all mandatory dependencies.
```
spark-submit --conf spark.es.nodes=elasticsearch --class com.alan.developer.bigdata.AppicationLogging "logging-streaming-kafka-1.0-SNAPSHOT.jar"
```
# Creates topic
```
kafka-topics.bat --create --topic logs-app --partitions 1 --replication-factor 1 --zookeeper kafka:2181
```
# Loads data
```
kafka-console-producer.bat --topic logs-app --broker-list kafka:9092 < online_retail_500.txt
```