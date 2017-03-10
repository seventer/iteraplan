++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
+ README iteraplan BUNDLE                                                        +
++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

This is a bundle (demo) version of iteraplan. It includes Apache Tomcat as Servlet engine
and hsqldb as database engine and requires only a Java Runtime Environment (JRE)
or Java Development Kit (JDK) in version 1.6 or higher to run.

The application files should be extracted as a whole into a folder somewhere in
a local file system. The path to the folder should not be nested too deep into the folder 
hierarchy, as some operating systems have problems with excessively long path names. 
The application will run within the extracted folder and not change anything outside of it.

One of the two environment variables JRE_HOME or JAVA_HOME needs to be set. If JRE_HOME
is set, it has to be set to the path to a JRE installation. If JAVA_HOME is set,
it has to be set to the path to a JDK installation. Which option you use is not
relevant for the application, as long as the JRE or JDK pointed to meets the
requirements. Make sure that the variable points to the root 
folder of your JDK or JRE (e.g. "C:\Program Files\Java\jdk1.6.0_29" or 
"/opt/java/jre1.6.0_29") and not to a subfolder contained in your java 
installation (such as for example "bin" or "lib"). Tomcat will refuse to run if 
this is not set up properly.

Started with the default settings, the application will use the following TCP/IP
ports: 8080, 8009, 8005, 9001. These ports should be free before starting the
application. Alternatively see the instructions at the end of this file for
changing the ports, although we strongly recommended to use the defaults.

To start both Tomcat and hsqldb run either "runIteraplan.bat" (on Windows) or 
"runIteraplan.sh" (on Linux or Mac OS X), to shut down both run the matching 
"shutdown.bat" or "shutdown.sh" script. Please note that it might be necessary to 
make these files executable on Linux or Mac OS first.

The URL for accessing iteraplan is http://localhost:8080/iteraplan. 

If you downloaded the iteraplan distribution with sample bank data (as opposed 
to initial data), the application includes example data describing a fictitious bank. 
This data can be edited and changes are saved to the integrated database. 
Your data is stored within the installation folder. You can create backups by creating a 
copy of the "data" folder in the hsqldb installation (e.g. "iteraplan/hsqldb/data").
Be sure that the database engine is not running while creating a backup. If you 
are not sure whether the database is running or not, simply run the shutdown 
command -- it will fail without side effects if the database is not running.

Have fun.


++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
+ Instructions on changing the ports                                             +
++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

This part is only relevant if you cannot use the default ports. In this case you
might want to edit the configuration of the underlying servers to use other ports.

++ Tomcat ++

Tomcat uses the ports 8080, 8009 and 8005 by default. These settings can be
changed by editing the file "conf/server.xml" in the Tomcat directory and
replacing all occurrences of these numbers.

++ hsqldb ++

Hsqldb uses the port 9001 by default. To change this you have to edit your startup
file ("runIteraplan.sh/bat") to pass a different port number on startup of the
database server. The matching line looks something like this (all on one line):

  start "HSQL Server" "%JRE_HOME%\bin\javaw" -cp hsqldb/lib/hsqldb.jar org.hsqldb.Server 
    -database.0 hsqldb/data/iteraplan -dbname.0 iteraplan

It varies slightly between operating systems and versions. You need to add 
"-port <NUMBER>" after the word "iteraplan", such as this:

  start "HSQL Server" "%JRE_HOME%\bin\javaw" -cp hsqldb/lib/hsqldb.jar org.hsqldb.Server 
    -database.0 hsqldb/data/iteraplan -dbname.0 iteraplan -port 9002

You also need to change the shutdown script and for example replace 
	jdbc:hsqldb:hsql://localhost/iteraplan
with 
	jdbc:hsqldb:hsql://localhost:9002/iteraplan
to make sure you shut down the correct hsqldb database.

Additionally the connection strings in two files within the Tomcat directory have
to be adjusted. These two files are:

  conf/Catalina/localhost/iteraplan.xml
  webapps/iteraplan/WEB-INF/classes/iteraplan-db.properties

both contain the string "jdbc:hsqldb:hsql://localhost/iteraplan", which needs to
be expanded to "jdbc:hsqldb:hsql://localhost:<PORT>/iteraplan", e.g.
"jdbc:hsqldb:hsql://localhost:9002/iteraplan".

++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
+ License                                                                        +
++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

This software is distributed under the terms of the FSF Affero Gnu Public License 
version 3. See LICENSE.txt or http://www.fsf.org/licensing/licenses/agpl-3.0.html.

This product includes software developed by the Apache Software Foundation
(http://www.apache.org/).