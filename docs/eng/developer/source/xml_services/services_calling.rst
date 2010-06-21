.. _services_calling:

Calling specifications
======================

Calling XML services
--------------------

GeoNetwork provides access to several internal structures through the use of
XML services. These are much like HTML addresses but return XML instead. As an
example, consider the xml.info service: you can use this service to get some
system’s information without fancy styles and graphics. In GeoNetwork, XML
services have usually the xml. prefix in their address.

Request
```````

Each service accepts a set of parameters, which must be embedded into the
request. A service can be called using different HTTP methods, depending on
the structure of its request:

GET The parameters are sent using the URL address. On the server side,
these parameters are grouped into a flat XML document with one root and
several simple children. A service can be called this way only if the
parameters it accepts are not structured. :ref:`xml_request`
shows an example of such request and the parameters encoded in XML. POST
There are 3 variants of this method:

**ENCODED** The request has one of the following content types:
application/x-www-form-urlencoded or
multipart/form-data. The first case is very common
when sending web forms while the second one is used to send binary data
(usually files) to the server. In these cases, the parameters are not
structured so the rules of the GET method applies. Even if the second case
could be used to send XML documents, this possibility is not considered on
the server side.

**XML** The content type is application/xml.
This is the common case when the client is not a browser but a specialised
client. The request is a pure XML document in string form, encoded using the
encoding specified into the prologue of the XML document. Using this form,
any type of request can be made (structured or not) so any service can be
called.

**SOAP** The content type is application/soap+xml.
SOAP is a simple protocol used to access objects and services using XML.
Clients that use this protocol can embed XML requests into a SOAP structure.
On the server side, GeoNetwork will remove the SOAP structure and feed the
content to the service. Its response will be embedded again into a SOAP
structure and sent back to the caller. It makes sense to use this protocol
if it is the only protocol understood by the client.

**A GET request to a XML service and its request encoding**::

    <request>
        <hitsPerPage>10</hitsPerPage>
        <any />
    </request>

Response
````````

The response of an XML service always has a content type of
application/xml (the only exception are those
services which return binary data). The document encoding is the one
specified into the document’s prologue. Anyway, all GeoNetwork services
return documents in the UTF-8 encoding.

On a GET request, the client can force a SOAP response adding the
application/soap+xml content type to the Accept
header parameter.

Exception handling
------------------

A response document having an error root element means that the XML service
raised an exception. This can happen under several conditions: bad parameters,
internal errors et cetera. In this cases the returned XML document has the following
structure:

- **error**: This is the root element of the document. It has a mandatory
  id attribute that represents an identifier of the error from a common
  set. See :ref:`error2_ids` for a list of all id values.
  
  - **message**: A message related to the error. It can be a short
    description about the error type or it can contain some other
    information that completes the id code.
  - **class**: The Java class of the raised error (name without
    package information).
  - **stack**: The server’s stacktrace up to the point that generated
    the exception. It contains several at children, one for each
    nested level. Useful for debugging purposes.

    - **at**: Information about a nested level of called code.
      It has the following mandatory attributes:
      **class** Java class of the called method. **method** Java
      called method. **line** Line, inside the called method’s
      source code where there the method call of the next
      nested level. **file** Source file where the class is
      defined.

  - **object**: An optional container for parameters or other values
    that caused the exception. In case a parameter is an XML object,
    this container will contain that object in XML form.
  - **request**: A container for some useful information that can be
    needed to debug the service.

    - **language**: Language used when the service was called.
    - **service**: Name of the called service.

.. _error2_ids:

**Summary of error ids:**

=========================   ===============================     =============================
**id**                      Meaning of message element          Meaning of object element
=========================   ===============================     =============================
**error**                   General message, human readable     x
**bad-format**              Reason                              x
**bad-parameter**           Name of the parameter               Parameter’s bad value
**file-not-found**          x                                   File’s name
**file-upload-too-big**     x                                   x
**missing-parameter**       Name of the parameter               XML container where the
                                                                parameter should have been
                                                                present.
**object-not-found**        x                                   Object’s name
**operation-aborted**       Reason of abort                     If present, the object that 
                                                                caused the abort
**operation-not-allowed**   x                                   x
**resource-not-found**      x                                   Resource’s name
**service-not-allowed**     x                                   Service’s name
**service-not-found**       x                                   Service’s name
**user-login**              User login failed message           User’s name
**user-not-found**          x                                   User’s id or name
**metadata-not-found**      The requested metadata was not      Metadata’s id
                            found
=========================   ===============================     =============================


:ref:`mef_export_exception` shows an example of exception generated
by the mef.export service. The service complains about a missing parameter, as
you can see from the content of the id attribute. The object element contains
the xml request with an unknown test parameter while the mandatory UUID
parameter (as specified by the message element) is missing.

**An example of generated exception**::

    <error>
        <message>UUID</message>
        <class>MissingParameterEx</class>
        <stack>
            <at class="jeeves.utils.Util" file="Util.java" line="66"
                method="getParam"/>
            <at class="org.fao.geonet.services.mef.Export" file="Export.java"
                line="60" method="exec"/>
            <at class="jeeves.server.dispatchers.ServiceInfo" file="ServiceInfo.java"
                line="226" method="execService"/>
            <at class="jeeves.server.dispatchers.ServiceInfo" file="ServiceInfo.java"
                line="129" method="execServices"/>
            <at class="jeeves.server.dispatchers.ServiceManager" file="ServiceManager.java"
                line="370" method="dispatch"/>
        </stack>
        <object>
            <request>
                <asd>ee</asd>
            </request>
        </object>
        <request>
            <language>en</language>
            <service>mef.export</service>
        </request>
    </error>

