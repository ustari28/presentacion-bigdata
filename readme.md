# Launches application spark-submit
We use shade plugin for packaging all mandatory dependencies.
```
--class com.alan.developer.bigdata.AppicationLogging --jars  "logging-streaming-kafka-1.0-SNAPSHOT.jar"
```
# Creates topic
```
kafka-topics.bat --create --topic logs-app --partitions 1 --replication-factor 1
```
# Loads data
```
kafka-console-producer.bat --topic logs-app --broker-list 127.0.0.1:9092 < online_retail_500.txt
```