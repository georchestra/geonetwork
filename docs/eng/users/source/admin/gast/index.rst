.. _gast:

GeoNetwork’s Administrator Survival Tool - GAST
###############################################

What is GAST?
=============

GAST stands for GeoNetwork’s Administrator Survival Tool and is a standalone
application whose purpose is to simplify some low level tasks like change of the
servlet, configuration of the JDBC account, setup the database and so on. Most
of the GAST’s facilities work only for the GeoNetwork’s installation where GAST
is in. This implies that if you are using a servlet container other than Jetty
(like Tomcat) you will not be able to change some options (like the servlet’s
name). Other facilities work for any servlet container but you have to specify
the GeoNetwork’s URL into the GAST’s configuration dialogue.

Starting GAST
=============

GAST belongs to the core components so it is installed by default.

On Windows computers, simply select the Start GAST option under the
GeoNetwork opensource program group under 
:menuselection:`Start --> Programs --> GeoNetwork opensource`

Other options to start GAST are either to use a Java command **from a terminal window**
or just click its jar’s icon. To issue the Java command you have to:

#. change directory to the GeoNetwork installation folder

#. issue the command ``java -jar gast/gast.jar``

GAST will be in current system language if any translation is available. If you want to force
GAST GUI language, you could start GAST using the -Duser.language option (e.g. 
``./gast.sh -Duser.language=fr or java -Duser.language=fr -jar gast/gast.jar``).

You can also try to simply open the GeoNetwork installation folder, go to the
gast folder and double click on the gast.jar file. If you have Java installed, GAST should
start in a few seconds.

To run, GAST requires Java 1.5. It will not work on Java 1.4 and it
should run on Java 1.6.

Operating modes
===============

On the left side you have a panel with the tools you can use. After selecting a
tool, on the right side you get the tool’s options panel.

.. figure:: gast-main.png

    *GAST’s main window with a tool selected*

Every function has an operating mode, which defines the condition under which
the tool can be used. The tool’s mode is shown with an icon on the right side of
the tool’s name. The operating modes, with their icons are summarised in the
following table:

.. |reload| image:: icons/reload.png
.. |launch| image:: icons/launch.png
.. |stop| image:: icons/stop.png

============    ========    ====================================================
Mode            Icon        Description
============    ========    ====================================================
Restarted       |reload|    The tool can be always used, but GeoNetwork must be
                            restarted in order to make the change effective.
Running         |launch|    The tool can be used only if GeoNetwork is running.
Stopped         |stop|      The tool can be used only if GeoNetwork is stopped. 
                            This is important because some tools change the 
                            database’s account or create the database from 
                            scratch. These are sensitive operations that cannot 
                            be performed while GeoNetwork is running.
============    ========    ====================================================


Tools subdivision
=================

All GAST tools present into the left panel are logically subdivided into
groups. Each group represents a GeoNetwork’s aspect for which GAST allows you a
graphic interface. The groups are:

Configuration You can change some configuration parameters, like the servlet’s
name, JDBC account etc... Management General purpose tools related to the site’s
administration. Database Operations that regard the database. Here you can find
tools to create a database from scratch, creating the schema and filling it with
proper data. Migration Tools that allow you to migrate metadata from old
installation.

Server and Account configuration dialogue
=========================================

Some of the GAST’s tools access a running GeoNetwork application. Usually, GAST connects
to GeoNetwork using the connection parameters it finds on the installation
folder but you can specify other parameters in order to connect to other
instances. This is required when the GeoNetwork instance is not running on the
embedded Jetty server. In addition to that, some tools require authentication so
account parameters must be provided.

To provide these parameters, you have to use the GAST ’s configuration dialogue.
To open the dialogue, select ``Options >> Config`` from the menu bar. You will
get the following dialogue:

.. figure:: gast-options.png

    *The configuration dialogue*

The dialogue is subdivided into 2 areas: 

- **Server** - Tells GAST how to connect to a running GeoNetwork. If you select the embedded option, GAST will get the connection parameters from the installation directory. Alternatively, if you use Tomcat or an external servlet container you have to choose the external option and provide the connection parameters yourself. Remember that this will work only for tools which operating mode is *Running*. For all the others, GAST will access the parameters from the installation directory. 
- **Account** - Some tools require authentication. To authenticate, simply select the Use this account option and provide the username and password of a valid account. These parameters will work for both the embedded instance and for any external instance.
