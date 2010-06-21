.. _development:

Software development
====================

System Requirements
-------------------

GeoNetwork is a Java application that runs as a servlet so the Java Runtime
Environment (JRE) must be installed in order to run it. You can get the JRE from the
following address http://java.sun.com and you have to download the Java 5 Standard
Edition (SE). GeoNetwork won’t run with Java 1.4 and Java 6 has some problems with
it so we recommend to use Java 5. Being written in Java, GeoNetwork can run on any
platform that supports Java, so it can run on Windows, Linux and Mac OSX. For the
latter one, make sure to use version 10.4 (Tiger) or newer. Version 10.3 (Panther)
has only Java 1.4 so it cannot run GeoNetwork.

Next, you need a servlet container. GeoNetwork comes with an embedded one (Jetty)
which is fast and well suited for most applications. If you need a stronger one, you
can install Tomcat from the Apache Software Foundation (http://tomcat.apache.org).
It provides load balance, fault tolerance and other corporate needed stuff. If you
work for an organisation, it is probable that you already have it up and running.
The tested version is 5.5 but GeoNetwork should work with all other versions.

Regarding storage, you need a Database Management System (DBMS) like Oracle,
MySQL, Postgresql and so on. GeoNetwork comes with an embedded one (McKoi) which is
used by default during installation. This DBMS can be used for small or desktop
installations, where the speed is not an issue. You can use this DBMS for several
thousands of metadata. If you manage more than 10.000 metadata it is better to use a
professional, stand alone DBMS. In this case, using a separate DBMS also frees up
some memory for the application.

GeoNetwork does not require a strong machine to run. A good performance can be
obtained even with 128 Mb of RAM. The suggested amount is 512 Mb. For the hard disk
space, you have to consider the space required for the application itself (about 40
Mb) and the space required for data maps, which can require 50 GB or more. A simple
disk of 250 GB should be OK. Maybe you can choose a fast one to reduce backup time
but GeoNetwork itself does not speed up on a faster disk. You also need some space
for the search index which is located in ``web/WEB-INF/lucene``. Even with a lot of
metadata the index is small so usually 10-20 Mb of space is enough.

Running the software with a servlet engine
------------------------------------------

The software is run in different ways depending on the servlet container you are
using:

- **Tomcat** - You can use the manager web application to start/stop GeoNetwork. You can also use the startup.* and shutdown.* scripts located into Tomcat’s bin folder (.* means .sh or .bat depending on your OS) but this way you restart all applications you are running, not only GeoNetwork. After installation and before running GeoNetwork you must link it to Tomcat. 
- **Jetty** - If you use the provided container you can use the scripts into GeoNetwork’s bin folder. The scripts are start-geonetwork.* and stop-geonetwork.* and you must be inside the bin folder to run them. You can use these scripts just after installation.

Development
-----------

Compiling GeoNetwork
--------------------

To compile GeoNetwork you first need to install the source code during
installation. If you do so, you get a build.xml script and a src folder with the
full source.

You also need the Ant tool to run the build script. You can download Ant from
http://ant.apache.org. Version 1.6.5 works but any other recent version should
be OK. Once installed, you should have the ant command in your path (on Windows
systems, you have to open a shell to check).

When all is in place, go inside the GeoNetwork’s root folder (the one where
the build.xml file is located) and issue the ant command. You should see an
output like this one::

    gemini:/geonetwork/trunk# ant
    Buildfile: build.xml
    compile:
    \[delete] Deleting: /geonetwork/trunk/web/WEB-INF/lib/geonetwork.jar
    \[delete] Deleting: /geonetwork/trunk/csw/lib/csw-client.jar
    \[delete] Deleting: /geonetwork/trunk/csw/lib/csw-common.jar
    \[delete] Deleting: /geonetwork/trunk/gast/gast.jar
    \[mkdir] Created dir: /geonetwork/trunk/.build
    \[javac] Compiling 267 source files to /geonetwork/trunk/.build
    \[javac] Note: Some input files use or override a deprecated API.
    \[javac] Note: Recompile with -Xlint:deprecation for details.
    \[javac] Note: Some input files use unchecked or unsafe operations.
    \[javac] Note: Recompile with -Xlint:unchecked for details.
    \[copy] Copying 1 file to /geonetwork/trunk/.build
    \[jar] Building jar: /geonetwork/trunk/web/WEB-INF/lib/geonetwork.jar
    \[jar] Building jar: /geonetwork/trunk/csw/lib/csw-client.jar
    \[jar] Building jar: /geonetwork/trunk/csw/lib/csw-common.jar
    \[jar] Building jar: /geonetwork/trunk/gast/gast.jar
    \[delete] Deleting directory /geonetwork/trunk/.build
    BUILD SUCCESSFUL
    Total time: 9 seconds
    gemini:/geonetwork/trunk#

The compilation phase, if it has success, puts all jars into the proper place
(most of them will be copied into web/geonetwork/WEB-INF/lib and
web/intermap/WEB-INF/lib). After this phase, simply restart GeoNetwork to see
the effects.

Source code documentation
`````````````````````````

The GeoNetwork Java source code is based on Javadoc. Javadoc is a tool for
generating API documentation in HTML format from doc comments in source code. To
see documentation generated by the Javadoc tool, go to:

- `GeoNetwork opensource
  Javadoc <../../../javadoc/geonetwork/index.html>`_

Creating the installer
``````````````````````

You can generate an installer by running the ant command
inside the installer directory.

Both platform independent and Windows specific installers are generated by
default.

Make sure you update version number and other relevant properties in the
installer/build.xml file

You can also create an installer that includes a Java Runtime Environment
(JRE) for Windows. This will allow GeoNetwork to run on a compatible, embedded
JRE and thus avoid error messages caused by JRE incompatibilities on the PC.

Creating an installer with an embedded JRE requires you to first download and
unzip the JRE in a folder jre1.5.0_12 at the project root
level. Refer to the installer-config-win-jre.xml file for
exact configuration.
