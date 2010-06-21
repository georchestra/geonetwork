.. _mef:

Metadata Exchange Format v1.1
=============================

Introduction
------------

The metadata exchange format (MEF in short) is a special designed file format
whose purpose is to allow metadata exchange between different platforms. A metadata
exported into this format can be imported by any platform which is able to
understand it. This format has been developed with GeoNetwork in mind so the
information it contains is mainly related to it. Nevertheless, it can be used as an
interoperability format between any platform.

This format has been designed with these needs in mind:

#.  Export a metadata record for backup purposes

#.  Import a metadata record from a previous backup

#.  Import a metadata record from a different GeoNetwork version to allow a
    smooth migration from one version to another.

All these operations regard the metadata and its related data as well.

In the paragraphs below, some terms should be intended as follows:

#.  the term actor is used to indicate any system (application, service
    etc...) that operates on metadata.

#.  the term reader will be used to indicate any actor that can import
    metadata from a MEF file.

#.  the term writer will be used to indicate any actor that can generate a MEF
    file.

File format
-----------

A MEF file is simply a ZIP file which contains the following files:

#.  *metadata.xml*: this file contains the metadata itself, in XML format. The
    text encoding of the metadata is that one specified into the XML
    declaration.

#.  *info.xml*: this is a special XML file which contains information related
    to the metadata but that cannot be stored into it. Examples of such
    information are the creation date, the last change date, privileges on the
    metadata and so on. Now this information is related to the GeoNetwork’s
    architecture.

#.  *public*: this is a directory used to store the metadata thumbnails and
    other public files. There are no restrictions on the images’ format but it
    is strongly recommended to use the portable network graphics (PNG), the JPEG
    or the GIF formats.

#.  *private*: this is a directory used to store all data (maps, shape files
    etc...) associated to the metadata. Files in this directory are
    *private* in the sense that an authorisation is
    required to access them. There are no restrictions on the file types that
    can be stored into this directory.

Any other file or directory present into the MEF file should be ignored by readers
that don’t recognise them. This allows actors to add custom extensions to the MEF
file.

A MEF file can have empty public and private folders depending on the export
format, which can be:

#.  *simple*: both public and private are omitted.

#.  *partial*: only public files are provided.

#.  *full*: both public and private files are provided.

It is recommended to use the .mef extension when naming MEF files.

The info.xml file
-----------------

This file contains general information about a metadata. It must have an info root
element with a mandatory version attribute. This attribute must be in the X.Y form,
where X represents the major version and Y the minor one. The purpose of this
attribute is to allow future changes of this format maintaining compatibility with
older readers. The policy behind the version is this:

#.  A change to Y means a minor change. All existing elements in the previous
    version must be left unchanged: only new elements or attributes may be
    added. A reader capable of reading version X.Y is also capable of reading
    version X.Y’ with Y’>Y.

#.  A change to X means a major change. Usually, a reader of version X.Y is
    not able to read version X’.Y with X’>X.

The root element must have the following children:

#.  *general*: a container for general information. It must have the following children:

    #.  *UUID*: this is the universally unique identifier assigned to the
        metadata and must be a valid UUID. This element is optional and,
        when omitted, the reader should generate one. A metadata without a
        UUID can be imported several times into the same system without
        breaking uniqueness constraints. When missing, the reader should
        also generate the siteId value.
    #.  *createDate*: This date indicates when the metadata was created.
    #.  *changeDate*: This date keeps track of the most recent change to
        the metadata.
    #.  *siteId*: This is an UUID that identifies the actor that created
        the metadata and must be a valid UUID. When the UUID element is
        missing, this element should be missing too. If present, it will be
        ignored.
    #.  *siteName*: This is a human readable name for the actor that
        created the metadata. It must be present only if the siteId is
        present.
    #.  *schema*: Indicates the metadata’s schema. The value can be
        assigned as will but if the schema is one of those describe below,
        that value must be used:
        
        #.  *dublin-core*: A metadata in the Dublin Core format as described in http://dublincore.org
        #.  *fgdc-std*: A metadata in the Federal Geographic Data Committee.
        #.  *iso19115*: A metadata in the ISO 19115 format
        #.  *iso19139*: A metadata in the ISO 19115/2003 format for which the ISO19139 is the XML encoding.
        
    #.  *format*: Indicates the MEF export format. The element’s value must
        belong to the following set: { *simple, partial*, *full* }.
    #.  *localId*: This is an optional element. If present, indicates the
        id used locally by the sourceId actor to store the metadata. Its
        purpose is just to allow the reuse of the same local id when
        reimporting a metadata.
    #.  *isTemplate*: A boolean field that indicates if this metadata is a
        template used to create new ones. There is no real distinction
        between a real metadata and a template but some actors use it to
        allow fast metadata creation. The value must be: {
        *true*, *false* }.
    #.  *rating*: This is an optional element. If present, indicates the
        users’ rating of the metadata ranging from 1 (a bad rating) to 5 (an
        excellent rating). The special value 0 means that the metadata has
        not been rated yet. Can be used to sort search results.
    #.  *popularity*: Another optional value. If present, indicates the
        popularity of the metadata. The value must be positive and high
        values mean high popularity. The criteria used to set the popularity
        is left to the writer. Its main purpose is to provide a metadata
        ordering during a search.

#.  *categories*: a container for categories associated to this metadata. A
    category is just a name, like ’audio-video’ that classifies the metadata to
    allow an easy search. Each category is specified by a category element which
    must have a name attribute. This attribute is used to store the category’s
    name. If there are no categories, the categories element will be empty.

#.  *privileges*: a container for privileges associated to this metadata.
    Privileges are operations that a group (which represents a set of users) can
    do on a metadata and are specified by a set of group elements. Each one of
    these, has a mandatory name attribute to store the group’s name and a set of
    operation elements used to store the operations allowed on the metadata.
    Each operation element must have a name attribute which value must belong to
    the following set: { *view*, *download*, *notify*, *dynamic*, *featured* }. 
    If there are no groups or the actor does not have the concept of group, the
    privileges element will be empty. A group element without any operation
    element must be ignored by readers.

#.  *public*: All metadata thumbnails (and any other public file) must be
    listed here. This container contains a file element for each file. Mandatory
    attributes of this element are name, which represents the file’s name and
    changeDate, which contains the date of the latest change to the file. The
    public element is optional but, if present, must contain all the files
    present in the metadata’s public directory and any reader that imports these
    files must set the latest change date on these using the provided ones. The
    purpose of this element is to provide more information in the case the MEF
    format is used for metadata harvesting.

#.  *private*: This element has the same purpose and structure of the public
    element but is related to maps and all other private files.

Any other element or attribute should be ignored by readers that don’t understand
them. This allows actors to add custom attributes or subtrees to the XML.

Date format
```````````

Unless differently specified, all dates in this file must be in the ISO/8601
format. The pattern must be YYYY-MM-DDTHH:mm:SS and the timezone should be the
local one.

.. _info_xml:

Example of info file::

    <info version="1.0">
        <general>
            <UUID>0619abc0-708b-eeda-8202-000d98959033</uuid>
            <createDate>2006-12-11T10:33:21</createDate>
            <changeDate>2006-12-14T08:44:43</changeDate>
            <siteId>0619cc50-708b-11da-8202-000d9335906e</siteId>
            <siteName>FAO main site</siteName>
            <schema>iso19139</schema>
            <format>full</format>
            <localId>204</localId>
            <isTemplate>false</isTemplate>
        </general>
        <categories>
            <category name="maps"/>
            <category name="datasets"/>
        </categories>
        <privileges>
            <group name="editors">
                <operation name="view"/>
                <operation name="download"/>
            </group>
        </privileges>
        <public>
            <file name="small.png" changeDate="2006-10-07T13:44:32"/>
            <file name="large.png" changeDate="2006-11-11T09:33:21"/>
        </public>
        <private>
            <file name="map.zip" changeDate="2006-11-12T13:23:01"/>
        </private>
    </info>


