.. _group_services:

Group services
==============

Groups retrieving
-----------------

Groups list (xml.group.list)
````````````````````````````

The **xml.group.list** service can be used to retrieve the user groups avalaible in GeoNetwork.

Requires authentication: No

Request
^^^^^^^

Parameters:

- **None**

Group list request example::

  Url:
  http://localhost:8080/geonetwork/srv/en/xml.group.list

  Mime-type:
  application/xml

  Post request::
  <?xml version="1.0" encoding="UTF-8"?>
  <request />

Response
^^^^^^^^

Here follows the structure of the response:

- **record**: This is the container for each group element returned
- **id**: Group identifier
- **name**: Human readable group name
- **description**: Group description
- **email**: Group email address
- **label**: This is just a container
  to hold the group names translated in the languages
  supported by GeoNetwork. Each translated label it's enclosed
  in a tag that identifies the language code

Group list response example::

  <?xml version="1.0" encoding="UTF-8"?>
  <response>
    <record>
      <id>2</id>
      <name>sample</name>
      <description />
      <email />
      <referrer />
      <label>
        <en>Sample group</en>
        <fr>Sample group</fr>
        <es>Sample group</es>
        <de>Beispielgruppe</de>
        <nl>Voorbeeldgroep</nl>
      </label>
    </record>
    <record>
      <id>3</id>
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
    </record>
  </response>

Group information (group.get)
`````````````````````````````

Retrieves group information. **Non XML response.**

Groups maintenance
------------------

Create/update a group (group.update)
````````````````````````````````````

The **group.update** service can be used to
create new groups and update the information of an existing group.
Only users with **Administrator** profile can
create/update groups.

Requires authentication: Yes

Request
^^^^^^^

Parameters:

- **id**: Group identifier to update. If
  not provided a new group it's created with name, description
  and email parameters provided.

- **name**: (mandatory) Name of the
  group

- **description**: Group
  description

- **email**: Mail address for the
  group

Group update request example::

  Url:
  http://localhost:8080/geonetwork/srv/en/group.update

  Mime-type:
  application/xml

  Post request:
  <request>
      <id>2</id>
      <name>sample</name>
      <description>Demo group</description>
      <email>group@mail.net</email>
  </request>

Response
^^^^^^^^

If request it's executed succesfully HTTP 200 status code it's
returned. If request fails an HTTP status code error it's returned
and the response contains the XML document with the
exception.

Errors
^^^^^^

- **Service not allowed (error id: service-not-allowed)**, when the
  user is not authenticated or his profile has no rights to
  execute the service. Returned 401 HTTP code

- **Missing parameter (error id: missing-parameter)**, when mandatory parameters
  are not provided. Returned 400 HTTP code

- **bad-parameter name**, when **name** it's
  empty. Returned 400 HTTP code

- **ERROR: duplicate key violates unique constraint
  "groups_name_key"**, when trying to create a new group using an existing
  group name. Returned 500 HTTP code

Update label translations (xml.group.update)
````````````````````````````````````````````

The **xml.group.update** service can be used to
update translations of a group name. Only users with
**Administrator** profile can update groups
translations.

Requires authentication: Yes

Request
^^^^^^^

Parameters:

- **group**: Container for group information
- **id**: (mandatory) Group identifier to update
- **label**: (mandatory) This is just
  a container to hold the group names translated in the
  languages supported by GeoNetwork. Each translated label
  it's enclosed in a tag that identifies the language code

Group label update request example::

  Url:
  http://localhost:8080/geonetwork/srv/en/xml.group.update

  Mime-type:
  application/xml

  Post request:  
  <request>
      <group id="2">
          <label>
              <es>Grupo de ejemplo</es>
          </label>
      </group>
  </request>

Response
^^^^^^^^

Group label update response example::

  <?xml version="1.0" encoding="UTF-8"?>
  <ok />

Errors
^^^^^^

- **Service not allowed (error id: service-not-allowed)**, when the
  user is not authenticated or his profile has no rights to
  execute the service. Returned 401 HTTP code

- **Missing parameter (error id: missing-parameter)**, when mandatory parameters
  are not provided. Returned 400 HTTP code

Remove a group (group.remove)
`````````````````````````````

The **group.remove** service can be used to
remove an existing group. Only users with
**Administrator** profile can delete groups.

Requires authentification: Yes

Request
^^^^^^^

Parameters:

- **id**: (mandatory) Group identifier to delete

Group remove request example::

  Url:
  http://localhost:8080/geonetwork/srv/en/group.remove

  Mime-type:
  application/xml

  Post request:
  <request>
      <id>2</id>
  </request>

Response
^^^^^^^^

If request it's executed succesfully HTTP 200 status code it's
returned. If request fails an HTTP status code error it's returned
and the response contains the XML document with the
exception.

Errors
^^^^^^

- **Service not allowed (error id: service-not-allowed)**, when the
  user is not authenticated or his profile has no rights to
  execute the service. Returned 401 HTTP code

- **Missing parameter (error id: missing-parameter)**, when mandatory parameters
  are not provided. Returned 400 HTTP code

- **bad-parameter id**, when **id** parameter it's
  empty. Returned 400 HTTP code


