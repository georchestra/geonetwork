.. _installing:

Installing the software
=======================

New version - New funtionality
------------------------------

The new GeoNetwork opensource comes with substantial upgrades of different components for a more intuitive and responsive user-system interaction. Web2 technologies have been adopted, in particular AJAX techniques, to allow for more interactive and faster services in the web interface and for the integration of the existing web map viewer in the home page. Similar functionalities have been implemented in the administrative part of the system, to provide an easier access to the configuration pages related to site settings, catalogue harvesting, scheduling and maintenance.

The search interface has been completely overhauled to provide highly interactive searching capabilities. Furthermore, the new version of GNos embeds GeoServer as map server. Users can now not only overlay OGC web map services available on the web, but also create their own map services for other users to browse without having to download additional plugins. Maps created with web map services can be now saved as PDF and sent to others.

The metadata catalogue handles the latest ISO19115:2003 geographic metadata format based on the ISO19139:2007 schemas, as well as the older ISO19115 final draft format, FGDC and Dublin Core. The metadata editor is able to handle the majority of these complex standards, providing default, advanced and XML editing online tools.

The new version has a number of different harvesting interfaces allowing users to connect their own server to many other catalogues around the world. This is the result of the implementation of the open source reference for the web catalog services according to OGC specifications. Harvesting in the new version is fully compatible with GeoNetwork 2.0 and higher nodes.

We have added avanced online and offline administration funcionalities to configure, backup and migrate the application. We have also added a convenient import and export format "MEF" or Metadata Exchange Format, that allows the users to move metadata, previews and even data in a convenient single file. GNos can be easily expanded with plugins to export/import metadata to/from other software supporting MEF.

.. figure:: Home_page_s.png

    *Standard home page of GeoNetwork opensource*
  
Where do I get the installer?
-----------------------------

You can find the software on the Internet at the GeoNetwork opensource Community website. The software is also distributed through the SourceForge.net Website at http://sourceforge.net/projects/geonetwork.

Use the platform independent installer (.jar) if you need anything more than a plain Windows installation.

System requirements
-------------------

GeoNetwork can run either on **MS Windows** , **Linux** or **Mac OS X** .

Some general system requirements for the software to run without problems are listed below:

**Processor** : 1 GHz or higher

**Memory (RAM)** : 512 MB or higher

**Disk Space** : 30 MB minimum. However, it is suggested to have a minimum of 250 MB of free disk space. Additional space is required depending on the amount of spatial data that you expect to upload into the internal geodatabse.

**Other Software requirements** : A Java Runtime Environment (JRE 1.5.0). For server installations, Apache Tomcat and a dedicated JDBC compliant DBMS (MySQL, Postgresql, Oracle) can be used instead of Jetty and McKoiDB respectively.

Additional Software
```````````````````

The software listed here is not required to run GeoNetwork, but can be used for custom installations.

#. MySQL DBMS v5.5+ (All) [#all_os]_
#. Postgresql DBMS v7+ (All) [#all_os]_
#. Apache Tomcat v5.5+ (All) [#all_os]_
#. Druid v3.8 (All) [#all_os]_ to inspect the database

Supported browsers
``````````````````

GeoNetwork should work normally with the following browsers:

#. Firefox v1.5+ (All) [#all_os]_
#. Internet Explorer v6+ (Windows)
#. Safari v3+ (Mac OS X Leopard)

How do I install GeoNetwork opensource?
---------------------------------------

Before running the GeoNetwork installer, make sure that all system requirements are satisfied, and in particular that the Java Runtime Environment version 1.5.0 is set up on your machine.

On Windows
``````````

If you use Windows, the following steps will guide you to complete the installation (other FOSS will follow):

1. Double click on **geonetwork-install-2.2.0.exe** to start the GeoNetwork opensource desktop installer
2. Follow the instructions on screen. You can choose to install sample data, install the embedded map server (based on `GeoServer <http://www.geoserver.org>`_ and the CSW 2.0.1 test client. Developers may be interested in installing the source code and installer building tools. Full source code can be found in the GeoNetwork SubVersion code repository.
3. After completion of the installation process, a 'GeoNetwork desktop' menu will be added to your Windows Start menu under 'Programs'
4. Click Start\>Programs\>GeoNetwork desktop\>Start server to start the Geonetwork opensource Web server. The first time you do this, the system will require about 1 minute to complete startup.
5. Click Start\>Programs\>Geonetwork desktop\>Open GeoNetwork opensource to start using GeoNetwork opensource, or connect your Web browser to `http://localhost:8080/geonetwork/ <http://localhost:8080/geonetwork/>`_

.. figure:: installer.png

   *Installer*

.. figure:: install_packages.png

   *Packages to be installed*

Installation using the platform independent installer
`````````````````````````````````````````````````````

If you downloaded the platform independent installer (a .jar file), you can in most cases start the installer by simply double clicking on it.

Follow the instructions on screen (see also the section called On Windows).

At the end of the installation process you can choose to save the installation script (Figure Save the installation script for commandline installations).

.. figure:: install_script.png
   
   *Save the installation script for commandline installations*


Commandline installation
````````````````````````

If you downloaded the platform independent installer (a .jar file), you can perform commandline installations on computers without a graphical interface. You first need to generate an install script (see Figure Save the installation script for commandline installations). This install script can be edited in a text editor to change some installation parameters.

To run the installation from the commandline, issue the following command in a terminal window and hit enter to start::

    java -jar geonetwork-install-2.2.0-0.jar install.xml
    [ Starting automated installation ]
    [ Starting to unpack ]
    [ Processing package: Core (1/3) ]
    [ Processing package: Sample metadata (2/3) ]
    [ Processing package: GeoServer web map server (3/3) ]
    [ Unpacking finished ]
    [ Writing the uninstaller data ... ]
    [ Automated installation done ]

You can also run the installation with lots of debug output. To do so run the installer with the flag *-DTRACE=true*::

  java -DTRACE=true -jar geonetwork-install-2.6.0-0.jar

.. [#all_os] All = Windows, Linux and Mac OS X


XSLT processor configuration
----------------------------

The file ``INSTALL_DIR/web/geonetwork/WEB-INF/classes/META-INF/javax.xml.transform.TransformerFactory`` defines
the XSL processor to use in GeoNetwork. The allowed values are:

#. ``de.fzi.dbs.xml.transform.CachingTransformerFactory`` for XSL caching (recommended value for production use). When caching is on, only the main XSL stylesheet last update date is checked and not all included XSL. If you update an included stylesheet, cache is still used. 
#. ``net.sf.saxon.TransformerFactoryImpl`` to use Saxon 

GeoNetwork sets the XSLT processor configuration on the JVM system properties at startup time for an instant to obtain its TransformerFactory implementation, then resets it to original value, to minimize affect the XSL processor configuration for other applications. 