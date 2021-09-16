# BookstoreRestfulAPI
My RESTful bookstore web application

## Requirements
Apache Tomcat 9.0 or higher
Eclipse for Enterprise Java and Web Developers IDE

## Setup
Import project into Eclipse: File > Import > Git > Projects from Git using the Clone URI as Repository Source
Select Import as general project when selecting a wizard to use for importing projects

### Server Creation and Configuration in Eclipse
Skip below steps if Tomcat server is already set up in your Eclipse
#### Create a new Tomcat server in Eclipse
Window > Show View > Other > Servers > Server
Click link to create a new server
Select Apache and the Tomcat version downloaded

#### Create new Tomcat Configuration
Run > Run Configurations
Select Apache Tomcat and press New Configuration button
Name the configuration if you wish and click Apply

Troubleshooting:
Error: Could not find or load main class org.apache.catalina.startup.Bootstrap
