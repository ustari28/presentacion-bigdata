# Launches application spark-submit
We use shade plugin for packaging all mandatory dependencies.
```
/spark/bin/spark-submit --master spark://spark-master.local:7077 --conf spark.es.nodes=elasticsearch.local --class com.alan.developer.bigdata.CentralLogging presentacion-bigdata-1.0-SNAPSHOT.jar "kafka.local:9092" "2"
```
Docker instuctions
````jshelllanguage
doker-compose
````
We get a sample of records from ES with
```
http://localhost:9200/logging-*/_search
```
Mapping *logging* for indexes in ES
http://localhost:9200/_template/template_id
```json
{"index_patterns":["jaeger-v1-*"],"settings":{"number_of_shards":1},"mappings":{"_source":{"enabled":true},"properties":{"duration":{"type":"text","fields":{"keyword":{"type":"keyword","ignore_above":256}}},"flags":{"type":"long"},"operationName":{"type":"text","fields":{"keyword":{"type":"keyword","ignore_above":256}}},"process":{"properties":{"serviceName":{"type":"text","fields":{"keyword":{"type":"keyword","ignore_above":256}}},"tags":{"properties":{"key":{"type":"text","fields":{"keyword":{"type":"keyword","ignore_above":256}}},"vStr":{"type":"text","fields":{"keyword":{"type":"keyword","ignore_above":256}}}}}}},"references":{"properties":{"spanId":{"type":"text","fields":{"keyword":{"type":"keyword","ignore_above":256}}},"traceId":{"type":"text","fields":{"keyword":{"type":"keyword","ignore_above":256}}}}},"spanId":{"type":"text","fields":{"keyword":{"type":"keyword","ignore_above":256}}},"startTime":{"type":"date","format":"yyyy-MM-dd HH:mm:ss.SSS"},"tags":{"properties":{"key":{"type":"text","fields":{"keyword":{"type":"keyword","ignore_above":256}}},"vStr":{"type":"text","fields":{"keyword":{"type":"keyword","ignore_above":256}}}}},"traceId":{"type":"text","fields":{"keyword":{"type":"keyword","ignore_above":256}}},"processDate":{"type":"date","format":"yyyy-MM-dd HH:mm:ss.SSS"},"ingestDate":{"type":"date","format":"yyyy-MM-dd HH:mm:ss.SSS"}}}}
```

# Create topic
```
kafka-topics.bat --create --topic logs-app --partitions 1 --replication-factor 1 --zookeeper kafka:2181
```
# Load data
```
kafka-console-producer.bat --topic logs-app --broker-list kafka:9092 < online_retail_500.txt
```