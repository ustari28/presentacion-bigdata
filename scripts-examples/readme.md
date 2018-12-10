# Example 1
Create dir
```jshelllanguage
hadoop fs -mkdir /bigdata/ficheros
```
Copy file from local system to HDFS System
```jshelllanguage
hadoop fs -copyFromLocal /media/sf_ficheros-bd/bank.csv /bigdata/ficheros/
```
Copy a file between file systems
```jshelllanguage
hadoop fs -put /media/sf_ficheros-bd/bank.csv /bigdata/ficheros/
```
List files in HDFS system
```jshelllanguage
hadoop fs -ls /bigdata/ficheros
```
Remove a file from HDFS system
```jshelllanguage
hadoop fs -rm /bigdata/ficheros/bank.csv
```
Copy a file from HDFS system to local system
```jshelllanguage
hadoop fs -copyToLocal /bigdata/ficheros/bank.csv /media/sf_ficheros-bd/
```
Download a file from HDFS system to local system
```jshelllanguage
hadoop fs -get /bigdata/ficheros/bank.csv /media/sf_ficheros-bd/
```

# Example 2
```jshelllanguage
split -l 10000 fichero
```
```jshelllanguage
hadoop fs -mkdir /bigdata/retail
haddop fs -put presentacion/online-retail.txt /bigdata/retail
```
Create schema
```jql
create database bigdata;
```
Create table
```jql
create table bigdata.retail(
    invoiceno STRING,
    stockcode STRING,
    description STRING,
    quantity INT,
    invoicedate STRING,
    unitprice FLOAT,
    customerid BIGINT,
    country STRING
);
```
ver el sql usado para crear la tabla
````jql
show create table bigdata.retail;
````
Create a table from from source files
````jql
create table bigdata.retail(
    invoiceno STRING,
    stockcode STRING,
    description STRING,
    quantity INT,
    invoicedate STRING,
    unitprice FLOAT,
    customerid BIGINT,
    country STRING
) location '/bigdata/retail';
````
Delete table
````jql
drop table bigdata.retail;
````
Create a table with separator, header and source
````jql
create table bigdata.retail(
    invoiceno STRING,
    stockcode STRING,
    description STRING,
    quantity INT,
    invoicedate STRING,
    unitprice FLOAT,
    customerid BIGINT,
    country STRING
)
row format delimited fields terminated by ';'
location '/bigdata/retail'
TBLPROPERTIES ("skip.header.line.count"="1");
````
Create table first and upload later
````jql
create table bigdata.retail(
    invoiceno STRING,
    stockcode STRING,
    description STRING,
    quantity INT,
    invoicedate STRING,
    unitprice FLOAT,
    customerid BIGINT,
    country STRING
)
row format delimited fields terminated by ';';
````
Upload files into tables
```jshelllanguage
load data inpath '/bigdata/retail/' overwrite into table bigdata.retail;
```
Partition a table
````jql
create table bigdata.retail-par partitioned by (country) stored as parquet
as select * from bigdata.retail;
````
Upload compress files into tables
````jql
create table bigdata.retail(
    invoiceno STRING,
    stockcode STRING,
    description STRING,
    quantity INT,
    invoicedate STRING,
    unitprice FLOAT,
    customerid BIGINT,
    country string
)
row format delimited fields terminated by ';'
TBLPROPERTIES ("skip.header.line.count"="1");
````
Upload files into a table
````jshelllanguage
load data inpath '/bigdata/retail-compress/online-retail.txt.bz2' overwrite into table bigdata.retail;
````

# Example 3
Set up Hive with spark
````jshelllanguage
cp /usr/lib/hive/conf/hive-site.xml  /usr/lib/spark/conf/
````
Launch spark-shell
```jshelllanguage
$SPARK_HOME/bin/spark-shell
```
Starting with Scala
````scala
import sqlContext.implicits._
import org.apache.spark.sql._
case class Retail(InvoiceNo: String,StockCode: String,Description: String,Quantity: Integer,InvoiceDate: String, UnitPrice: Float,CustomerID: String, Country: String)
val fich = sc.textFile("/bigdata/retail/online-retail.txt")
fich take 10
val df_retail = fich.map(_.split(";").toSeq).map(x => Retail(x(0),x(1),x(2),x(3),x(4),x(5),x(6),x(7))).toDF
val df_retail = fich.map(_.split(";").toSeq).map(x => Retail(x(0),x(1),x(2),x(3).toInt,x(4),x(5).toFloat,x(6),x(7))).toDF
````
Create table into hive again for spark example
````jql
create table bigdata.retail(
    invoiceno STRING,
    stockcode STRING,
    description STRING,
    quantity INT,
    invoicedate STRING,
    unitprice FLOAT,
    customerid BIGINT,
    country string
)
stored as parquet;
````
Spark and hive
````scala
import sqlContext.implicits._
import org.apache.spark.sql._
val fic = sc.textFile("/bigdata/retail/online-retail-nohead.txt")
val df_retail = fic.map(_.split(";").toSeq).map(x => Row(x(0), x(1), x(2), x(3).toInt, x(4), x(5).replace(",",".").toFloat, x(6).toLong, x(7)))
val df_retail = fic.map(_.split(";").toSeq).map(x => Row(x(0), x(1), x(2), Option(x(3)).getOrElse("0").toInt, x(4), Option(x(5)).getOrElse("0.0").replace(",",".").toFloat, Option(x(6)).map(d => if(d.isEmpty) "0" else d).getOrElse("0").toLong, x(7)))
val sc_retail = sqlContext.table("bigdata.retail").schema
val df = sqlContext.createDataFrame(df_retail, sc_retail)
df.write.mode(“overwrite”).insertInto(“bigdata.retail”)
invalidate medatada bigdata.retail
df count
````
More Spark
````scala
spark.read.format("csv").option("header", "true").option("delimiter",";").load("")
spark.read.option("header", "true").csv("")
````
Using sql functions in Dataframes
````scala
case class Retail(InvoiceNo: String,StockCode: String,Description: String,Quantity: Integer,InvoiceDate: String,UnitPrice: Float,CustomerID: String,Country: String)
val raw = spark.read.format("csv").option("header", "true").option("delimiter",";").load("online_retail.txt").withColumn("Quantity", 'Quantity.cast(IntegerType)).withColumn("UnitPrice", when(col("UnitPrice").isNull,
 lit("0.0")).otherwise(regexp_replace(col("UnitPrice"),",",".")).cast(FloatType)).as[Retail]
````