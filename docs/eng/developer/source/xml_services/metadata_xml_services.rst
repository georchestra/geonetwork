.. _metadata_xml_services:

Metadata services
=================

Retrieve metadata services
--------------------------

Search metadata (xml.search)
````````````````````````````

The **xml.search** service can be used to retrieve the metadata stored in GeoNetwork.

Requires authentication: Optional

Request
^^^^^^^

Search configuration parameters (all values are optional)

- **remote**: Search in local catalog or in a remote catalog. Values: off (default), on

- **extended**: Values: on, off (default)

- **timeout**: Timeout for request in seconds (default: 20)

- **hitsPerPage**: Results per page (default: 10)

- **similarity**: Lucene accuracy for searches (default 0.8)

- **sortBy**: Sorting criteria. Values: relevance (default), rating, popularity, changeDate, title

Search parameters (all values are optional):

- **eastBL, southBL, northBL, westBL**:
  Bounding box to restrict the search

- **relation**: Bounding box criteria.
  Values: equal, overlaps (default), encloses, fullyOutsideOf,
  intersection, crosses, touches, within

- **any**: Text to search in a free text search

- **title**: Metadata title

- **abstract**: Metadata abstract

- **themeKey**: Metadata keywords. To search for several use a value like "Global" or "watersheds"

- **template**: Indicates if search for templates or not. Values: n (default), y

- **dynamic**: Map type. Values: off (default), on

- **download**: Map type. Values: off (default), on

- **digital**: Map type. Values: off (default), on

- **paper**: Map type. Values: off (default), on

- **group**: Filter metadata by group, if missing search in all groups

- **attrset**:

- **dateFrom**: Filter metadata created after specified date

- **dateTo**: Filter metadata created before specified date

- **category**: Metadata category. If not specified, search all categories

Request to search for all metadata example::

  Url:
  http://localhost:8080/geonetwork/srv/en/xml.search

  Mime-type:
  application/xml

  Post request:
  <?xml version="1.0" encoding="UTF-8"?>
  <request />

Request with free text search example::

  Url:
  http://localhost:8080/geonetwork/srv/en/xml.search

  Mime-type:
  application/xml

  Post request:s
  <?xml version="1.0" encoding="UTF-8"?>
  <request>
    <any>africa</any>
  </request>

Request with a geographic search example::

  Url:
  http://localhost:8080/geonetwork/srv/en/xml.search

  Mime-type:
  application/xml

  Post request:
  <?xml version="1.0" encoding="UTF-8"?>
  <request>
    <any>africa</any>
    <eastBL>74.91574</eastBL>
    <southBL>29.40611</southBL>
    <northBL>38.47198</northBL>
    <westBL>60.50417</westBL>
    <relation>overlaps</relation>
    <sortBy>relevance</sortBy>
    <attrset>geo</attrset>
  </request>

Request to search using dates and keywords example::

  Url:
  http://localhost:8080/geonetwork/srv/en/xml.search

  Mime-type:
  application/xml

  Post request:
  <?xml version="1.0" encoding="UTF-8"?>
  <request>
    <title>africa</title>
    <themekey>"Global" or "World"</themekey>
    <dateFrom>2000-02-03T12:47:00</dateFrom>
    <dateTo>2010-02-03T12:49:00</dateTo>
  </request>

Response
^^^^^^^^

The response is the metadata record with additional
**geonet:info** section. The main fields for
**geonet:info** are:

- **response**: Response container.

  - **summary**: Attribute
    **count** indicates the number of metadata records retrieved

    - **keywords**: List of keywords
      that are part of the metadata resultset. Each keyword
      contains the value and the number of occurences in the
      retrieved metadata

  - **metadata**: Container for
    metadata records found. Each record contains an
    **geonet:info** element with the
    following information:

    - **title**: RSS channel
      title
    - **description**: RSS channel
      description
    - **item**: Metadata RSS item
      (one item for each metadata retrieved)

      - **id**: Metadata internal
        identifier
      - **uuid** : Metadata
        Universally Unique Identifier (UUID)
      - **schema**: Metadata
        schema
      - **createDate**: Metadata
        creation date
      - **changeDate**: Metadata last
        modification date
      - **source**: Source catalogue
        the metadata
      - **category**: Metadata
        category (Can be multiple elements)
      - **score**: Value indicating
        the accuracy of search

Metadata search response example::
  
  <?xml version="1.0" encoding="UTF-8"?>
  <response from="1" to="7">
    <summary count="7" type="local">
      <keywords>
        <keyword count="2" name="Global"/>
        <keyword count="2" name="World"/>
        <keyword count="2" name="watersheds"/>
        <keyword count="1" name="Biology"/>
        <keyword count="1" name="water resources"/>
        <keyword count="1" name="endangered plant species"/>
        <keyword count="1" name="Africa"/>
        <keyword count="1" name="Eurasia"/>
        <keyword count="1" name="endangered animal species"/>
        <keyword count="1" name="Antarctic ecosystem"/>
      </keywords>
    </summary>
    <metadata xmlns:gmx="http://www.isotc211.org/2005/gmx">
      <geonet:info xmlns:geonet="http://www.fao.org/geonetwork">
        <id>12</id>
        <uuid>bc179f91-11c1-4878-b9b4-2270abde98eb</uuid>
        <schema>iso19139</schema>
        <createDate>2007-07-25T12:05:45</createDate>
        <changeDate>2007-11-06T12:10:47</changeDate>
        <source>881a1630-d4e7-4c9c-aa01-7a9bbbbc47b2</source>
        <category>maps</category>
        <category>interactiveResources</category>
        <score>1.0</score>
      </geonet:info>
    </metadata>
    <metadata xmlns:gmx="http://www.isotc211.org/2005/gmx">
      <geonet:info xmlns:geonet="http://www.fao.org/geonetwork">
        <id>11</id>
        <uuid>5df54bf0-3a7d-44bf-9abf-84d772da8df1</uuid>
        <schema>iso19139</schema>
        <createDate>2007-07-19T14:45:07</createDate>
        <changeDate>2007-11-06T12:13:00</changeDate>
        <source>881a1630-d4e7-4c9c-aa01-7a9bbbbc47b2</source>
        <category>maps</category>
        <category>datasets</category>
        <category>interactiveResources</category>
        <score>0.9178859</score>
      </geonet:info>
    </metadata>
  </response>

Get metadata (xml.metadata.get)
```````````````````````````````

The **xml.metadata.get** service can be used to retrieve a metadata record stored in GeoNetwork.

Requires authentication: Optional

Request
^^^^^^^

Parameters (one of them mandatory):

- **uuid** : Metadata Universally Unique Identifier (UUID)

- **id**: Metadata internal identifier

Get metadata request example::

  Url:
  http://localhost:8080/geonetwork/srv/en/xml.metadata.get

  Mime-type:
  application/xml

  Post request:
  <?xml version="1.0" encoding="UTF-8"?>
  <request>
    <uuid>aa9bc613-8eef-4859-a9eb-4df35d8b21e4</uuid>
  </request>

Response
^^^^^^^^

The response is the metadata record with additional **geonet:info** section. The principal fields for **geonet:info** are:

- **schema**: Metadata schema

- **createDate**: Metadata creation date

- **changeDate**: Metadata last modification date

- **isTemplate**: Indicates if the metadata returned is a template

- **title**: Metadata title

- **source**: Source catalogue the metadata

- **uuid** : Metadata Universally Unique Identifier (UUID)

- **isHarvested**: Indicates if the metadata is harvested

- **popularity**: Indicates how often the record is retrieved

- **rating**: Average rating provided by users

- State of operation on metadata for the user: view, notify, download, dynamic, featured, edit

- **owner**: Indicates if the user that executed the service is the owner of metadata

- **ownername**: Metadata owner name

Get metadata response example::

  <?xml version="1.0" encoding="UTF-8"?>
  <Metadata xmlns:geonet="http://www.fao.org/geonetwork"
    xmlns:csw="http://www.opengis.net/cat/csw/2.0.2">
    <mdFileID>aa9bc613-8eef-4859-a9eb-4df35d8b21e4</mdFileID>
    ...
    <geonet:info>
      <id>10</id>
      <schema>iso19115</schema>
      <createDate>2005-08-23T17:58:18</createDate>
      <changeDate>2007-03-12T17:49:50</changeDate>
      <isTemplate>n</isTemplate>
      <title />
      <source>881a1630-d4e7-4c9c-aa01-7a9bbbbc47b2</source>
      <uuid>aa9bc613-8eef-4859-a9eb-4df35d8b21e4</uuid>
      <isHarvested>n</isHarvested>
      <popularity>0</popularity>
      <rating>0</rating>
      <view>true</view>
      <notify>true</notify>
      <download>true</download>
      <dynamic>true</dynamic>
      <featured>true</featured>
      <edit>true</edit>
      <owner>true</owner>
      <ownername>admin</ownername>
      <subtemplates />
    </geonet:info>
  </Metadata>

Errors
^^^^^^

- **Request must contain a UUID or an ID**, when no uuid or id parameter is provided

- **Operation not allowed (error id:
  operation-not-allowed)**, when the user is not allowed
  to show the metadata record. Returned 403 HTTP code

RSS Search: Search metadata and retrieve in RSS format (rss.search)
```````````````````````````````````````````````````````````````````

The **rss.search** service can be used to
retrieve metadata records in RSS format, using regular search
parameters. This service can be configured in
**WEB-INF\\config.xml** file setting the next parameters:

- **maxSummaryKeys**: Maximum number of RSS records to retrieve (default = 10)

Requires authentication: Optional. If not provided only public metadata records are retrieved

Request
^^^^^^^

Parameters:

- **georss**: valid values are simple,
  simplepoint and default. See also http://georss.org

  - **simple**: Bounding box in georss
    simple format
  - **simplepoint**: Bounding box in
    georss simplepoint format
  - **default**: Bounding box in georss
    GML format

- **eastBL, southBL, northBL, westBL**:
  Bounding box to restrict the search****

- **relation**: Bounding box criteria.
  Values: equal, overlaps (default), encloses, fullyOutsideOf,
  intersection, crosses, touches, within

- **any**: Text to search in a free text search

- **title**: Metadata title

- **abstract**: Metadata abstract

- themeKey: Metadata keywords. To search for several use a value like "Global" or "watersheds"

- **dynamic**: Map type. Values: off (default), on

- **download**: Map type. Values: off (default), on

- **digital**: Map type. Values: off (default), on

- **paper**: Map type. Values: off (default), on

- **group**: Filter metadata by group, if missing search in all groups

- **attrset**:

- **dateFrom**: Filter metadata created after specified date

- **dateTo**: Filter metadata created before specified date

- **category**: Metadata category. If not specified, search all categories

RSS search request example::

  Url:
  http://localhost:8080/geonetwork/srv/en/rss.search

  Mime-type:
  application/xml

  Post request:
  <?xml version="1.0" encoding="UTF-8"?>
  <request>
    <georss>simplepoint</georss>
    <any>africa</any>
    <eastBL>74.91574</eastBL>
    <southBL>29.40611</southBL>
    <northBL>38.47198</northBL>
    <westBL>60.50417</westBL>
    <relation>overlaps</relation>
    <sortBy>relevance</sortBy>
    <attrset>geo</attrset>
  </request>

Response
^^^^^^^^

Here follows the principal fields of the response:

- **channel**: This is the container for
  the RSS response

  - **title**: RSS channel title
  - **description**: RSS channel description
  - **item**: Metadata RSS item (one item for each metadata
    retrieved)

    - **title**: Metadata title
    - **link**: Link to show metadata page. Additional link
      elements (with rel="alternate") to OGC WXS services,
      shapefile/images files, Google KML, etc. can be returned
      depending on metadata
    - **description**: Metadata description
    - **pubDate**: Metadata publication date
    - **media**: Metadata thumbnails
    - **georrs:point**: Bounding box in georss simplepoint
      format

RSS latest response example::

  Mimetype:
  application/rss+xml

  Response:
  <?xml version="1.0" encoding="UTF-8"?>
  <rss xmlns:media="http://search.yahoo.com/mrss/" xmlns:georss="http://www.georss.org/georss" xmlns:gml="http://www.opengis.net/gml" version="2.0">
    <channel>
      <title>GeoNetwork opensource portal to spatial data and information</title>
      <link>http://localhost:8080/geonetwork</link>
      <description>GeoNetwork opensource provides Internet access to interactive maps, satellite imagery and related spatial databases ... </description>
      <language>en</language>
      <copyright>All rights reserved. Your generic copyright statement </copyright>
      <category>Geographic metadata catalog</category>
      <generator>GeoNetwork opensource</generator>
      <ttl>30</ttl>
      <item>
        <title>Hydrological Basins in Africa (Sample record, please remove!)</title>
        <link>http://localhost:8080/geonetwork?uuid=5df54bf0-3a7d-44bf-9abf-84d772da8df1</link>
        <link href="http://geonetwork3.fao.org/ows/296?SERVICE=wms$amp;VERSION=1.1.1&REQUEST=GetMap&BBOX=-17.3,-34.6,51.1,38.2&LAYERS=hydrological_basins&SRS=EPSG:4326&WIDTH=200&HEIGHT=213&FORMAT=image/png&TRANSPARENT=TRUE&STYLES=default" type="image/png" rel="alternate" title="Hydrological basins in Africa"/>
        <link href="http://localhost:8080/geonetwork/srv/en/google.kml?uuid=5df54bf0-3a7d-44bf-9abf-84d772da8df1&layers=hydrological_basins" type="application/vnd.google-earth.kml+xml" rel="alternate" title="Hydrological basins in Africa"/>
        <category>Geographic metadata catalog</category>
        <description><![CDATA[ ... ]]></description>
        <pubDate>06 Nov 2007 12:13:00 EST</pubDate>
        <guid>http://localhost:8080/geonetwork?uuid=5df54bf0-3a7d-44bf-9abf-84d772da8df1</guid>
        <media:content url="/geonetwork/srv/en/resources.get?id=11&fname=thumbnail_s.gif&access=public" type="image/gif" width="100"/>
        <media:text>Major hydrological basins and their sub-basins ...</media:text>
        <!--Bounding box in georss simplepoint format (default) (http://georss.org)-->
        <georss:point>16.9 1.8</georss:point>
        </item>
    </channel>
  </rss>

RSS latest: Get latest updated metadata (rss.latest)
````````````````````````````````````````````````````

The **rss.latest** service can be used to retrieve the latest added metadata records in RSS format. This service can be configured in **WEB-INF\\config.xml** file setting the next parameters:

- **maxItems**: Maximum number of RSS records to retrieve (default = 20)

- **timeBetweenUpdates**: Minutes to query database for new metadata (default = 60)

Requires authentication: Optional. If not provided only public metadata records are retrieved

Request
^^^^^^^

Parameters:

- **georss**: valid values are simple, simplepoint and default. See also http://georss.org

  - **simple**: Bounding box in georss simple format
  - **simplepoint**: Bounding box in georss simplepoint format
  - **default**: Bounding box in georss GML format

RSS latest request example::

  Url:
  http://localhost:8080/geonetwork/srv/en/rss.latest

  Mime-type:
  application/xml

  Post request:
  <?xml version="1.0" encoding="UTF-8"?>
  <request>
    <georss>default</georss>
    <maxItems>1</maxItems>
  </request>

Response
^^^^^^^^

Here follows the principal fields of the response:

- **channel**: This is the container for the RSS response

  - **title**: RSS channel title
  - **description**: RSS channel description
  - **item**: Metadata RSS item (one item for each metadata
    retrieved)

    - **title**: Metadata title
    - **link**: Link to show metadata page. Additional link
      elements (with rel="alternate") to OGC WXS services,
      shapefile/images files, Google KML, etc. can be returned
      depending on metadata
    - **description**: Metadata description
    - **pubDate**: Metadata publication date
    - **media**: Metadata thumbnails
    - **georrs:where**: Bounding box with the metadata
      extent

RSS latest response example::

  Mimetype:
  application/rss+xml

  Response:
  <?xml version="1.0" encoding="UTF-8"?>
  <rss xmlns:media="http://search.yahoo.com/mrss/" xmlns:georss="http://www.georss.org/georss"
    xmlns:gml="http://www.opengis.net/gml" version="2.0">
  <channel>
    <title>GeoNetwork opensource portal to spatial data and information</title>
    <link>http://localhost:8080/geonetwork</link>
    <description>GeoNetwork opensource provides Internet access to interactive maps,
    satellite imagery and related spatial databases ... </description>
    <language>en</language>
    <copyright>All rights reserved. Your generic copyright statement </copyright>
    <category>Geographic metadata catalog</category>
    <generator>GeoNetwork opensource</generator>
    <ttl>30</ttl>
    <item>
      <title>Hydrological Basins in Africa (Sample record, please remove!)</title>
      <link>http://localhost:8080/geonetwork?uuid=5df54bf0-3a7d-44bf-9abf-84d772da8df1</link>
      <link href="http://geonetwork3.fao.org/ows/296?SERVICE=wms$amp;VERSION=1.1.1&REQUEST=GetMap
        &BBOX=-17.3,-34.6,51.1,38.2&LAYERS=hydrological_basins&SRS=EPSG:4326&WIDTH=200
        &HEIGHT=213&FORMAT=image/png&TRANSPARENT=TRUE&STYLES=default" type="image/png"
        rel="alternate" title="Hydrological basins in Africa"/>
      <link href="http://localhost:8080/geonetwork/srv/en/google.kml?
        uuid=5df54bf0-3a7d-44bf-9abf-84d772da8df1&layers=hydrological_basins"
        type="application/vnd.google-earth.kml+xml"
        rel="alternate" title="Hydrological basins in Africa"/>
      <category>Geographic metadata catalog</category>
      <description><![CDATA[ ... ]]></description>
      <pubDate>06 Nov 2007 12:13:00 EST</pubDate>
      <guid>http://localhost:8080/geonetwork?uuid=5df54bf0-3a7d-44bf-9abf-84d772da8df1</guid>
      <media:content url="/geonetwork/srv/en/resources.get?id=11&fname=thumbnail_s.gif
        &access=public" type="image/gif" width="100"/>
        <media:text>Major hydrological basins and their sub-basins ...</media:text>
     <!--Bounding box in georss GML format (http://georss.org)-->
     <georss:where>
       <gml:Envelope>
         <gml:lowerCorner>-34.6 -17.3</gml:lowerCorner>
         <gml:upperCorner>38.2 51.1</gml:upperCorner>
       </gml:Envelope>
     </georss:where>
    </item>
  </channel>
  </rss>

Metadata administration services
--------------------------------

Update operations allowed for a metadata (metadata.admin)
`````````````````````````````````````````````````````````

The **metadata.admin** service updates the
operations allowed for a metadata with the list of operations allowed
send in the parameters, **deleting all the
operations allowed assigned previously**.

Requires authentication: Yes

Request to metadata.admin service
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

Parameters:

- **id**: Identifier of metadata to update

- **_G_O**: (can be multiple elements)

  - **G**: Group identifier
  - **O**: Operation identifier

Operation identifiers:

- 0: view
- 1: download
- 2: editing
- 3: notify
- 4: dynamic
- 5: featured

Request metadata update operations allowed example:

**POST**::

  Url:
  http://localhost:8080/geonetwork/srv/en/metadata.admin

  Mime-type:
  application/xml

  Post request:
  <?xml version="1.0" encoding="UTF-8"?>
  <request>
    <id>6</id>
    <_1_2 />
    <_1_1 />
  </request>

**GET**::

  Url:
  http://localhost:8080/geonetwork/srv/en/metadata.admin?id=6&_1_2&_1_1

Response to metadata.admin service
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

The response contains the identifier of the metadata updated.

Response metadata update operations allowed example::

  <?xml version="1.0" encoding="UTF-8"?>
  <request>
    <id>6</id>
  </request>

Errors
^^^^^^

- **Service not allowed (error id:
  service-not-allowed)**, when the user is not
  authenticated or his profile has no rights to execute the
  service. Returned 401 HTTP code

- **Metadata not found (error id: metadata-not-found)** if not
  exists a metadata record with the identifier provided

- **ERROR: insert or update on table "operationallowed"
  violates foreign key 'operationallowed_operationid_fkey »**, if an
  operation identifier provided is not valid

- **ERROR: insert or update on table "operationallowed"
  violates foreign key 'operationallowed_groupid_fkey »**, if a
  group identifier provided is not valid

Massive update privilegies (metadata.massive.update.privileges)
```````````````````````````````````````````````````````````````

The **metadata.massive.update.privileges** service updates the operations allowed for a selected metadata with the list of operations allowed send in the parameters, **deleting all the operations allowed assigned previously**.

This service requires a previous call to **metadata.select** service to select the metadata records to update.

Requires authentication: Yes

Request to metadata.select service
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

Parameters:

- **id**: Identifier of metadata to select

- **selected**: Selection state. Values: add, add-all, remove, remove-all

Select all metadata allowed example::

  Url:
  http://localhost:8080/geonetwork/srv/en/metadata.select

  Mime-type:
  application/xml

  Post request:
  <?xml version="1.0" encoding="UTF-8"?>
  <request>
    <selected>add-all</selected>
  </request>

Select a metadata record example::

  Url:
  http://localhost:8080/geonetwork/srv/en/metadata.select

  Mime-type:
  application/xml

  Post request:
  <?xml version="1.0" encoding="UTF-8"?>
  <request>
    <id>2</id>
    <selected>add</selected>
  </request>

Clear metadata selection example::

  Url:
  http://localhost:8080/geonetwork/srv/en/metadata.select

  Mime-type:
  application/xml

  Post request:
  <?xml version="1.0" encoding="UTF-8"?>
  <request>
    <selected>remove-all</selected>
  </request>

Response to metadata.select service
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

The response contains the number of metadata selected.

Response select metadata example::

  <?xml version="1.0" encoding="UTF-8"?>
  <request>
    <Selected>10</Selected>
  </request>

Request to metadata.massive.update.privileges
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

Parameters:

- **_G_O**: (can be multiple elements)
  - **G**: Group identifier
  - **O**: Operation identifier

Operation identifiers:

- 0: view
- 1: download
- 2: editing
- 3: notify
- 4: dynamic
- 5: featured

Request metadata massive update privilegies example:

**POST**::

  Url:
  http://localhost:8080/geonetwork/srv/en/metadata.massive.update.privileges

  Mime-type:
  application/xml

  Post request:
  <?xml version="1.0" encoding="UTF-8"?>
  <request>
    <_1_2 />
    <_1_1 />
  </request>

**GET**::

  Url:
  http://localhost:8080/geonetwork/srv/en/metadata.massive.update.privileges?_1_2&_1_1

Response to metadata.massive.update.privileges
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

If request is executed succesfully HTTP 200 status code is
returned. If request fails an HTTP status code error is returned and
the response contains the XML document with the exception.

Errors
^^^^^^

- **Service not allowed (error id:
  service-not-allowed)**, when the user is not
  authenticated or his profile has no rights to execute the
  service. Returned 401 HTTP code

- **Metadata not found (error id: metadata-not-found)** if not
  exists a metadata record with the identifier provided

- **ERROR: insert or update on table "operationallowed"
  violates foreign key 'operationallowed_operationid_fkey »**, if an
  operation identifier provided is not valid

- **ERROR: insert or update on table "operationallowed"
  violates foreign key 'operationallowed_groupid_fkey »**, if a
  group identifier provided is not valid

Metadata ownership services
---------------------------

This services allow to manage the metadata ownership (the user who
created the metadata), for example to get information about the users
who created metadata records or transfer the ownership of metadata
records to another user. Only users with
**Administrator** and **UserAdmin**
profiles can execute these services.

Massive new owner (metadata.massive.newowner)
`````````````````````````````````````````````

The **metadata.massive.newowner** service
allows to change the owner of a group of metadata. This service
requires a previous call to **metadata.select**
service to select the metadata records to update.

Requires authentication: Yes

Request to metadata.select service
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

Parameters:

- **id**: Identifier of metadata to select (can be multiple elements)

- **selected**: Selection state. Values: add, add-all, remove, remove-all

Select metadata request example::

  Url:
  http://localhost:8080/geonetwork/srv/en/metadata.select

  Mime-type:
  application/xml

  Post request:
  <?xml version="1.0" encoding="UTF-8"?>
  <request>
    <selected>add-all</selected>
  </request>

Response to metadata.select service
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

The response contains the number of metadata selected.

Select metadata response example::

  <?xml version="1.0" encoding="UTF-8"?>
  <request>
    <Selected>10</Selected>
  </request>

Request to metadata.massive.newowner
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

Once the metadata records have been selected can be
**metadata.massive.newowner** invoked with the next
parameters:

- **user**: (mandatory) New owner user identifier
- **group**: (mandatory) New owner group user identifier

Transfer ownership request example::

  Url:
  http://localhost:8080/geonetwork/srv/en/metadata.massive.newowner

  Mime-type:
  application/xml

  Post request:
  <?xml version="1.0" encoding="UTF-8"?>
  <request>
    <user>2</user>
    <group>2</group>
  </request>

Response to metadata.massive.newowner
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

If request is executed succesfully HTTP 200 status code is
returned. If request fails an HTTP status code error is returned and
the response contains the XML document with the exception.

Transfer ownership (xml.ownership.transfer)
```````````````````````````````````````````

The **xml.ownership.transfer** service can be
used to transfer ownership and privileges of metadata owned by a user
(in a group) to another user (in a group). This service should be used
with data retrieved from previous invocations to the services :ref:`xml.ownership.editors <xml.ownership.editors>` and :ref:`xml.ownership.groups <xml.ownership.groups>`, described
below.

Requires authentication: Yes

Request
^^^^^^^

Parameters:

- **sourceUser**: (mandatory) Identifier
  of the user to transfer the ownership of her
  metadata****

- **sourceGroup**: (mandatory) Identifier
  of source group of the metadata to transfer ownership

- **targetUser**: (mandatory) Identifier
  of the user to get the set the new metadata ownership

- **targetGroup**: (mandatory) Identifier
  of target group of the transferred ownership metadata

Example: In the next example we are going to transfer the
ownership and privileges of metadata owned of user John (id=2) in
group RWS (id=5) to user Samantha(id=7) in group NLR (id=6)

Transfer ownership request example::

  Url:
  http://localhost:8080/geonetwork/srv/en/xml.ownership.transfer

  Mime-type:
  application/xml

  Post request:
  <?xml version="1.0" encoding="UTF-8"?>
  <request>
    <sourceUser>2</sourceUser>
    <sourceGroup>5</sourceGroup>
    <targetUser>7</targetUser>
    <targetGroup>6</targetGroup>
  </request>

Response
^^^^^^^^

Here follows the structure of the response:

- **response**: This is the container for
  the response
  
  - **privileges**: Transferred privileges
  - **metadata**: Transferred metadata records

Transfer ownership response example::

  <?xml version="1.0" encoding="UTF-8"?>
  <response>
    <privileges>4</privileges>
    <metadata>2</metadata>
  </response>

Errors
^^^^^^

- **Service not allowed (error id: service-not-allowed)**, when the user is not authenticated or his profile has no rights to execute the service. Returned 401 HTTP code

- **Missing parameter (error id: missing-parameter)**, when mandatory parameters are not provided

- **bad-parameter XXXX**, when a mandatory parameter is empty

.. _xml.ownership.editors:

Retrieve metadata owners (xml.ownership.editors)
````````````````````````````````````````````````

The **xml.ownership.editors** service can be used to retrieve the users that own metadata records.

Requires authentication: Yes

Request
^^^^^^^

Parameters:

- **None**

Retrieve metadata owners request example::

  Url:
  http://localhost:8080/geonetwork/srv/en/xml.ownership.editors

  Mime-type:
  application/xml

  Post request:
  <?xml version="1.0" encoding="UTF-8"?>
  <request />

Response
^^^^^^^^

Here follows the structure of the response:

- **root**: This is the container for the response

  - **editor**: Container for each editor user information
  
    - **id**: User identifier
    - **username**: User login
    - **name**: User name
    - **surname**: User surname
    - **profile**: User profile

Retrieve metadata editors response example::

  <?xml version="1.0" encoding="UTF-8"?>
  <root>
    <editor>
      <id>1</id>
      <username>admin</username>
      <name>admin</name>
      <surname>admin</surname>
      <profile>Administrator</profile>
    </editor>
    <editor>
      <id>2</id>
      <username>samantha</username>
      <name>Samantha</name>
      <surname>Smith</surname>
      <profile>Editor</profile>
    </editor>
  </root>

Errors
^^^^^^

- **Service not allowed (error id: service-not-allowed)**, when the user is not authenticated or his profile has no rights to execute the service. Returned 401 HTTP code

.. _xml.ownership.groups:

Retrieve groups/users allowed to transfer metadata ownership from a user (xml.ownership.groups)
```````````````````````````````````````````````````````````````````````````````````````````````

The **xml.ownership.groups** service can be
used to retrieve the groups/users to which can be transferred the
metadata ownership/privilegies from the specified user.

Request
^^^^^^^

Parameters:

- **id**: (mandatory) User identifier of
  the user to check to which groups/users can be transferred the
  ownership/privilegies of her metadata

Retrieve ownership groups request example::

  Url:
  http://localhost:8080/geonetwork/srv/en/xml.ownership.groups

  Mime-type:
  application/xml

  Post request:
  <?xml version="1.0" encoding="UTF-8"?>
  <request>
    <id>2</id>
  </request>

Response
^^^^^^^^

Here follows the structure of the response:

- **response**: This is the container for the response

  - **targetGroup**: Allowed target
    group to transfer ownership of user metadata (can be
    multiple **targetGroup** elements)

    - **id, name, description, email, referrer, label**: Group information
    - **editor**: Users of the group that own metadata (can be multiple **editor** elements)

      - **id,surname, name**: Metadata user owner information

Retrieve ownership groups response example::

  <?xml version="1.0" encoding="UTF-8"?>
  <response>
    <targetGroup>
      <id>2</id>
      <name>sample</name>
      <description>Demo group</description>
      <email>group@mail.net</email>
      <referrer />
      <label>
        <en>Sample group</en>
        <fr>Sample group</fr>
        <es>Sample group</es>
        <de>Beispielgruppe</de>
        <nl>Voorbeeldgroep</nl>
      </label>
      <editor>
        <id>12</id>
        <surname />
        <name />
      </editor>
      <editor>
        <id>13</id>
        <surname />
        <name>Samantha</name>
      </editor>
    </targetGroup>
    <targetGroup>
      <id>6</id>
      <name>RWS</name>
      <description />
      <email />
      <referrer />
      <label>
        <de>RWS</de>
        <fr>RWS</fr>
        <en>RWS</en>
        <es>RWS</es>
        <nl>RWS</nl>
      </label>
      <editor>
        <id>7</id>
        <surname />
        <name>Samantha</name>
      </editor>
    </targetGroup>
    ...
  </response>

Errors
^^^^^^

- **Service not allowed (error id:
  service-not-allowed)**, when the user is not
  authenticated or his profile has no rights to execute the
  service. Returned 401 HTTP code

Metadata editing
----------------

This services allow to maintaining the metadata in the
catalog.

Insert metadata (metadata.insert)
`````````````````````````````````

The **metadata.insert** service allows to
create a new metadata record in the catalog.

Requires authentication: Yes

Request
^^^^^^^

Parameters:

- **data**: (mandatory) Contains the
  metadata record

- **group** (mandatory): Owner group
  identifier for metadata

- **isTemplate**: indicates if the
  metadata content is a new template or not. Default value:
  "n"

- **title**: Metadata title. Only
  required if isTemplate = "y"

- **category** (mandatory): Metadata
  category. Use "_none_" value to don't assign any
  category

- **styleSheet** (mandatory): Stylesheet
  name to transform the metadata before inserting in the
  catalog. Use "_none_" value to don't apply any
  stylesheet

- **validate**: Indicates if the metadata
  should be validated before inserting in the catalog. Values:
  on, off (default)

Insert metadata request example::

  Url:
  http://localhost:8080/geonetwork/srv/en/metadata.insert

  Mime-type:
  application/xml

  Post request:
  <?xml version="1.0" encoding="UTF-8"?>
  <request>
    <group>2</group>
    <category>_none_</category>
    <styleSheet>_none_</styleSheet>
    <data><![CDATA[
      <gmd:MD_Metadata xmlns:gmd="http://www.isotc211.org/2005/gmd"
                   xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
      ...
         </gmd:DQ_DataQuality>
        </gmd:dataQualityInfo>
      </gmd:MD_Metadata>]]>
    </data>
  </request>

Response
^^^^^^^^

If request is executed succesfully HTTP 200 status code is
returned. If request fails an HTTP status code error is returned and
the response contains the XML document with the exception.

If validate parameter is set to "on" and the provided metadata
is not valid confirming the xsd schema an exception report is
returned.

Validation metadata report::

  <?xml version="1.0" encoding="UTF-8"?>
  <error id="xsd-validation-error">
    <message>XSD Validation error(s)</message>
    <class>XSDValidationErrorEx</class>
    <stack>
      <at class="org.fao.geonet.services.metadata.ImportFromDir"
        file="ImportFromDir.java" line="297" method="validateIt" />
      <at class="org.fao.geonet.services.metadata.ImportFromDir"
        file="ImportFromDir.java" line="281" method="validateIt" />
      <at class="org.fao.geonet.services.metadata.Insert"
        file="Insert.java" line="102" method="exec" />
      <at class="jeeves.server.dispatchers.ServiceInfo"
        file="ServiceInfo.java" line="238" method="execService" />
      <at class="jeeves.server.dispatchers.ServiceInfo"
        file="ServiceInfo.java" line="141" method="execServices" />
      <at class="jeeves.server.dispatchers.ServiceManager"
        file="ServiceManager.java" line="377" method="dispatch" />
      <at class="jeeves.server.JeevesEngine"
        file="JeevesEngine.java" line="621" method="dispatch" />
      <at class="jeeves.server.sources.http.JeevesServlet"
        file="JeevesServlet.java" line="174" method="execute" />
      <at class="jeeves.server.sources.http.JeevesServlet"
        file="JeevesServlet.java" line="99" method="doPost" />
      <at class="javax.servlet.http.HttpServlet"
        file="HttpServlet.java" line="727" method="service" />
    </stack>
    <object>
      <xsderrors>
        <error>
          <message>ERROR(1) org.xml.sax.SAXParseException: cvc-datatype-valid.1.2.1: '' is not a valid value for 'dateTime'. (Element: gco:DateTime with parent element: gmd:date)</message>
          <xpath>gmd:identificationInfo/gmd:MD_DataIdentification/gmd:citation/gmd:CI_Citation/gmd:date/gmd:CI_Date/gmd:date/gco:DateTime</xpath>
        </error>
        <error>
          <message>ERROR(2) org.xml.sax.SAXParseException: cvc-type.3.1.3: The value '' of element 'gco:DateTime' is not valid. (Element: gco:DateTime with parent element: gmd:date)</message>
          <xpath>gmd:identificationInfo/gmd:MD_DataIdentification/gmd:citation/gmd:CI_Citation/gmd:date/gmd:CI_Date/gmd:date/gco:DateTime</xpath>
        </error>
        <error>
          <message>ERROR(3) org.xml.sax.SAXParseException: cvc-datatype-valid.1.2.1: '' is not a valid value for 'integer'. (Element: gco:Integer with parent element: gmd:denominator)</message>
          <xpath>gmd:identificationInfo/gmd:MD_DataIdentification/gmd:spatialResolution/gmd:MD_Resolution/gmd:equivalentScale/gmd:MD_RepresentativeFraction/gmd:denominator/gco:Integer</xpath>
        </error>
        <error>
          <message>ERROR(4) org.xml.sax.SAXParseException: cvc-type.3.1.3: The value '' of element 'gco:Integer' is not valid. (Element: gco:Integer with parent element: gmd:denominator)</message>
          <xpath>gmd:identificationInfo/gmd:MD_DataIdentification/gmd:spatialResolution/gmd:MD_Resolution/gmd:equivalentScale/gmd:MD_RepresentativeFraction/gmd:denominator/gco:Integer</xpath>
        </error>
      </xsderrors>
    </object>
    <request>
      <language>en</language>
      <service>metadata.insert</service>
    </request>
  </error>

Errors
^^^^^^

- **Service not allowed (error id:
  service-not-allowed)**, when the user is not
  authenticated or his profile has no rights to execute the
  service. Returned 401 HTTP code

- **Missing parameter (error id:
  missing-parameter)**, when mandatory parameters are
  not provided. Returned 400 HTTP code

- **bad-parameter XXXX**, when a
  mandatory parameter is empty. Returned 400 HTTP code

- **ERROR: duplicate key violates unique
  constraint "metadata_uuid_key"**, if exists another
  metadata record in catalog with the same uuid of the metadata
  provided to insert

Update metadata (metadata.update)
`````````````````````````````````

The metadata.update service allows to update the content of a
metadata record in the catalog.

Requires authentication: Yes

Request
^^^^^^^

Parameters:

- **id**: (mandatory) Identifier of the metadata to update

- **version**: (mandatory) This parameter
  is used to check if another user has updated the metadata
  after we retrieved it and before involking the update metadata
  service. **CHECK how to provide value to
  the user**

- **isTemplate**: indicates if the
  metadata content is a new template or not. Default value: "n"

- **showValidationErrors**: Indicates if
  the metadata should be validated before updating in the
  catalog.

- **title**: Metadata title (for templates)

- **data** (mandatory) Contains the metadata record

Update metadata request example::

  Url:
  http://localhost:8080/geonetwork/srv/en/metadata.update

  Mime-type:
  application/xml

  Post request:

  <?xml version="1.0" encoding="UTF-8"?>
  <request>
    <id>11</id>
    **<version>1</version>**
    <data><![CDATA[
      <gmd:MD_Metadata xmlns:gmd="http://www.isotc211.org/2005/gmd"
                       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
      
      ...
      
            </gmd:DQ_DataQuality>
        </gmd:dataQualityInfo>
      </gmd:MD_Metadata>]]>
    </data>
  </request>

Response
^^^^^^^^

If request is executed succesfully HTTP 200 status code is
returned. If request fails an HTTP status code error is returned and
the response contains the XML document with the exception.

Errors
^^^^^^

- **Service not allowed (error id:
  service-not-allowed)**, when the user is not
  authenticated or his profile has no rights to execute the
  service. Returned 401 HTTP code

- **Missing parameter (error id:
  missing-parameter)**, when mandatory parameters are
  not provided. Returned 400 HTTP code

- **bad-parameter XXXX**, when a
  mandatory parameter is empty. Returned 400 HTTP code

- **Concurrent update (error id:
  client)**, when the version number provided is
  different from actual version number for metatada. Returned
  400 HTTP code

Delete metadata (metadata.delete)
`````````````````````````````````

The **metadata.delete** service allows to
remove a metadata record from the catalog. The metadata content is
backup in MEF format by default in data\\removed folder. This folder
can be configured in geonetwork\\WEB-INF\\config.xml.

Requires authentication: Yes

Request
^^^^^^^

Parameters:

- **id**: (mandatory) Identifier of the metadata to delete

Delete metadata request example::

  Url:
  http://localhost:8080/geonetwork/srv/en/metadata.delete

  Mime-type:
  application/xml

  Post request:
  <?xml version="1.0" encoding="UTF-8"?>
  <request>
    <id>10</id>
  </request>

Response
^^^^^^^^

If request is executed succesfully HTTP 200 status code is
returned. If request fails an HTTP status code error is returned and
the response contains the XML document with the exception.

Errors
^^^^^^

- **Service not allowed (error id:
  service-not-allowed)**, when the user is not
  authenticated or his profile has no rights to execute the
  service. Returned 401 HTTP code

- **Metadata not found (error id:
  error)**, if the identifier provided don't correspond
  to an existing metadata. Returned 500 HTTP code

- **Operation not allowed** **(error id: operation-not-allowed)**, when
  the user is not authorized to edit the metadata. To edit a metadata:
  
  - The user is the metadata owner
  - The user is an Administrator
  - The user has edit rights over the metadata
  - The user is a Reviewer and/or UserAdmin and the
    metadata groupOwner is one of his groups


