.. _schema_information:

Schema information
==================

Introduction
------------

GeoNetwork is able to handle several metadata schema formats. Up to now, the
supported schemas are:

- **ISO-19115 (iso19115)**: GeoNetwork implements an old version of the
  draft, which uses short names for elements. This is not so standard so
  this schema is obsolete and will be removed in future releases.

- **ISO-19139 (iso19139)**: This is the XML encoding of the ISO 19115:2007 metadata and ISO 19119
  service metadata specifications.

- **Dublin core (dublin-core)**: This is a simple metadata schema based on
  a set of elements capable of describing any metadata.

- **FGDC (fgdc-std)**: It stands for Federal Geographic Data Committee and
  it is a metadata schema used in North America.

In parenthesis is indicated the name used by GeoNetwork to refer to that
schema. These schemas are handled through their XML schema files (XSD), which
GeoNetwork loads and interprets to allow the editor to add and remove elements.
Beside its internal use, GeoNetwork provides some useful XML services to find
out some element properties, like label, description and so on.

xml.schema.info
---------------

This service returns information about a set of schema elements or codelists.
The returned information consists of a localised label, a description,
conditions that the element must satisfy etc...

Request
```````

Due to its nature, this service accepts only the POST binding with
application/XML content type. The request can contain
several element and codelist elements. Each element indicate the will to
retrieve information for that element. Here follows the element
descriptions:

- **element**: It must contain a **schema** and a **name** attribute. The first
  one must be one of the supported schemas (see the section above).
  The second must be the qualified name of the element which
  information must be retrieved. The namespace must be declared into
  this element or into the root element of the request.

- **codelist**: Works like the previous one but returns information
  about codelists.

::

    <request xmlns:gmd="http://www.isotc211.org/2005/gmd">
        <element schema="iso19139" name="gmd:constraintLanguage" />
        <codelist schema="iso19115" name="DateTypCd" />
    </request>

.. note:: The returned text is localised depending on the language specified during
  the service call. A call to /geonetwork/srv/en/xml.schema.info
  will return text in the English language.

Response
````````

The response’s root element will be populated with information of the
elements/codelists specified into the request. The structure is the
following:

- **element**: A container for information about an element. It has a
  name attribute which contains the qualified name of the element.

  - **label**: The human readable name of the element, localised
    into the request’s language.
  - **description**: A generic description of the element.
  - **condition \[0..1]**: This element is optional and indicates
    if the element must satisfy a condition, like the element is
    always mandatory or is mandatory if another one is
    missing.

- **codelist**: A container for information about a codelist. It has a
  name attribute which contains the qualified name of the codelist.

  - **entry \[1..n]**: A container for a codelist entry. There can
    be many entries.

    - **code**: The entry’s code. This is the value that
      will be present inside the metadata.
    - **label**: This is a human readable name, used to
      show the entry into the user interface. It is
      localised.
    - **description**: A generic localised description of
      the codelist.

::

    <response>
        <element name="gmd:constraintLanguage">
            <label>Constraint language</label>
            <description>language used in Application Schema</description>
            <condition>mandatory</condition>
        </element>
        <codelist name="DateTypCd">
            <entry>
                <code>creation</code>
                <label>Creation</label>
                <description>date when the resource was brought into existence</description>
            </entry>
            <entry>
                <code>publication</code>
                <label>Publication</label>
                <description>date when the resource was issued</description>
            </entry>
            <entry>
                <code>revision</code>
                <label>Revision</label>
                <description>date identifies when the resource was examined
                or re-examined and improved or amended</description>
            </entry>
        </codelist>
    </response>

Error management
````````````````

Beside the normal exceptions management, the
service can encounter some errors trying to retrieve an element/codelist
information. In this case, the object is copied verbatim to the response
with the addition of an error attribute that describes the encountered
error. Here follows an example of such response::

    <response>
        <element schema="iso19139" name="blablabla" error="not-found"/>
    </response>

.. _table_schema_errors:

Possible errors returned by xml.schema.info service:

=================   ============================================================
Error code          Description
=================   ============================================================
unknown-schema      The specified schema is not supported
unknown-namespace   The namespace of the specified prefix was not found
not-found           The requested element / codelist was not found
=================   ============================================================


