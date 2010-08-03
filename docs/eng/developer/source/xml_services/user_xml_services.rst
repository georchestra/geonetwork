.. _user_xml_services:

User services
=============

Users retrieving
----------------

Users list (xml.user.list)
``````````````````````````

The **xml.user.list** service can be used to retrieve the users defined in GeoNetwork.

Requires authentication: Yes

Request
^^^^^^^

Parameters:

- **None**

User list request example::

  Url:
  http://localhost:8080/geonetwork/srv/en/xml.user.list

  Mime-type:
  application/xml

  Post request:
  <?xml version="1.0" encoding="UTF-8"?>
  <request />

Response
^^^^^^^^

Here follows the structure of the response:

- **record**: This is the container for each user element returned
- **id**: User identifier
- **username**: Login name for the user
- **password**: Password encoded in md5
- **surname**: User surname
- **name**: User name
- **profile**: User profile. The profiles defined in GeoNetwork are: Administrator, User administrator, Content Reviewer, Editor, Registered user
- **address**: User physical address
- **city**: User address city
- **state**: User address state
- **zip**: User address zip
- **country**: User address country
- **email**: User email address
- **organisation**: User organisation/department
- **kind**: Kind of organisation

User list response example::

  <?xml version="1.0" encoding="UTF-8"?>
  <response>
    <record>
      <id>1</id>
      <username>admin</username>
      <password>d033e22ae348aeb566fc214aec3585c4da997</password>
      <surname>admin</surname>
      <name>admin</name>
      <profile>Administrator</profile>
      <address />
      <city />
      <state />
      <zip />
      <country />
      <email />
      <organisation />
      <kind />
    </record>
    <record>
      <id>2</id>
      <username>editor</username>
      <password>ab41949825606da179db7c89ddcedcc167b64847</password>
      <surname>Smith</surname>
      <name>John</name>
      <profile>Editor</profile>
      <address />
      <city>Amsterdam</city>
      <state />
      <zip />
      <country>nl</country>
      <email>john.smith@mail.com</email>
      <organisation />
      <kind>gov</kind>
    </record>
  </response>

Exceptions:

- **Service not allowed (error id: service-not-allowed)**, when the
  user is not authenticated or his profile has no rights to
  execute the service

User groups list (xml.usergroups.list)
``````````````````````````````````````

The **xml.usergroups.list** service can be used
to retrieve the groups assigned to a user.

Requires authentication: Yes

Request
^^^^^^^

Parameters:

- **id:** User identifier (multiple id elements can be espeficied)

User groups list request example::

  Url:
  http://localhost:8080/geonetwork/srv/en/xml.usergroups.list

  Mime-type:
  application/xml

  Post request:
  <?xml version="1.0" encoding="UTF-8"?>
  <request>
    <id>3</id>
  <request>

Response
^^^^^^^^

Here follows the structure of the response:

- **group:** This is the container for each user group element returned
- **id**: Group identifier
- name: Group name
- description: Group description

User groups list response example::

  <?xml version="1.0" encoding="UTF-8"?>
  <groups>
    <group>
      <id>3</id>
      <name>RWS</name>
      <description />
    </group>
  </groups>

Exceptions:

- **Service not allowed (error id: service-not-allowed)**, when the user is not authenticated or his profile has no rights to execute the service

- **User XXXX doesn't exist**, if no exists a user with provided **id** value

User information (user.get)
```````````````````````````

Retrieves user information. **Non XML response.**

Users maintenance
-----------------

Create a user (user.update)
```````````````````````````

The **user.update** service can be used to
create new users, update user information and reset user password,
depending on the value of the **operation**
parameter. Only users with profiles **Administrator**
or **UserAdmin** can create new users.

Users with profile **Administrator** can create
users in any group, while users with profile
**UserAdmin** can create users only in the groups
where they belong.

Requires authentication: Yes

Request
^^^^^^^

Parameters:

- **operation**: (mandatory) **newuser**
- **username**: (mandatory) User login name
- **password**: (mandatory) User password
- **profile**: (mandatory) User profile
- **surname**:User surname
- **name**: User name
- **address**: User physical address
- **city**: User address city
- **state**: User address state
- **zip**: User address zip
- **country**: User address country
- **email**: User email
- **org**: User organisation/departament
- **kind**: Kind of organisation
- **groups**: Group identifier to set for the user, can be multiple **groups** elements
- **groupid**: Group identifier

User create request example::

  Url:
  http://localhost:8080/geonetwork/srv/en/user.update

  Mime-type:
  application/xml

  Post request:
  <request>
    <operation>**newuser**</operation>
    <username>samantha</username>
    <password>editor2</password>
    <profile>Editor</profile>
    <name>Samantha</name>
    <city>Amsterdam</city>
    <country>Netherlands</country>
    <email>samantha@mail.net</email>
    <groups>2</groups>
    <groups>4</groups>
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
  are not provided

- **bad-parameter**, when a mandatory fields is empty

- **Unknow profile XXXX (error id: error)**, when the profile is
  not valid

- **ERROR: duplicate key violates unique constraint
  "users_username_key"**, when trying to create a new user using an existing
  username

- **ERROR: insert or update on table "usergroups" violates
  foreign key constraint "usergroups_groupid_fkey"**, when group
  identifier is not an existing group identifier

- **ERROR: tried to add group id XX to user XXXX - not
  allowed because you are not a member of that group**, when the
  authenticated user has profile **UserAdmin** and tries to add the
  user to a group in which the **UserAdmin** user is not allowed
  to manage

- **ERROR: you don't have rights to do this**, when the
  authenticated user has a profile that is not
  **Administrator** or
  **UserAdmin**

Update user information (user.update)
`````````````````````````````````````

The **user.update** service can be used to
create new users, update user information and reset user password,
depending on the value of the **operation**
parameter. Only users with profiles **Administrator**
or **UserAdmin** can update users information.

Users with profile **Administrator** can update
any user, while users with profile **UserAdmin** can
update users only in the groups where they belong.

Requires authentication: Yes

Request
^^^^^^^

Parameters:

- **operation**: (mandatory) **editinfo**
- **id**: (mandatory) Identifier of the user to update
- **username**: (mandatory) User login name
- **password**: (mandatory) User password
- **profile**: (mandatory) User profile
- **surname**: User surname
- **name**: User name
- **address**: User physical address
- **city**: User address city
- **state**: User address state
- **zip**: User address zip
- **country**: User address country
- **email**: User email
- **org**: User organisation/departament
- **kind**: Kind of organisation
- **groups**: Group identifier to set for the user, can be multiple **groups** elements
- **groupid**: Group identifier

**Remarks**: If an optional parameter it's not provided the value it's updated in the database with an empty string.

Update user information request example::

  Url:
  http://localhost:8080/geonetwork/srv/en/user.update

  Mime-type:
  application/xml

  Post request:
  <?xml version="1.0" encoding="UTF-8"?>
  <request>
    <operation>**editinfo**</operation>
    <id>5</id>
    <username>samantha</username>
    <password>editor2</password>
    <profile>Editor</profile>
    <name>Samantha</name>
    <city>Rotterdam</city>
    <country>Netherlands</country>
    <email>samantha@mail.net</email>
  </request>

Response
^^^^^^^^

If request it's executed succesfully HTTP 200 status code it's
returned. If request fails an HTTP status code error it's returned
and the response contains the XML document with the exception.

Errors
^^^^^^

- **Service not allowed (error id: service-not-allowed)**, when the
  user is not authenticated or his profile has no rights to
  execute the service. Returned 401 HTTP code

- **Missing parameter (error id: missing-parameter)**, when the mandatory parameters
  are not provided. Returned 400 HTTP code

- **bad-parameter**, when a mandatory field is empty.
  Returned 400 HTTP code

- **Unknow profile XXXX (error id: error)**, when the  profile is
  not valid. Returned 500 HTTP code

- **ERROR: duplicate key violates unique constraint
  "users_username_key"**, when trying to create a new user using an existing
  username. Returned 500 HTTP code

- **ERROR: insert or update on table "usergroups" violates
  foreign key constraint "usergroups_groupid_fkey"**, when the group
  identifier is not an existing group identifier. Returned 500
  HTTP code

- **ERROR: tried to add group id XX to user XXXX - not
  allowed because you are not a member of that group**, when the
  authenticated user has profile **UserAdmin** and tries to add the
  user to a group in which the **UserAdmin** user is not allowed
  to manage. Returned 500 HTTP code

- **ERROR: you don't have rights to do this**, when  the
  authenticated user has a profile that is not
  **Administrator** or
  **UserAdmin**. Returned 500 HTTP
  code****

Reset user password (user.update)
`````````````````````````````````

The **user.update** service can be used to
create new users, update user information and reset user password,
depending on the value of the **operation**
parameter. Only users with profiles **Administrator**
or **UserAdmin** can reset users password.

Users with profile **Administrator** can reset
the password for any user, while users with profile
**UserAdmin** can reset the password for users only
in the groups where they belong.

Requires authentication: Yes

Request
^^^^^^^

Parameters:

- **operation**: (mandatory) **resetpw**
- **id**: (mandatory) Identifier of the user to reset the password
- **username**: (mandatory) User login name
- **password**: (mandatory) User new password
- **profile**: (mandatory) User profile

Reset user password request example::

  Url:
  http://localhost:8080/geonetwork/srv/en/user.update

  Mime-type:
  application/xml

  Post request:
  <?xml version="1.0" encoding="UTF-8"?>
  <request>
    <operation>**resetpw**</operation>
    <id>2</id>
    <username>editor</username>
    <password>newpassword</password>
    <profile>Editor</profile>
  </request>

Response
^^^^^^^^

If request it's executed succesfully HTTP 200 status code it's
returned. If request fails an HTTP status code error it's returned
and the response contains the XML document with the exception.

Errors
^^^^^^

- **Service not allowed (error id: service-not-allowed)**, when the
  user is not authenticated or his profile has no rights to
  execute the service. Returned 401 HTTP code

- **Missing parameter (error id: missing-parameter)**, when the mandatory parameters
  are not provided. Returned 400 HTTP code

- **bad-parameter**, when a mandatory field is empty.
  Returned 400 HTTP code

- **Unknow profile XXXX (error id: error)**, when the profile is
  not valid. Returned 500 HTTP code

- **ERROR: you don't have rights to do this**, when the
  authenticated user has a profile that it's not
  **Administrator** or
  **UserAdmin**. Returned 500 HTTP code****

Update current authenticated user information (user.infoupdate)
```````````````````````````````````````````````````````````````

The **user.infoupdate** service can be used to update the information related to the current authenticated user.

Requires authentication: Yes

Request
^^^^^^^

Parameters:

- **surname**: (mandatory) User surname
- **name**: (mandatory) User name
- **address**: User physical address
- **city**: User address city
- **state**: User address state
- **zip**: User address zip
- **country**: User address country
- **email**: User email
- **org**: User organisation/departament
- **kind**: Kind of organisation

**Remarks**: If an optional parameter is not provided the value is updated in the database with an empty string.

Current user info update request example::

  Url:
  http://localhost:8080/geonetwork/srv/en/user.infoupdate

  Mime-type:
  application/xml

  Post request:
  <request>
    <name>admin</name>
    <surname>admin</surname>
    <address>address</address>
    <city>Amsterdam</city>
    <zip>55555</zip>
    <country>Netherlands</country>
    <email>user@mail.net</email>
    <org>GeoCat</org>
    <kind>gov</kind>
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
  user is not authenticated. Returned 401 HTTP code

Change current authenticated user password (user.pwupdate)
``````````````````````````````````````````````````````````

The **user.pwupdate** service can be used to
change the password of the current user authenticated.

Requires authentication: Yes

Request
^^^^^^^

Parameters:

- **password**: Actual user password

- **newPassword**: New password to set for the user

Example::

  <request>
      <password>admin</password>
      <newPassword>admin2</newPassword>
  </request>

Response
^^^^^^^^

If request it's executed succesfully HTTP 200 status code it's
returned. If request fails an HTTP status code error it's returned
and the response contains the XML document with the exception.

Errors
^^^^^^

- **Service not allowed (error id: service-not-allowed)**, when the
  user is not authenticated. Returned 401 HTTP code

- **Old password is not correct**. Returned 500 HTTP code

- **Bad parameter (newPassword)**, when an empty password is
  provided. Returned 400 HTTP code

Remove a user (user.remove)
```````````````````````````

The **user.remove** service can be used to
remove an existing user. Only users with profiles
**Administrator** or **UserAdmin**
can delete users.

Users with profile **Administrator** can delete
any user (except himself), while users with profile
**UserAdmin** can delete users only in the groups
where they belong (except himself).

Requires authentification: Yes

Request
^^^^^^^

Parameters:

- **id**: (mandatory) User identifier to
  delete

User remove request example::

  Url:
  http://localhost:8080/geonetwork/srv/en/user.remove

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

- **Missing parameter (error id: missing-parameter)**, when the
  **id** parameter is not provided. Returned
  400 HTTP code

- **You cannot delete yourself from the user database (error
  id: error)**, when trying to delete the authenticated user himself.
  Returned 500 HTTP code

- **You don't have rights to delete this user (error id:
  error)**, when trying to delete using an authenticated user that
  don't belongs to **Administrator** or
  **User administrator** profiles. Returned 500
  HTTP code

- **You don't have rights to delete this user because the
  user is not part of your group (error id: error)**, when trying to
  delete a user that is not in the same group of the
  authenticated user (belonging the authenticated user to
  profile **User administrator**). Returned 500
  HTTP code


