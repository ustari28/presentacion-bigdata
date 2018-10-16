# Ejercicio 1
Crear Directorio
```jshelllanguage
hadoop fs -mkdir /bigdata/ficheros
```
Copiar un fichero del sistema local a HDFS
```jshelllanguage
hadoop fs -copyFromLocal /media/sf_ficheros-bd/bank.csv /bigdata/ficheros/
```
Copiar un fichero entre sistemas de archivos
```jshelllanguage
hadoop fs -put /media/sf_ficheros-bd/bank.csv /bigdata/ficheros/
```
Lista ficheros en HDFS
```jshelllanguage
hadoop fs -ls /bigdata/ficheros
```
Borrar un fichero en HDFS
```jshelllanguage
hadoop fs -rm /bigdata/ficheros/bank.csv
```
Copiar un fichero desde HDFS al sistema local
```jshelllanguage
hadoop fs -copyToLocal /bigdata/ficheros/bank.csv /media/sf_ficheros-bd/
```
Descargar un fichero desde HDFS
```jshelllanguage
hadoop fs -get /bigdata/ficheros/bank.csv /media/sf_ficheros-bd/
```

# Ejercicio 2
```jshelllanguage
split -l 10000 fichero
```
```jshelllanguage
hadoop fs -mkdir /bigdata/retail
haddop fs -put presentacion/online-retail.txt /bigdata/retail
```
crear esquema
```jql
create database bigdata;
```
crear tabla
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
crear tabla con origen
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
borrar la tabla
````jql
drop table bigdata.retail;
````
crear tabla con separador, cabecera y origen
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
crear tabla y cargarla luego
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
Cargar ficheros en tablas
```jshelllanguage
load data inpath '/bigdata/retail/' overwrite into table bigdata.retail;
```
Particionar una tabla
````jql
create table bigdata.retail-par partitioned by (country) stored as parquet
as select * from bigdata.retail;
````
Cargar ficheros comprimidos
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
Carga de ficheros a tabla
````jshelllanguage
load data inpath '/bigdata/retail-compress/online-retail.txt.bz2' overwrite into table bigdata.retail;
````
# Ejercicio 3
Configuramos hdfs con spark
````jshelllanguage
cp /usr/lib/hive/conf/hive-site.xml  /usr/lib/spark/conf/
````
Arrancamos spark-shell
```jshelllanguage
$SPARK_HOME/bin/spark-shell
```
Arrancamos con Scala
````scala
import sqlContext.implicits._
import org.apache.spark.sql._
case class Retail(InvoiceNo: String,StockCode: String,Description: String,Quantity: Integer,InvoiceDate: String, UnitPrice: Float,CustomerID: String, Country: String)
val fich = sc.textFile("/bigdata/retail/online-retail.txt")
fich take 10
val df_retail = fich.map(_.split(";").toSeq).map(x => Retail(x(0),x(1),x(2),x(3),x(4),x(5),x(6),x(7))).toDF
val df_retail = fich.map(_.split(";").toSeq).map(x => Retail(x(0),x(1),x(2),x(3).toInt,x(4),x(5).toFloat,x(6),x(7))).toDF
````
Creamos otra tabla en hive
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
Combinamos spark y hive
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