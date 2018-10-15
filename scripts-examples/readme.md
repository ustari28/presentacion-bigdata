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
load data inpath '/bigdata/retail-compress/online-retail.txt.bz2' overwrite into table bigdata.retail;
````