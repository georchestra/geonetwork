-- ======================================================================
-- ===   Sql Script for Database : Geonet
-- ===
-- === Build : 153
-- ======================================================================

CREATE TABLE Relations
  (
    id         int,
    relatedId  int,

    primary key(id,relatedId)
  );

-- ======================================================================

CREATE TABLE Categories
  (
    id    int,
    name  varchar(32)   not null,

    primary key(id),
    unique(name)
  );

-- ======================================================================

CREATE TABLE Settings
  (
    id        int,
    parentId  int,
    name      varchar(32)    not null,
    value     text,

    primary key(id),

    foreign key(parentId) references Settings(id)
  );

-- ======================================================================

CREATE TABLE Languages
  (
    id    varchar(5),
    name  varchar(32)   not null,
    isocode varchar(3)  not null,

    primary key(id)
  );

-- ======================================================================

CREATE TABLE Sources
  (
    uuid     varchar(250),
    name     varchar(250),
    isLocal  char(1)        default 'y',

    primary key(uuid)
  );

-- ======================================================================

CREATE TABLE IsoLanguages
  (
    id    int,
    code  varchar(3)   not null,

    primary key(id),
    unique(code)
  );

-- ======================================================================

CREATE TABLE IsoLanguagesDes
  (
    idDes   int,
    langId  varchar(5),
    label   varchar(96)   not null,

    primary key(idDes,langId),

    foreign key(idDes) references IsoLanguages(id),
    foreign key(langId) references Languages(id)
  );

-- ======================================================================

CREATE TABLE Regions
  (
    id     int,
    north  float   not null,
    south  float   not null,
    west   float   not null,
    east   float   not null,

    primary key(id)
  );

-- ======================================================================

CREATE TABLE RegionsDes
  (
    idDes   int,
    langId  varchar(5),
    label   varchar(96)   not null,

    primary key(idDes,langId),

    foreign key(idDes) references Regions(id),
    foreign key(langId) references Languages(id)
  );

-- ======================================================================

CREATE TABLE Users
  (
    id            int,
    username      varchar(32)    not null,
    password      varchar(40)    not null,
    surname       varchar(32),
    name          varchar(32),
    profile       varchar(32)    not null,
    address       varchar(128),
    city          varchar(128),
    state         varchar(32),
    zip           varchar(16),
    country       varchar(128),
    email         varchar(128),
    organisation  varchar(128),
    kind          varchar(16),

    primary key(id),
    unique(username)
  );

-- ======================================================================

CREATE TABLE Operations
  (
    id        int,
    name      varchar(32)   not null,
    reserved  char(1)       default 'n' not null,

    primary key(id)
  );

-- ======================================================================

CREATE TABLE OperationsDes
  (
    idDes   int,
    langId  varchar(5),
    label   varchar(96)   not null,

    primary key(idDes,langId),

    foreign key(idDes) references Operations(id),
    foreign key(langId) references Languages(id)
  );

-- ======================================================================

CREATE TABLE Groups
  (
    id           int,
    name         varchar(32)    not null,
    description  varchar(255),
    email        varchar(32),
    referrer     int,

    primary key(id),
    unique(name),

    foreign key(referrer) references Users(id)
  );

-- ======================================================================

CREATE TABLE GroupsDes
  (
    idDes   int,
    langId  varchar(5),
    label   varchar(96)   not null,

    primary key(idDes,langId),

    foreign key(idDes) references Groups(id),
    foreign key(langId) references Languages(id)
  );

-- ======================================================================

CREATE TABLE UserGroups
  (
    userId   int,
    groupId  int,

    primary key(userId,groupId),

    foreign key(userId) references Users(id),
    foreign key(groupId) references Groups(id)
  );

-- ======================================================================

CREATE TABLE CategoriesDes
  (
    idDes   int,
    langId  varchar(5),
    label   varchar(96)   not null,

    primary key(idDes,langId),

    foreign key(idDes) references Categories(id),
    foreign key(langId) references Languages(id)
  );

-- ======================================================================

CREATE TABLE Metadata
  (
    id           int,
    uuid         varchar(250)   not null,
    schemaId     varchar(32)    not null,
    isTemplate   char(1)        default 'n' not null,
    isHarvested  char(1)        default 'n' not null,
    createDate   varchar(24)    not null,
    changeDate   varchar(24)    not null,
    data         text           not null,
    source       varchar(250)   not null,
    title        varchar(255),
    root         varchar(255),
    harvestUuid  varchar(250)   default null,
    owner        int            not null,
    groupOwner   int            default null,
    harvestUri   varchar(255)   default null,
    rating       int            default 0 not null,
    popularity   int            default 0 not null,
	displayorder int,

    primary key(id),
    unique(uuid),

    foreign key(owner) references Users(id),
    foreign key(groupOwner) references Groups(id)
  );

CREATE INDEX MetadataNDX1 ON Metadata(uuid);
CREATE INDEX MetadataNDX2 ON Metadata(source);

-- ======================================================================

CREATE TABLE MetadataCateg
  (
    metadataId  int,
    categoryId  int,

    primary key(metadataId,categoryId),

    foreign key(metadataId) references Metadata(id),
    foreign key(categoryId) references Categories(id)
  );

-- ======================================================================

CREATE TABLE OperationAllowed
  (
    groupId      int,
    metadataId   int,
    operationId  int,

    primary key(groupId,metadataId,operationId),

    foreign key(groupId) references Groups(id),
    foreign key(metadataId) references Metadata(id),
    foreign key(operationId) references Operations(id)
  );

-- ======================================================================

CREATE TABLE MetadataRating
  (
    metadataId  int,
    ipAddress   varchar(32),
    rating      int           not null,

    primary key(metadataId,ipAddress),

    foreign key(metadataId) references Metadata(id)
  );

-- ======================================================================

CREATE TABLE MetadataNotifiers
  (
    id         int,
    name       varchar(32)    not null,
    url        varchar(255)   not null,
    enabled    char(1)        default 'n' not null,
    username       varchar(32),
    password       varchar(32),

    primary key(id)
  );

-- ======================================================================

CREATE TABLE MetadataNotifications
  (
    metadataId         int,
    notifierId         int,
    notified           char(1)        default 'n' not null,
    metadataUuid       varchar(250)   not null,
    action             char(1)        not null,
    errormsg           text,

    primary key(metadataId,notifierId),

    foreign key(notifierId) references MetadataNotifiers(id)
  );

-- ======================================================================

CREATE TABLE CswServerCapabilitiesInfo
  (
    idField   int,
    langId    varchar(5)    not null,
    field     varchar(32)   not null,
    label     varchar(96),

    primary key(idField),

    foreign key(langId) references Languages(id)
  );

-- ======================================================================
CREATE TABLE IndexLanguages
  (
    id            int,
    languageName  varchar(32)   not null,
    selected      char(1)       default 'n' not null,

    primary key(id, languageName)

  );
--  -- ======================================================================

--  -- ======================================================================
-- 
-- GeOrchestra modification : adding shared object tables
-- This implies to use a PostGreSQL spatial database (postgis)

CREATE TABLE sharedobject_templates (
    typename character varying(32) NOT NULL,
    template text NOT NULL
);


CREATE TABLE spatialindex (
    fid integer NOT NULL,
    id character varying(250),
    the_geom geometry,
    CONSTRAINT enforce_dims_the_geom CHECK ((ndims(the_geom) = 2)),
    CONSTRAINT enforce_geotype_the_geom CHECK (((geometrytype(the_geom) = 'MULTIPOLYGON'::text) OR (the_geom IS NULL))),
    CONSTRAINT enforce_srid_the_geom CHECK ((srid(the_geom) = 4326))
);

INSERT INTO sharedobject_templates VALUES ('shared_contacts', '<gmd:CI_ResponsibleParty xmlns:gmd="http://www.isotc211.org/2005/gmd" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:gco="http://www.isotc211.org/2005/gco" xsi:schemaLocation="http://www.isotc211.org/2005/gmd http://www.isotc211.org/2005/gmd/gmd.xsd">
  <gmd:individualName xsi:type="gmd:PT_FreeText_PropertyType">
    <gco:CharacterString>Nom</gco:CharacterString>
  </gmd:individualName>
  <gmd:organisationName xsi:type="gmd:PT_FreeText_PropertyType">
    <gco:CharacterString>Organisme</gco:CharacterString>
  </gmd:organisationName>
  <gmd:positionName xsi:type="gmd:PT_FreeText_PropertyType">
    <gco:CharacterString>Position</gco:CharacterString>
  </gmd:positionName>
  <gmd:contactInfo>
    <gmd:CI_Contact>
      <gmd:phone>
        <gmd:CI_Telephone>
          <gmd:voice>
            <gco:CharacterString>Voice 1</gco:CharacterString>
          </gmd:voice>
        </gmd:CI_Telephone>
      </gmd:phone>
      <gmd:address>
        <gmd:CI_Address>
          <gmd:deliveryPoint xsi:type="gmd:PT_FreeText_PropertyType">
            <gco:CharacterString>Adresse</gco:CharacterString>
          </gmd:deliveryPoint>
          <gmd:city>
            <gco:CharacterString>Ville</gco:CharacterString>
          </gmd:city>
          <gmd:administrativeArea>
            <gco:CharacterString>Incorporation Administrative</gco:CharacterString>
          </gmd:administrativeArea>
          <gmd:postalCode>
            <gco:CharacterString>Code postal</gco:CharacterString>
          </gmd:postalCode>
          <gmd:country>
            <gco:CharacterString>Pays</gco:CharacterString>
          </gmd:country>
          <gmd:electronicMailAddress>
            <gco:CharacterString>email@address.com</gco:CharacterString>
          </gmd:electronicMailAddress>
        </gmd:CI_Address>
      </gmd:address>
      <gmd:onlineResource>
        <gmd:CI_OnlineResource>
          <gmd:linkage>
            <gmd:URL>http://website.com</gmd:URL>
          </gmd:linkage>
          <gmd:protocol>
            <gco:CharacterString>WWW:LINK-1.0-http--link</gco:CharacterString>
          </gmd:protocol>
          <gmd:applicationProfile>
            <gco:CharacterString>applciation profile</gco:CharacterString>
          </gmd:applicationProfile>
          <gmd:name>
            <gco:CharacterString gco:nilReason="missing" />
          </gmd:name>
          <gmd:description>
            <gco:CharacterString gco:nilReason="missing" />
          </gmd:description>
          <gmd:function>
            <gmd:CI_OnLineFunctionCode codeList="http://www.isotc211.org/2005/resources/codeList.xml#CI_OnLineFunctionCode" codeListValue="information" />
          </gmd:function>
        </gmd:CI_OnlineResource>
      </gmd:onlineResource>
      <gmd:hoursOfService>
        <gco:CharacterString>hours of service</gco:CharacterString>
      </gmd:hoursOfService>
      <gmd:contactInstructions>
        <gco:CharacterString>contact instructions</gco:CharacterString>
      </gmd:contactInstructions>
    </gmd:CI_Contact>
  </gmd:contactInfo>
  <gmd:role>
    <gmd:CI_RoleCode codeListValue="originator" codeList="http://www.isotc211.org/2005/resources/codeList.xml#CI_RoleCode" />
  </gmd:role>
</gmd:CI_ResponsibleParty>');

    
ALTER TABLE ONLY spatialindex
    ADD CONSTRAINT spatialindex_pkey PRIMARY KEY (fid);

CREATE INDEX spatialindexndx1 ON spatialindex USING btree (id);

CREATE INDEX spatialindexndx2 ON spatialindex USING gist (the_geom);

--  -- ======================================================================

