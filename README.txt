++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
+ README iteraplan                                                               +
++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

For further information about iteraplan, please go to http://www.iteraplan.de/

++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
+ License                                                                        +
++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

This software is distributed under the terms of the FSF Affero Gnu Public License.
See LICENSE.txt or http://www.fsf.org/licensing/licenses/agpl-3.0.html.

This product includes software developed by the Apache Software Foundation
(http://www.apache.org/).

++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
+ Prerequisites                                                                  +
++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

Before you can install and build iteraplan, you need to make sure that the following third party software is installed:


1. A Java Development Kit (JDK) of version 6 or higher
2. Apache Ant
3. Apache Maven
4. If you intend to deploy a WAR, Apache Tomcat (Optional)

Make sure that the /bin folders of the JDK, Ant and Maven are added to the PATH environment variable.
The JAVA_HOME, ANT_HOME, MVN_HOME and CATALINA_HOME (only if Tomcat is used) must exist and point to the installation directories of the JDK, Ant, Maven and Tomcat.

Also, during the installation process, a stable internet connection is required to enable Maven to automatically download dependencies.

++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
+ Installation                                                                   +
++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

In order to build and deploy iteraplan, either create a bundle, which contains a 
complete runnable installation including Tomcat and database or create a WAR file
to deploy on an already existing Tomcat.

I. Building and running a bundle
1. Execute the ant task "createBundle" from bundle/build.xml (this is also the default task in this buildfile)
2. Unzip bundle/build/iteraplan-bundle-X.Y.Z.zip to a location of your choice on your computer
3. Execute runIteraplan.bat or runIteraplan.sh (you may have to make sure that a Java Runtime Environment is found)
4. After the server has started, iteraplan should be available at http://{$hostname}:8080/iteraplan
5. To shut down the server, execute shutdown.bat or shutdown.sh

II. Building and deploying a WAR
1. Setup the database
1.1 If you intend to use an already existing database, enter its connection parameters by changing the database.* properties in build.properties
1.2.1 Otherwise, execute the ant task hsqldb.setup in the build.xml to start a new hsqldb server and fill it with initial data.
1.2.2 In order to stop the sever, execute the ant task hsqldb.stopServer from the build.xml. hsqldb.startServer.spawned will start the server again.
2. If you do not have your Tomcat configured to support SSL, change the value of the web.security.transport in build.properties to "NONE". If you want to configure your Tomcat to use SSL instead, please refer to the Tomcat documentation. 
3. Execute the ant task "dist.war" from the build.xml
4. Copy the file build/iteraplan.war to the webapps folder in the Tomcat you want to deploy
5. iteraplan should be available at http://{$hostname}:8080/iteraplan (possibly a Tomcat restart is necessary)