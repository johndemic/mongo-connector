Mule Mongo Connector
====================

MongoDB is an open source, high-performance, schema-free, document-oriented database that manages collections of BSON documents. The Mongo Connector allows to connect to a Mongo DB instance and run the almost all the operations that can be performed from the command line.

Installation and Usage
----------------------

For information about usage and installation you can check our documentation at http://mulesoft.github.com/mongo-connector

Running the Tests
-----------------

Connect to your locally running MongoDB (on port `27017`) with `mongo` and run:

    use test
    db.addUser( { user: "admin", pwd: "pepe", roles: ["dbAdmin"] })
    use mongo-connector-test
    db.addUser( { user: "foobar", pwd: "1234", roles: ["readWrite"] })

Reporting Issues
----------------

We use GitHub:Issues for tracking issues with this connector. You can report new issues at this link https://github.com/mulesoft/mongo-connector/issues
