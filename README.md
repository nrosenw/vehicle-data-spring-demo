# Vehicle Emissions Data Spring Demo
## Summary
The purpose of this project was to learn some of the Spring framework and its additional libraries. I had no experience of Spring before developing this project. As I've invested more time into this project, I've decided to keep and document it. This project will include features such as a MariaDB repository containing vehicle emissions data from the Department of Energy, REST endpoints that provide the emissions data from the repository, and a templated web front end for users to select a vehicle and view its data.

This project requires Java 8, Eclipse EE, a MariaDB server, and a servlet container such as Wildfly. This project has not been tested in any servlet container other than Wildfly. If you do not have Maven installed, the Maven wrapper `./mvnw` is provided.

## Build the WAR
The project WAR file can be built from your command line/shell with the command `./mvnw clean install`.