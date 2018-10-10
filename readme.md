# Launches application spark-submit
We use shade plugin for packaging all mandatory dependencies.
```
spark-submit --conf spark.es.nodes=elasticsearch --class com.alan.developer.bigdata.AppicationLogging "logging-streaming-kafka-1.0-SNAPSHOT.jar"
```
We get a sample of records from ES with
```
http://localhost:9200/logging-*/_search
```
Mapping for indexes in ES
```
http://localhost:9200/_template/logging
{
  "index_patterns": [
    "logging-*"
  ],
  "settings": {
    "number_of_shards": 1
  },
  "mappings": {
    "log": {
      "_source": {
        "enabled": true
      },
      "properties": {
        "appName": {
          "type": "keyword",
          "fields": {
            "keyword": {
              "type": "keyword",
              "ignore_above": 256
            }
          }
        },
        "clazz": {
          "type": "text",
          "fields": {
            "keyword": {
              "type": "keyword",
              "ignore_above": 256
            }
          }
        },
        "level": {
          "type": "text",
          "fields": {
            "keyword": {
              "type": "keyword",
              "ignore_above": 256
            }
          }
        },
        "message": {
          "type": "text",
          "fields": {
            "keyword": {
              "type": "keyword",
              "ignore_above": 256
            }
          }
        },
        "timestamp": {
          "type": "date",
          "format": "yyyy-MM-dd HH:mm:ss"
        }
      }
    }
  }
}
```


# Creates topic
```
kafka-topics.bat --create --topic logs-app --partitions 1 --replication-factor 1 --zookeeper kafka:2181
```
# Loads data
```
kafka-console-producer.bat --topic logs-app --broker-list kafka:9092 < online_retail_500.txt
```