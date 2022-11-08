# Event Sourcing + CQRS Example
Example of [Event Sourcing Pattern](https://martinfowler.com/eaaDev/EventSourcing.html) with [CQRS](https://martinfowler.com/bliki/CQRS.html) implemented in [Spring Boot](https://projects.spring.io/spring-boot/) with [EventStoreDB](https://www.eventstore.com/eventstoredb).

Read model stored in [MongoDB](https://www.mongodb.com/).  
Used [Docker](https://www.docker.com/) to containerize environment.

## Architecture
![Architecture](https://user-images.githubusercontent.com/15820051/200380836-5c10d819-0d7e-485d-a271-dc98e4c14268.png)

## Services

### Citizen Command Service
Exposes REST endpoints to create, update, and delete citizens.  
The endpoints send EventStoreDB events.  

To avoid eventual consistency, `currentRevision` validation was introduced (this obviously depends on the project use-case).

Sometimes it happens that the connection to EventStoreDB is broken, hence a very simple retry mechanism was implemented.

### Citizen Event Handler MongoDB Read Model Service
Consumes EventStoreDB events and updated customers' read models stored in MongoDB.

It is  possible to create more services similar to this one, which will handle the data differently, store it in different databases, etc.  
It is also possible not to have a read model service at all. If there are not many events per aggregate, it is possible to build a model based on all occurred events from EventStoreDB every time an endpoint is requested.

### Citizen Query Service
Exposes REST endpoints to query citizens. Returns a MongoDB Citizen read model data when the read model endpoint is requested, and all the events stored in EventStoreDB when the audit endpoint is requested.  

## Build and testing
* Make sure you have `Docker` installed  
  [Docker installation](https://docs.docker.com/install/linux/docker-ce/ubuntu/)
* Install `Docker Compose` to run the importer locally  
  [Docker Compose installation](https://docs.docker.com/compose/install/)

### Commands

```shell
# Build and run services
$ docker-compose up --build

# Create a citizen
$ curl --location --request POST 'localhost:8081/citizens' \
  --header 'Content-Type: application/json' \
  --data-raw '{
    "citizenId": "4jf74jfi84jmf",
    "firstName": "John",
    "lastName": "Smith",
    "address": "Mount Pleasant Gardens 33, London"
    }'

# Update the citizen
$ curl --location --request PUT 'localhost:8081/citizens' \
  --header 'Content-Type: application/json' \
  --data-raw '{
    "citizenId": "4jf74jfi84jmf",
    "firstName": "John",
    "lastName": "Smith",
    "address": "Moorfield Alley 15, London"
    }'

# Delete the citizen
$ curl --location --request DELETE 'localhost:8081/citizens?citizenId=4jf74jfi84jmf'

# Get the citizen's read model
$ curl --location --request GET 'localhost:8082/citizens?citizenId=4jf74jfi84jmf'

# Get the citizen's audit
$ curl --location --request GET 'localhost:8082/audit?citizenId=4jf74jfi84jmf'
```
