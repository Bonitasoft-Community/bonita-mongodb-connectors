bonita-mongodb-connectors
=========================
**MongoDB connectors for Bonita BPM**
v1.0 of this project is built and tested with:
- MongoDB java client v2.12.2
- Bonita BPM 6.3.7

##Deliverables:
- MongoDB direct connection connector
- MongoDB datasource connection connector (requires MongoDB datasource - see: https://github.com/pozil/mongodb-jndi-datasource)
- A sample Bonita process diagram to test the connectors

##Build instructions:
Project can be built with Maven using this command:
```
mvn clean install
```

##Usage instructions:
If you have built the project: import the generated ZIP files in the Bonita Studio using the Development/Connectors/Import connector menu.

If you are downloading the project deliverable:
1. Extract the provided ZIP parent file (it contains the connector ZIP files)
2. Import the connector ZIP files in the Bonita Studio using the Development/Connectors/Import connector menu.
