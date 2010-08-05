.. _services_mef:

MEF services
============

Introduction
------------

This chapter describes the services related to the Metadata Exchange Format.
These services allow to import/export metadata using the MEF format.

mef.export
----------

As the name suggests, this service exports a GeoNetwork’s metadata using the
MEF file format.

This service is public but metadata access rules apply. For a partial export,
the view privilege is enough but for a full export the download privilege is
also required. Without a login step, only partial exports on public metadata are
allowed.

This service uses the system’s temporary directory to build the MEF file. With
full exports of big data maybe it is necessary to change this directory. In this
case, use the Java’s -D command line option to set the new directory before
running GeoNetwork (if you use Jetty, simply change the script into the bin
directory).

Request
```````

This service accepts requests in GET/POST and XML form. The input
parameters are:

- **UUID** the universal unique identifier of the metadata

- **format** which format to use. Can be one of: simple, partial, full.

- **skipUuid** If provided, tells the exporter to not export the metadata’s UUID. Without the UUID (which is a unique key inside the database) the metadata can be  imported over and over again. Can be one of: true, false. The default value is false.

Response
````````

The service’s response is a MEF file with these characteristics:

- the name of the file is the metadata’s UUID

- the extension of the file is mef

mef.import
----------

This service is reserved to administrators and is used to import a metadata
provided in the MEF format.

Request
```````

The service accepts a multipart/form-data POST request
with a single **mefFile** parameter that must contain the MEF
information.

Response
````````

If all goes well, the service returns an OK element containing the local
id of the created metadata. Example::

    <ok>123</ok>

Metadata ownership
------------------

Version 1.0 of the MEF format does not take into account the metadata owner
(the creator) and the group owner. This implies that this information is not
contained into the MEF file. During import, the user that is performing this
operation will become the metadata owner and the group owner will be set to
null.
