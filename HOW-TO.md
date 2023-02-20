## Requirements
* JDK 17
* Maven
## How to compile and run tests
`mvn clean test`
## How to build application
`mvn clean package`
## How to run application
`java -jar target/meterhub-0.0.1-SNAPSHOT.jar`
## How to use application
See below curl commands to use the endpoints
* Upload a meter reading report via file at file_path

`curl --location 'http://localhost:8080/upload' --form 'file=@"{file_path}"'`
* Get meter list

`curl --location 'http://localhost:8080/report/meters'`
* Get total reading for a meter with identifier meter_id

`curl --location 'http://localhost:8080/report/total_reading/{meter_id}'`
* Get total cost for a meter with identifier meter_id

`curl --location 'http://localhost:8080/report/total_cost/{meter_id}'`
* Get hourly report for a meter with identifier meter_id

`curl --location 'http://localhost:8080/report/hourly_report/{meter_id}'`