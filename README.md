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
 
Adding more nodes reduces the time considerably.
 
 E.g 

 A single node with a 8GB Heap takes 121 secs to insert and 764ms to query 381393 records
 
 3 nodes - 28 secs to add, 1 secs to query.
 
 4 nodes - 29 secs to add, 1 secs to query.
 
 5 nodes - 29 secs to add, 1.2 secs to query.
 
 6 nodes - 20 secs to add, 785ms to query.
 
 If you run without a cluster - reduce the number of records in the LIMIT constant to around 1,000,000 or add LOTS of memory
 
# Downloading the data
 
 
 Use the following curl command to down load the file - place it into src/data
 
 `curl https://data.cityofchicago.org/api/views/ijzp-q8t2/rows.csv?accessType=DOWNLOAD>crimes.csv`
 
 Note that the file has over 6,000,000 records and is around 1.5GB in size.
 
