.. _system_configuration:

System configuration
====================

Introduction
------------

The GeoNetwork’s configuration is made up of a set of parameters that can be
changed to accommodate any installation need. These parameters are subdivided
into 2 groups:

- parameters that can be easily changed through a web interface.

- parameters not accessible from a web interface and that must be
  changed when the system is not running.

The first group of parameters can be queried or changed through 2 services:
xml.config.get and xml.config.update. The second group of parameters can be
changed using the GAST tool.

xml.config.get
--------------

This service returns the system configuration’s parameters.

Request
```````

No parameters are needed.

Response
````````

The response is an XML tree similar to the system hierarchy into the
settings structure. The response has the
following elements:

- **site**: A container for site information.

  - **name**: Site’s name.
  - **organisation**: Site’s organisation name.

- **server**: A container for server information.

  - **host**: Name of the host from which the site is reached.
  - **port**: Port number of the previous host.

- **Intranet**: Information about the Intranet of the organisation.

  - **network**: IP address that specifies the network.
  - **netmask**: netmask of the network.

- **z3950**: Configuration about Z39.50 protocol.

  - **enable**: true means that the server component is running.
  - **port**: Port number to use to listen for incoming Z39.50
    requests.

- **proxy**: Proxy configuration

  - **use**: true means that the proxy is used when connecting to
    external nodes.
  - **host**: Proxy’s server host.
  - **port**: Proxy’s server port.
  - **username**: Proxy’s credentials.
  - **password**: Proxy’s credentials.

- **feedback**: A container for feedback information

  - **email**: Administrator’s email address
  - **mailServer**: Email server to use to send feedback

    - **host**: Email’s host address
    - **port**: Email’s port to use in host address

- **removedMetadata**: A container for removed metadata information

  - **dir**: Folder used to store removed metadata in MEF
    format

- **ldap**: A container for LDAP parameters

  - **use**:
  - **host**:
  - **port**:
  - **defaultProfile**:
  - **login**:

    - **userDN**:
    - **password**:

  - **distinguishedNames**:

    - **base**:
    - **users**:

  - **userAttribs**:

    - **name**:
    - **password**:
    - **profile**:

Example of xml.config.get response::

    <config>
        <site>
            <name>dummy</name>
            <organisation>dummy</organization>
        </site>
        <server>
            <host>localhost</host>
            <port>8080</port>
        </server>
        <Intranet>
            <network>127.0.0.1</network>
            <netmask>255.255.255.0</netmask>
        </intranet>
        <z3950>
            <enable>true</enable>
            <port>2100</port>
        </z3950>
        <proxy>
            <use>false</use>
            <host/>
            <port/>
            <username>proxyuser</username>
            <password>proxypass</password>
        </proxy>
        <feedback>
            <email/>
            <mailServer>
                <host/>
                <port>25</port>
            </mailServer>
        </feedback>
        <removedMetadata>
            <dir>WEB-INF/removed</dir>
        </removedMetadata>
        <ldap>
            <use>false</use>
            <host />
            <port />
            <defaultProfile>RegisteredUser</defaultProfile>
            <login>
                <userDN>cn=Manager</userDN>
                <password />
            </login>
            <distinguishedNames>
                <base>dc=fao,dc=org</base>
                <users>ou=people</users>
            </distinguishedNames>
            <userAttribs>
                <name>cn</name>
                <password>userPassword</password>
                <profile>profile</profile>
            </userAttribs>
        </ldap>
    </config>

xml.config.update
-----------------

This service is used to update the system’s information and so it is
restricted to administrators.

Request
```````

The request format must have the same structure returned by the
xml.config.get service and can contain only elements
that the caller wants to be updated. If an element is not included, it will
not be updated. However, when included some elements require mandatory
information (i.e. the value cannot be empty). Please, refer to :ref:`table_config_parameters`.

.. _table_config_parameters:

**Mandatory and optional parameters for the xml.config.update service:**

=============================       ========            ==========
Parameter                           Type                Mandatory
=============================       ========            ==========
site/name                           string              yes
site/organization                   string              no
server/host                         string              yes
server/port                         integer             no
intranet/network                    string              yes
intranet/netmask                    string              yes
z3950/enable                        boolean             yes
z3950/port                          integer             no
proxy/use                           boolean             yes
proxy/host                          string              no
proxy/port                          integer             no
proxy/username                      string              no
proxy/password                      string              no
feedback/email                      string              no
feedback/mailServer/host            string              no
feedback/mailServer/port            integer             no
removedMetadata/dir                 string              yes
ldap/use                            boolean             yes
ldap/host                           string              no
ldap/port                           integer             no
ldap/defaultProfile                 string              yes
ldap/login/userDN                   string              yes
ldap/login/password                 string              no
ldap/distinguishedNames/base        string              yes
ldap/distinguishedNames/users       string              yes
ldap/userAttribs/name               string              yes
ldap/userAttribs/password           string              yes
ldap/userAttribs/profile            string              no
=============================       ========            ==========

Response
````````

On success, the service returns a response element with the OK text.
Example::

    <response>ok</response>

Otherwise a proper error element is returned.

