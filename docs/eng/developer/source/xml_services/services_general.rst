.. _services_general:

General services
================

xml.info
--------

The xml.info service can be used to query the site about its configuration,
services, status and so on. For example, it is used by the harvesting web
interface to retrieve information about a remote node.

Request
```````

The XML request should contain at least one type element to indicates the
kind of information to retrieve. More type elements can be specified to
obtain more information at once. The set of allowed values are:

#.  **site**: Returns general information about the site like its name, id, etc...

#.  **categories**: Returns all site’s categories

#.  **groups**: Returns all site’s groups visible to the requesting user. If the user does not authenticate
    himself, only the Intranet and the all groups are visible.

#.  **operations**: Returns all possible operations on metadata

#.  **regions**: Returns all geographical regions usable for queries

#.  **sources**: Returns all GeoNetwork sources that the remote site knows.

The result will contain:

- The remote node’s name and siteId

- All source UUIDs and names that have been discovered through
  harvesting.

- All source UUIDs and names of metadata that have been imported
  into the remote node through the MEF format.

- Administrators can see all users into the system (normal, other
  administrators, etc...)

- User administrators can see all users they can administrate and
  all other user administrators in the same group set. The group set
  is defined by all groups visible to the user administration, beside
  the All and the Intranet groups.

- An authenticated user can see only himself.

- A guest cannot see any user.

Request example::

    <request>
        <type>site</type>
        <type>groups</type>
    </request>

Response
````````

Each type element produces an XML subtree so the response to the previous
request is like this::

    <info>
        <site>...</site>
        <categories>...</categories>
        <groups>...</groups>
        ...
    </info>

Here follows the structure of each subtree:

- **site**: This is the container

  - **name**: Human readable site name
  - **siteId**: Universal unique identifier of the site
  - **platform**: This is just a container to hold the site’s
    back end

    - **name**: Platform name. For GeoNetwork installations
      it must be GeoNetwork.
    - **version**: Platform version, given in the X.Y.Z
      format
    - **subVersion**: Additional version notes, like
      ’alpha-1’ or ’beta-2’.
      
Example site information::
  
      <site>
          <name>My site</name>
          <organisation>FAO</organization>
          <siteId>0619cc50-708b-11da-8202-000d9335906e</siteId>
          <platform>
              <name>geonetwork</name>
              <version>2.2.0</version>
          </platform>
      </site>

- **categories**: This is the container for categories.

  - **category \[0..n]**: A single GeoNetwork’s category. This
    element has an id attribute which represents the local
    identifier for the category. It can be useful to a client
    to link back to this category.

    - **name**: Category’s name
    - **label**: The localised labels used to show the
      category on screen. See :ref:`xml_response_categories`.

Example response for categories::
  
      <categories>
          <category id="1">
              <name>datasets</name>
              <label>
                  <en>Datasets</en>
                  <fr>Jeux de données</fr>
              </label>
          </category>
      </categories>

- **groups**: This is the container for groups

  - **group \[2..n]**: This is a GeoNetwork group. There are at
    least the Internet and Intranet groups. This element has an
    id attribute which represents the local identifier for the
    group.

    - **name**: Group’s name
    - **description**: Group’s description
    - **referrer**: The user responsible for this group
    - **email**: The email address to notify when a map is
      downloaded
    - **label**: The localised labels used to show the
      group on screen. See :ref:`xml_response_groups`.

Example response for groups::
  
      <groups>
          <group id="1">
              <name>editors</name>
              <label>
                  <en>Editors</en>
                  <fr>Éditeurs</fr>
              </label>
          </group>
      </groups>

- **operations**: This is the container for the operations

  - **operation \[0..n]**: This is a possible operation on
    metadata. This element has an id attribute which represents
    the local identifier for the operation.

    - **name**: Short name for the operation.
    - **reserved**: Can be y or n and is used to
      distinguish between system reserved and user defined
      operations.
    - **label**: The localised labels used to show the
      operation on screen. See :ref:`xml_response_operations`.

Example response for operations::
  
      <operations>
          <operation id="0">
              <name>view</name>
              <label>
                  <en>View</en>
                  <fr>Voir</fr>
              </label>
          </operation>
      </operations>

- **regions**: This is the container for geographical regions

  - **region \[0..n]**: This is a region present into the system.
    This element has an id attribute which represents the local
    identifier for the operation.

    - **north**: North coordinate of the bounding box.
    - **south**: South coordinate of the bounding box.
    - **west**: West coordinate of the bounding box.
    - **east**: east coordinate of the bounding box.
    - **label**: The localised labels used to show the
      region on screen. See :ref:`xml_response_regions`.

Example response for regions::
  
      <regions>
          <region id="303">
              <north>82.99</north>
              <south>26.92</south>
              <west>-37.32</west>
              <east>39.24</east>
              <label>
                  <en>Western Europe</en>
                  <fr>Western Europe</fr>
              </label>
          </region>
      </regions>

- **sources**: This is the container.

  - **source \[0..n]**: A source known to the remote node.

    - **name**: Source’s name
    - **UUID**: Source’s unique identifier

Example response for a source::
  
      <sources>
          <source>
              <name>My Host</name>
              <UUID>0619cc50-708b-11da-8202-000d9335906e</uuid>
          </source>
      </sources>

- **users**: This is the container for user information

  - **user \[0..n]**: A user of the system

    - **id**: The local identifier of the user
    - **username**: The login name
    - **surname**: The user’s surname. Used for display
      purposes.
    - **name**: The user’s name. Used for display purposes.
    - **profile**: User’s profile, like Administrator,
      Editor, UserAdmin etc...
    - **address**: The user’s address.
    - **state**: The user’s state.
    - **zip**: The user’s address zip code.
    - **country**: The user’s country.
    - **email**: The user’s email address.
    - **organisation**: The user’s organisation.
    - **kind**:

Example response for a user::
  
      <users>
          <user>
              <id>3</id>
              <username>eddi</username>
              <surname>Smith</surname>
              <name>John</name>
              <profile>Editor</profile>
              <address/>
              <state/>
              <zip/>
              <country/>
              <email/>
              <organisation/>
              <kind>gov</kind>
          </user>
      </users>

Localised entities
``````````````````

Localised entities have a general label element which contains the
localised strings in all supported languages. This element has as many
children as the supported languages. Each child has a name that reflect the
language code while its content is the localised text. Here is an example of
such elements::

    <label>
        <en>Editors</en>
        <fr>Éditeurs</fr>
        <es>Editores</es>
    </label>

xml.forward
-----------

This is just a router service. It is used by JavaScript code to connect to a
remote host because a JavaScript program cannot access a machine other than its
server. For example, it is used by the harvesting web interface to query a
remote host and retrieve the list of site ids.

Request
```````

The service’s request::

    <request>
        <site>
            <url>...</url>
            <type>...</type>
            <account>
                <username>...</username>
                <password>...</password>
            </account>
        </site>
        <params>...</params>
    </request>

Where:

#.  **site**: A container for site information where the request will be forwarded.

#.  **url**: Refers to the remote URL to connect to. Usually it points to a
    GeoNetwork XML service but it can point to any XML service.

#.  **type**: Its only purpose is to distinguish GeoNetwork nodes which use a different
    authentication scheme. The value GeoNetwork refers to these nodes. Any other
    value, or if the element is missing, refers to a generic node.

#.  **account**: This element is optional. If present, the provided credentials will be used to
    authenticate to the remote site.

#.  **params**: This is just a container for the request that must be executed remotely.

Request for info from a remote server::

    <request>
        <site>
            <url>http://mynode.org:8080/geonetwork/srv/en/xml.info</url>
        </site>
        <params>
            <request>
                <type>site<type>
            </request>
        </params>
    </request>

Please note that this service uses the GeoNetwork’s proxy
configuration.

Response
````````

The response is just the response from the remote service.

