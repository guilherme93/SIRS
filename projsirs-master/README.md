# Secure Child Locator 

##SERVER
Server is running in Ubuntu 16.04.1 x64, the server needs a certificate, and some extra libraries, all included.

Running the server:

->cd server/maven

->mvn install

->java -cp target/maven-0.0.1-SNAPSHOT.jar:lib/jsch-0.1.53.jar:lib/mysql-connector-java-5.1.40-bin.jar org.child.secure.locator.maven.sslServer

##APPLICATION

You need to generate the APK of the project and run it in a Android device with at least the 21 SDK (Android Lollipop)

Client needs a truststore(must have server public key) and a set of public and private keys, all included.

##Database

The database is ruuning in the SIGMA server from IST

We have a file called create tables with all the tables in the project.This file is written in mysql, so it is easy to create all the tables.
