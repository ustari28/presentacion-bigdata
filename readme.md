# Launches application spark-submit
We use shade plugin for packaging all mandatory dependencies.
```
spark-submit --master spark://spark-master:7077 --conf spark.es.nodes=elasticsearch --class com.alan.developer.bigdata.CentralLogging logging-streaming-kafka-1.0-SNAPSHOT.jar "kafka-server" "zookeeper-server"
spark-submit --master spark://spark-master:7077 --conf spark.es.nodes=elasticsearch --class com.alan.developer.bigdata.RequestLogging logging-streaming-kafka-1.0-SNAPSHOT.jar "kafka-server" "zookeeper-server"
```
We get a sample of records from ES with
```
http://localhost:9200/logging-*/_search
```
Mapping *logging* for indexes in ES
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
Mapping *logsapp* for indexes ES:
```
http://localhost:9200/_template/logsapp
{
  "index_patterns": [
    "logsapp-*"
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
        "duration": {
          "type": "long"
        },
        "id": {
          "type": "text",
          "fields": {
            "keyword": {
              "type": "keyword",
              "ignore_above": 256
            }
          }
        },
        "kind": {
          "type": "text",
          "fields": {
            "keyword": {
              "type": "keyword",
              "ignore_above": 256
            }
          }
        },
        "localEndpoint": {
          "properties": {
            "serviceName": {
              "type": "text",
              "fields": {
                "keyword": {
                  "type": "keyword",
                  "ignore_above": 256
                }
              }
            }
          }
        },
        "name": {
          "type": "text",
          "fields": {
            "keyword": {
              "type": "keyword",
              "ignore_above": 256
            }
          }
        },
        "remoteEndpoint": {
          "properties": {
            "ipv4": {
              "type": "text",
              "fields": {
                "keyword": {
                  "type": "keyword",
                  "ignore_above": 256
                }
              }
            },
            "port": {
              "type": "long"
            }
          }
        },
        "tags": {
          "properties": {
            "http": {
              "properties": {
                "method": {
                  "type": "text",
                  "fields": {
                    "keyword": {
                      "type": "keyword",
                      "ignore_above": 256
                    }
                  }
                },
                "path": {
                  "type": "text",
                  "fields": {
                    "keyword": {
                      "type": "keyword",
                      "ignore_above": 256
                    }
                  }
                }
              }
            },
            "mvc": {
              "properties": {
                "controller": {
                  "properties": {
                    "class": {
                      "type": "text",
                      "fields": {
                        "keyword": {
                          "type": "keyword",
                          "ignore_above": 256
                        }
                      }
                    },
                    "method": {
                      "type": "text",
                      "fields": {
                        "keyword": {
                          "type": "keyword",
                          "ignore_above": 256
                        }
                      }
                    }
                  }
                }
              }
            }
          }
        },
        "timestamp": {
          "type": "long"
        },
        "createDate": {
          "type": "date",
          "format": "yyyy-MM-dd HH:mm:ss"
        },
        "traceId": {
          "type": "text",
          "fields": {
            "keyword": {
              "type": "keyword",
              "ignore_above": 256
            }
          }
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