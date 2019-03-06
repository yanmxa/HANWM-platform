# HANWPlatform : Human and Nature Water Management Platform
## * especially for agent based modelling *
---------------------------------------
>## main dependencies
        maven *
        mysql ^5.0
        java 1.8    
>## How to use ?

>>### Configuration your project
      configuration configuration/application.properties
      configuration configuration/database.properties
      configuration configuration/simulation.properties
      configuration configuration/location.properties

>>### Run database migrations:
        mvn db-migrator:create        - create database hanwu       
        mvn db-migrator:migrate       - create tables
          
>>### Build program:
        mvn clean install
          
>>### Instrumentation(IDE):
        mvn process-classes 
        (or) mvn activejdbc-instrumentation:instrument

>>###  Initial data
        use InitData.java to initial project datas which is located at src/com/utils/   
          
>##  How to write a new migration ?

>>### Generate a new migration file:
        mvn db-migrator:new -Dname=create_example_table

>>### The newly created fle is empty. Go ahead and add raw SQL to the file:
        create table people ( name varchar (10));......

>>### Run migration:   
        mvn db-migrator:migrate
        