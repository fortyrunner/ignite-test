# Introduction 

A sample project that uses Apache Camel and Ignite to 

1. watch for a CSV file of Chicago crimes
2. parse the file and create a collection of objects
3. insert them into an Apache Ignite instance
4. Perform selected reads using SQL filters

# Data
 
This demo is best run when connecting to an Ignite cluster of 4 nodes or more.

This was tested on a 4.0GHZ 24GB Core i7 iMac
There are 6,000,000 lines in the CSV file.
 
Adding more nodes reduces the time a fair bit but there is a point of diminishing returns YMMV.
 
 E.g 

 A single node with a 8GB Heap takes 121 secs to insert and 764ms to query 381393 records
 
 3 nodes - 28 secs to add, 1 secs to query.
 
 4 nodes - 29 secs to add, 1 secs to query.
 
 5 nodes - 29 secs to add, 1.2 secs to query.
 
 6 nodes - 20 secs to add, 785ms to query.
 
 If you run without a cluster - reduce the number of records in the LIMIT constant to around 1,000,000 or add LOTS of memory
 
# Downloading the data
  
 Use the following curl command to download the file - place it into `src/data`
 
 `curl https://data.cityofchicago.org/api/views/ijzp-q8t2/rows.csv?accessType=DOWNLOAD>crimes.csv`
 
 Note that the file has over 6,000,000 records and is around 1.5GB in size.
 
 A small file with a few lines has been added to this distro
 
In the root folder there is a bash script called `node`. This will start a single node up using the `crime-cache.xml` file.
Don't forget to `chmod` it!

# Setting up a JDBC Connection

This can be quite fiddly the first time. I had a few false starts and the documentation on the Ignite website and elsewhere
can be a bit difficult to parse.

Here are my tips.

* Make sure that you name your tables correctly. 
  * I created a cache called CRIMES and a class (to insert into the table) called Crime. 
  * This is wrong, you must name the table the same name as the class
* You may need to explicitly set PeerClassLoading in code and config
  * E.g. `cfg.setPeerClassLoadingEnabled(true);`
  * You will also need to do this in the XML files
* Setting up your own `ignite-jdbc.xml` file is worth while.
  * It makes connection a lot easier - see the `beans.xml` file for how to setup a datasource
* You will need to add custom classes to the xml config for indexing
  * E.g. `<property name="indexedTypes" value="java.lang.String,fortyrunner.Crime"/>`






.

