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
for the search index which is located in ``WEB-INF/lucene``. Even with a lot of
metadata the index is small so usually 10-20 Mb of space is enough.

The software is run in different ways depending on the servlet container you are
using:

- **Tomcat** - You can use the manager web application to start/stop GeoNetwork. You can also use the startup.* and shutdown.* scripts located into Tomcat’s bin folder (.* means .sh or .bat depending on your OS) but this way you restart all applications you are running, not only GeoNetwork. After installation and before running GeoNetwork you must link it to Tomcat. 
- **Jetty** - If you use the provided container you can use the scripts into GeoNetwork’s bin folder. The scripts are start-geonetwork.* and stop-geonetwork.* and you must be inside the bin folder to run them. You can use these scripts just after installation.

Tools
---------------------

The following tools are required to be installed to setup a development environment for GeoNetwork:

- **Java** - Developing with GeoNetwork requires a `Java Development Kit (JDK) <http://java.sun.com/javase/downloads/index_jdk5.jsp>`_ 1.5 or greater. 

- **Maven** - GeoNetwork uses `Maven <http://maven.apache.org/>`_ to manage the build process and the dependencies. Once is installed, you should have the mvn command in your path (on Windows systems, you have to open a shell to check).

- **Subversion** - GeoNetwork source code is stored and versioned in a subversion repository. Depending on your operating system a variety of subversion clients are avalaible. Check in http://subversion.tigris.org/ for some alternatives.

- **Ant** - GeoNetwork uses `Ant <http://ant.apache.org/>`_ to build the installer.  Version 1.6.5 works but any other recent version should be OK. Once installed, you should have the ant command in your path (on Windows systems, you have to open a shell to check).

- **Sphinx** - To create the GeoNetwork documentation in a nice format `Sphinx <http://sphinx.pocoo.org/>`_  is used.

Check out source code
---------------------

Check out the source code from trunk from the GeoNetwork subversion repository to develop using the latest development code::

     $ svn co https://geonetwork.svn.sourceforge.net/svnroot/geonetwork/trunk trunk

or from a stable branch for versions less likely to change often::

     $ svn co https://geonetwork.svn.sourceforge.net/svnroot/geonetwork/branches/2.4.x branch24

Build GeoNetwork
----------------

Once you checked out the code from subversion repository, go inside the GeoNetwork’s root folder and execute the maven build command::

     $ mvn clean install
    
    
If the build is succesful you'll get an output like::

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

and your local maven repository should contain the GeoNetwork artifacts created (``$HOME/.m2/repository/org/geonetwork-opensource``).

.. note :: Many Maven build options are available. Please refer to the maven documentation for any other options, `Maven: The Complete Reference <http://www.sonatype.com/books/mvnref-book/reference/public-book.html>`_

For instance, you would like to use following options : ::
    
    -- Skip test
    $ mvn install -Dmaven.test.skip=true
    
    -- Offline use
    $ mvn install -o

Please refer to the maven documentation for any other options, `Maven: The Complete Reference <http://www.sonatype.com/books/mvnref-book/reference/public-book.html>`_

Run embedded jetty server
`````````````````````````

Maven comes with built-in support for Jetty via a `plug-in <http://docs.codehaus.org/display/JETTY/Maven+Jetty+Plugin>`_.

To run GeoNetwork with embedded jetty server you have to change directory to the root of the **web** module, 
and then execute the following maven command::

    $ mvn jetty:run
    
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

To run the build script that creates the installer you need the Ant tool. You can generate an installer by running the ant command inside the **installer** directory::

    $ ant
    
    Buildfile: build.xml
    setProperties:
    ...
    BUILD SUCCESSFUL
    Total time: 31 seconds
        
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

Packaging GeoNetwork using Maven
````````````````````````````````

Using Maven, you have the ability to package GeoNetwork in two different ways :

- WAR files (geonetwork.war, geoserver.war)
- Binary ZIP package (with Jetty embedded)

The `Assembly Plugin <http://maven.apache.org/plugins/maven-assembly-plugin/>`_ 
is used to create the packages using ::

    $ mvn package assembly:assembly

The Assembly Plugin configuration is in the release module (See bin.xml and zip-war.xml).


Eclipse setup
-------------

Setting eclipse preferences
```````````````````````````

- **M2_REPO** Classpath Variable:

- Navigate to **Java**> **Build Path**> **Classpath Variable**
- Press New.. button
- In Name field enter M2_REPO
- In Path field enter the path to your .m2/repository_directory

 - Example: "C:\Documents and Settings\m.coudert\.m2\repository"

An alternative to set up this variable directly using maven could to run 
the following command into your workspace directory ::

    $ mvn -Declipse.workspace=. eclipse:add-maven-repo
    
- Generate Eclipse project files

To generate all the .classpath and .project files execute the following command
at the project root directory ::

    $ mvn eclipse:eclipse
    
Import source code
``````````````````
In order to import the source code, follow instructions below :

- Press **File**> **Import** Menu item
- In new dialog Select **General**> **Existing Projects into Workspace**
- Press Next 

.. figure:: eclipse-import-existing-projects.png

- In **Select root directory** field enter where your code is:

 - example: C:\dev\geonetwork\trunk

- Select All projects and Press **Finish** button. 


Setting `m2eclipse <http://m2eclipse.sonatype.org/>`_ plugin
````````````````````````````````````````````````````````````

To install m2eclipse, please refer to the following `documentation 
<http://m2eclipse.sonatype.org/installing-m2eclipse.html>`_.

Then click on **File** > **Import** > **Maven** > **Check out Maven Projects From SCM**
Choose svn and https://geonetwork.svn.sourceforge.net/svnroot/geonetwork/trunk as **SCM URL** options.

.. figure:: eclipse-checkout-from-SCM.png

.. note :: 
    It is also possible to import existing Maven projects using Maven (m2eclipse) import facilities 
    choosing the **Existing Maven projects** option.
 
Debugging into eclipse
``````````````````````
- Tomcat Server :

TODO

- Remote debbuging :

 - `How do I configure Tomcat to support remote debugging? <http://wiki.apache.org/tomcat/FAQ/Developing#Q1>`_
 - `How do I remotely debug Tomcat using Eclipse? <http://wiki.apache.org/tomcat/FAQ/Developing#Q2>`_

