.. _login_xml_services:

Login and logout services
=========================

Login services
--------------

GeoNetwork standard login (xml.user.login)
``````````````````````````````````````````

The **xml.user.login** service is used to
authenticate the user in GeoNetwork, allowing using the Xml services
that require authentication. For example, the services to maintain
group or user information.

Request
^^^^^^^

Parameters:

- **username** (mandatory): Login for the user to authenticate

- **password** (mandatory): Password for the user to authenticate

Login request example::

  Url:
  http://localhost:8080/geonetwork/srv/en/xml.user.login

  Mime-type:
  application/xml

  Post request:
  <?xml version="1.0" encoding="UTF-8"?>
  <request>
      <username>admin</username>
      <password>admin</password>
  </request>

Response
^^^^^^^^

When user authentication is succesful the next response is received::

  OK
  
  Date: Mon, 01 Feb 2010 09:29:43 GMT
  Expires: Thu, 01 Jan 1970 00:00:00 GMT
  Set-Cookie: JSESSIONID=1xh3kpownhmjh;Path=/geonetwork
  Content-Type: application/xml; charset=UTF-8
  Pragma: no-cache
  Cache-Control: no-cache
  Expires: -1
  Transfer-Encoding: chunked
  Server: Jetty(6.1.14)

The authentication process sets **JSESSIONID** cookie with the authentication token
that should be send in the services that need authentication to be
invoqued. Otherwise, a **Service not allowed**
exception will be returned by these services.

Errors
^^^^^^

- **Missing parameter (error id: missing-parameter)**, when
  mandatory parameters are not send. Returned 400 HTTP code

- **bad-parameter XXXX**, when an empty username or password
  is provided. Returned 400 HTTP code

- **User login failed (error id: user-login)**, when login
  information is not valid. Returned 400 HTTP code

Example returning **User login failed** exception::
  
  <?xml version="1.0" encoding="UTF-8"?>
  <error id="user-login">
    <message>User login failed</message>
    <class>UserLoginEx</class>
    <stack>
      <at class="org.fao.geonet.services.login.Login" file="Login.java" line="90" method="exec" />
      <at class="jeeves.server.dispatchers.ServiceInfo" file="ServiceInfo.java" line="238" method="execService" />
      <at class="jeeves.server.dispatchers.ServiceInfo" file="ServiceInfo.java" line="141" method="execServices" />
      <at class="jeeves.server.dispatchers.ServiceManager" file="ServiceManager.java" line="377" method="dispatch" />
      <at class="jeeves.server.JeevesEngine" file="JeevesEngine.java" line="621" method="dispatch" />
      <at class="jeeves.server.sources.http.JeevesServlet" file="JeevesServlet.java" line="174" method="execute" />
      <at class="jeeves.server.sources.http.JeevesServlet" file="JeevesServlet.java" line="99" method="doPost" />
      <at class="javax.servlet.http.HttpServlet" file="HttpServlet.java" line="727" method="service" />
      <at class="javax.servlet.http.HttpServlet" file="HttpServlet.java" line="820" method="service" />
      <at class="org.mortbay.jetty.servlet.ServletHolder" file="ServletHolder.java" line="502" method="handle" />
    </stack>
    <object>admin2</object>
    <request>
      <language>en</language>
      <service>user.login</service>
    </request>
  </error>

Shibboleth login (shib.user.login)
``````````````````````````````````

The **shib.user.login** service process the creadentials of a Shibboleth login.

To use this service the user previously should be authenticated to Shibboleth.
If the authentication is succesful, the HTTP headers will contain the user credentials.

When calling **shib.user.login** service in GeoNetwork, the Shibboleth credentials
are then used to find or create (if don't exists) the user account in GeoNetwork.

GeoNetwork processes the next HTTP header parameters filled by Shibboleth authentication:

- system/shib/attrib/username

- system/shib/attrib/surname

- system/shib/attrib/firstname

- system/shib/attrib/profile: User profile. Values:
  Administrator, UserAdmin, Reviewer, Editor and Guest

GeoNetwork checks if exists a user with the specified **username** in the users table, creating
it if not found.

Logout service
--------------

Logout (xml.user.logout)
````````````````````````

The **xml.user.logout** service clears user authentication session, removing the **JSESSIONID** cookie.

Request
^^^^^^^

Parameters:

- **None**:This request requires no parameters, just it's required sending the **JSESSIONID** cookie value.

Logout request example::

  Url:
  http://localhost:8080/geonetwork/srv/en/xml.user.logout

  Mime-type:
  application/xml

  Post request:
  <?xml version="1.0" encoding="UTF-8"?>
  <request/>

Response
^^^^^^^^

Logout response example::

  <?xml version="1.0" encoding="UTF-8"?>
  <ok />


