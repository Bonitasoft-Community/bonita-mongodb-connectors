bonita-mongodb-connectors
=========================
**MongoDB connectors for Bonita BPM**

v1.0 of this project is built and tested with:

- MongoDB java client v2.12.2
- Bonita BPM 6.3.7

##Deliverables:

Bonita Connectors:
- MongoDB direct connection connector
- MongoDB datasource connection connector (requires MongoDB datasource - see: https://github.com/pozil/mongodb-jndi-datasource)

Sample Bonita processes:
- Mongo Connectors Test - Test processes that allows to test the connectors
- Mongo Store - A sample use case of business processes using MongoDB


##Usage instructions:
If you are downloading the project deliverable:

1. Extract the provided ZIP parent file (it contains the individual connector ZIP files)

2. Import the connector ZIP files in the Bonita Studio using the Development/Connectors/Import connector menu.

If you have built the project (see build instructions): directly import the generated connector ZIP files in the Bonita Studio using the Development/Connectors/Import connector menu.


##Build instructions:
Project can be built from the sources with Maven using this command:
```
mvn clean install
```
