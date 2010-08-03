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

The software is run in different ways depending on the servlet container you are
using:

- **Tomcat** - You can use the manager web application to start/stop GeoNetwork. You can also use the startup.* and shutdown.* scripts located into Tomcat’s bin folder (.* means .sh or .bat depending on your OS) but this way you restart all applications you are running, not only GeoNetwork. After installation and before running GeoNetwork you must link it to Tomcat. 
- **Jetty** - If you use the provided container you can use the scripts into GeoNetwork’s bin folder. The scripts are start-geonetwork.* and stop-geonetwork.* and you must be inside the bin folder to run them. You can use these scripts just after installation.

Tools
---------------------

The following tools are required to be installed to setup a development environment for GeoNetwork:

- **Java** - Developing with GeoNetwork requires a `Java Development Kit (JDK) <http://java.sun.com/javase/downloads/index_jdk5.jsp>`_ 1.5 or greater. 

- **Maven** - GeoNetwork uses a `Maven <http://maven.apache.org/>`_ to manage the build process and the dependencies. Once is installed, you should have the mvn command in your path (on Windows systems, you have to open a shell to check).

- **Subversion** - GeoNetwork source code is stored and versioned in a subversion repository. Depending on your operating system a variety of subversion clients are avalaible. Check in http://subversion.tigris.org/ for some alternatives.

- **Ant** - GeoNetwork uses `Ant <http://ant.apache.org/>`_ to build the installer.  Version 1.6.5 works but any other recent version should be OK. Once installed, you should have the ant command in your path (on Windows systems, you have to open a shell to check).

- **Sphinx** - To create the GeoNetwork documentation in a nice format `Sphinx <http://sphinx.pocoo.org/>`_  is used.

Check out source code
---------------------

Check out the source code from trunk from the GeoNetwork subversion repository to develop using the latest development code::

     gemini:/geonetwork# svn co https://geonetwork.svn.sourceforge.net/svnroot/geonetwork/trunk trunk

or from a stable branch for versions less likely to change often::

     gemini:/geonetwork# svn co https://geonetwork.svn.sourceforge.net/svnroot/geonetwork/branches/2.4.x branch24

Build GeoNetwork
----------------

Once you checked out the code from subversion repository, go inside the GeoNetwork’s root folder and execute the maven build command::

    gemini:/geonetwork# cd trunk
    gemini:/geonetwork/trunk# mvn clean install
    
    
If the build is succesful you'll get an output like::

    [INFO] 
    [INFO] 
    [INFO] ------------------------------------------------------------------------
    [INFO] Reactor Summary:
    [INFO] ------------------------------------------------------------------------
    [INFO] GeoNetwork opensource ................................. SUCCESS [1.825s]
    [INFO] Caching xslt module ................................... SUCCESS [1.579s]
    [INFO] Jeeves modules ........................................ SUCCESS [1.140s]
    [INFO] Oaipmh modules ........................................ SUCCESS [0.477s]
    [INFO] ArcSDE module (dummy-api) ............................. SUCCESS [0.503s]
    [INFO] GeoNetwork Web module ................................. SUCCESS [31.758s]
    [INFO] GeoServer module ...................................... SUCCESS [16.510s]
    [INFO] Gast module ........................................... SUCCESS [24.961s]
    [INFO] ------------------------------------------------------------------------
    [INFO] ------------------------------------------------------------------------
    [INFO] BUILD SUCCESSFUL	
    [INFO] ------------------------------------------------------------------------
    [INFO] Total time: 1 minute 19 seconds
    [INFO] Finished at: Tue Aug 03 16:49:15 CEST 2010
    [INFO] Final Memory: 79M/123M
    [INFO] ------------------------------------------------------------------------
    gemini:/geonetwork/trunk#

and your local maven repository should contain the GeoNetwork artifacts created (``$HOME/.m2/repository/org/geonetwork-opensource``).

Run embedded jetty server
`````````````````````````

To run GeoNetwork with embedded jetty server you can issue in web folder the next maven goal::

    gemini:/geonetwork/trunk# cd web
    gemini:/geonetwork/trunk/web# mvn jetty:run
    
After a moment, GeoNetwork should be accessible at: http://localhost:8080/geonetwork    
    
Source code documentation
`````````````````````````

The GeoNetwork Java source code is based on Javadoc. Javadoc is a tool for
generating API documentation in HTML format from doc comments in source code. To
see documentation generated by the Javadoc tool, go to:

- `GeoNetwork opensource
  Javadoc <../../../javadoc/geonetwork/index.html>`_

Creating the installer
----------------------

To run the build script that creates the installer you need the Ant tool. You can generate an installer by running the ant command
inside the installer directory::

    gemini:/geonetwork/trunk# cd installer
    gemini:/geonetwork/trunk/installer# ant
    Buildfile: build.xml

    setProperties:
    ...
    BUILD SUCCESSFUL
    Total time: 31 seconds
    gemini:/geonetwork/trunk/installer#
    
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

Eclipse setup
-------------

Generate Eclipse project files
``````````````````````````````
To generate the eclipse .project and .classpath files execute::

    gemini:/geonetwork/trunk# mvn eclipse:eclipse
    
Import modules into Eclipse
```````````````````````````

TODO